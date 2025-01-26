package com.example.projectcm.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

    fun register(username: String, password: String, role: String = "User") {
        viewModelScope.launch {
            _registerResult.value = Result.Loading
            try {
                if (userRepository.isUsernameTaken(username)) {
                    _registerResult.value = Result.Error("Username already exists")
                } else {
                    val user = User(username = username, password = password, role = role)
                    userRepository.registerUser(user)
                    _registerResult.value = Result.Success(Unit)
                }
                _registerResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _registerResult.value = Result.Error("Registration failed")
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
    var selectedRole by remember { mutableStateOf("User") }
    var expanded by remember { mutableStateOf(false) }

    val roles = listOf("User", "Admin")

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
            TextField(value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            // Spacer between username and password field
            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            TextField(value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(value = selectedRole,
                    onValueChange = {},
                    label = { Text("Select Role") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = if (expanded) androidx.compose.material.icons.Icons.Filled.ArrowDropUp else androidx.compose.material.icons.Icons.Filled.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    enabled = false // Prevent manual input
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(text = { Text(role) }, onClick = {
                            selectedRole = role
                            expanded = false
                        })
                    }
                }

                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = !expanded })
            }


            // Spacer between password field and error message
            Spacer(modifier = Modifier.height(16.dp))

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