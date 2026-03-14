package com.example.madproject.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.madproject.ui.addword.AddWordScreen
import com.example.madproject.ui.devicestats.DeviceStatsScreen
import com.example.madproject.ui.home.HomeScreen
import com.example.madproject.ui.login.LoginScreen
import com.example.madproject.ui.quiz.QuizScreen
import com.example.madproject.ui.signup.SignupScreen
import com.example.madproject.ui.viewwords.ViewWordsScreen

/**
 * AppNavGraph defines every screen in the app and how to navigate between them.
 *
 * rememberNavController() creates the NavController — the object that manages the back stack.
 * NavHost watches it and swaps in whichever composable matches the current route.
 *
 * Navigation callbacks are passed down as lambdas so screens don't hold a reference
 * to NavController directly — this keeps screens testable and decoupled from nav logic.
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // popUpTo removes "login" from the back stack so the user
                    // can't press Back to return to the login screen after logging in
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignupClick = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            SignupScreen(
                onSignupSuccess = { navController.popBackStack() } // back to login
            )
        }

        composable("home") {
            HomeScreen(
                onAddWord = { navController.navigate("addword") },
                onViewWords = { navController.navigate("viewwords") },
                onQuiz = { navController.navigate("quiz") },
                onDeviceStats = { navController.navigate("devicestats") }
            )
        }

        composable("addword") {
            AddWordScreen(onHomeClick = { navController.popBackStack() })
        }

        composable("viewwords") {
            ViewWordsScreen(onHomeClick = { navController.popBackStack() })
        }

        composable("quiz") {
            QuizScreen(onHomeClick = { navController.popBackStack() })
        }

        composable("devicestats") {
            DeviceStatsScreen(onHomeClick = { navController.popBackStack() })
        }
    }
}
