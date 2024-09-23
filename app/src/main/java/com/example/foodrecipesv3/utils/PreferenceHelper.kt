package com.example.foodrecipesv3.utils

import android.content.Context

object PreferenceHelper {
    private const val PREFS_NAME = "app_prefs"
    private const val LAYOUT_PREF_KEY = "is_grid_layout"

    fun saveLayoutPreference(context: Context, isGridLayout: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(LAYOUT_PREF_KEY, isGridLayout).apply()
    }

    fun loadLayoutPreference(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(LAYOUT_PREF_KEY, false) // Default is false (list view)
    }
}