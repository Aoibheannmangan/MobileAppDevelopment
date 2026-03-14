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
    /** used anthropic ai to figure out wiring the Supabase auth session to the Room database queries so each user only sees their own words:
    Specifically, after login you had to:
    1. Retrieve the user ID from the Supabase Auth session
    2. Pass it down through the ViewModel into the repository
    3. Filter the Room DAO query by that user ID
    4. Handle the case where the session is null or expired*/
    fun getCurrentUserId(): String? = client.auth.currentUserOrNull()?.id
}
