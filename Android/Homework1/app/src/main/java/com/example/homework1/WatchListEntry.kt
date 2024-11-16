package com.example.homework1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class WatchListEntry(
    val id: Int, val label: String, initialState: Boolean = false
) {
    var watched by mutableStateOf(initialState)
}