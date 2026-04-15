package com.kayzwilson.retrace

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
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

// ─── Navigation ───────────────────────────────────────────────────────────────
enum class Screen { SPLASH, LOGIN, SIGNUP, LOST, FOUND, ACCOUNT }
enum class NavTab  { LOST, FOUND, ACCOUNT }

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
            onNavigateToAccount = { currentScreen = Screen.ACCOUNT }
        )

        Screen.FOUND -> FoundScreen(
            onNavigateToLost    = { currentScreen = Screen.LOST },
            onNavigateToAccount = { currentScreen = Screen.ACCOUNT }
        )

        Screen.ACCOUNT -> AccountScreen(
            onNavigateToLost  = { currentScreen = Screen.LOST },
            onNavigateToFound = { currentScreen = Screen.FOUND },
            onBack            = { currentScreen = Screen.LOST }
        )
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
            Text("RETRACE", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = RetraceNavy)
        }
    }
}

// ─── Shared Bottom Nav ────────────────────────────────────────────────────────
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
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavItem(icon = "⚠️", label = "Lost",    isActive = activeTab == NavTab.LOST,    onClick = onNavigateToLost)
        NavItem(icon = "✅", label = "Found",   isActive = activeTab == NavTab.FOUND,   onClick = onNavigateToFound)
        NavItem(icon = "👤", label = "Account", isActive = activeTab == NavTab.ACCOUNT, onClick = onNavigateToAccount)
    }
}

@Composable
fun NavItem(icon: String, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Text(text = icon, fontSize = if (isActive) 22.sp else 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) Color(0xFF2C7DA0) else Color.Gray
        )
        Spacer(modifier = Modifier.height(3.dp))
        // Active indicator pill
        Box(
            modifier = Modifier
                .width(if (isActive) 20.dp else 0.dp)
                .height(3.dp)
                .background(
                    color = if (isActive) Color(0xFF2C7DA0) else Color.Transparent,
                    shape = RoundedCornerShape(50)
                )
        )
    }
}

// ─── Shared Screen Header ─────────────────────────────────────────────────────
@Composable
fun RetraceScreenHeader() {
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(colors = listOf(RetraceNavy, RetraceMidBlue, RetraceLightBg))
            )
        )
        Box(
            modifier = Modifier.size(180.dp).offset(x = (-50).dp, y = (-40).dp)
                .clip(CircleShape).background(RetraceAccent.copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier.size(120.dp).align(Alignment.TopEnd)
                .offset(x = 40.dp, y = 20.dp)
                .clip(CircleShape).background(RetraceSkyBlue.copy(alpha = 0.18f))
        )
    }
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
        value = value, onValueChange = onValueChange,
        label = { Text(label) }, placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon, trailingIcon = trailingIcon,
        isError = isError,
        supportingText = {
            if (isError && errorMessage.isNotEmpty())
                Text(errorMessage, color = RetraceError, fontSize = 12.sp)
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions, keyboardActions = keyboardActions,
        singleLine = true, modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RetraceMidBlue, unfocusedBorderColor = Color(0xFFDDE3F0),
            focusedLabelColor = RetraceMidBlue, errorBorderColor = RetraceError
        )
    )
}

