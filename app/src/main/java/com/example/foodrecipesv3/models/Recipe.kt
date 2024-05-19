package com.example.foodrecipesv3.models

data class Recipe(
    val title: String = "",
    val description: String = "",
    val images: List<String> = emptyList(), // Use List<String> to store image URLs from Firebase
    val userId: String = "",
    val hashtags: String = "",
    val ingredients: List<String> = emptyList(), // Use List<String> to store image URLs from Firebase
)
