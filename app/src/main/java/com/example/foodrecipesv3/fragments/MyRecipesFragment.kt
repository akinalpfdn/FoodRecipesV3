package com.example.foodrecipesv3.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.adapters.MyRecipeAdapter
import com.example.foodrecipesv3.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MyRecipesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myRecipeAdapter: MyRecipeAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var toggleButton: ImageButton
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_recipes, container, false)
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
    private fun toggleLayout() {
        isGridLayout = !isGridLayout
        if (isGridLayout) {
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            toggleButton.setImageResource(R.drawable.two_column)
        } else {
            recyclerView.layoutManager = LinearLayoutManager(context)
            toggleButton.setImageResource(R.drawable.one_column)
        }
        myRecipeAdapter.notifyItemRangeChanged(0, myRecipeAdapter.itemCount)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = view.findViewById(R.id.recipesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        myRecipeAdapter = MyRecipeAdapter(mutableListOf()){ recipe ->//bu callback funtion oluyor
            showDeleteConfirmationDialog(recipe)
        }
        recyclerView.adapter = myRecipeAdapter
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

        // Set up the FragmentResultListener
        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val refresh = bundle.getBoolean("refresh")
            if (refresh) {
                fetchRecipes(true)
            }
        }

    }
    private fun showDeleteConfirmationDialog(recipe: Recipe) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Recipe")
            .setMessage("Are you sure you want to delete this recipe?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteRecipe(recipe)
                dialog.dismiss()
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun deleteRecipe(recipe: Recipe) {
        progressBar?.visibility = View.VISIBLE
        firestore.collection("recipes").document(recipe.id)
            .delete()
            .addOnSuccessListener {
                myRecipeAdapter.removeRecipe(recipe)
                progressBar?.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                // Handle the error
                progressBar?.visibility = View.GONE
            }
    }
    fun fetchRecipes(initialLoad: Boolean) {
        if (isLoading) return
        isLoading = true

        progressBar?.visibility = View.VISIBLE
        val userId = auth.currentUser?.uid

        var query: Query = firestore.collection("recipes")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        if (lastVisible != null && !initialLoad) {
            query = query.startAfter(lastVisible!!)
        }

        query.get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    lastVisible = documents.documents[documents.size() - 1]
                    val recipes = mutableListOf<Recipe>()
                    for (document in documents) {
                        val recipe = document.toObject(Recipe::class.java)
                        recipe.id = document.id // Set the document ID
                        recipes.add(recipe)
                    }
                    if (initialLoad) {
                        myRecipeAdapter.updateRecipes(recipes)
                    } else {
                        myRecipeAdapter.addRecipes(recipes)
                    }
                }
                isLoading = false
                progressBar?.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                // Handle the error
                isLoading = false
                progressBar?.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
    }

    private fun openUpdateRecipeFragment(recipeId: String) {
        val fragment = UpdateRecipeDialogFragment.newInstance(recipeId)
        fragment.show(parentFragmentManager, "UpdateRecipeDialogFragment")
    }
}
