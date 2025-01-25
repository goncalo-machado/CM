package com.example.cm_project

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
import com.example.cm_project.ui.CameraScreen
import com.example.cm_project.ui.ExternalAPIScreen
import com.example.cm_project.ui.HomeScreen
import com.example.cm_project.ui.MapScreen
import com.example.cm_project.ui.PushNotificationScreen

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
        NavigationHost(navController, Modifier.padding(innerPadding))
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
        composable("Camera") { CameraScreen() }
        composable("PushNotification") { PushNotificationScreen() }
        composable("ExternalAPI") { ExternalAPIScreen() }
    }
}