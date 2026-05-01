package com.kayzwilson.retrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.kayzwilson.retrace.ui.theme.RetraceAccent
import com.kayzwilson.retrace.ui.theme.RetraceLightBg
import com.kayzwilson.retrace.ui.theme.RetraceMidBlue
import com.kayzwilson.retrace.ui.theme.RetraceNavy
import com.kayzwilson.retrace.ui.theme.RetraceSkyBlue

@Composable
fun RetraceScreenHeader() {
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(RetraceNavy, RetraceMidBlue, RetraceLightBg)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-50).dp, y = (-40).dp)
                .clip(CircleShape)
                .background(RetraceAccent.copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = 20.dp)
                .clip(CircleShape)
                .background(RetraceSkyBlue.copy(alpha = 0.18f))
        )
    }
}