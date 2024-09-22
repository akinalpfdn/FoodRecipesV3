package com.example.foodrecipesv3.fragments

import CustomTypefaceSpan
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
    private lateinit var spinnerSearchOptions: Spinner
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
        recipeAdapter = RecipeAdapter(mutableListOf()) { recipe ->//bu callback funtion oluyor

        }
        recyclerView.adapter = recipeAdapter

        //for ordering
        orderSpinner = view.findViewById(R.id.orderSpinner)
        spinnerSearchOptions = view.findViewById(R.id.spinnerSearchOptions)
        ascendingIcon = view.findViewById(R.id.ascendingIcon)
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



        val orderOptions = resources.getStringArray(R.array.order_options)
        //sorting spinneri icin
        val adapter = object : ArrayAdapter<String>(requireContext(), R.layout.spinner_item, orderOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view2 = super.getView(position, convertView, parent) as TextView
                setIcon(view2, position)
                return view2
            }
            private fun setFontForOptionItem(textView: TextView, typeface: Typeface) {
                val spannableTitle = SpannableString(textView.text)
                spannableTitle.setSpan(CustomTypefaceSpan("", typeface), 0, spannableTitle.length, 0)
                textView.text = spannableTitle
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view2 = super.getDropDownView(position, convertView, parent) as TextView
                view2.text = orderOptions[position] // Show full text in dropdown
                val typeface: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.merienda)
                typeface?.let { nonNullTypeface ->
                    setFontForOptionItem(view2, nonNullTypeface)
                }
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

        //search spinneri icin
        val search_options = resources.getStringArray(R.array.search_options)
        val adapterSearch = object : ArrayAdapter<String>(requireContext(), R.layout.search_spinner_item, search_options) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view2 = super.getView(position, convertView, parent) as TextView
                setIcon(view2, position)
                return view2
            }
            private fun setFontForOptionItem(textView: TextView, typeface: Typeface) {
                val spannableTitle = SpannableString(textView.text)
                spannableTitle.setSpan(CustomTypefaceSpan("", typeface), 0, spannableTitle.length, 0)
                textView.text = spannableTitle
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view2 = super.getDropDownView(position, convertView, parent) as TextView
                view2.text = search_options[position] // Show full text in dropdown
                val typeface: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.merienda)
                typeface?.let { nonNullTypeface ->
                    setFontForOptionItem(view2, nonNullTypeface)
                }
                return view2
            }

            private fun setIcon(view: TextView, position: Int) {
                view.text = when (position) {
                    0 -> "Title"
                    1 -> "Hashtag"
                    2 -> "Ingredients"
                    else -> "Title"
                }
            }
        }

        adapterSearch.setDropDownViewResource(R.layout.spinner_dropdown_item) // Custom layout for dropdown item
        spinnerSearchOptions.adapter = adapterSearch
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val halfScreenWidth = screenWidth / 2
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnSearchClickListener {
            // User has clicked the search icon to expand the SearchView
            val paramsSpinner = spinnerSearchOptions.layoutParams as LinearLayout.LayoutParams
            paramsSpinner.width = 0 // Use 0dp to respect weights in LinearLayout
            spinnerSearchOptions.layoutParams = paramsSpinner
            spinnerSearchOptions.visibility = View.GONE

            val params = spinnerSearchOptions.layoutParams
            params.width = halfScreenWidth.toInt()
               // 30.dpToPx(requireContext())  // Convert 30 dp to pixel
            searchView.layoutParams = params


            try {
                val field = SearchView::class.java.getDeclaredField("mSearchSrcTextView")
                field.isAccessible = true
                val textView = field.get(searchView) as? TextView
                textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchRecipes(true, query)
                    hideKeyboard()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optionally handle live query changes
                return false
            }
        })
        searchView.setOnCloseListener {
            // User has clicked the close button to collapse the SearchView
            val params = searchView.layoutParams as LinearLayout.LayoutParams
            params.weight = 1.0f // Ensure it uses the space responsibly
            params.width = 0 // Use 0dp to respect weights in LinearLayout
            searchView.layoutParams = params
            spinnerSearchOptions.visibility = View.VISIBLE
            val paramsSpinner = spinnerSearchOptions.layoutParams as LinearLayout.LayoutParams
            paramsSpinner.width = LinearLayout.LayoutParams.WRAP_CONTENT  // or another value based on your design
            spinnerSearchOptions.layoutParams = paramsSpinner
            false
        }

    }
    fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view = activity?.currentFocus
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()
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
    fun generateSearchTerms(input: String): List<String> {
        return input.toLowerCase().split("\\s+".toRegex()) // Split by whitespace and convert to lowercase
            .map { it.filter { char -> char.isLetterOrDigit() } } // Remove non-alphanumeric characters
            .distinct() // Remove duplicates
    }
    private fun fetchRecipes(initialLoad: Boolean, queryText: String = "") {
        if (isLoading) return
        isLoading = true

        progressBar?.visibility = View.VISIBLE
        val userId = auth.currentUser?.uid

        var query: Query = firestore.collection("recipes")
            //.whereNotEqualTo("userId", userId)
            .whereEqualTo("isApproved", true)
        val searchOption = spinnerSearchOptions.selectedItem.toString()
        if(queryText!="") {
            when (searchOption) {
                "Title" -> query =
                    query.whereArrayContainsAny("titleTerms", generateSearchTerms(queryText))

                "Hashtag" -> query = query.whereArrayContains("hashtags", queryText.toLowerCase())
                "Ingredients" -> query =
                    query.whereArrayContainsAny("ingredientTerms", generateSearchTerms(queryText))
            }
        }

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

        isLoading = false
        progressBar?.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false
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
