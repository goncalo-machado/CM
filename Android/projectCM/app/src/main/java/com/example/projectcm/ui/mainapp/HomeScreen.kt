package com.example.projectcm.ui.mainapp

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.projectcm.SharedViewModel


@Composable
fun HomeScreen(navController: NavHostController, sharedViewModel: SharedViewModel) {
    val currentUser by remember { sharedViewModel.currentUser }.collectAsState()

    Log.d("HomeScreen", "Recomposed with user: $currentUser")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to the Home Screen!")
        Text(text = "Your username: ${currentUser?.username}")
        Text(text = "Your role: ${currentUser?.role}")
    }
}