package com.example.cit234_activityfinal

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        val mainView = findViewById<android.view.View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val firstName = findViewById<TextInputEditText>(R.id.first_name)
        val lastName = findViewById<TextInputEditText>(R.id.last_name)
        val enterButton = findViewById<MaterialButton>(R.id.enter_button)
        val greetingDisplay = findViewById<TextView>(R.id.greeting_display)

        enterButton.setOnClickListener {
            val fName = firstName.text.toString().trim()
            val lName = lastName.text.toString().trim()

            if (fName.isNotEmpty() && lName.isNotEmpty()) {
                val welcomeMessage = getString(R.string.welcome_to_the_app)
                greetingDisplay.text = "$welcomeMessage $fName $lName"
            } else {
                greetingDisplay.text = getString(R.string.please_enter_a_name)
            }
        }
    }
}