package com.example.foodrecipesv3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toggleButton: ImageButton

    private var isGridLayout = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerView)
        toggleButton = findViewById(R.id.toggleButton)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // RecyclerView setup
        val recipeList = listOf(
            Recipe( "My Recipes", "Description for My Recipes",listOf(R.drawable.image1, R.drawable.image2)),
            Recipe( "Neurth Ihinige", "Cook time: 20 mins",listOf(R.drawable.image2, R.drawable.image3)),
            Recipe( "My Poffielt. Mcke", "Description for My Poffielt. Mcke",listOf(R.drawable.image3, R.drawable.image2)),
            Recipe( "My Preylfe", "Short description",listOf(R.drawable.image4, R.drawable.image2)),
            Recipe( "My Recipes", "Description for My Recipes",listOf(R.drawable.image1, R.drawable.image2)),
            Recipe( "Neurth Ihinige", "Cook time: 20 mins",listOf(R.drawable.image2, R.drawable.image3)),
            Recipe( "My Poffielt. Mcke", "Description for My Poffielt. Mcke",listOf(R.drawable.image3, R.drawable.image2)),
            Recipe( "My Preylfe", "Short description",listOf(R.drawable.image4, R.drawable.image2)),
        )

        recipeAdapter = RecipeAdapter(recipeList)
        recyclerView.layoutManager =  GridLayoutManager(this,2)
        recyclerView.adapter = recipeAdapter

        toggleButton.setOnClickListener {
            toggleLayout()
        }

        // Bottom navigation setup
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Navigate to Home
                    true
                }
                R.id.navigation_favorites -> {
                    // Navigate to Favorites
                    true
                }
                R.id.navigation_my_recipes -> {
                    // Navigate to My Recipes
                    true
                }
                R.id.navigation_new_recipe -> {
                    // Navigate to New Recipe
                    true
                }
                else -> false
            }
        }
    }
    private fun toggleLayout() {
        isGridLayout = !isGridLayout
        if (isGridLayout) {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            toggleButton.setImageResource(R.drawable.two_column)
        } else {
            recyclerView.layoutManager = LinearLayoutManager(this)
            toggleButton.setImageResource(R.drawable.one_column)
        }
        recipeAdapter.notifyItemRangeChanged(0, recipeAdapter.itemCount)
    }
}
