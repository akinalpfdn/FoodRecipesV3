package com.example.foodrecipesv3.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodrecipesv3.R

class NewRecipeFragment : Fragment() {

    private lateinit var recipeTitle: EditText
    private lateinit var recipeHashtags: EditText
    private lateinit var addIngredientButton: Button
    private lateinit var recipeInstructions: EditText
    private lateinit var addImageButton: Button
    private lateinit var imageContainer: LinearLayout
    private var imageUris: MutableList<Uri> = mutableListOf()

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
        recipeInstructions = view.findViewById(R.id.recipeInstructions)
        addImageButton = view.findViewById(R.id.addImageButton)
        imageContainer = view.findViewById(R.id.imageContainer)

        addIngredientButton.setOnClickListener {
            addIngredientField()
        }

        addImageButton.setOnClickListener {
            selectImage()
        }
    }

    private fun addIngredientField() {
        val ingredientField = EditText(requireContext())
        ingredientField.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 8, 0, 8)
        }
        ingredientField.hint = "Add an ingredient"
        imageContainer.addView(ingredientField)
    }

    private fun selectImage() {
        if (imageUris.size >= 3) {
            Toast.makeText(requireContext(), "You can only add up to 3 images.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                imageUris.add(uri)
                displaySelectedImages()
            }
        }
    }

    private fun displaySelectedImages() {
        imageContainer.removeAllViews()
        for (uri in imageUris) {
            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(8,8,8,8)
                }
                setImageURI(uri)
            }
            imageContainer.addView(imageView)
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 100
    }
}
