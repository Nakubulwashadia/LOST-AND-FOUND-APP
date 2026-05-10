package com.kayzwilson.retrace.ui.screens  // ← correct package

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kayzwilson.retrace.R
import com.kayzwilson.retrace.ui.theme.RetraceNavy
import com.kayzwilson.retrace.ui.theme.RetraceWhite

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RetraceWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.retrace_logo_new),
                contentDescription = "Retrace Logo",
                modifier = Modifier.size(360.dp)
            )

        }
    }
}