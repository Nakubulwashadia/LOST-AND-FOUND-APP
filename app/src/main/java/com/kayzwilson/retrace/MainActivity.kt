package com.kayzwilson.retrace

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
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

import com.google.firebase.firestore.FirebaseFirestore

// ─── Brand Colors ─────────────────────────────────────────────────────────────
val RetraceNavy    = Color(0xFF1F1A44)
val RetraceMidBlue = Color(0xFF2B4EA8)
val RetraceSkyBlue = Color(0xFF4A90D9)
val RetraceAccent  = Color(0xFF5B8FF9)
val RetraceWhite   = Color.White
val RetraceLightBg = Color(0xFFF4F7FE)
val RetraceGrey    = Color(0xFF8A94A6)
val RetraceError   = Color(0xFFE53935)

// ─── Navigation State ─────────────────────────────────────────────────────────
enum class Screen { SPLASH, LOGIN, SIGNUP }

// ─── Entry Point ──────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RetraceApp() }
    }
}

@Composable
fun RetraceApp() {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }

    LaunchedEffect(Unit) {
        delay(2000)
        currentScreen = Screen.LOGIN
    }

    when (currentScreen) {
        Screen.SPLASH  -> SplashScreen()
        Screen.LOGIN   -> LoginScreen(onNavigateToSignUp = { currentScreen = Screen.SIGNUP })
        Screen.SIGNUP  -> SignUpScreen(onNavigateToLogin = { currentScreen = Screen.LOGIN })
    }
}

// ─── Splash Screen ────────────────────────────────────────────────────────────
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

// ─── Shared Header Block ──────────────────────────────────────────────────────
@Composable
fun RetraceScreenHeader(subtitle: String) {
    // Gradient background
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
    // Decorative circles
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
            .offset(x = 40.dp, y = 20.dp)
            .clip(CircleShape)
            .background(RetraceSkyBlue.copy(alpha = 0.18f))
    )
}

// ─── Shared Text Field ────────────────────────────────────────────────────────
@Composable
fun RetraceTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isError: Boolean = false,
    errorMessage: String = "",
    leadingIcon: @Composable () -> Unit,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = {
            if (isError && errorMessage.isNotEmpty())
                Text(errorMessage, color = RetraceError, fontSize = 12.sp)
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
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
}

