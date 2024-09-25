package com.example.foodrecipesv3.tasks

import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.foodrecipesv3.R
import com.google.firebase.firestore.FirebaseFirestore

class PollingWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val db = FirebaseFirestore.getInstance()
        val configDocRef = db.collection("config").document("recipeCounter")
        Log.w(TAG, "its working" )

        configDocRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val remoteRecipeCount = document.getLong("recipeCount") ?: 0

                // Retrieve the locally cached recipe count
                val sharedPreferences = applicationContext.getSharedPreferences("adminPrefs", Context.MODE_PRIVATE)
                val localRecipeCount = sharedPreferences.getLong("cachedRecipeCount", 0)

                // If the remote count is greater, a new recipe was added
                if (remoteRecipeCount > localRecipeCount) {
                    sendNotification()
                    Log.w(TAG, "notification sended successfully" )

                    // Update the cached value with the new count
                    sharedPreferences.edit().putLong("cachedRecipeCount", remoteRecipeCount).apply()
                }
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error fetching recipe count", e)
        }

        return Result.success()
    }

    private fun sendNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(applicationContext, "admin_channel")
            .setSmallIcon(R.drawable.baseline_android_24)
            .setContentTitle("New Recipe Added")
            .setContentText("A new recipe has been added and needs approval.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(1, notificationBuilder.build())
    }
}
