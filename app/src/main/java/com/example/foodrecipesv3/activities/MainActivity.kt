package com.example.foodrecipesv3.activities

import CustomTypefaceSpan
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.foodrecipesv3.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.foodrecipesv3.fragments.FavoritesFragment
import com.example.foodrecipesv3.fragments.HomeFragment
import com.example.foodrecipesv3.fragments.MyRecipesFragment
import com.example.foodrecipesv3.fragments.NewRecipeFragment
import com.example.foodrecipesv3.models.Recipe
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    var likeCount = 0
    var saveCount = 0
    var postCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //duplicateDocuments(25)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.getOverflowIcon()
            ?.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

        val title: TextView = findViewById(R.id.levelTitle)
        title.setOnClickListener {
            showTooltip(title)
        }
        val toolBarProgressBar:ProgressBar = findViewById(R.id.toolBarProgressBar )
        toolBarProgressBar.setOnClickListener {
            showTooltip(title)//bu daha iyi hizalamak içi, barı verince hizalanmiyordu
        }
        // Örnek olarak progress bar'ı %50 yapalım
        toolBarProgressBar.progress = 70
        getUserScore(title,toolBarProgressBar)
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
        val menu = navView.menu
        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.merienda)
        typeface?.let { nonNullTypeface ->
            for (i in 0 until menu.size()) {
                val menuItem = menu.getItem(i)
                setFontForMenuItem(menuItem, nonNullTypeface)
            }
        }
       // firestore = FirebaseFirestore.getInstance()

      //  updateRecipesWithRandomCounts()
        // Varsayılan olarak HomeFragment'i yükleyin
        loadFragment(HomeFragment())
    }
    private fun setFontForMenuItem(menuItem: MenuItem, typeface: Typeface) {
        val spannableTitle = SpannableString(menuItem.title)
        spannableTitle.setSpan(CustomTypefaceSpan("", typeface), 0, spannableTitle.length, 0)
        menuItem.title = spannableTitle
    }
    private fun getUserScore(title: TextView, toolBarProgressBar: ProgressBar) {
        fetchScore() {
            var likeScore = likeCount * 1
            var saveScore = saveCount * 3
            var postScore = postCount * 10
            var totalScore = likeScore + saveScore + postScore
            if (totalScore > 800) {
                toolBarProgressBar.progress = 100
            } else {
                toolBarProgressBar.progress = totalScore % 100
            }

            val scoreTextView: TextView = findViewById(R.id.scoreTextView)
            scoreTextView.text = totalScore.toString()
            title.text = when (totalScore / 100) {
                0 -> "Bulaşıkçı"
                1 -> "Garson"
                2 -> "Aşçı Çırağı"
                3 -> "Aşçı"
                4 -> "Şef"
                5 -> "Master Şef"
                6 -> "1 Yıldız Şef"
                7 -> "2 Yıldız Şef"
                8 -> "3 Yıldız Şef"
                else -> "3 Yıldız Şef"
            }

            val drawable = when (totalScore / 100) {
                0 -> ContextCompat.getDrawable(this, R.drawable.bulasikci)
                1 -> ContextCompat.getDrawable(this, R.drawable.garson)
                2 -> ContextCompat.getDrawable(this, R.drawable.cirak)
                3 -> ContextCompat.getDrawable(this, R.drawable.asci)
                4 -> ContextCompat.getDrawable(this, R.drawable.sef)
                5 -> ContextCompat.getDrawable(this, R.drawable.master)
                6 -> ContextCompat.getDrawable(this, R.drawable.star)
                7 -> ContextCompat.getDrawable(this, R.drawable.start2)
                8 -> ContextCompat.getDrawable(this, R.drawable.star3)
                else -> ContextCompat.getDrawable(this, R.drawable.star3)
            }
            drawable?.let {
                val scaledDrawable = when (totalScore / 100) {
                    0 -> scaleDrawable(it, 35, 35) // Adjust width and height as needed
                    1 -> scaleDrawable(it, 35, 35) // Adjust width and height as needed
                    2 -> scaleDrawable(it, 35, 35) // Adjust width and height as needed
                    3 -> scaleDrawable(it, 35, 35) // Adjust width and height as needed
                    4 -> scaleDrawable(it, 35, 35) // Adjust width and height as needed
                    5 -> scaleDrawable(it, 35, 35) // Adjust width and height as needed
                    6 -> scaleDrawable(it, 20, 20) // Adjust width and height as needed
                    7 -> scaleDrawable(it, 35, 20) // Adjust width and height as needed
                    8 -> scaleDrawable(it, 30, 30) // Adjust width and height as needed
                    else -> scaleDrawable(it, 30, 30) // Adjust width and height as needed
                }
                title.setCompoundDrawablesWithIntrinsicBounds(scaledDrawable, null, null, null)
            }
        }
    }
    private fun scaleDrawable(drawable: Drawable, width: Int, height: Int): Drawable {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        return BitmapDrawable(resources, scaledBitmap)
    }
    fun fetchScore(callback: () -> Unit ) {

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val query: Query = firestore.collection("recipes")
            .whereEqualTo("userId", userId)

        query.get()
            .addOnSuccessListener { documents ->
                Log.d("FetchScore", "Query successful: ${documents.size()} documents retrieved")
                if (documents.size() > 0) {
                    for (document in documents) {
                        val recipe = document.toObject(Recipe::class.java)
                        likeCount += recipe.likeCount
                        saveCount += recipe.savedCount
                        postCount++
                    }

                    callback()
                    // Log the counts for verification
                    Log.d("RecipeStats", "Like Count: $likeCount, Save Count: $saveCount, Post Count: $postCount")
                } else {
                    Log.d("RecipeStats", "No recipes found for user: $userId")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error loading recipes: ${e.message}")
                Toast.makeText(this, "Error loading recipe for score: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun showTooltip(anchorView: View) {
        val inflater = LayoutInflater.from(this)
        val tooltipView = inflater.inflate(R.layout.tooltip_layout, null)

        val tooltipPopup = PopupWindow(tooltipView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tooltipPopup.isOutsideTouchable = true
        tooltipPopup.isFocusable = true

        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val xOffset = location[0] - (tooltipView.measuredWidth - anchorView.width) / 2
        val yOffset = location[1] - tooltipView.measuredHeight - 20

        tooltipPopup.showAtLocation(anchorView, Gravity.NO_GRAVITY, xOffset, yOffset)
    }

    /*
    private fun updateRecipesWithRandomCounts() {
        val recipesRef = firestore.collection("recipes")

        recipesRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val likeCount = Random.nextInt(0, 1001)
                    val savedCount = Random.nextInt(0, 1001)

                    val recipeRef = recipesRef.document(document.id)
                    recipeRef.update(mapOf(
                        "likeCount" to likeCount,
                        "savedCount" to savedCount
                    ))
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                exception.printStackTrace()
            }
    }
    */
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
