package com.example.projectcm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.projectcm.ui.auth.LoginScreen
import com.example.projectcm.ui.auth.LoginViewModel
import com.example.projectcm.ui.auth.RegisterScreen
import com.example.projectcm.ui.auth.RegisterViewModel
import com.example.projectcm.ui.mainapp.ExternalAPIScreen
import com.example.projectcm.ui.mainapp.HomeScreen
import com.example.projectcm.ui.mainapp.HomeViewModel
import com.example.projectcm.ui.mainapp.PushNotificationScreen
import com.example.projectcm.ui.mainapp.camera.CameraScreen
import com.example.projectcm.ui.mainapp.camera.ImageViewerScreen
import com.example.projectcm.ui.mainapp.map.MapScreen

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this) // Pass the context
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            AppScaffold(navController = navController, container = appContainer)
        }
    }
}

@Composable
fun AppScaffold(navController: NavHostController, container: AppContainer) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route
    val shouldShowBottomBar = currentDestination !in listOf("login", "register")

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavHost(navController = navController, container = container)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("home", "map", "camera", "push_notification", "external_api")
    NavigationBar {
        val currentRoute = navController.currentDestination?.route
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen,
                onClick = { navController.navigate(screen) },
                label = { Text(screen) },
                icon = { /* Add icons if necessary */ }
            )
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, container: AppContainer) {
    val loginViewModel = remember { LoginViewModel(container.userRepository) }
    val registerViewModel = remember { RegisterViewModel(container.userRepository) }
    val homeViewModel = remember { HomeViewModel(container.userRepository) }

    NavHost(navController = navController, startDestination = "login") {
        // Login Screen
        composable("login") {
            LoginScreen(viewModel = loginViewModel, onLoginSuccess = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }, onRegisterClick = {
                navController.navigate("register")
            })
        }

        // Register Screen
        composable("register") {
            RegisterScreen(viewModel = registerViewModel, onRegisterSuccess = {
                navController.navigate("home") {
                    popUpTo("register") { inclusive = true }
                }
            }, onLoginClick = {
                navController.navigate("login")
            })
        }

        // Home with Bottom Navigation
        composable("home") { HomeScreen(viewModel = homeViewModel) }
        composable("map") { MapScreen() }
        composable("camera") { CameraScreen(navController = navController) }
        composable("push_notification") { PushNotificationScreen() }
        composable("external_api") { ExternalAPIScreen() }
        composable("image_viewer/{imageUri}/{imageName}") { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            val imageName = backStackEntry.arguments?.getString("imageName")
            ImageViewerScreen(imageUri = imageUri, imageName = imageName) // Pass the name and URI
        }
    }
}