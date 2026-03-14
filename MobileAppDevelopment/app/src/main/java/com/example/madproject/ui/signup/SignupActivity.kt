package com.example.madproject.ui.signup

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.madproject.R
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private val viewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val emailInput = findViewById<EditText>(R.id.signupEmail)
        val passwordInput = findViewById<EditText>(R.id.signupPassword)
        val signupButton = findViewById<Button>(R.id.signupButton)

        signupButton.setOnClickListener {
            viewModel.signup(emailInput.text.toString(), passwordInput.text.toString())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is SignupState.Loading -> signupButton.isEnabled = false
                        is SignupState.Success -> {
                            Toast.makeText(this@SignupActivity, "Signup Successful!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is SignupState.Error -> {
                            signupButton.isEnabled = true
                            Toast.makeText(this@SignupActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                        is SignupState.Idle -> signupButton.isEnabled = true
                    }
                }
            }
        }
    }
}
