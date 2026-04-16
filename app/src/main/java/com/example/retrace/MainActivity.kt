package com.kayzwilson.retrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.retrace.ui.theme.RETRACETheme
import com.example.retrace.LostItemReportingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RETRACETheme {
                LostItemReportingScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LostItemPreview() {
     RETRACETheme{
        LostItemReportingScreen()
    }
}