package com.example.foodrecipesv3.models

import com.google.firebase.Timestamp

data class Recipe(
    val title: String = "",
    val description: String = "",
    val images: List<String> = emptyList(), // Use List<String> to store image URLs from Firebase
    val userId: String = "",
    val hashtags: String = "",
    val ingredients: List<String> = emptyList(), // Use List<String> to store image URLs from Firebase
    val timestamp: Timestamp? = null,
    val likeCount: Int = 0,
    val savedCount: Int = 0,

    var id: String = "",
) {
      constructor() : this("", "", emptyList(), "", "", emptyList(), null, 0, 0,"")

}
