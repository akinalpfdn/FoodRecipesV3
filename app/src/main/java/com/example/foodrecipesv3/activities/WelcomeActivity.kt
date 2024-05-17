package com.example.foodrecipesv3.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.utils.blurBitmap

class WelcomeActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val goToLoginButton: Button = findViewById(R.id.goToLoginButton)
        val goToRegisterButton: Button = findViewById(R.id.goToRegisterButton)

        goToLoginButton.setOnClickListener {
            //val intent = Intent(this, LoginActivity::class.java)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        goToRegisterButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        // Butonları bulanıklaştırma
        //applyBlurEffectToButton(goToLoginButton)
         applyBlurEffectToButton(goToRegisterButton)

    }

    private fun applyBlurEffectToButton(button: Button) {
        button.doOnPreDraw {
            if (button.width > 0 && button.height > 0) {
            val bitmap = getBitmapFromView(button)
            val blurredBitmap = blurBitmap(this, bitmap, 10f)
            button.background = BitmapDrawable(resources, blurredBitmap)
        }
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


}