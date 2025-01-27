package com.example.projectcm.ui.mainapp

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectcm.database.entities.User
import com.example.projectcm.database.repositories.UserRepository
import com.example.projectcm.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _usersResult = MutableStateFlow<Result<List<User>>>(Result.Start).apply {
        onEach { Log.d("HomeViewModel", "State updated: $it") }
    }
    val usersResult: StateFlow<Result<List<User>>> = _usersResult

    private var hasFetched = false

    fun getUsers() {
        if (hasFetched) return // Prevent repeated fetches
        hasFetched = true

        viewModelScope.launch {
            _usersResult.value = Result.Loading
            try {
                val users = userRepository.getAllUsers()
                _usersResult.value = Result.Success(users)
            } catch (e: Exception) {
                _usersResult.value = Result.Error("Failed to fetch users")
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    Log.d("HomeScreen", "Recomposing HomeScreen")
    val usersResult by viewModel.usersResult.collectAsState()

    // Fetch users when the screen is first loaded
    LaunchedEffect(Unit) {
        viewModel.getUsers()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Users List", fontSize = 24.sp, modifier = Modifier.padding(bottom = 32.dp)
            )

            when (val result = usersResult) {
                is Result.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is Result.Success -> {
                    LazyColumn {
                        items(result.data, key = { it.id }) { user ->
                            UserItem(user)
                        }
                    }
                }

                is Result.Error -> Text(result.message, color = MaterialTheme.colorScheme.error)
                Result.Start -> null
            }
        }
    }
}

@Composable
fun UserItem(user: User) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Username: ${user.username}")
        Text(text = "Password: ${user.password}")
        Text(text = "Role: ${user.role}")
    }
}