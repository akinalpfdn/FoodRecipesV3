package com.example.foodrecipesv3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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

    //for ordering
    private lateinit var orderSpinner: Spinner
    private lateinit var ascendingIcon: ImageView
    private lateinit var descendingIcon: ImageView
    private var isAscending = false
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

        //for ordering
        orderSpinner = view.findViewById(R.id.orderSpinner)
        ascendingIcon = view.findViewById(R.id.ascendingIcon)
        descendingIcon = view.findViewById(R.id.descendingIcon)


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
        /*
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.order_options,
            R.layout.spinner_item // Use the new layout here
        )

         */

        val spinner: Spinner = view.findViewById(R.id.orderSpinner)
        val orderOptions = resources.getStringArray(R.array.order_options)

        val adapter = object : ArrayAdapter<String>(requireContext(), R.layout.spinner_item, orderOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view2 = super.getView(position, convertView, parent) as TextView
                setIcon(view2, position)
                return view2
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view2 = super.getDropDownView(position, convertView, parent) as TextView
                view2.text = orderOptions[position] // Show full text in dropdown
                return view2
            }

            private fun setIcon(view: TextView, position: Int) {
                val drawable = when (position) {
                    0 -> ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_calendar
                    ) // Your black calendar icon
                    1 -> ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_heart_outline
                    ) // Your black heart icon
                    2 -> ContextCompat.getDrawable(
                        context,
                        R.drawable.baseline_bookmark_border_24
                    ) // Your black bookmark icon
                    else -> null
                }
                view.text = when (position) {
                    0 -> ""
                    1 -> ""
                    2 -> ""
                    else -> ""
                }
                val drawableEnd =ContextCompat.getDrawable( context,R.drawable.baseline_arrow_drop_down_24)

                view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, drawableEnd, null)
            }
        }

        //spinner.adapter = adapter

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item) // Custom layout for dropdown item
        orderSpinner.adapter = adapter
        setupOrderMenu()

    }
    private fun setupOrderMenu() {
        orderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                fetchRecipes(true)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        ascendingIcon.setOnClickListener {
            isAscending = true
            ascendingIcon.visibility = View.GONE
            descendingIcon.visibility = View.VISIBLE
            fetchRecipes(true)
        }

        descendingIcon.setOnClickListener {
            isAscending = false
            ascendingIcon.visibility = View.VISIBLE
            descendingIcon.visibility = View.GONE
            fetchRecipes(true)
        }

    }
    private fun fetchRecipes(initialLoad: Boolean) {
        if (isLoading) return
        isLoading = true

        progressBar?.visibility = View.VISIBLE
        val userId = auth.currentUser?.uid

        var query: Query = firestore.collection("recipes")
            .whereNotEqualTo("userId", userId)

        when (orderSpinner.selectedItem.toString()) {
            "Date" -> query = query.orderBy("timestamp", if (isAscending) Query.Direction.ASCENDING else Query.Direction.DESCENDING)
            "Like Count" -> query = query.orderBy("likeCount", if (isAscending) Query.Direction.ASCENDING else Query.Direction.DESCENDING)
            "Save Count" -> query = query.orderBy("savedCount", if (isAscending) Query.Direction.ASCENDING else Query.Direction.DESCENDING)
        }
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
                    // Fetch liked recipes
                    val likedRecipesQuery = firestore.collection("likedRecipes")
                        .whereEqualTo("userId", userId)
                        .whereIn("recipeId", recipeIds)

                    likedRecipesQuery.get().addOnSuccessListener { likedDocs ->
                        val likedRecipeIds = likedDocs.documents.map { it.getString("recipeId") }

                        // Query to fetch saved recipes for the current user
                        val savedRecipesQuery = firestore.collection("savedRecipes")
                            .whereEqualTo("userId", userId)
                            .whereIn("recipeId", recipeIds)

                        savedRecipesQuery.get().addOnSuccessListener { savedDocs ->
                            val savedRecipeIds =
                                savedDocs.documents.map { it.getString("recipeId") }

                            for (document in documents) {
                                val recipe = document.toObject(Recipe::class.java)
                                recipe.id = document.id
                                recipe.isLiked = likedRecipeIds.contains(recipe.id)
                                recipe.isSaved = savedRecipeIds.contains(recipe.id)
                                recipes.add(recipe)
                            }

                            if (initialLoad) {
                                recipeAdapter.updateRecipes(recipes)
                            } else {
                                recipeAdapter.addRecipes(recipes)
                            }

                        }
                    }
                        isLoading = false
                        progressBar?.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            .addOnFailureListener { exception ->
                isLoading = false
                progressBar?.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
                // Handle the error
                Toast.makeText(requireContext(), "Error fetching recipes: ${exception.message}", Toast.LENGTH_SHORT).show()
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
