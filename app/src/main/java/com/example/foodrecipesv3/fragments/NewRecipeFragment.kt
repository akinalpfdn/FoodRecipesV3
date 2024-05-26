package com.example.foodrecipesv3.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.adapters.AddedImageSliderAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class NewRecipeFragment : Fragment() {

    private val imageUris: MutableList<Uri> = mutableListOf()
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 100
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
    private lateinit var saveRecipeButton: ImageButton
    private   var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeTitle = view.findViewById(R.id.recipeTitle)
        recipeHashtags = view.findViewById(R.id.recipeHashtags)
        addIngredientButton = view.findViewById(R.id.addIngredientButton)
        ingredientContainer= view.findViewById(R.id.ingredientContainer)
        charCountText = view.findViewById(R.id.charCountText)
        recipeInstructions = view.findViewById(R.id.recipeInstructions)
        addImageButton = view.findViewById(R.id.addImageButton)
        viewPager = view.findViewById(R.id.viewPager)
        indicatorLayout = view.findViewById(R.id.indicatorLayout)
        saveRecipeButton= view.findViewById(R.id.saveRecipeButton)
        progressBar = activity?.findViewById(R.id.progressBar)




        recipeTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                charCountText.text = "$length/50"
            }

            override fun afterTextChanged(s: Editable?) {}
        })



        addImageButton.setOnClickListener {
            selectImage()
        }
        addIngredientButton.setOnClickListener {
            addNewIngredientRow(ingredientContainer)
        }

        // Adapter'ı Ayarlama
        viewPager.adapter = AddedImageSliderAdapter(requireContext(), imageUris) { position ->
            imageUris.removeAt(position)
            viewPager.adapter?.notifyItemRemoved(position)
            viewPager.adapter?.notifyItemRangeChanged(position, imageUris.size)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

        saveRecipeButton.setOnClickListener {
            showConfirmationDialog()
        }
    }
    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to save this recipe?")
            .setPositiveButton("Yes") { dialog, which ->
                saveRecipe(recipeTitle.text.toString(),recipeHashtags.text.toString(),recipeInstructions.text.toString(), ingredientContainer, imageUris)
            }
            .setNegativeButton("No", null)
            .show()
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
        // Request focus on the new EditText and show the keyboard
        newEditText.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(newEditText, InputMethodManager.SHOW_IMPLICIT)
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
                        if (imageUris.size <3) {
                            imageUris.add(uri)
                        } else {
                            Toast.makeText(requireContext(), "You can only add up to 3 images.", Toast.LENGTH_SHORT).show()
                            break
                        }
                    }
                } else {
                    intentData.data?.let { uri ->
                        if (imageUris.size <3) {
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




    private fun saveRecipe(title: String, hashtags: String,description: String,ingredientContainer: LinearLayout, imageUris: List<Uri>) {
        progressBar?.visibility = View.VISIBLE // Spinner'ı göster
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            progressBar?.visibility = View.GONE // Spinner'ı gizle
            return
        }

        val ingredients = mutableListOf<String>()
        for (i in 0 until ingredientContainer.childCount) {
            val row = ingredientContainer.getChildAt(i) as LinearLayout
            val editText = row.getChildAt(0) as EditText
            ingredients.add(editText.text.toString())
        }
        val firestore = FirebaseFirestore.getInstance()

        val hashtagList = hashtags.split(" ").filter { it.isNotBlank() }

        val recipe = hashMapOf(
            "title" to title,
            "ingredients" to ingredients,
            "description" to description,
            "hashtags" to hashtags,
            "userId" to userId,
            "images" to mutableListOf<String>(),
            "timestamp" to FieldValue.serverTimestamp() // Adding the timestamp field
        )

        val recipeRef = firestore.collection("recipes").document()
        val storageRef = storageReference.child("recipe_images/${recipeRef.id}")

        uploadImages(storageRef, imageUris) { imageUrls ->
            recipe["images"] = imageUrls
            recipeRef.set(recipe)
                .addOnSuccessListener {

                    saveHashtagsAndAssociations(recipeRef.id, hashtagList)
                    Toast.makeText(requireContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show()
                    progressBar?.visibility = View.GONE // Spinner'ı gizle
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error saving recipe: ${e.message}", Toast.LENGTH_SHORT).show()
                    progressBar?.visibility = View.GONE // Spinner'ı gizle
                }
        }
    }
    private fun saveHashtagsAndAssociations(recipeId: String, hashtags: List<String>) {
        val firestore = FirebaseFirestore.getInstance()

        for (hashtag in hashtags) {
            if (!hashtag.startsWith("#") || !hashtag.matches(Regex("^#[A-Za-z0-9_]+$")))
            {
                continue
            }
            val hashtagRef = firestore.collection("hashtags").document(hashtag)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(hashtagRef)
                if (snapshot.exists()) {
                    // If the hashtag exists, increment the post count
                    val newPostCount = snapshot.getLong("postCount")!! + 1
                    transaction.update(hashtagRef, "postCount", newPostCount)
                } else {
                    // If the hashtag does not exist, create a new one with post count 1
                    transaction.set(hashtagRef, hashMapOf("name" to hashtag, "postCount" to 1))
                }

                // Create or update the post_hashtags collection
                val postHashtagRef = firestore.collection("post_hashtags").document("${hashtag}_${recipeId}")
                transaction.set(postHashtagRef, hashMapOf("recipeId" to recipeId, "hashtag" to hashtag))
            }.addOnSuccessListener {
                // Handle success if needed
            }.addOnFailureListener { e ->
                // Handle failure if needed
                Toast.makeText(requireContext(), "Error updating hashtag: ${e.message}", Toast.LENGTH_SHORT).show()
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