package com.kayzwilson.retrace.ui.screens
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kayzwilson.retrace.ui.components.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kayzwilson.retrace.viewmodel.LostViewModel
import com.kayzwilson.retrace.ui.components.BottomNavBar
import com.kayzwilson.retrace.ui.components.NavTab



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.kayzwilson.retrace.ui.components.BottomNavBar
import com.kayzwilson.retrace.ui.components.LostItemCard

@Composable
fun LostScreen(
    onNavigateToFound: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    var search by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf("All") }

    val viewModel: LostViewModel = viewModel()

    val state = viewModel.uiState

    val filters = listOf(
        "All",
        "Electronics",
        "Bags",
        "Books",
        "ID Cards",
        "Keys",
        "Other"
    )



    // ── Filter + search logic ─────────────────────────────────────────────
    val filteredItems = state.items.filter { item ->
        val matchesSearch = search.isBlank() ||
                item.itemName.contains(search, ignoreCase = true) ||
                item.description.contains(search, ignoreCase = true) ||
                item.location.contains(search, ignoreCase = true) ||
                item.category.contains(search, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "All"         -> true
            "Electronics" -> item.category.contains("Computer", ignoreCase = true) ||
                    item.category.contains("Laptop", ignoreCase = true) ||
                    item.category.contains("Phone", ignoreCase = true) ||
                    item.category.contains("Earphone", ignoreCase = true) ||
                    item.category.contains("Headphone", ignoreCase = true)
            "Bags"        -> item.category.contains("Bag", ignoreCase = true) ||
                    item.category.contains("Backpack", ignoreCase = true)
            "Books"       -> item.category.contains("Book", ignoreCase = true) ||
                    item.category.contains("Notes", ignoreCase = true) ||
                    item.category.contains("Stationery", ignoreCase = true)
            "ID Cards"    -> item.category.contains("ID", ignoreCase = true) ||
                    item.category.contains("Card", ignoreCase = true)
            "Keys"        -> item.category.contains("Key", ignoreCase = true)
            else          -> true
        }
        matchesSearch && matchesFilter
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F4FA))) {

        // ── Gradient header ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F3B5C), Color(0xFF1A5C8A))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Lost Items",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            "${state.items.size} item${if (state.items.size != 1) "s" else ""} reported",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                    // Report button (top-right)
                    Button(
                        onClick = onNavigateToReport,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B8FF9)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("+ Report", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search bar inside header
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search lost items...", color = Color.White.copy(alpha = 0.5f)) },
                    leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 4.dp)) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                        focusedBorderColor = Color(0xFF5B8FF9),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        // ── Filter chips ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isActive = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isActive)
                                Brush.linearGradient(listOf(Color(0xFF1A5C8A), Color(0xFF2C7DA0)))
                            else
                                Brush.linearGradient(listOf(Color(0xFFEEF4FB), Color(0xFFEEF4FB)))
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        filter,
                        color = if (isActive) Color.White else Color(0xFF1A5C8A),
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // ── Body ──────────────────────────────────────────────────────────
        when {
            state.isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF2C7DA0), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading lost items...", fontSize = 14.sp, color = Color(0xFF5B6E8C))
                    }
                }
            }

            state.errorMessage.isNotEmpty() -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text("⚠️", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.errorMessage, fontSize = 14.sp, color = Color(0xFFE53935), textAlign = TextAlign.Center)
                    }
                }
            }

            filteredItems.isEmpty() -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text(if (search.isNotBlank()) "🔍" else "📭", fontSize = 52.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            if (search.isNotBlank()) "No results for \"$search\""
                            else "No lost items yet",
                            fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F3B5C),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            if (search.isNotBlank()) "Try a different search term"
                            else "Be the first to report a lost item",
                            fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center
                        )
                        if (search.isBlank()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onNavigateToReport,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C7DA0))
                            ) { Text("Report Lost Item", color = Color.White, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredItems.size) { index ->
                        LostItemCard(item = filteredItems[index])
                    }
                }
            }
        }

        BottomNavBar(
            activeTab           = NavTab.LOST,
            onNavigateToLost    = { },
            onNavigateToFound   = onNavigateToFound,
            onNavigateToAccount = onNavigateToAccount
        )
    }
}


