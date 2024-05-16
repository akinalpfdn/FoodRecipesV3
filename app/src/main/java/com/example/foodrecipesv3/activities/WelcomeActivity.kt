package com.example.foodrecipesv3.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.foodrecipesv3.activities.MainActivity
import com.example.foodrecipesv3.R

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


    }
}