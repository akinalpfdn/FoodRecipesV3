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
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.adapters.ImageSliderAdapter
import com.example.foodrecipesv3.adapters.UpdateImageSliderAdapter
import com.example.foodrecipesv3.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class OtherRecipeDialogFragment : DialogFragment() {

    private val imageUris: MutableList<Uri> = mutableListOf()
    private val imageUrls: MutableList<String> = mutableListOf()
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recipeId: String
    private lateinit var currentRecipe: Recipe

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 100
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(recipeId: String): OtherRecipeDialogFragment {
            val fragment = OtherRecipeDialogFragment()
            val args = Bundle()
            args.putString(ARG_RECIPE_ID, recipeId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var recipeTitle: TextView
    private lateinit var recipeHashtags: TextView
    private lateinit var recipeInstructions: TextView

    private lateinit var ingredientContainer: LinearLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recipeId = arguments?.getString(ARG_RECIPE_ID) ?: throw IllegalArgumentException("Recipe ID must be passed to UpdateRecipeDialogFragment")




    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val displayMetrics = DisplayMetrics()
            val display = activity?.windowManager?.defaultDisplay
            display?.getMetrics(displayMetrics)
            val height = (displayMetrics.heightPixels * 0.7).toInt()
            val width = (displayMetrics.widthPixels * 0.9).toInt()
            dialog.window?.setLayout(width, height)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_other_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeTitle = view.findViewById(R.id.recipeTitle)
        recipeHashtags = view.findViewById(R.id.recipeHashtags)
        ingredientContainer = view.findViewById(R.id.ingredientContainer)
        recipeInstructions = view.findViewById(R.id.recipeInstructions)
        viewPager = view.findViewById(R.id.viewPager)
        indicatorLayout = view.findViewById(R.id.indicatorLayout)



        viewPager.adapter = ImageSliderAdapter(requireContext(),  imageUrls)

        loadRecipeData()
    }

    private fun loadRecipeData() {
        firestore.collection("recipes").document(recipeId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentRecipe = document.toObject(Recipe::class.java)!!
                    fetchHashtagsForRecipe(currentRecipe)
                } else {
                    Toast.makeText(requireContext(), "Recipe not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error loading recipe: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun fetchHashtagsForRecipe(recipe: Recipe) {
        firestore.collection("post_hashtags")
            .whereEqualTo("recipeId", recipeId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val hashtags = querySnapshot.documents.mapNotNull { it.getString("hashtag") }
                fillRecipeData(recipe, hashtags.joinToString(" "))
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error loading hashtags: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun fillRecipeData(recipe: Recipe, hashtags: String) {
        recipeTitle.setText(recipe.title)
        recipeHashtags.setText(hashtags)
        recipeInstructions.setText(recipe.description)

        recipe.ingredients.forEach { ingredient ->
            addNewIngredientRowWithText(ingredientContainer, ingredient)
        }

        imageUrls.addAll(recipe.images)
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

        val newEditText = TextView(requireContext()).apply {
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



        newRow.addView(newEditText)
        container.addView(newRow)
    }



}
