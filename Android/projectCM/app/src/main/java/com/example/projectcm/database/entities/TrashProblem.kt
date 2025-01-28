package com.example.projectcm.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "TrashProblems")
data class TrashProblem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val imagePath: String, // Store the image path
    val status: String, // "Reported" or "Resolved"
    val adminName: String? = null, // Nullable, as it may not be set initially
    val reportedAt: LocalDateTime = LocalDateTime.now(),
    val resolvedAt: LocalDateTime? = null, // Nullable, as it will be set only when resolved
    val latitude: Double, // Latitude of the problem location
    val longitude: Double // Longitude of the problem location
)