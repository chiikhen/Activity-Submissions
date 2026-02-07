package com.example.cit_238tilanoexercise1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Set up the button click listener
        findViewById<Button>(R.id.enter_button)?.setOnClickListener {
            // Get the greeting display TextView
            val greetingDisplay = findViewById<TextView>(R.id.greeting_display)

            // Get the first name from the input field
            val firstName = findViewById<TextInputEditText>(R.id.first_name)
                ?.text.toString().trim()

            // Get the last name from the input field
            val lastName = findViewById<TextInputEditText>(R.id.last_name)
                ?.text.toString().trim()


            // Check if both names are filled in
            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                // Create the full name
                val fullName = "$firstName $lastName"

                // Display the greeting message
                greetingDisplay?.text = "${getString(R.string.welcome_to_the_app)} $fullName!"
            } else {
                // Show error message if fields are empty
                Toast.makeText(
                    this,
                    getString(R.string.please_enter_a_name),
                    Toast.LENGTH_LONG
                ).apply {
                    setGravity(Gravity.CENTER, 0, 0)
                    show()
                }
            }
        }
    }
}