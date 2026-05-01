package com.kayzwilson.retrace

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import com.kayzwilson.retrace.ui.screens.AccountScreen
import com.kayzwilson.retrace.ui.screens.FoundScreen
import com.kayzwilson.retrace.ui.screens.LoginScreen
import com.kayzwilson.retrace.ui.screens.LostScreen
import com.kayzwilson.retrace.ui.screens.ReportFoundItemScreen
import com.kayzwilson.retrace.ui.screens.ReportLostItemScreen
import com.kayzwilson.retrace.ui.screens.SignUpScreen
import com.kayzwilson.retrace.ui.screens.SplashScreen

enum class Screen {
    SPLASH, LOGIN, SIGNUP, LOST, FOUND, ACCOUNT, REPORT, REPORT_FOUND
}

@Composable
fun RetraceApp() {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }

    LaunchedEffect(Unit) {
        delay(3000)
        currentScreen = Screen.LOGIN
    }

    when (currentScreen) {
        Screen.SPLASH -> SplashScreen()

        Screen.LOGIN -> LoginScreen(
            onNavigateToSignUp = { currentScreen = Screen.SIGNUP },
            onLoginSuccess     = { currentScreen = Screen.LOST }
        )

        Screen.SIGNUP -> SignUpScreen(
            onNavigateToLogin = { currentScreen = Screen.LOGIN },
            onSignUpSuccess   = { currentScreen = Screen.LOST }
        )

        Screen.LOST -> LostScreen(
            onNavigateToFound   = { currentScreen = Screen.FOUND },
            onNavigateToAccount = { currentScreen = Screen.ACCOUNT },
            onNavigateToReport  = { currentScreen = Screen.REPORT }
        )

        Screen.FOUND -> FoundScreen(
            onNavigateToLost    = { currentScreen = Screen.LOST },
            onNavigateToAccount = { currentScreen = Screen.ACCOUNT },
            onNavigateToReport  = { currentScreen = Screen.REPORT_FOUND }
        )

        Screen.ACCOUNT -> AccountScreen(
            onNavigateToLost  = { currentScreen = Screen.LOST },
            onNavigateToFound = { currentScreen = Screen.FOUND },
            onBack            = { currentScreen = Screen.LOST }
        )

        Screen.REPORT -> ReportLostItemScreen(
            onBack   = { currentScreen = Screen.LOST },
            onSubmit = { currentScreen = Screen.LOST }
        )

        Screen.REPORT_FOUND -> ReportFoundItemScreen(
            onBack   = { currentScreen = Screen.FOUND },
            onSubmit = { currentScreen = Screen.FOUND }
        )
    }
}