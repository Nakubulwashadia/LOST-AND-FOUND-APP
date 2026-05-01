package com.kayzwilson.retrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContactDetailRow(
    icon: String,
    label: String,
    value: String,
    actionLabel: String?,
    actionColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF0F4FA), CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(icon, fontSize = 18.sp) }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 14.sp, color = Color(0xFF0F3B5C), fontWeight = FontWeight.SemiBold)
        }

        if (actionLabel != null) {
            TextButton(onClick = { }) {
                Text(actionLabel, color = actionColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}