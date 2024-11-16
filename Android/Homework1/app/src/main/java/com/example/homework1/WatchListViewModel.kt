package com.example.homework1

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class WatchListViewModel : ViewModel() {
    private val _entries = getStartWatchListItems().toMutableStateList()
    private var nextEntryId = _entries.count() + 1
    val entries: List<WatchListEntry>
        get() = _entries


    fun remove(entry: WatchListEntry) {
        _entries.remove(entry)
    }

    fun add(entryName: String) {
        _entries.add(WatchListEntry(nextEntryId, entryName))
        nextEntryId++
    }

    fun changeEntryWatched(item: WatchListEntry, watched: Boolean) =
        _entries.find { it.id == item.id }?.let { task ->
            task.watched = watched
        }
}

private fun getStartWatchListItems() = listOf(
    WatchListEntry(1, "Prison Break"),
    WatchListEntry(2, "Suits"),
    WatchListEntry(3, "Breaking Bad"),
    WatchListEntry(4, "Better Call Saul"),
    WatchListEntry(5, "One Piece"),
    WatchListEntry(6, "Iron Man 2"),
    WatchListEntry(7, "Big Hero 6"),
    WatchListEntry(8, "Blacklist"),
    WatchListEntry(9, "Noddy"),
    WatchListEntry(10, "Demon Slayer"),
//    WatchListEntry(11, "Prison Break"),
//    WatchListEntry(12, "Suits"),
//    WatchListEntry(13, "Breaking Bad"),
//    WatchListEntry(14, "Better Call Saul"),
//    WatchListEntry(15, "One Piece"),
//    WatchListEntry(16, "Iron Man 2"),
//    WatchListEntry(17, "Big Hero 6"),
//    WatchListEntry(18, "Blacklist"),
//    WatchListEntry(19, "Noddy"),
//    WatchListEntry(20, "Demon Slayer"),
//    WatchListEntry(21, "Prison Break"),
//    WatchListEntry(22, "Suits"),
//    WatchListEntry(23, "Breaking Bad"),
//    WatchListEntry(24, "Better Call Saul"),
//    WatchListEntry(25, "One Piece"),
//    WatchListEntry(26, "Iron Man 2"),
//    WatchListEntry(27, "Big Hero 6"),
//    WatchListEntry(28, "Blacklist"),
//    WatchListEntry(29, "Noddy"),
//    WatchListEntry(30, "Demon Slayer")
)
