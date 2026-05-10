package com.kayzwilson.retrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kayzwilson.retrace.viewmodel.AuthViewModel
import com.kayzwilson.retrace.viewmodel.LostViewModel

import kotlinx.coroutines.delay
import com.kayzwilson.retrace.ui.screens.*

// ─── Entry Point ──────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RetraceApp() }
    }
}

