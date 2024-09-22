package com.example.foodrecipesv3.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.fragments.OtherRecipeDialogFragment
import com.example.foodrecipesv3.fragments.UpdateRecipeDialogFragment
import com.example.foodrecipesv3.models.Recipe

class ApproveRecipeAdapter(private val recipeList: MutableList<Recipe>, private val onDeleteClick: (Recipe) -> Unit
                           , private val onApproveClick: (Recipe) -> Unit) :
    RecyclerView.Adapter<ApproveRecipeAdapter.RecipeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_approve_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.titleTextView.text = recipe.title
        holder.descriptionTextView.text = recipe.description
        holder.viewPager.adapter = ImageSliderAdapter(
            holder.itemView.context,
            recipe.images,
            recipe.id,
            true,false
        )
        // Set the click listener
        holder.itemView.setOnClickListener {
            val fragment = OtherRecipeDialogFragment.newInstance(recipe.id)
            fragment.show((holder.itemView.context as FragmentActivity).supportFragmentManager, "OtherRecipeDialogFragment")
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(recipe)
        }
        holder.approveButton.setOnClickListener {
            onApproveClick(recipe)
        }
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
    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.recipeTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.recipeDescription)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.viewPager)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        val approveButton: ImageButton = itemView.findViewById(R.id.approveButton)
    }
}
