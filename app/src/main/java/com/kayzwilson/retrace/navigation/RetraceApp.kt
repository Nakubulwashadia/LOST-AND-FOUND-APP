package com.kayzwilson.retrace

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.kayzwilson.retrace.ui.screens.AccountScreen
import com.kayzwilson.retrace.ui.screens.FoundScreen
import com.kayzwilson.retrace.ui.screens.LoginScreen
import com.kayzwilson.retrace.ui.screens.LostScreen
import com.kayzwilson.retrace.ui.screens.ReportFoundItemScreen
import com.kayzwilson.retrace.ui.screens.ReportLostItemScreen
import com.kayzwilson.retrace.ui.screens.SignUpScreen
import com.kayzwilson.retrace.ui.screens.SplashScreen
import com.kayzwilson.retrace.viewmodel.AuthViewModel
import com.kayzwilson.retrace.viewmodel.LostViewModel

enum class Screen {
    SPLASH, LOGIN, SIGNUP, LOST, FOUND, ACCOUNT, REPORT, REPORT_FOUND
}


@Composable
fun RetraceApp() {
    var currentScreen by rememberSaveable { mutableStateOf(Screen.SPLASH) }

    val lostViewModel: LostViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    LaunchedEffect("splashShown") {
        if (currentScreen == Screen.SPLASH) {
            delay(3000)
            currentScreen = Screen.LOGIN
        }
    }

    when (currentScreen) {

        Screen.SPLASH -> SplashScreen()

        Screen.LOGIN -> LoginScreen(
            viewModel = authViewModel,
            onNavigateToSignUp = { currentScreen = Screen.SIGNUP },
            onLoginSuccess = {
                authViewModel.resetState()
                currentScreen = Screen.LOST
            }
        )

        Screen.SIGNUP -> SignUpScreen(
            viewModel = authViewModel,
            onNavigateToLogin = { currentScreen = Screen.LOGIN },
            onSignUpSuccess = {
                authViewModel.resetState()
                currentScreen = Screen.LOST
            }
        )

        Screen.LOST -> LostScreen(
            onNavigateToFound = { currentScreen = Screen.FOUND },
            onNavigateToAccount = { currentScreen = Screen.ACCOUNT },
            onNavigateToReport = { currentScreen = Screen.REPORT }
        )

        Screen.FOUND -> FoundScreen(
            onNavigateToLost = { currentScreen = Screen.LOST },
            onNavigateToAccount = { currentScreen = Screen.ACCOUNT },
            onNavigateToReport = { currentScreen = Screen.REPORT_FOUND }
        )

        Screen.ACCOUNT -> AccountScreen(
            onNavigateToLost = { currentScreen = Screen.LOST },
            onNavigateToFound = { currentScreen = Screen.FOUND },
            onBack = { currentScreen = Screen.LOST }
        )

        Screen.REPORT -> ReportLostItemScreen(
            onBack = { currentScreen = Screen.LOST },
            onSubmit = { currentScreen = Screen.LOST }
        )

        Screen.REPORT_FOUND -> ReportFoundItemScreen(
            onBack = { currentScreen = Screen.FOUND },
            onSubmit = { currentScreen = Screen.FOUND }
        )
    }
}