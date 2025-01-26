package com.example.projectcm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectcm.ui.auth.LoginScreen
import com.example.projectcm.ui.auth.LoginViewModel
import com.example.projectcm.ui.auth.RegisterScreen
import com.example.projectcm.ui.auth.RegisterViewModel
import com.example.projectcm.ui.home.HomeScreen
import com.example.projectcm.ui.home.HomeViewModel
import com.example.projectcm.ui.theme.ProjectCMTheme

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this) // Pass the context
        enableEdgeToEdge()
        setContent {
            AppNavHost(navController = rememberNavController(), container = appContainer)
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, container: AppContainer) {
    val loginViewModel = remember { LoginViewModel(container.userRepository) }
    val registerViewModel = remember { RegisterViewModel(container.userRepository) }
    val homeViewModel = remember { HomeViewModel(container.userRepository) }

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(viewModel = loginViewModel, onLoginSuccess = {
                navController.navigate("home")
            }, onRegisterClick = {
                navController.navigate("register")
            })
        }
        composable("register") {
            RegisterScreen(viewModel = registerViewModel, onRegisterSuccess = {
                navController.navigate("home")
            }, onLoginClick = {
                navController.navigate("login")
            })
        }
        composable("home") {
            HomeScreen(viewModel = homeViewModel)
        }
    }
}