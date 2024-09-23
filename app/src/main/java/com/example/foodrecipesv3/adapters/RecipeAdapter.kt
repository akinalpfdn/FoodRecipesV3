package com.example.foodrecipesv3.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.fragments.OtherRecipeDialogFragment
import com.example.foodrecipesv3.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RecipeAdapter(private val recipeList: MutableList<Recipe>, private val onUnsaveClick: (Recipe) -> Unit)  :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    private lateinit var firestore: FirebaseFirestore//SQl connection gibi dusunebiliriz
    private lateinit var auth: FirebaseAuth//bu da auth bilgileri icin
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




        fun handleSave(  recipe :Recipe){
            recipe.isSaved = ! recipe.isSaved
            if ( recipe.isSaved) {
                recipe.savedCount += 1
            } else {
                recipe.savedCount -= 1
            }
            savedCount.text = recipe.savedCount.toString()
            updateRecipeSaveCount(recipe )
            updateSaveState(recipe)
        }
        fun handleLike(  recipe :Recipe){
            recipe.isLiked = ! recipe.isLiked
            if ( recipe.isLiked) {
                recipe.likeCount += 1
            } else {
                recipe.likeCount -= 1
            }
            likeCount.text = recipe.likeCount.toString()
            updateRecipeLikeCount(recipe )
            updateLikeState( recipe )
        }

        fun updateLikeState( recipe: Recipe) {
            if (recipe.isLiked) {
                likeIcon.visibility = View.GONE
                likeIconFilled.visibility = View.VISIBLE
            } else {
                likeIcon.visibility = View.VISIBLE
                likeIconFilled.visibility = View.GONE
            }
        }

         fun updateSaveState(recipe: Recipe) {
            if (recipe.isSaved) {
                saveIcon.visibility = View.GONE
                saveIconFilled.visibility = View.VISIBLE
            } else {
                saveIcon.visibility = View.VISIBLE
                saveIconFilled.visibility = View.GONE
            }
        }
        private fun updateRecipeLikeCount(recipe: Recipe ) {
            val recipeRef = firestore.collection("recipes").document(recipe.id)
            recipeRef.update("likeCount", recipe.likeCount)

            val userId = auth.currentUser?.uid
            if(recipe.isLiked)
            {
                val likedRecipeRef = firestore.collection("likedRecipes").document()
                likedRecipeRef.set(mapOf("userId" to userId, "recipeId" to recipe.id))
            }
            else
            {
                val likedRecipeRef = firestore.collection("likedRecipes")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("recipeId", recipe.id)

                likedRecipeRef.get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        firestore.collection("likedRecipes").document(document.id).delete()
                    }
                }
            }
        }

        private fun updateRecipeSaveCount(recipe: Recipe ) {
            val recipeRef = firestore.collection("recipes").document(recipe.id)
            recipeRef.update("savedCount", recipe.savedCount)

            val userId = auth.currentUser?.uid
            if(recipe.isSaved)
            {
                val savedRecipeRef = firestore.collection("savedRecipes").document()
                savedRecipeRef.set(mapOf("userId" to userId, "recipeId" to recipe.id))
            }
            else
            {
                val savedRecipeRef = firestore.collection("savedRecipes")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("recipeId", recipe.id)

                savedRecipeRef.get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        firestore.collection("savedRecipes").document(document.id).delete()
                    }
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.titleTextView.text = recipe.title
        holder.descriptionTextView.text = recipe.description
        holder.viewPager.adapter = ImageSliderAdapter(holder.itemView.context, recipe.images,recipe.id,true,true)
        // Set like and save counts
        holder.likeCount.text = recipe.likeCount.toString()
        holder.savedCount.text = recipe.savedCount.toString()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        // Set the click listener
        holder.itemView.setOnClickListener {
            val fragment = OtherRecipeDialogFragment.newInstance(recipe.id)
             fragment.show((holder.itemView.context as FragmentActivity).supportFragmentManager, "OtherRecipeDialogFragment")
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
            onUnsaveClick(recipe)
        }

        // Update UI based on the saved state
        holder.updateSaveState(recipe)
        // Update UI based on the saved state
        holder.updateLikeState(recipe)
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

    fun removeRecipe(recipe: Recipe) {

        val position = recipeList.indexOf(recipe)
        if (position != -1) {
            recipeList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}
