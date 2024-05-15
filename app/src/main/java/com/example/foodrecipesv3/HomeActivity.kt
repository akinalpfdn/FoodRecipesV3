package com.example.foodrecipesv3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerView)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // RecyclerView setup
        val recipeList = listOf(
            Recipe(R.drawable.image1, "My Recipes", "Description for My Recipes"),
            Recipe(R.drawable.image2, "Neurth Ihinige", "Cook time: 20 mins"),
            Recipe(R.drawable.image3, "My Poffielt. Mcke", "Description for My Poffielt. Mcke"),
            Recipe(R.drawable.image4, "My Preylfe", "Short description")
        )

        recipeAdapter = RecipeAdapter(recipeList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipeAdapter

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
}
