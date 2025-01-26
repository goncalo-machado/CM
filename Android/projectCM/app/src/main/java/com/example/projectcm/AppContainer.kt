package com.example.projectcm

import android.content.Context
import com.example.projectcm.database.AppDatabase
import com.example.projectcm.database.repositories.UserRepository

class AppContainer(context: Context) {
    // Instantiate the AppDatabase and UserDao
    private val appDatabase = AppDatabase.getInstance(context)
    private val userDao = appDatabase.userDao()

    // Pass UserDao to the repository
    val userRepository: UserRepository by lazy { UserRepository(userDao) }
}