// ─── Login Screen ─────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(onNavigateToSignUp: () -> Unit) {
    val focusManager = LocalFocusManager.current

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe      by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }

    val emailError    = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passwordError = password.isNotEmpty() && password.length < 6
    val canSubmit     = email.isNotEmpty() && password.isNotEmpty() && !emailError && !passwordError

    val auth = FirebaseAuth.getInstance()
    println("Firebase connected: $auth")
    Box(modifier = Modifier.fillMaxSize().background(RetraceLightBg)) {

        // Decorative header
        Box(
            modifier = Modifier.fillMaxWidth().height(260.dp).background(
                Brush.verticalGradient(colors = listOf(RetraceNavy, RetraceMidBlue, RetraceLightBg))
            )
        )
        Box(
            modifier = Modifier.size(180.dp).offset(x = (-50).dp, y = (-40).dp)
                .clip(CircleShape).background(RetraceAccent.copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier.size(120.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = 20.dp)
                .clip(CircleShape).background(RetraceSkyBlue.copy(alpha = 0.18f))
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
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
            Text("RETRACE", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
                color = RetraceWhite, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Find what's lost. Return what matters.",
                fontSize = 13.sp, color = RetraceWhite.copy(alpha = 0.75f),
                textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(36.dp))

            // Card
            Card(
                modifier = Modifier.fillMaxWidth().shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = RetraceWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome back", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = RetraceNavy)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Sign in to continue", fontSize = 14.sp, color = RetraceGrey)
                    Spacer(modifier = Modifier.height(28.dp))

                    // Email
                    RetraceTextField(
                        value = email, onValueChange = { email = it },
                        label = "Email address", placeholder = "you@campus.ac.ug",
                        isError = emailError, errorMessage = "Enter a valid email",
                        leadingIcon = {
                            Icon(Icons.Default.Email, null,
                                tint = if (emailError) RetraceError else RetraceMidBlue)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    RetraceTextField(
                        value = password, onValueChange = { password = it },
                        label = "Password", placeholder = "Min. 6 characters",
                        isError = passwordError, errorMessage = "Password must be at least 6 characters",
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null,
                                tint = if (passwordError) RetraceError else RetraceMidBlue)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide" else "Show",
                                    tint = RetraceGrey
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Remember me + Forgot password
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
                                checked = rememberMe, onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = RetraceMidBlue, uncheckedColor = RetraceGrey)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remember me", fontSize = 13.sp, color = RetraceGrey)
                        }
                        TextButton(onClick = { /* TODO: forgot password */ }) {
                            Text("Forgot password?", fontSize = 13.sp,
                                color = RetraceMidBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sign In button
                    val auth = FirebaseAuth.getInstance()
                    Button(
                        onClick = {
                            isLoading = true

                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        isLoading = false
                                        // TODO: Navigate to home
                                    } else {
                                        isLoading = false
                                        println("Login failed: ${task.exception?.message}")
                                    }
                                }
                        },
                        enabled = canSubmit && !isLoading,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
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
                                color = RetraceWhite, strokeWidth = 2.5.dp)
                        } else {
                            Text("Sign In", fontSize = 16.sp,
                                fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Divider
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Divider(modifier = Modifier.weight(1f), color = Color(0xFFDDE3F0))
                        Text("  Don't have an account?  ", fontSize = 12.sp, color = RetraceGrey)
                        Divider(modifier = Modifier.weight(1f), color = Color(0xFFDDE3F0))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign Up button
                    OutlinedButton(
                        onClick = onNavigateToSignUp,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, RetraceMidBlue),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RetraceMidBlue)
                    ) {
                        Text("Sign Up", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Sign Up Screen ───────────────────────────────────────────────────────────
@Composable
fun SignUpScreen(onNavigateToLogin: () -> Unit) {
    val focusManager = LocalFocusManager.current

    var studentId       by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible        by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }

    // Validation
    val studentIdError      = studentId.isNotEmpty() && studentId.length < 4
    val emailError          = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passwordError       = password.isNotEmpty() && password.length < 6
    val confirmPasswordError = confirmPassword.isNotEmpty() && confirmPassword != password
    val canSubmit = studentId.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
            confirmPassword.isNotEmpty() && !studentIdError && !emailError &&
            !passwordError && !confirmPasswordError

    Box(modifier = Modifier.fillMaxSize().background(RetraceLightBg)) {

        // Decorative header
        Box(
            modifier = Modifier.fillMaxWidth().height(260.dp).background(
                Brush.verticalGradient(colors = listOf(RetraceNavy, RetraceMidBlue, RetraceLightBg))
            )
        )
        Box(
            modifier = Modifier.size(180.dp).offset(x = (-50).dp, y = (-40).dp)
                .clip(CircleShape).background(RetraceAccent.copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier.size(120.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = 20.dp)
                .clip(CircleShape).background(RetraceSkyBlue.copy(alpha = 0.18f))
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
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
            Text("RETRACE", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
                color = RetraceWhite, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Find what's lost. Return what matters.",
                fontSize = 13.sp, color = RetraceWhite.copy(alpha = 0.75f),
                textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(36.dp))

            // Card
            Card(
                modifier = Modifier.fillMaxWidth().shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = RetraceWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Create Account", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = RetraceNavy)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Join Retrace on your campus", fontSize = 14.sp, color = RetraceGrey)
                    Spacer(modifier = Modifier.height(28.dp))

                    // Student ID / Email
                    RetraceTextField(
                        value = studentId, onValueChange = { studentId = it },
                        label = "StudentEmail", placeholder = "e.g. student@campus.ac.ug",
                        isError = studentIdError, errorMessage = "Enter a valid studentEmail",
                        leadingIcon = {
                            Icon(Icons.Default.Badge, null,
                                tint = if (studentIdError) RetraceError else RetraceMidBlue)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    RetraceTextField(
                        value = email, onValueChange = { email = it },
                        label = "Email address", placeholder = "you@campus.ac.ug",
                        isError = emailError, errorMessage = "Enter a valid email",
                        leadingIcon = {
                            Icon(Icons.Default.Email, null,
                                tint = if (emailError) RetraceError else RetraceMidBlue)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    RetraceTextField(
                        value = password, onValueChange = { password = it },
                        label = "Password", placeholder = "Min. 6 characters",
                        isError = passwordError, errorMessage = "Password must be at least 6 characters",
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null,
                                tint = if (passwordError) RetraceError else RetraceMidBlue)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide" else "Show",
                                    tint = RetraceGrey
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password
                    RetraceTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it },
                        label = "Confirm password", placeholder = "Re-enter your password",
                        isError = confirmPasswordError, errorMessage = "Passwords do not match",
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null,
                                tint = if (confirmPasswordError) RetraceError else RetraceMidBlue)
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide" else "Show",
                                    tint = RetraceGrey
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    val auth = FirebaseAuth.getInstance()
                    val db = FirebaseFirestore.getInstance()
                    // Create Account button
                    Button(

                        onClick = {
                            isLoading = true

                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        val userId = auth.currentUser?.uid

                                        val userMap = hashMapOf(
                                            "studentId" to studentId,
                                            "email" to email
                                        )

                                        userId?.let {
                                            db.collection("users")
                                                .document(it)
                                                .set(userMap)
                                        }

                                        isLoading = false
                                        // TODO: Navigate to home

                                    } else {
                                        isLoading = false
                                        println("Signup failed: ${task.exception?.message}")
                                    }
                                }
                        },
                        enabled = canSubmit && !isLoading,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
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
                                color = RetraceWhite, strokeWidth = 2.5.dp)
                        } else {
                            Text("Create Account", fontSize = 16.sp,
                                fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Back to login
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToLogin() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = Color(0xFFDDE3F0))
                        Text(
                            "  Already have an account? Sign In  ",
                            fontSize = 12.sp,
                            color = RetraceMidBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Divider(modifier = Modifier.weight(1f), color = Color(0xFFDDE3F0))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}