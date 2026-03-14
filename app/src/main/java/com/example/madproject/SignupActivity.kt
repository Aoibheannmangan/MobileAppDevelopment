package com.example.madproject

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

/**
 * SignupActivity handles when users signup
 * It gets an email and a password from input and sends them to supabase
 * The login screen (MainActivity) is returned if successful
 */
class SignupActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //Binds UI elements from activity_signup.xml
        val emailInput = findViewById<EditText>(R.id.signupEmail)
        val passwordInput = findViewById<EditText>(R.id.signupPassword)
        val confirmPasswordInput = findViewById<EditText>(R.id.signupPasswordConfirm)
        val signupButton = findViewById<Button>(R.id.signupButton)

        //This happens when the user clicks the signup button
        signupButton.setOnClickListener {
            val userEmail = emailInput.text.toString().trim()
            val userPassword = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (userEmail.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userPassword.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signupButton.isEnabled = false;

            /**
             * Launches a coroutine tied to the Activities Lifecycle
             * Ensures its cancelled if Activity is destroyed and prevents memory leaks or crashes on background threads
             */
            lifecycleScope.launch {
                try {
                    //Attempts to make a new account via Supabase Email auth stuff
                    SupabaseClientProvider.client.auth.signUpWith(Email){
                        this.email = userEmail
                        this.password = userPassword
                    }

                    //Notify user of success
                    Toast.makeText(
                        this@SignupActivity,
                        "Signup Successful!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    //Close the signup screen and return to login
                    finish()
                } catch (e: Exception) {
                    //Log the full error to Logcat for debugging
                    android.util.Log.e("SignupError", "Signup failed", e)
                    signupButton.isEnabled = true
                    //Error message for whens signup fails (cause it keeps doing that)
                    Toast.makeText(
                        this@SignupActivity,
                        "Signup Failed. Please try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}