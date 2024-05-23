package com.example.foodrecipesv3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.adapters.RecipeAdapter
import com.example.foodrecipesv3.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var toggleButton: ImageButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var progressBar: ProgressBar? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var isGridLayout = false
    private var lastVisible: DocumentSnapshot? = null
    private val pageSize = 20
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        toggleButton = view.findViewById(R.id.toggleButton)
        toggleButton.setOnClickListener {
            toggleLayout()
        }

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchRecipes(true)
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

        fetchRecipes(true)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + 5)) {
                    fetchRecipes(false)
                }
            }
        })
    }

    private fun fetchRecipes(initialLoad: Boolean) {
        if (isLoading) return
        isLoading = true

        progressBar?.visibility = View.VISIBLE
        val userId = auth.currentUser?.uid

        var query: Query = firestore.collection("recipes")
            //.whereNotEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        if (lastVisible != null && !initialLoad) {
            query = query.startAfter(lastVisible!!)
        }

        query.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    progressBar?.visibility = View.GONE
                    isLoading = false
                    return@addOnSuccessListener
                }

                val recipes = mutableListOf<Recipe>()
                for (document in documents) {
                    val recipe = document.toObject(Recipe::class.java)
                    if (recipe.userId != userId) { // Manually filter the current user's recipes
                        recipes.add(recipe)
                    }
                }

                // Update the last visible document
                if (documents.size() > 0) {
                    lastVisible = documents.documents[documents.size() - 1]
                }

                // Update the UI with the new recipes
                recipeAdapter.addRecipes(recipes)

                progressBar?.visibility = View.GONE
                isLoading = false
            }
            .addOnFailureListener { exception ->
                progressBar?.visibility = View.GONE
                isLoading = false
                Toast.makeText(requireContext(), "Error loading recipes: ${exception.message}", Toast.LENGTH_SHORT).show()
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
