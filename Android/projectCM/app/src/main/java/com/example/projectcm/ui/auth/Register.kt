package com.example.projectcm.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectcm.database.entities.User
import com.example.projectcm.database.repositories.UserRepository
import com.example.projectcm.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registerResult = MutableStateFlow<Result<Unit>>(Result.Start)
    val registerResult: StateFlow<Result<Unit>> = _registerResult

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = Result.Loading
            try {
                Log.d("InsertUser", "Inserting user: $username $password" )
                userRepository.registerUser(User(username = username, password = password))
                Log.d("InsertUser", "Success Inserting user: $username $password")
                _registerResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _registerResult.value = Result.Error("Registration failed")
                Log.e("DatabaseError", "Error inserting user: ${e.message}")
            }
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel, onRegisterSuccess: () -> Unit, onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val registerResult by viewModel.registerResult.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Padding for content to not touch the edges
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally, // Center the contents horizontally
            verticalArrangement = Arrangement.spacedBy(16.dp) // Space between the items
        ) {
            // Title
            Text(
                text = "Register",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 32.dp) // Padding between title and form
            )

            // Spacer between title and username field
            Spacer(modifier = Modifier.height(16.dp))

            // Username field
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            // Spacer between username and password field
            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // Spacer between password field and error message
            Spacer(modifier = Modifier.height(8.dp))

            // Show error message if any
            when (val result = registerResult) {
                is Result.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) // Center the loader
                is Result.Success -> onRegisterSuccess()
                is Result.Error -> Text(result.message, color = MaterialTheme.colorScheme.error)
                Result.Start -> null
            }

            // Spacer between error message and register button
            Spacer(modifier = Modifier.height(16.dp))

            // Register button
            Button(
                onClick = { viewModel.register(username, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }

            // Spacer between register button and login button
            Spacer(modifier = Modifier.height(16.dp))

            // Login button to navigate to the login screen
            Button(
                onClick = { onLoginClick() }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Already have an account? Login")
            }
        }
    }
}
