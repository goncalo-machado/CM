package com.example.projectcm.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.projectcm.database.entities.TrashProblem
import kotlinx.coroutines.flow.Flow

@Dao
interface TrashProblemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrashProblem(problem: TrashProblem)

    @Update
    suspend fun updateTrashProblem(problem: TrashProblem)

    @Delete
    suspend fun deleteTrashProblem(problem: TrashProblem)

    @Query("SELECT * FROM TrashProblems WHERE id = :id")
    suspend fun getTrashProblemById(id: Int): TrashProblem?

    @Query("SELECT * FROM TrashProblems WHERE status = :status")
    fun getTrashProblemsByStatus(status: String): Flow<List<TrashProblem>>

    @Query("SELECT * FROM TrashProblems")
    fun getAllTrashProblems(): Flow<List<TrashProblem>>
}