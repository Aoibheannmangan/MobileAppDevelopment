package com.example.madproject.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.madproject.R
import com.example.madproject.ui.home.HomeActivity
import com.example.madproject.ui.signup.SignupActivity
import kotlinx.coroutines.launch

/**
 * login screen, shows ui & forwards user actions to viewmodel
 * viewmofels() ties viewmodel to this screens lifecycle & survives rotation
 * repeatonlifecycle(start) pauses collection when app goes to background, resumes on return
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupLink = findViewById<TextView>(R.id.signupLink)

        loginButton.setOnClickListener {
            viewModel.login(emailInput.text.toString(), passwordInput.text.toString())
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // Observe state changes — Activity just reacts, it doesn't decide what to do
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is LoginState.Loading -> loginButton.isEnabled = false
                        is LoginState.Success -> {
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                        }
                        is LoginState.Error -> {
                            loginButton.isEnabled = true
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                        is LoginState.Idle -> loginButton.isEnabled = true
                    }
                }
            }
        }
    }
}
