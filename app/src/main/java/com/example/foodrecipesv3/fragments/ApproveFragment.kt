package com.example.foodrecipesv3.fragments

import CustomTypefaceSpan
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.adapters.ApproveRecipeAdapter
import com.example.foodrecipesv3.adapters.RecipeAdapter
import com.example.foodrecipesv3.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ApproveFragment : Fragment() {

    private lateinit var approveRecipeAdapter: ApproveRecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var progressBar: ProgressBar? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var lastVisible: DocumentSnapshot? = null
    private val pageSize = 20
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_approve, container, false)

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
        approveRecipeAdapter = ApproveRecipeAdapter(mutableListOf(),{ recipe ->//bu callback funtion oluyor
            showDeleteConfirmationDialog(recipe)

        }, { recipe ->
            showApproveConfirmationDialog(recipe) // Your approveRecipe function to handle approval
        })
        recyclerView.adapter = approveRecipeAdapter

        //for ordering
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
    private fun fetchRecipes(initialLoad: Boolean, queryText: String = "") {
        if (isLoading) return
        isLoading = true

        progressBar?.visibility = View.VISIBLE
        val userId = auth.currentUser?.uid

        var query: Query = firestore.collection("recipes")
            //.whereNotEqualTo("userId", userId)
            .whereNotEqualTo("isApproved", true) // This ensures only recipes where isApproved is not true


        query = query.limit(pageSize.toLong())

        if (lastVisible != null && !initialLoad) {
            query = query.startAfter(lastVisible!!)
        }

        query.get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {

                    lastVisible = documents.documents[documents.size() - 1]
                    val recipes = mutableListOf<Recipe>()
                    val recipeIds = documents.documents.map { it.id }

                            for (document in documents) {
                                val recipe = document.toObject(Recipe::class.java)
                                recipe.id = document.id
                                recipes.add(recipe)
                            }

                            if (initialLoad) {
                                approveRecipeAdapter.updateRecipes(recipes)
                            } else {
                                approveRecipeAdapter.addRecipes(recipes)
                            }

                    }
                    isLoading = false
                    progressBar?.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing = false
                }
            .addOnFailureListener { exception ->
                isLoading = false
                progressBar?.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
                // Handle the error
                Toast.makeText(requireContext(), "Error fetching recipes: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        isLoading = false
        progressBar?.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false
    }
    private fun showApproveConfirmationDialog(recipe: Recipe) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Recipe")
            .setMessage("Are you sure you want to Approve this recipe?")
            .setPositiveButton("Yes") { dialog, _ ->
                approveRecipe(recipe)
                dialog.dismiss()
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun approveRecipe(recipe: Recipe) {
        progressBar?.visibility = View.VISIBLE
        firestore.collection("recipes").document(recipe.id)
            .update("isApproved", true)
            .addOnSuccessListener {
                approveRecipeAdapter.removeRecipe(recipe)  // You can remove it from the list if needed
                progressBar?.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                // Handle the error, perhaps showing a message to the user
                Toast.makeText(requireContext(), "Error approving recipe!", Toast.LENGTH_SHORT).show()
                progressBar?.visibility = View.GONE
            }
    }
    private fun deleteRecipe(recipe: Recipe) {
        progressBar?.visibility = View.VISIBLE
        firestore.collection("recipes").document(recipe.id)
            .delete()
            .addOnSuccessListener {
                approveRecipeAdapter.removeRecipe(recipe)
                progressBar?.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                // Handle the error
                progressBar?.visibility = View.GONE
            }
    }

}