// ─── Login Screen ─────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(onNavigateToSignUp: () -> Unit, onLoginSuccess: () -> Unit) {
    val focusManager = LocalFocusManager.current
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe      by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }
    var loginErrorMessage by remember { mutableStateOf("") }

    val emailError    = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passwordError = password.isNotEmpty() && password.length < 6
    val canSubmit     = email.isNotEmpty() && password.isNotEmpty() && !emailError && !passwordError
    val auth = FirebaseAuth.getInstance()

    Box(modifier = Modifier.fillMaxSize().background(RetraceLightBg)) {
        RetraceScreenHeader()
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            Image(painter = painterResource(id = R.drawable.retrace_logo),
                contentDescription = "Retrace Logo", modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text("RETRACE", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
                color = RetraceWhite, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Find what's lost. Return what matters.",
                fontSize = 13.sp, color = RetraceWhite.copy(alpha = 0.75f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(36.dp))

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

                    RetraceTextField(
                        value = email, onValueChange = { email = it },
                        label = "Email address", placeholder = "you@campus.ac.ug",
                        isError = emailError, errorMessage = "Enter a valid email",
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = if (emailError) RetraceError else RetraceMidBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RetraceTextField(
                        value = password, onValueChange = { password = it },
                        label = "Password", placeholder = "Min. 6 characters",
                        isError = passwordError, errorMessage = "Password must be at least 6 characters",
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = if (passwordError) RetraceError else RetraceMidBlue) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide" else "Show", tint = RetraceGrey
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { rememberMe = !rememberMe }
                        ) {
                            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = RetraceMidBlue, uncheckedColor = RetraceGrey))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remember me", fontSize = 13.sp, color = RetraceGrey)
                        }
                        TextButton(onClick = { }) {
                            Text("Forgot password?", fontSize = 13.sp, color = RetraceMidBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    if (loginErrorMessage.isNotEmpty()) {
                        Text(text = loginErrorMessage, color = RetraceError, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            isLoading = true
                            loginErrorMessage = ""
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) onLoginSuccess()
                                    else loginErrorMessage = task.exception?.message ?: "Login failed ❌"
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
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = RetraceWhite, strokeWidth = 2.5.dp)
                        else Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDE3F0))
                        Text("  Don't have an account?  ", fontSize = 12.sp, color = RetraceGrey)
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDE3F0))
                    }
                    Spacer(modifier = Modifier.height(20.dp))

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
fun SignUpScreen(onNavigateToLogin: () -> Unit, onSignUpSuccess: () -> Unit) {
    val focusManager = LocalFocusManager.current
    var studentId           by remember { mutableStateOf("") }
    var email               by remember { mutableStateOf("") }
    var password            by remember { mutableStateOf("") }
    var confirmPassword     by remember { mutableStateOf("") }
    var passwordVisible        by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading           by remember { mutableStateOf(false) }

    val studentIdError       = studentId.isNotEmpty() && studentId.length < 4
    val emailError           = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passwordError        = password.isNotEmpty() && password.length < 6
    val confirmPasswordError = confirmPassword.isNotEmpty() && confirmPassword != password
    val canSubmit = studentId.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
            confirmPassword.isNotEmpty() && !studentIdError && !emailError && !passwordError && !confirmPasswordError

    val auth = FirebaseAuth.getInstance()
    val db   = FirebaseFirestore.getInstance()

    Box(modifier = Modifier.fillMaxSize().background(RetraceLightBg)) {
        RetraceScreenHeader()
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            Image(painter = painterResource(id = R.drawable.retrace_logo),
                contentDescription = "Retrace Logo", modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text("RETRACE", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = RetraceWhite, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Find what's lost. Return what matters.",
                fontSize = 13.sp, color = RetraceWhite.copy(alpha = 0.75f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(36.dp))

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

                    RetraceTextField(
                        value = studentId, onValueChange = { studentId = it },
                        label = "Student Email", placeholder = "e.g. student@campus.ac.ug",
                        isError = studentIdError, errorMessage = "Enter a valid student email",
                        leadingIcon = { Icon(Icons.Default.Badge, null, tint = if (studentIdError) RetraceError else RetraceMidBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RetraceTextField(
                        value = email, onValueChange = { email = it },
                        label = "Email address", placeholder = "you@campus.ac.ug",
                        isError = emailError, errorMessage = "Enter a valid email",
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = if (emailError) RetraceError else RetraceMidBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RetraceTextField(
                        value = password, onValueChange = { password = it },
                        label = "Password", placeholder = "Min. 6 characters",
                        isError = passwordError, errorMessage = "Password must be at least 6 characters",
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = if (passwordError) RetraceError else RetraceMidBlue) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide" else "Show", tint = RetraceGrey)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RetraceTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it },
                        label = "Confirm password", placeholder = "Re-enter your password",
                        isError = confirmPasswordError, errorMessage = "Passwords do not match",
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = if (confirmPasswordError) RetraceError else RetraceMidBlue) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide" else "Show", tint = RetraceGrey)
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )
                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val userId = auth.currentUser?.uid
                                        val userMap = hashMapOf(
                                            "studentId" to studentId,
                                            "email"     to email,
                                            "name"      to "",
                                            "role"      to "Student"
                                        )
                                        userId?.let { db.collection("users").document(it).set(userMap) }
                                        isLoading = false
                                        onSignUpSuccess()
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
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = RetraceWhite, strokeWidth = 2.5.dp)
                        else Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { onNavigateToLogin() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDE3F0))
                        Text("  Already have an account? Sign In  ",
                            fontSize = 12.sp, color = RetraceMidBlue, fontWeight = FontWeight.SemiBold)
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDE3F0))
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Filter Chip ─────────────────────────────────────────────────────────────
@Composable
fun FilterChip(text: String, isActive: Boolean) {
    Text(
        text = text,
        modifier = Modifier
            .background(if (isActive) Color(0xFF2C7DA0) else Color(0xFFEEF2FF), shape = RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        color = if (isActive) Color.White else Color(0xFF1F5068),
        fontSize = 13.sp
    )
}

// ─── Lost Screen ─────────────────────────────────────────────────────────────
@Composable
fun LostScreen(onNavigateToFound: () -> Unit, onNavigateToAccount: () -> Unit) {
    var search by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {

        // Header
        Text("Lost Items", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = Color(0xFF0F3B5C), modifier = Modifier.padding(16.dp))

        // Filter Chips
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip("All", true)
            FilterChip("Electronics", false)
            FilterChip("Accessories", false)
            FilterChip("Books", false)
            FilterChip("ID Cards", false)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = search, onValueChange = { search = it },
            placeholder = { Text("Search lost items...") },
            leadingIcon = { Text("🔍") },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Feed
        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            item {
                LostItemCard(name = "Emma Rodriguez", course = "Computer Science • 2nd year",
                    item = "MacBook Pro 14\"", emoji = "💻")
            }
            item {
                LostItemCard(name = "James Chen", course = "Business • 3rd year",
                    item = "AirPods Pro", emoji = "🎧")
            }
            item {
                LostItemCard(name = "Sophia Martinez", course = "Biology • 1st year",
                    item = "Student ID Card", emoji = "🪪")
            }
        }

        // Bottom Nav — Lost is active
        BottomNavBar(
            activeTab           = NavTab.LOST,
            onNavigateToLost    = { /* already here */ },
            onNavigateToFound   = onNavigateToFound,
            onNavigateToAccount = onNavigateToAccount
        )
    }
}

// ─── Found Screen ─────────────────────────────────────────────────────────────
@Composable
fun FoundScreen(onNavigateToLost: () -> Unit, onNavigateToAccount: () -> Unit) {
    var search by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {

        // Header
        Text("Found Items", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = Color(0xFF0F3B5C), modifier = Modifier.padding(16.dp))

        // Filter Chips
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip("All", true)
            FilterChip("Electronics", false)
            FilterChip("Accessories", false)
            FilterChip("Books", false)
            FilterChip("ID Cards", false)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = search, onValueChange = { search = it },
            placeholder = { Text("Search found items...") },
            leadingIcon = { Text("🔍") },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Empty state
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✅", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("No found items yet", fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold, color = Color(0xFF0F3B5C))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Items reported as found will appear here",
                    fontSize = 13.sp, color = Color.Gray)
            }
        }

        // Bottom Nav — Found is active
        BottomNavBar(
            activeTab           = NavTab.FOUND,
            onNavigateToLost    = onNavigateToLost,
            onNavigateToFound   = { /* already here */ },
            onNavigateToAccount = onNavigateToAccount
        )
    }
}

