package com.example.projectcm.ui.mainapp.problem_page

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.example.projectcm.database.entities.TrashProblem
import com.example.projectcm.database.entities.User
import com.example.projectcm.database.repositories.TrashProblemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TrashProblemViewModel(private val repository: TrashProblemRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null) // Set from the shared view model
    val currentUser: StateFlow<User?> = _currentUser

    private val _sortByStatus = MutableStateFlow(true) // Default sorting is by status
    val sortByStatus: StateFlow<Boolean> = _sortByStatus

    val trashProblems = combine(
        repository.getAllTrashProblems(),
        _sortByStatus,
        _currentUser
    ) { problems, sortByStatus, user ->
        val filteredProblems = if (user?.role == "User") {
            problems.filter { it.userId == user.id }
        } else {
            problems // Admin sees all problems
        }
        filteredProblems.sortedWith(compareBy({ if (sortByStatus) it.status else null },
            { it.reportedAt }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleSortByStatus() {
        _sortByStatus.value = !_sortByStatus.value
    }

    fun resolveProblem(problem: TrashProblem) {
        viewModelScope.launch {
            try {
                repository.updateTrashProblem(
                    problem.copy(
                        status = "Resolved",
                        resolvedAt = LocalDateTime.now()
                    )
                )
            } catch (e: Exception) {
                Log.e("TrashProblemViewModel", "Error resolving problem", e)
            }
        }

    }

}

@Composable
fun ProblemsPage(viewModel: TrashProblemViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val problems by viewModel.trashProblems.collectAsState(emptyList())
    val sortByStatus by viewModel.sortByStatus.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // Page Title
        Text(
            text = if (currentUser?.role == "User") "My Problems" else "All Problems",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Sorting Button
        Button(
            onClick = { viewModel.toggleSortByStatus() },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = if (sortByStatus) "Sort by Date" else "Sort by Status")
        }

        // Problems List
        LazyColumn {
            items(items = problems, key = { it.id }) { problem ->
                ProblemCard(problem = problem, isAdmin = currentUser?.role == "Admin", onResolve = {
                    viewModel.resolveProblem(problem)
                })
            }
        }
    }
}

@Composable
fun ProblemCard(problem: TrashProblem, isAdmin: Boolean, onResolve: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Problem Details
            Text(
                "Reported by User ID: ${problem.userId}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text("Status: ${problem.status}", style = MaterialTheme.typography.bodyMedium)
            Text("Reported At: ${problem.reportedAt}", style = MaterialTheme.typography.bodyMedium)
            problem.resolvedAt?.let {
                Text("Resolved At: $it", style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                "Coordinates: (${problem.latitude}, ${problem.longitude})",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Admin: ${problem.adminName ?: "None"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display Image
            if (problem.imagePath.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = problem.imagePath),
                    contentDescription = "Problem Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            // Resolve Button (for Admins only)
            if (isAdmin && problem.status == "Reported") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onResolve) {
                    Text("Resolve")
                }
            }
        }
    }
}

