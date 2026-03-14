package com.example.madproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.*
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

/**
 * MainActivity is basically just the login screen.
 * It serves as the app's entry point
 * Collects email and password as inputs and authenticates with the supabase.
 * If the user is successful when they log in, they get to go to the HomeActivity!! (Home screen)
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Bind UI elements from the layout (activitymain.xml) to variables
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupLink = findViewById<TextView>(R.id.signupLink)

        //This all happens when the user click the "Login" button
        loginButton.setOnClickListener {
            val userEmail = emailInput.text.toString().trim()
            val userPassword = passwordInput.text.toString()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Please enter your email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginButton.isEnabled = false

            /**
             * Launches a coroutine tied to the Activities Lifecycle
             * Ensures its cancelled if Activity is destroyed and prevents memory leaks or crashes on background threads
             */
            lifecycleScope.launch {
                try{
                    //Sign in using Supabase Email/Password authentication
                    SupabaseClientProvider.client.auth.signInWith(Email) {
                        this.email = userEmail
                        this.password = userPassword
                    }
                    //Lets user know if successful
                    Toast.makeText(
                        this@MainActivity,
                        "Login Successful!!",
                        Toast.LENGTH_LONG
                    ).show()

                    /**
                     * Navigates a successful user to HomeActivity
                     * removes MainActivity from back stack so user cant press back to return to login
                     */
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()

                } catch (e:Exception){
                    loginButton.isEnabled = true
                    //Catches any authentication errors and kindly lets user know
                    Toast.makeText(
                        this@MainActivity,
                        "Login Failed. Check your email and password",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        //Navigate to the Signup screen (SignupActivity) when the signup link is tapped
        signupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}