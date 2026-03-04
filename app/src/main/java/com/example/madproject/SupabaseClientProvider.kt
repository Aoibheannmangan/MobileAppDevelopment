package com.example.madproject

import com.example.madproject.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

/**
 * This is a Singleton object which provides a single shared instance of the Supabase.
 * Using an object ensures only one connection is created throught the app's lifecycle
 */
object SupabaseClientProvider {

    /**
     * Client is configured with a project URL and an ANON_KEY
     * These are loaded from BuildConfig and the values are defined in build.gradle
     * Auth plugin installed to enable supabase's authentication features
     */

    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}