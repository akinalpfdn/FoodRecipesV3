package com.example.foodrecipesv3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Recipe(val image: Int, val title: String, val info: String)

class RecipeAdapter(private val recipeList: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val recipeInfo: TextView = itemView.findViewById(R.id.recipeInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.recipeImage.setImageResource(recipe.image)
        holder.recipeTitle.text = recipe.title
        holder.recipeInfo.text = recipe.info
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}
