package com.example.foodrecipesv3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.models.Recipe

class RecipeAdapter(private var recipeList: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.titleTextView.text = recipe.title
        holder.descriptionTextView.text = recipe.description
        holder.viewPager.adapter = ImageSliderAdapter(holder.itemView.context, recipe.images)
    }

    override fun getItemCount(): Int = recipeList.size

    fun updateRecipes(newRecipes: List<Recipe>) {
        recipeList = newRecipes
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.recipeTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.recipeDescription)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.viewPager)
    }
}
