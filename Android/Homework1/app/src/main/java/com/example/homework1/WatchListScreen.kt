package com.example.homework1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WatchListScreen(
    modifier: Modifier = Modifier, watchListViewModel: WatchListViewModel = viewModel()
) {
    var showEntryField by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(modifier = modifier.padding(padding)) {
            Row {
                Text(
                    text = "Watch List",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                SmallFloatingActionButton(
                    onClick = {
                        showEntryField = true
                    }, modifier = Modifier.padding(end = 10.dp)
                ) {
                    Icon(Icons.Filled.Add, "Open Add entry field")
                }
            }

            if (showEntryField) {
                WatchListAddEntryField(onAdd = { entryName ->
                    watchListViewModel.add(entryName)
                    showEntryField = false
                })

            }

            WatchListEntryList(list = watchListViewModel.entries,
                onWatchedEntry = { entry, watched ->
                    watchListViewModel.changeEntryWatched(entry, watched)
                },
                onCloseEntry = { entry ->
                    watchListViewModel.remove(entry)
                })
        }
    }
}


@Composable
fun WatchListEntryList(
    list: List<WatchListEntry>,
    onWatchedEntry: (WatchListEntry, Boolean) -> Unit,
    onCloseEntry: (WatchListEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items = list, key = { entry -> entry.id }) { entry ->
            WatchListEntryItem(entryName = entry.label,
                watched = entry.watched,
                onWatchedChange = { watched -> onWatchedEntry(entry, watched) },
                onClose = { onCloseEntry(entry) })
        }
    }
}

@Composable
fun WatchListAddEntryField(
    onAdd: (String) -> Unit, modifier: Modifier = Modifier
) {
    var entryName by remember { mutableStateOf("") }

    Row {
        TextField(
            value = entryName,
            onValueChange = { newText -> entryName = newText },
            label = { Text("Entry Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp)
                .weight(1f)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
        )
        Button(
            onClick = {
                onAdd(entryName)
            }, modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically)
        ) {
            Icon(Icons.Filled.Add, "Add Entry")
        }
    }
}