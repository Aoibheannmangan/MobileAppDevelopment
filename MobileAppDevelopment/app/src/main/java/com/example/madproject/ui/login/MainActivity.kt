package com.example.madproject.ui.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.madproject.ui.nav.AppNavGraph

/**
 * The one and only Activity in the app.
 * setContent {} hands the entire UI over to Compose.
 * All screens live as composables inside AppNavGraph.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavGraph()
            }
        }
    }
}