// ─── Lost Item Card ───────────────────────────────────────────────────────────
@Composable
fun LostItemCard(name: String, course: String, item: String, emoji: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(Color(0xFF2C7DA0), CircleShape),
                    contentAlignment = Alignment.Center) { Text("👩‍🎓") }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.Bold)
                    Text(course, fontSize = 12.sp, color = Color.Gray)
                }
                Text("LOST", color = Color.Red, fontSize = 11.sp,
                    modifier = Modifier.background(Color(0xFFFEE2E2), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp))
            }
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center) { Text(emoji, fontSize = 40.sp) }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(item, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("📅 March 25, 2026", fontSize = 13.sp)
                Text("⏰ 2:30 PM", fontSize = 13.sp)
                Text("🏫 Engineering Hall", fontSize = 13.sp)
            }
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C7DA0))
            ) { Text("Contact", color = Color.White) }
        }
    }
}

// ─── Account Screen ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onNavigateToLost: () -> Unit,
    onNavigateToFound: () -> Unit,
    onBack: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var name      by remember { mutableStateOf("") }
    var role      by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var phone     by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var year      by remember { mutableStateOf("") }
    var semester  by remember { mutableStateOf("") }
    var college   by remember { mutableStateOf("") }
    var school    by remember { mutableStateOf("") }

    var isSaving        by remember { mutableStateOf(false) }
    var saveMessage     by remember { mutableStateOf("") }
    var isSavingPersonal  by remember { mutableStateOf(false) }
    var savePersonalMessage by remember { mutableStateOf("") }
    var isSavingAcademic  by remember { mutableStateOf(false) }
    var saveAcademicMessage by remember { mutableStateOf("") }

    var lostCount    by remember { mutableIntStateOf(3) }
    var foundCount   by remember { mutableIntStateOf(2) }
    var reportsCount by remember { mutableIntStateOf(12) }

    val auth   = FirebaseAuth.getInstance()
    val db     = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let { uid ->
            email = auth.currentUser?.email ?: ""
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        name       = doc.getString("name")       ?: ""
                        role       = doc.getString("role")       ?: ""
                        studentId  = doc.getString("studentId")  ?: ""
                        phone      = doc.getString("phone")      ?: ""
                        department = doc.getString("department") ?: ""
                        year       = doc.getString("year")       ?: ""
                        semester   = doc.getString("semester")   ?: ""
                        college    = doc.getString("college")    ?: ""
                        school     = doc.getString("school")     ?: ""
                    }
                }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F3B5C)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF2C7DA0))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        // ─── Account tab is highlighted in the bottom nav ───────────────
        bottomBar = {
            BottomNavBar(
                activeTab           = NavTab.ACCOUNT,
                onNavigateToLost    = onNavigateToLost,
                onNavigateToFound   = onNavigateToFound,
                onNavigateToAccount = { /* already here */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Profile Header Card ────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(
                                brush = Brush.linearGradient(colors = listOf(Color(0xFF2C7DA0), Color(0xFF1F5068))),
                                shape = CircleShape
                            )
                            .clickable(enabled = !isEditing) { isEditing = true; saveMessage = "" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.split(" ").filter { it.isNotEmpty() }.take(2)
                                .joinToString("") { it.first().uppercase() },
                            color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold
                        )
                        if (!isEditing) {
                            Box(
                                modifier = Modifier.align(Alignment.BottomEnd).size(32.dp)
                                    .background(Color.White, CircleShape)
                                    .border(2.dp, Color(0xFFE2E8F0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit",
                                    modifier = Modifier.size(16.dp), tint = Color(0xFF2C7DA0))
                            }
                        }
                    }

                    if (saveMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = saveMessage,
                            color = if (saveMessage.startsWith("✅")) Color(0xFF2C7DA0) else Color.Red,
                            fontSize = 13.sp, textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isEditing) {
                        Text(text = name.ifEmpty { "Tap avatar to set name" },
                            fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F3B5C))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = role.ifEmpty { "Role not set" }, fontSize = 14.sp, color = Color(0xFF5B6E8C))
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        OutlinedTextField(
                            value = name, onValueChange = { name = it },
                            label = { Text("Full Name", color = Color(0xFF5B6E8C)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedLabelColor = Color(0xFF2C7DA0)),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = role, onValueChange = { role = it },
                            label = { Text("Role", color = Color(0xFF5B6E8C)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedLabelColor = Color(0xFF2C7DA0)),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                isSaving = true; saveMessage = ""
                                userId?.let {
                                    db.collection("users").document(it)
                                        .set(mapOf("name" to name, "role" to role),
                                            com.google.firebase.firestore.SetOptions.merge())
                                        .addOnSuccessListener { isSaving = false; saveMessage = "✅ Profile updated!"; isEditing = false }
                                        .addOnFailureListener { e -> isSaving = false; saveMessage = "❌ ${e.message}" }
                                }
                            },
                            enabled = !isSaving && name.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2C7DA0), contentColor = Color.White,
                                disabledContainerColor = Color(0xFF2C7DA0).copy(alpha = 0.4f))
                        ) {
                            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            else Text("Update Profile", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Stats Row ─────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(value = lostCount.toString(),    label = "Lost Items",    icon = "⚠️", modifier = Modifier.weight(1f))
                StatCard(value = foundCount.toString(),   label = "Found Items",   icon = "✅", modifier = Modifier.weight(1f))
                StatCard(value = reportsCount.toString(), label = "Total Reports", icon = "📊", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Personal Information ───────────────────────────────────────
            InfoSectionCard(title = "Personal Information", icon = "👤") {
                if (!isEditing) {
                    InfoRow(icon = "📧", label = "Email Address", value = email.ifEmpty { "Not set" })
                    InfoRow(icon = "📞", label = "Contact Number", value = phone.ifEmpty { "Not set" })
                    InfoRow(icon = "🎓", label = "Student Email", value = studentId.ifEmpty { "Not set" })
                } else {
                    OutlinedTextField(
                        value = email, onValueChange = { }, enabled = false,
                        label = { Text("Email Address") },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color(0xFFE2E8F0), disabledTextColor = Color(0xFF8A99B4),
                            disabledLabelColor = Color(0xFF8A99B4)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it },
                        label = { Text("Contact Number") }, placeholder = { Text("+256 700 000000") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = studentId, onValueChange = { }, enabled = false,
                        label = { Text("Student Email") },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color(0xFFE2E8F0), disabledTextColor = Color(0xFF8A99B4),
                            disabledLabelColor = Color(0xFF8A99B4)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (savePersonalMessage.isNotEmpty()) {
                        Text(text = savePersonalMessage,
                            color = if (savePersonalMessage.startsWith("✅")) Color(0xFF2C7DA0) else Color.Red,
                            fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Button(
                        onClick = {
                            isSavingPersonal = true; savePersonalMessage = ""
                            userId?.let {
                                db.collection("users").document(it)
                                    .set(mapOf("phone" to phone), com.google.firebase.firestore.SetOptions.merge())
                                    .addOnSuccessListener { isSavingPersonal = false; savePersonalMessage = "✅ Contact info saved!" }
                                    .addOnFailureListener { e -> isSavingPersonal = false; savePersonalMessage = "❌ ${e.message}" }
                            }
                        },
                        enabled = !isSavingPersonal,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C7DA0), contentColor = Color.White,
                            disabledContainerColor = Color(0xFF2C7DA0).copy(alpha = 0.4f))
                    ) {
                        if (isSavingPersonal) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Update Contact Info", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Academic Information ───────────────────────────────────────
            InfoSectionCard(title = "Academic Information", icon = "📚") {
                if (!isEditing) {
                    InfoRow(icon = "🏛️", label = "College",         value = college.ifEmpty { "Not set" })
                    InfoRow(icon = "🏫", label = "School",          value = school.ifEmpty { "Not set" })
                    InfoRow(icon = "📂", label = "Department",      value = department.ifEmpty { "Not set" })
                    InfoRow(icon = "📖", label = "Year / Semester", value = if (year.isEmpty() && semester.isEmpty()) "Not set" else "$year • $semester")
                } else {
                    OutlinedTextField(value = college, onValueChange = { college = it },
                        label = { Text("College") }, placeholder = { Text("e.g. College of Engineering") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = school, onValueChange = { school = it },
                        label = { Text("School") }, placeholder = { Text("e.g. School of Computing") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = department, onValueChange = { department = it },
                        label = { Text("Department") }, placeholder = { Text("e.g. Computer Science") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = year, onValueChange = { year = it },
                            label = { Text("Year") }, placeholder = { Text("e.g. 3rd") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f))
                        OutlinedTextField(value = semester, onValueChange = { semester = it },
                            label = { Text("Semester") }, placeholder = { Text("e.g. 2") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2C7DA0), unfocusedBorderColor = Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (saveAcademicMessage.isNotEmpty()) {
                        Text(text = saveAcademicMessage,
                            color = if (saveAcademicMessage.startsWith("✅")) Color(0xFF2C7DA0) else Color.Red,
                            fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Button(
                        onClick = {
                            isSavingAcademic = true; saveAcademicMessage = ""
                            userId?.let {
                                db.collection("users").document(it)
                                    .set(mapOf("college" to college, "school" to school,
                                        "department" to department, "year" to year, "semester" to semester),
                                        com.google.firebase.firestore.SetOptions.merge())
                                    .addOnSuccessListener { isSavingAcademic = false; saveAcademicMessage = "✅ Academic info saved!" }
                                    .addOnFailureListener { e -> isSavingAcademic = false; saveAcademicMessage = "❌ ${e.message}" }
                            }
                        },
                        enabled = !isSavingAcademic,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C7DA0), contentColor = Color.White,
                            disabledContainerColor = Color(0xFF2C7DA0).copy(alpha = 0.4f))
                    ) {
                        if (isSavingAcademic) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Update Academic Info", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Account Settings ──────────────────────────────────────────
            InfoSectionCard(title = "Account Settings", icon = "⚙️") {
                SettingsRow(icon = "🔔", title = "Notification Preferences", subtitle = "Email & Push Notifications")
                HorizontalDivider(color = Color(0xFFF0F2F5), modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow(icon = "🔒", title = "Privacy & Security", subtitle = "Two-factor authentication off")
                HorizontalDivider(color = Color(0xFFF0F2F5), modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow(icon = "🌙", title = "Dark Mode", subtitle = "System default")
            }

            // ── Cancel button (edit mode only) ────────────────────────────
            if (isEditing) {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { isEditing = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5B6E8C)),
                    shape = RoundedCornerShape(44.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) { Text("Cancel", fontSize = 15.sp) }
            }
        }
    }
}

// ─── Reusable Composables ─────────────────────────────────────────────────────
@Composable
fun StatCard(value: String, label: String, icon: String, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C7DA0))
            Text(text = label, fontSize = 11.sp, color = Color(0xFF5B6E8C), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun InfoSectionCard(title: String, icon: String, content: @Composable () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                Text(text = icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F3B5C))
            }
            content()
        }
    }
}

@Composable
fun InfoRow(icon: String, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 18.sp, modifier = Modifier.width(32.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF8A99B4), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 15.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SettingsRow(icon: String, title: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 20.sp, modifier = Modifier.width(36.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
            Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF8A99B4))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null,
            tint = Color(0xFFCBD5E1), modifier = Modifier.size(20.dp))
    }
}