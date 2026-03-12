package com.kayzwilson.retrace.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kayzwilson.retrace.ItemCard
import com.kayzwilson.retrace.data.Item
import com.kayzwilson.retrace.data.ItemType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundItemsScreen(onBackClick: () -> Unit) {
    // Dummy data for now - in a real app, this would come from a ViewModel/Database
    val foundItems = listOf(
        Item(title = "Keys", description = "Keychain with a red car key", location = "Library Second Floor", type = ItemType.FOUND),
        Item(title = "Glasses", description = "Black frame prescription glasses", location = "Student Center", type = ItemType.FOUND),
        Item(title = "Water Bottle", description = "Hydro Flask, black", location = "Engineering Lab", type = ItemType.FOUND)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Found Items", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (foundItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No found items reported yet.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(foundItems) { item ->
                        ItemCard(item = item)
                    }
                }
            }
        }
    }
}
