package com.example.foodrecipesv3.activities

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import com.example.foodrecipesv3.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.foodrecipesv3.fragments.FavoritesFragment
import com.example.foodrecipesv3.fragments.HomeFragment
import com.example.foodrecipesv3.fragments.MyRecipesFragment
import com.example.foodrecipesv3.fragments.NewRecipeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //duplicateDocuments(25)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.getOverflowIcon()
            ?.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

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
                    loadFragment(MyRecipesFragment())
                    return@setOnNavigationItemSelectedListener true
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
// burası test amaclıdır daha sonra silinecektir
    fun duplicateDocuments(times: Int) {
        val firestore = FirebaseFirestore.getInstance()
        val collectionRef = firestore.collection("recipes")

        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val data = document.data.toMutableMap()

                    for (i in 1..times) {
                        // Ensure the duplicated document has a timestamp
                        data["timestamp"] = Timestamp.now()

                        // Modify the title to indicate it's a duplicate for testing purposes
                        data["title"] = "${data["title"]} - Copy $i"

                        // Add the duplicated document to the collection
                        collectionRef.add(data)
                            .addOnSuccessListener { newDocumentRef ->
                                println("Document duplicated with ID: ${newDocumentRef.id}")
                            }
                            .addOnFailureListener { e ->
                                println("Error duplicating document: $e")
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching documents: $e")
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.menu_light_mode -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                true
            }
            R.id.menu_dark_mode -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
