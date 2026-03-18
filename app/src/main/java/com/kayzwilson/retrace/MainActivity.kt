package com.kayzwilson.retrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


// ─── Brand Colors ────────────────────────────────────────────────────────────
val RetraceNavy     = Color(0xFF1F1A44)
val RetraceMidBlue  = Color(0xFF2B4EA8)
val RetraceSkyBlue  = Color(0xFF4A90D9)
val RetraceAccent   = Color(0xFF5B8FF9)
val RetraceWhite    = Color.White
val RetraceLightBg  = Color(0xFFF4F7FE)
val RetraceGrey     = Color(0xFF8A94A6)
val RetraceError    = Color(0xFFE53935)

// ─── Entry Point ─────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RetraceApp() }
    }
}

@Composable
fun RetraceApp() {
    var showSplash by remember { mutableStateOf(true) }
    LaunchedEffect(true) {
        delay(2000)
        showSplash = false
    }
    if (showSplash) SplashScreen() else LoginScreen()
}

// ─── Splash Screen (unchanged) ───────────────────────────────────────────────
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(RetraceWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.retrace_logo),
                contentDescription = "Retrace Logo",
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "RETRACE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = RetraceNavy
            )
        }
    }
}

// ─── Login Screen ────────────────────────────────────────────────────────────
@Composable
fun LoginScreen() {
    val focusManager = LocalFocusManager.current

    var email        by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe   by remember { mutableStateOf(false) }
    var isLoading    by remember { mutableStateOf(false) }

    // Validation states
    val emailError    = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passwordError = password.isNotEmpty() && password.length < 6
    val canSubmit     = email.isNotEmpty() && password.isNotEmpty() && !emailError && !passwordError

    // Animated top wave offset
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "waveAnim"
    )

    Box(modifier = Modifier.fillMaxSize().background(RetraceLightBg)) {

        // ── Decorative header gradient ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(RetraceNavy, RetraceMidBlue, RetraceLightBg)
                    )
                )
        )

        // ── Decorative circle accent ────────────────────────────────────────
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

        // ── Main scrollable content ─────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(56.dp))

            // Logo + wordmark
            Image(
                painter = painterResource(id = R.drawable.retrace_logo),
                contentDescription = "Retrace Logo",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "RETRACE",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RetraceWhite,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Find what's lost. Return what matters.",
                fontSize = 13.sp,
                color = RetraceWhite.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ── Card ───────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = RetraceWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Welcome back",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = RetraceNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sign in to continue",
                        fontSize = 14.sp,
                        color = RetraceGrey
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── Email field ─────────────────────────────────────────
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email address") },
                        placeholder = { Text("you@example.com") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = if (emailError) RetraceError else RetraceMidBlue
                            )
                        },
                        isError = emailError,
                        supportingText = {
                            if (emailError) Text("Enter a valid email", color = RetraceError, fontSize = 12.sp)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RetraceMidBlue,
                            unfocusedBorderColor = Color(0xFFDDE3F0),
                            focusedLabelColor = RetraceMidBlue,
                            errorBorderColor = RetraceError
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Password field ──────────────────────────────────────
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("Min. 6 characters") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (passwordError) RetraceError else RetraceMidBlue
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = RetraceGrey
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError,
                        supportingText = {
                            if (passwordError) Text("Password must be at least 6 characters", color = RetraceError, fontSize = 12.sp)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RetraceMidBlue,
                            unfocusedBorderColor = Color(0xFFDDE3F0),
                            focusedLabelColor = RetraceMidBlue,
                            errorBorderColor = RetraceError
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Remember me + Forgot password ───────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { rememberMe = !rememberMe }
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = RetraceMidBlue,
                                    uncheckedColor = RetraceGrey
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remember me", fontSize = 13.sp, color = RetraceGrey)
                        }

                        TextButton(onClick = { /* navigate to forgot password */ }) {
                            Text(
                                "Forgot password?",
                                fontSize = 13.sp,
                                color = RetraceMidBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Sign In button ──────────────────────────────────────
                    Button(
                        onClick = {
                            isLoading = true
                            // TODO: handle authentication
                        },
                        enabled = canSubmit && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RetraceMidBlue,
                            disabledContainerColor = RetraceMidBlue.copy(alpha = 0.4f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = RetraceWhite,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Sign In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Divider ─────────────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = Color(0xFFDDE3F0))
                        Text(
                            "  Don't have an account?  ",
                            fontSize = 12.sp,
                            color = RetraceGrey
                        )
                        Divider(modifier = Modifier.weight(1f), color = Color(0xFFDDE3F0))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Google SSO button ───────────────────────────────────
                    OutlinedButton(
                        onClick = { /* TODO: Google Sign-In */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFDDE3F0)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RetraceNavy)
                    ) {
                        // Using a text placeholder — swap with actual Google icon drawable if available

                        Text("Sign Up", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))



            Spacer(modifier = Modifier.height(104.dp))
        }
    }
}