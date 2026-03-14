package com.example.madproject.data.repository

import com.example.madproject.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

/**
 * all auth logic. viewmodels call THIS, never sb directly
 * suspend funcs: pause, wait for sb respond, resume
 * no callbacks needed
 */
class AuthRepository {

    private val client = SupabaseClientProvider.client

    suspend fun login(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signup(email: String, password: String) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    /** returns current uses id, null if not logged in */
    fun getCurrentUserId(): String? = client.auth.currentUserOrNull()?.id
}
