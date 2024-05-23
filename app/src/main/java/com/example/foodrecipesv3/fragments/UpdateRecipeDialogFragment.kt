package com.example.foodrecipesv3.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.adapters.AddedImageSliderAdapter
import com.example.foodrecipesv3.adapters.UpdateImageSliderAdapter
import com.example.foodrecipesv3.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UpdateRecipeDialogFragment : DialogFragment() {

    private val imageUris: MutableList<Uri> = mutableListOf()
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recipeId: String
    private lateinit var currentRecipe: Recipe

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 100
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(recipeId: String): UpdateRecipeDialogFragment {
            val fragment = UpdateRecipeDialogFragment()
            val args = Bundle()
            args.putString(ARG_RECIPE_ID, recipeId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var recipeTitle: EditText
    private lateinit var recipeHashtags: EditText
    private lateinit var addIngredientButton: ImageButton
    private lateinit var recipeInstructions: EditText
    private lateinit var addImageButton: Button

    private lateinit var ingredientContainer: LinearLayout
    private lateinit var charCountText: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorLayout: LinearLayout
    private lateinit var saveRecipeButton:  Button
    private lateinit var cancelButton: Button
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recipeId = arguments?.getString(ARG_RECIPE_ID) ?: throw IllegalArgumentException("Recipe ID must be passed to UpdateRecipeDialogFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeTitle = view.findViewById(R.id.recipeTitle)
        recipeHashtags = view.findViewById(R.id.recipeHashtags)
        addIngredientButton = view.findViewById(R.id.addIngredientButton)
        ingredientContainer = view.findViewById(R.id.ingredientContainer)
        charCountText = view.findViewById(R.id.charCountText)
        recipeInstructions = view.findViewById(R.id.recipeInstructions)
        addImageButton = view.findViewById(R.id.addImageButton)
        viewPager = view.findViewById(R.id.viewPager)
        indicatorLayout = view.findViewById(R.id.indicatorLayout)
        saveRecipeButton = view.findViewById(R.id.saveButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        progressBar = activity?.findViewById(R.id.progressBar)

        recipeTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                charCountText.text = "$length/50"
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        addImageButton.setOnClickListener { selectImage() }
        addIngredientButton.setOnClickListener { addNewIngredientRow(ingredientContainer) }

        viewPager.adapter = UpdateImageSliderAdapter(requireContext(), imageUris) { position ->
            imageUris.removeAt(position)
            viewPager.adapter?.notifyItemRemoved(position)
            viewPager.adapter?.notifyItemRangeChanged(position, imageUris.size)
        }

        saveRecipeButton.setOnClickListener { showConfirmationDialog() }

        view.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dismiss()
        }

        loadRecipeData()
    }

    private fun loadRecipeData() {
        firestore.collection("recipes").document(recipeId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentRecipe = document.toObject(Recipe::class.java)!!
                    fillRecipeData(currentRecipe)
                } else {
                    Toast.makeText(requireContext(), "Recipe not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error loading recipe: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fillRecipeData(recipe: Recipe) {
        recipeTitle.setText(recipe.title)
        recipeHashtags.setText(recipe.hashtags)
        recipeInstructions.setText(recipe.description)

        recipe.ingredients.forEach { ingredient ->
            addNewIngredientRowWithText(ingredientContainer, ingredient)
        }

        imageUris.addAll(recipe.images.map { Uri.parse(it) })
        viewPager.adapter?.notifyDataSetChanged()
    }

    private fun addNewIngredientRowWithText(container: LinearLayout, text: String) {
        val newRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
        }

        val newEditText = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            hint = getString(R.string.add_an_ingredient)
            setText(text)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_primary))
            setHintTextColor(ContextCompat.getColor(requireContext(), R.color.hint_color))
        }

        val deleteButton = ImageButton(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 2, 2, 2)
            }
            setImageResource(R.drawable.baseline_delete_24)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.neumorphism_button_small)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setOnClickListener {
                container.removeView(newRow)
            }
        }

        newRow.addView(newEditText)
        newRow.addView(deleteButton)
        container.addView(newRow)
    }
    //tariflerim kısmında açılan modalın boyutlarını belirleme
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = WindowManager.LayoutParams.MATCH_PARENT
            val displayMetrics = DisplayMetrics()
            val display = context?.display
            display?.getRealMetrics(displayMetrics)
            val height = (displayMetrics.heightPixels * 0.7).toInt()
            dialog.window?.setLayout(width, height)
            //aşağıdaki kod corner verince çıkan siyah sivri çıkıntıları engellemek için
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
    private fun addNewIngredientRow(container: LinearLayout) {
        val newRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
        }

        val newEditText = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            hint = getString(R.string.add_an_ingredient)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_primary))
            setHintTextColor(ContextCompat.getColor(requireContext(), R.color.hint_color))
        }

        val deleteButton = ImageButton(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8,2,2,2)
            }
            setImageResource(R.drawable.baseline_delete_24)
            // setBackgroundResource(R.drawable.neumorphism_button_small)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.neumorphism_button_small)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setOnClickListener {
                container.removeView(newRow)
            }
        }

        newRow.addView(newEditText)
        newRow.addView(deleteButton)
        container.addView(newRow)
    }




    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to update this recipe?")
            .setPositiveButton("Yes") { dialog, which ->
                updateRecipe(recipeTitle.text.toString(), recipeHashtags.text.toString(), recipeInstructions.text.toString(), ingredientContainer, imageUris)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun selectImage() {
        if (imageUris.size >= 3) {
            Toast.makeText(requireContext(), "You can only add up to 3 images.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.let { intentData ->
                val clipData = intentData.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        if (imageUris.size < 3) {
                            imageUris.add(uri)
                        } else {
                            Toast.makeText(requireContext(), "You can only add up to 3 images.", Toast.LENGTH_SHORT).show()
                            break
                        }
                    }
                } else {
                    intentData.data?.let { uri ->
                        if (imageUris.size < 3) {
                            imageUris.add(uri)
                        } else {
                            Toast.makeText(requireContext(), "You can only add up to 3 images.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                viewPager.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun updateRecipe(title: String, hashtags: String, description: String, ingredientContainer: LinearLayout, imageUris: List<Uri>) {
        progressBar?.visibility = View.VISIBLE // Show spinner
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            progressBar?.visibility = View.GONE // Hide spinner
            return
        }

        val ingredients = mutableListOf<String>()
        for (i in 0 until ingredientContainer.childCount) {
            val row = ingredientContainer.getChildAt(i) as LinearLayout
            val editText = row.getChildAt(0) as EditText
            ingredients.add(editText.text.toString())
        }

        val updatedRecipe = hashMapOf(
            "title" to title,
            "ingredients" to ingredients,
            "description" to description,
            "hashtags" to hashtags,
            "userId" to userId,
            "images" to currentRecipe.images.toMutableList(),
            "timestamp" to FieldValue.serverTimestamp()
        )

        val recipeRef = firestore.collection("recipes").document(recipeId)
        val storageRef = storageReference.child("recipe_images/$recipeId")

        uploadImages(storageRef, imageUris) { imageUrls ->
            updatedRecipe["images"] = imageUrls
            recipeRef.update(updatedRecipe)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Recipe updated successfully", Toast.LENGTH_SHORT).show()
                    progressBar?.visibility = View.GONE // Hide spinner
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error updating recipe: ${e.message}", Toast.LENGTH_SHORT).show()
                    progressBar?.visibility = View.GONE // Hide spinner
                }
        }
    }

    private fun uploadImages(storageRef: StorageReference, imageUris: List<Uri>, callback: (List<String>) -> Unit) {
        val imageUrls = mutableListOf<String>()
        var uploadCount = 0

        for (uri in imageUris) {
            val imageRef = storageRef.child("${System.currentTimeMillis()}_${uri.lastPathSegment}")
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        imageUrls.add(downloadUrl.toString())
                        uploadCount++
                        if (uploadCount == imageUris.size) {
                            callback(imageUrls)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
