package com.example.cm_project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cm_project.ui.camera.CameraScreen
import com.example.cm_project.ui.ExternalAPIScreen
import com.example.cm_project.ui.HomeScreen
import com.example.cm_project.ui.camera.ImageViewerScreen
import com.example.cm_project.ui.PushNotificationScreen
import com.example.cm_project.ui.map.MapScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("CM Project") })
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationHost(navController, Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("Home", "Map", "Camera", "PushNotification", "ExternalAPI")
    NavigationBar {
        val currentRoute = navController.currentDestination?.route
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen,
                onClick = { navController.navigate(screen) },
                label = { Text(screen) },
                icon = { /* Add an icon if needed */ }
            )
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = "Home") {
        composable("Home") { HomeScreen() }
        composable("Map") { MapScreen() }
        composable("Camera") { CameraScreen(navController = navController) }
        composable("PushNotification") { PushNotificationScreen() }
        composable("ExternalAPI") { ExternalAPIScreen() }
        composable("image_viewer/{imageUri}/{imageName}") { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            val imageName = backStackEntry.arguments?.getString("imageName")
            ImageViewerScreen(imageUri = imageUri, imageName = imageName) // Pass the name and URI
        }
    }
}