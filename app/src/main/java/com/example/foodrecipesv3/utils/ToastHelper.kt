package com.example.foodrecipesv3.utils


import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

object ToastUtils {

    fun showToast(fragment: Fragment?, message: String) {
        // Always fall back to activity context if fragment context isn't available
        val context = fragment?.activity?.applicationContext
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showToast(context: Context?, message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}