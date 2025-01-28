package com.example.projectcm

import android.content.Context
import com.example.projectcm.database.AppDatabase
import com.example.projectcm.database.repositories.TrashProblemRepository
import com.example.projectcm.database.repositories.UserRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AppContainer(context: Context) {

    // Instantiate the AppDatabase and UserDao
    private val appDatabase = AppDatabase.getInstance(context)
    private val userDao = appDatabase.userDao()
    private val trashProblemDao = appDatabase.trashProblemDao()

    // Pass UserDao to the repository
    val userRepository: UserRepository by lazy { UserRepository(userDao) }
    val trashProblemRepository: TrashProblemRepository by lazy { TrashProblemRepository(trashProblemDao) }

    var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

}