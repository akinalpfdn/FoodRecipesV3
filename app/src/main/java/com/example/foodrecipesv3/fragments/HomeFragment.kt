package com.example.foodrecipesv3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.models.Recipe
import com.example.foodrecipesv3.adapters.RecipeAdapter

class HomeFragment : Fragment() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var toggleButton: ImageButton

    private var isGridLayout = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        toggleButton = view.findViewById(R.id.toggleButton)

        // RecyclerView setup
        val recipeList = listOf(
            Recipe("My Recipes", "Description for My Recipes", listOf(R.drawable.image1.toString(), R.drawable.image2.toString())),
            Recipe("Neurth Ihinige", "Cook time: 20 mins", listOf(R.drawable.image2.toString(), R.drawable.image3.toString())),
            Recipe("My Poffielt. Mcke", "Description for My Poffielt. Mcke", listOf(R.drawable.image3.toString(), R.drawable.image2.toString())),
            Recipe("My Preylfe", "Short description", listOf(R.drawable.image4.toString(), R.drawable.image2.toString())),
            Recipe("My Recipes", "Description for My Recipes", listOf(R.drawable.image1.toString(), R.drawable.image2.toString())),
        )

        recipeAdapter = RecipeAdapter(recipeList)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = recipeAdapter

        toggleButton.setOnClickListener {
            toggleLayout()
        }

        return view
    }

    private fun toggleLayout() {
        isGridLayout = !isGridLayout
        if (isGridLayout) {
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            toggleButton.setImageResource(R.drawable.two_column)
        } else {
            recyclerView.layoutManager = LinearLayoutManager(context)
            toggleButton.setImageResource(R.drawable.one_column)
        }
        recipeAdapter.notifyItemRangeChanged(0, recipeAdapter.itemCount)
    }
}
