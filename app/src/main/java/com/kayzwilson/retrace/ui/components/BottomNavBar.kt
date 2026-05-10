package com.kayzwilson.retrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                clip = false
            )
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavItem(
            icon = "🔍",
            label = "Lost",
            isActive = activeTab == NavTab.LOST,
            onClick = onNavigateToLost
        )
        NavItem(
            icon = "✨",
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
        // Just changed icon size and added subtle scale effect
        Text(
            text = icon,
            fontSize = if (isActive) 26.sp else 22.sp,
            modifier = Modifier.offset(y = if (isActive) (-2).dp else 0.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
            color = if (isActive) Color(0xFF2C7DA0) else Color(0xFF9E9E9E)
        )
        Spacer(modifier = Modifier.height(6.dp))
        // Made indicator more vibrant
        Box(
            modifier = Modifier
                .width(if (isActive) 28.dp else 0.dp)
                .height(3.dp)
                .background(
                    color = if (isActive) Color(0xFF2C7DA0) else Color.Transparent,
                    shape = RoundedCornerShape(50)
                )
        )
    }
}