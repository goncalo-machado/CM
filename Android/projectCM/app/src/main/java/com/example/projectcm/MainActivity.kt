package com.example.projectcm

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projectcm.database.entities.User
import com.example.projectcm.ui.auth.LoginScreen
import com.example.projectcm.ui.auth.LoginViewModel
import com.example.projectcm.ui.auth.RegisterScreen
import com.example.projectcm.ui.auth.RegisterViewModel
import com.example.projectcm.ui.mainapp.HomeScreen
import com.example.projectcm.ui.mainapp.PushNotificationScreen
import com.example.projectcm.ui.mainapp.camera.CameraScreen
import com.example.projectcm.ui.mainapp.camera.ImageViewerScreen
import com.example.projectcm.ui.mainapp.map.MapScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this)

        val sharedViewModel: SharedViewModel by viewModels()

        setContent {
            AppNavHost(
                navController = rememberNavController(),
                container = appContainer,
                sharedViewModel = sharedViewModel
            )
        }
    }
}

class SharedViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun setCurrentUser(user: User) {
        Log.d("SharedViewModel", "Setting current user: $user")
        if (_currentUser.value?.username != user.username || _currentUser.value?.role != user.role) {
            Log.d("SharedViewModel", "Updating current user: $user")
            _currentUser.value = user
        }
    }

    fun clearCurrentUser() {
        _currentUser.value = null
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController, container: AppContainer, sharedViewModel: SharedViewModel
) {
    val loginViewModel = remember { LoginViewModel(container.userRepository) }
    val registerViewModel = remember { RegisterViewModel(container.userRepository) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route

    val showBottomNav = currentDestination !in listOf("Login", "Register")
    val currentUser by sharedViewModel.currentUser.collectAsState()

    Log.d("AppNavHost", "Recomposed with user: $currentUser")

    Scaffold(bottomBar = {
        if (showBottomNav) {
            BottomNavigationBar(navController = navController, currentUser = currentUser)
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Login") {
                LoginScreen(viewModel = loginViewModel, onLoginSuccess = { user ->
                    Log.d("LoginScreen", "Enter onLoginSuccess with user: $user")
                    if (user.username != currentUser?.username) {
                        Log.d("LoginScreen", "Enter if on LoginSuccess")
                        sharedViewModel.setCurrentUser(user)
                        navController.navigate("Home") {
                            popUpTo("Login") { inclusive = true }
                        }
                    }

                }, onRegisterClick = { navController.navigate("Register") })
            }
            composable("Register") {
                RegisterScreen(viewModel = registerViewModel, onRegisterSuccess = { user ->
                    Log.d("RegisterScreen", "Enter onRegisterSuccess with user: $user")
                    if (user.username != currentUser?.username) {
                        Log.d("RegisterScreen", "Enter if on RegisterSuccess")
                        sharedViewModel.setCurrentUser(user)
                        navController.navigate("Home") {
                            popUpTo("Login") { inclusive = true }
                        }
                    }

                }, onLoginClick = { navController.navigate("Login") })
            }
            composable("home") {
                HomeScreen(navController = navController, sharedViewModel = sharedViewModel)
            }
            composable("map") { MapScreen() }
            composable("camera") { CameraScreen(navController) }
            composable("PushNotification") { PushNotificationScreen() }
            composable("image_viewer/{imageUri}/{imageName}",
                arguments = listOf(navArgument("imageUri") { type = NavType.StringType },
                    navArgument("imageName") { type = NavType.StringType })) { backStackEntry ->
                val imageUri = backStackEntry.arguments?.getString("imageUri")
                val imageName = backStackEntry.arguments?.getString("imageName")
                ImageViewerScreen(imageUri = imageUri, imageName = imageName)
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController, currentUser: User?) {
    val adminItems = listOf("Home", "Map", "Camera")
    val userItems = listOf("Home", "PushNotification", "Camera")
    val items = if (currentUser?.role == "Admin") adminItems else userItems

    Log.d("BottomNavigationBar", "Recomposed with user: $currentUser")

    NavigationBar {
        val currentRoute = navController.currentDestination?.route
        items.forEach { screen ->
            NavigationBarItem(selected = currentRoute == screen,
                onClick = { navController.navigate(screen) },
                label = { Text(screen) },
                icon = { /* Optional: Add icon here */ })
        }
    }
}
