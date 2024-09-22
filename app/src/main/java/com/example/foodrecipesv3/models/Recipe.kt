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
    var likeCount: Int = 0,
    var savedCount: Int = 0,
    var id: String = "",
    var isLiked: Boolean,
    var isSaved: Boolean,
    var isApproved: Boolean = false,
    var titleTerms:List<String> = emptyList(),
    var ingredientTerms:List<String> = emptyList(),

    ) {

    constructor() : this("", "", emptyList(), "", "", emptyList(), null, 0, 0,"",false,false)

}
