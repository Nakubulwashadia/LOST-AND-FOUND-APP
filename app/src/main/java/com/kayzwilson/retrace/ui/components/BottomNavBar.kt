package com.kayzwilson.retrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavBar(
    activeTab: NavTab,
    onNavigateToLost: () -> Unit,
    onNavigateToFound: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavItem(
            icon = "⚠️",
            label = "Lost",
            isActive = activeTab == NavTab.LOST,
            onClick = onNavigateToLost
        )
        NavItem(
            icon = "✅",
            label = "Found",
            isActive = activeTab == NavTab.FOUND,
            onClick = onNavigateToFound
        )
        NavItem(
            icon = "👤",
            label = "Account",
            isActive = activeTab == NavTab.ACCOUNT,
            onClick = onNavigateToAccount
        )
    }
}

@Composable
fun NavItem(
    icon: String,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Text(text = icon, fontSize = if (isActive) 22.sp else 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) Color(0xFF2C7DA0) else Color.Gray
        )
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .width(if (isActive) 20.dp else 0.dp)
                .height(3.dp)
                .background(
                    color = if (isActive) Color(0xFF2C7DA0) else Color.Transparent,
                    shape = RoundedCornerShape(50)
                )
        )
    }
}