package com.example.foodrecipesv3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.models.Recipe
import com.example.foodrecipesv3.adapters.RecipeAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var toggleButton: ImageButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private   var progressBar: ProgressBar? = null

    private var isGridLayout = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        toggleButton = view.findViewById(R.id.toggleButton)
        toggleButton.setOnClickListener {
            toggleLayout()
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipeAdapter = RecipeAdapter(mutableListOf())
        recyclerView.adapter = recipeAdapter
        progressBar = activity?.findViewById(R.id.progressBar)
        progressBar?.visibility = View.VISIBLE // Spinner'ı göster
        fetchFlowRecipes()
        progressBar?.visibility = View.GONE // Spinner'ı gizle
    }
    private fun fetchFlowRecipes() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("recipes")
                .whereNotEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val recipes = mutableListOf<Recipe>()
                    for (document in documents) {
                        val recipe = document.toObject(Recipe::class.java)
                        recipes.add(recipe)
                    }
                    recipeAdapter.updateRecipes(recipes)
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
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
