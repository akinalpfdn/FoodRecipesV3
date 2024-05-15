package com.example.foodrecipesv3

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase'i başlatma
        FirebaseApp.initializeApp(this)
    }
}