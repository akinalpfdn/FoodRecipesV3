package com.example.foodrecipesv3.activities

import android.os.Bundle
import com.example.foodrecipesv3.R
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.foodrecipesv3.fragments.FavoritesFragment
import com.example.foodrecipesv3.fragments.HomeFragment
import com.example.foodrecipesv3.fragments.MyRecipesFragment
import com.example.foodrecipesv3.fragments.NewRecipeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_favorites -> {
                    //loadFragment(FavoritesFragment())
                   // return@setOnNavigationItemSelectedListener true
                    true
                }
                R.id.navigation_my_recipes -> {
                   // loadFragment(MyRecipesFragment())
                    //return@setOnNavigationItemSelectedListener true
                    true
                }
                R.id.navigation_new_recipe -> {
                    loadFragment(NewRecipeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        // Varsayılan olarak HomeFragment'i yükleyin
        loadFragment(HomeFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
