package com.example.foodrecipesv3.fragments

import AddedImageSliderAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.foodrecipesv3.R

class NewRecipeFragment : Fragment() {

    private val imageUris: MutableList<Uri> = mutableListOf()

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 100
    }

    private lateinit var recipeTitle: EditText
    private lateinit var recipeHashtags: EditText
    private lateinit var addIngredientButton: ImageButton
    private lateinit var recipeInstructions: EditText
    private lateinit var addImageButton: Button
    private lateinit var imageContainer: LinearLayout

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
        val ingredientContainer: LinearLayout = view.findViewById(R.id.ingredientContainer)
        val charCountText: TextView = view.findViewById(R.id.charCountText)
        recipeInstructions = view.findViewById(R.id.recipeInstructions)
        addImageButton = view.findViewById(R.id.addImageButton)
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val indicatorLayout: LinearLayout = view.findViewById(R.id.indicatorLayout)




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

        viewPager.adapter = AddedImageSliderAdapter(requireContext(), imageUris)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
            }
        })
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
                        if (imageUris.size >3) {
                            imageUris.add(uri)
                        } else {
                            Toast.makeText(requireContext(), "You can only add up to 3 images.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                displaySelectedImages()
            }
        }
    }

    private fun displaySelectedImages() {
        val viewPager: ViewPager2 = view?.findViewById(R.id.viewPager) ?: return
        viewPager.adapter?.notifyDataSetChanged()
        setupIndicators(imageUris.size)
    }

    private fun setupIndicators(count: Int) {
        val indicatorLayout: LinearLayout = view?.findViewById(R.id.indicatorLayout) ?: return
        indicatorLayout.removeAllViews()
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext()).apply {
                setImageResource(R.drawable.indicator_inactive_dot)
                this.layoutParams = layoutParams
            }
            indicatorLayout.addView(indicators[i])
        }
        if (indicators.isNotEmpty()) {
            indicators[0]?.setImageResource(R.drawable.indicator_active_dot)
        }
    }

    private fun updateIndicators(position: Int) {
        val indicatorLayout: LinearLayout = view?.findViewById(R.id.indicatorLayout) ?: return
        for (i in 0 until indicatorLayout.childCount) {
            val imageView = indicatorLayout.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageResource(R.drawable.indicator_active_dot)
            } else {
                imageView.setImageResource(R.drawable.indicator_inactive_dot)
            }
        }
    }
}