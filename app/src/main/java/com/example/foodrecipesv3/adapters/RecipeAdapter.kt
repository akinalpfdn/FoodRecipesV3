package com.example.foodrecipesv3.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.fragments.UpdateRecipeDialogFragment
import com.example.foodrecipesv3.models.Recipe
import com.google.firebase.firestore.FirebaseFirestore

class RecipeAdapter(private val recipeList: MutableList<Recipe> ) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    private lateinit var firestore: FirebaseFirestore
    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.recipeTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.recipeDescription)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.viewPager)
        var likeCount: TextView = itemView.findViewById(R.id.likeCount)
        var savedCount: TextView = itemView.findViewById(R.id.savedCount)
        val likeIcon: ImageView = itemView.findViewById(R.id.likeIcon)
        val saveIcon: ImageView = itemView.findViewById(R.id.saveIcon)
        val likeIconFilled : ImageView = itemView.findViewById(R.id.likeIconFilled)
        val  saveIconFilled : ImageView  = itemView.findViewById(R.id.saveIconFilled)
        var isSaved = false
        var isLiked = false

        fun bind(recipe: Recipe) {
            titleTextView.text = recipe.title
            descriptionTextView.text = recipe.description
            likeCount.text = recipe.likeCount.toString()
            savedCount.text = recipe.savedCount.toString()


        }

        fun handleSave(  recipe :Recipe){
             isSaved = ! isSaved
            if ( isSaved) {
                recipe.savedCount += 1
            } else {
                recipe.savedCount -= 1
            }
            savedCount.text = recipe.savedCount.toString()
            updateRecipeSaveCount(recipe )
            updateSaveState()
        }
        fun handleLike(  recipe :Recipe){
             isLiked = ! isLiked
            if ( isLiked) {
                recipe.likeCount += 1
            } else {
                recipe.likeCount -= 1
            }
            likeCount.text = recipe.likeCount.toString()
            updateRecipeLikeCount(recipe )
            updateLikeState(  )
        }

        private fun updateLikeState( ) {
            if (isLiked) {
                likeIcon.visibility = View.GONE
                likeIconFilled.visibility = View.VISIBLE
            } else {
                likeIcon.visibility = View.VISIBLE
                likeIconFilled.visibility = View.GONE
            }
        }

         fun updateSaveState() {
            if (isSaved) {
                saveIcon.visibility = View.GONE
                saveIconFilled.visibility = View.VISIBLE
            } else {
                saveIcon.visibility = View.VISIBLE
                saveIconFilled.visibility = View.GONE
            }
        }
        private fun updateRecipeLikeCount(recipe: Recipe) {
            val recipeRef = firestore.collection("recipes").document(recipe.id)
            recipeRef.update("likeCount", recipe.likeCount)
        }

        private fun updateRecipeSaveCount(recipe: Recipe) {
            val recipeRef = firestore.collection("recipes").document(recipe.id)
            recipeRef.update("savedCount", recipe.savedCount)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.titleTextView.text = recipe.title
        holder.descriptionTextView.text = recipe.description
        holder.viewPager.adapter = ImageSliderAdapter(holder.itemView.context, recipe.images)
        // Set like and save counts
        holder.likeCount.text = recipe.likeCount.toString()
        holder.savedCount.text = recipe.savedCount.toString()
        firestore = FirebaseFirestore.getInstance()
        // Set the click listener
        holder.itemView.setOnClickListener {
            val fragment = UpdateRecipeDialogFragment.newInstance(recipe.id)
             fragment.show((holder.itemView.context as FragmentActivity).supportFragmentManager, "UpdateRecipeDialogFragment")
         }
// Set click listeners for like and save icons
        holder.likeIcon.setOnClickListener {
            // Handle like action
            holder.handleLike( recipe )
        }
        holder.likeIconFilled.setOnClickListener {
            holder.handleLike( recipe )
        }
        holder.saveIcon.setOnClickListener {
            holder.handleSave( recipe )
        }
        holder.saveIconFilled.setOnClickListener {
            holder.handleSave( recipe )
        }

        // Update UI based on the saved state
        holder.updateSaveState()
    }


    override fun getItemCount(): Int = recipeList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateRecipes(newRecipes: List<Recipe>) {
        recipeList.clear()
        recipeList.addAll(newRecipes)
        notifyDataSetChanged()
    }
    fun addRecipes(newRecipes: List<Recipe>) {
        val startPosition = recipeList.size
        recipeList.addAll(newRecipes)
        notifyItemRangeInserted(startPosition, newRecipes.size)
    }


}
