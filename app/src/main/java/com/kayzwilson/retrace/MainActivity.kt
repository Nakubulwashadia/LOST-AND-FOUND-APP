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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.lazy.items
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
// Add REPORT_FOUND to your Screen enum
enum class Screen { SPLASH, LOGIN, SIGNUP, LOST, FOUND, ACCOUNT, REPORT, REPORT_FOUND }
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
            onBack = { currentScreen = Screen.LOST },
            onSubmit = { currentScreen = Screen.LOST }
        )

        Screen.REPORT_FOUND -> ReportFoundItemScreen(
            onBack   = { currentScreen = Screen.FOUND },
            onSubmit = { currentScreen = Screen.FOUND }
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
// ─── Data class for Firestore items ──────────────────────────────────────────
data class LostItemData(
    val id: String = "",
    val itemName: String = "",
    val category: String = "",
    val college: String = "",
    val location: String = "",
    val timeLost: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val reportedBy: String = "",
    val timestamp: Long = 0L,
    val status: String = "lost"
)

// ─── Category → Emoji avatar mapper ──────────────────────────────────────────
fun categoryEmoji(category: String): String = when {
    category.contains("Computer", ignoreCase = true) ||
            category.contains("Laptop", ignoreCase = true)   -> "💻"
    category.contains("Phone", ignoreCase = true)     -> "📱"
    category.contains("Earphone", ignoreCase = true) ||
            category.contains("Headphone", ignoreCase = true) -> "🎧"
    category.contains("ID", ignoreCase = true) ||
            category.contains("Card", ignoreCase = true)      -> "🪪"
    category.contains("Bag", ignoreCase = true) ||
            category.contains("Backpack", ignoreCase = true)  -> "🎒"
    category.contains("Book", ignoreCase = true) ||
            category.contains("Notes", ignoreCase = true)     -> "📚"
    category.contains("Glass", ignoreCase = true) ||
            category.contains("Spectacle", ignoreCase = true) -> "👓"
    category.contains("Key", ignoreCase = true)       -> "🔑"
    category.contains("Wallet", ignoreCase = true)    -> "💳"
    category.contains("Cloth", ignoreCase = true)     -> "🧥"
    category.contains("Watch", ignoreCase = true) ||
            category.contains("Jewel", ignoreCase = true)     -> "⌚"
    category.contains("Stationery", ignoreCase = true)-> "🖊️"
    else -> "📦"
}

// ─── Lost Screen ─────────────────────────────────────────────────────────────
@Composable
fun LostScreen(
    onNavigateToFound: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var items by remember { mutableStateOf<List<LostItemData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val filters = listOf("All", "Electronics", "Bags", "Books", "ID Cards", "Keys", "Other")

    // ── Real-time Firestore listener ──────────────────────────────────────
    DisposableEffect(Unit) {
        val listener = db.collection("lost_items")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                isLoading = false
                if (error != null) {
                    errorMessage = "Failed to load items: ${error.message}"
                    return@addSnapshotListener
                }
                items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(LostItemData::class.java)?.copy(id = doc.id)
                } ?: emptyList()
            }
        onDispose { listener.remove() }
    }

    // ── Filter + search logic ─────────────────────────────────────────────
    val filteredItems = items.filter { item ->
        val matchesSearch = search.isBlank() ||
                item.itemName.contains(search, ignoreCase = true) ||
                item.description.contains(search, ignoreCase = true) ||
                item.location.contains(search, ignoreCase = true) ||
                item.category.contains(search, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "All"         -> true
            "Electronics" -> item.category.contains("Computer", ignoreCase = true) ||
                    item.category.contains("Laptop", ignoreCase = true) ||
                    item.category.contains("Phone", ignoreCase = true) ||
                    item.category.contains("Earphone", ignoreCase = true) ||
                    item.category.contains("Headphone", ignoreCase = true)
            "Bags"        -> item.category.contains("Bag", ignoreCase = true) ||
                    item.category.contains("Backpack", ignoreCase = true)
            "Books"       -> item.category.contains("Book", ignoreCase = true) ||
                    item.category.contains("Notes", ignoreCase = true) ||
                    item.category.contains("Stationery", ignoreCase = true)
            "ID Cards"    -> item.category.contains("ID", ignoreCase = true) ||
                    item.category.contains("Card", ignoreCase = true)
            "Keys"        -> item.category.contains("Key", ignoreCase = true)
            else          -> true
        }
        matchesSearch && matchesFilter
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F4FA))) {

        // ── Gradient header ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F3B5C), Color(0xFF1A5C8A))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Lost Items",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            "${items.size} item${if (items.size != 1) "s" else ""} reported",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                    // Report button (top-right)
                    Button(
                        onClick = onNavigateToReport,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B8FF9)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("+ Report", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search bar inside header
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search lost items...", color = Color.White.copy(alpha = 0.5f)) },
                    leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 4.dp)) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                        focusedBorderColor = Color(0xFF5B8FF9),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        // ── Filter chips ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isActive = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isActive)
                                Brush.linearGradient(listOf(Color(0xFF1A5C8A), Color(0xFF2C7DA0)))
                            else
                                Brush.linearGradient(listOf(Color(0xFFEEF4FB), Color(0xFFEEF4FB)))
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        filter,
                        color = if (isActive) Color.White else Color(0xFF1A5C8A),
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // ── Body ──────────────────────────────────────────────────────────
        when {
            isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF2C7DA0), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading lost items...", fontSize = 14.sp, color = Color(0xFF5B6E8C))
                    }
                }
            }

            errorMessage.isNotEmpty() -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text("⚠️", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage, fontSize = 14.sp, color = Color(0xFFE53935), textAlign = TextAlign.Center)
                    }
                }
            }

            filteredItems.isEmpty() -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text(if (search.isNotBlank()) "🔍" else "📭", fontSize = 52.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            if (search.isNotBlank()) "No results for \"$search\""
                            else "No lost items yet",
                            fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F3B5C),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            if (search.isNotBlank()) "Try a different search term"
                            else "Be the first to report a lost item",
                            fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center
                        )
                        if (search.isBlank()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onNavigateToReport,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C7DA0))
                            ) { Text("Report Lost Item", color = Color.White, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredItems.size) { index ->
                        LostItemCard(item = filteredItems[index])
                    }
                }
            }
        }

        BottomNavBar(
            activeTab           = NavTab.LOST,
            onNavigateToLost    = { },
            onNavigateToFound   = onNavigateToFound,
            onNavigateToAccount = onNavigateToAccount
        )
    }
}

// ─── Lost Item Card (dynamic) ─────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemCard(item: LostItemData) {
    val emoji = categoryEmoji(item.category)
    val formattedTime = remember(item.timestamp) {
        if (item.timestamp > 0) {
            val sdf = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
            sdf.format(java.util.Date(item.timestamp))
        } else "Unknown date"
    }

    // ── Contact sheet state ───────────────────────────────────────────────
    var showContactSheet   by remember { mutableStateOf(false) }
    var contactName        by remember { mutableStateOf("") }
    var contactEmail       by remember { mutableStateOf("") }
    var contactPhone       by remember { mutableStateOf("") }
    var contactStudentId   by remember { mutableStateOf("") }
    var contactRole        by remember { mutableStateOf("") }
    var isLoadingContact   by remember { mutableStateOf(false) }
    var contactError       by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ── Bottom Sheet ──────────────────────────────────────────────────────
    if (showContactSheet) {
        ModalBottomSheet(
            onDismissRequest = { showContactSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF1A5C8A), Color(0xFF2C7DA0))),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contactName.split(" ").filter { it.isNotEmpty() }.take(2)
                            .joinToString("") { it.first().uppercase() }
                            .ifEmpty { "?" },
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = contactName.ifEmpty { "Unknown User" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F3B5C)
                )

                if (contactRole.isNotEmpty()) {
                    Text(
                        text = contactRole,
                        fontSize = 13.sp,
                        color = Color(0xFF5B6E8C)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Item context pill
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEF4FB), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Owner of: ${item.itemName}",
                        fontSize = 12.sp,
                        color = Color(0xFF1A5C8A),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                when {
                    isLoadingContact -> {
                        CircularProgressIndicator(color = Color(0xFF2C7DA0), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Fetching contact details...", fontSize = 13.sp, color = Color(0xFF5B6E8C))
                    }

                    contactError.isNotEmpty() -> {
                        Text("⚠️", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(contactError, fontSize = 13.sp, color = Color(0xFFE53935), textAlign = TextAlign.Center)
                    }

                    else -> {
                        // Contact detail cards
                        if (contactEmail.isNotEmpty()) {
                            ContactDetailRow(
                                icon = "📧",
                                label = "Email Address",
                                value = contactEmail,
                                actionLabel = "Send Email",
                                actionColor = Color(0xFF2C7DA0)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (contactPhone.isNotEmpty()) {
                            ContactDetailRow(
                                icon = "📞",
                                label = "Phone Number",
                                value = contactPhone,
                                actionLabel = "Call",
                                actionColor = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (contactStudentId.isNotEmpty()) {
                            ContactDetailRow(
                                icon = "🎓",
                                label = "Student Email",
                                value = contactStudentId,
                                actionLabel = null,
                                actionColor = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (contactEmail.isEmpty() && contactPhone.isEmpty()) {
                            Text(
                                "⚠️ This user hasn't added contact details yet.",
                                fontSize = 13.sp,
                                color = Color(0xFF5B6E8C),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Close button
                OutlinedButton(
                    onClick = { showContactSheet = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, Color(0xFFDDE3F0)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5B6E8C))
                ) {
                    Text("Close", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // ── Image / avatar section ────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                if (item.imageUrl.isNotEmpty()) {
                    androidx.compose.foundation.Image(
                        painter = rememberAsyncImagePainter(item.imageUrl),
                        contentDescription = item.itemName,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(colors = listOf(Color(0xFFDCEAF7), Color(0xFFEEF4FB)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 72.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                item.category.substringAfter(" ").ifEmpty { item.category },
                                fontSize = 12.sp, color = Color(0xFF5B6E8C), fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(Color(0xFFE53935), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("LOST", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
            }

            // ── Details ───────────────────────────────────────────────────
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    item.itemName.ifEmpty { "Unnamed Item" },
                    fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF0F3B5C)
                )
                if (item.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        item.description, fontSize = 13.sp, color = Color(0xFF5B6E8C),
                        maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (item.location.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📍", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(item.location, fontSize = 12.sp, color = Color(0xFF5B6E8C),
                                maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 110.dp))
                        }
                    }
                    if (item.timeLost.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏰", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(item.timeLost, fontSize = 12.sp, color = Color(0xFF5B6E8C))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📅", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(formattedTime, fontSize = 12.sp, color = Color(0xFF5B6E8C))
                }
                if (item.college.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏛️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(item.college, fontSize = 12.sp, color = Color(0xFF5B6E8C),
                            maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        showContactSheet = true
                        // Only fetch if not already loaded
                        if (contactName.isEmpty() && contactError.isEmpty()) {
                            isLoadingContact = true
                            contactError = ""
                            if (item.reportedBy.isNullOrEmpty()) {
                                isLoadingContact = false
                                contactError = "No owner information linked to this item."
                            } else {
                                db.collection("users").document(item.reportedBy)
                                    .get()
                                    .addOnSuccessListener { doc ->
                                        isLoadingContact = false
                                        if (doc.exists()) {
                                            contactName      = doc.getString("name")      ?: ""
                                            contactEmail     = doc.getString("email")     ?: ""
                                            contactPhone     = doc.getString("phone")     ?: ""
                                            contactStudentId = doc.getString("studentId") ?: ""
                                            contactRole      = doc.getString("role")      ?: ""
                                        } else {
                                            contactError = "Owner profile not found."
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        isLoadingContact = false
                                        contactError = "Failed to load contact: ${e.message}"
                                    }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F3B5C),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text("Contact Owner", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Contact Detail Row ────────────────────────────────────────────────────────
@Composable
fun ContactDetailRow(
    icon: String,
    label: String,
    value: String,
    actionLabel: String?,
    actionColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 20.sp, modifier = Modifier.width(36.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, color = Color(0xFF8A99B4), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, color = Color(0xFF0F3B5C), fontWeight = FontWeight.SemiBold)
        }
        if (actionLabel != null) {
            Box(
                modifier = Modifier
                    .background(actionColor.copy(alpha = 0.1f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(actionLabel, fontSize = 11.sp, color = actionColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportLostItemScreen(
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    var itemName     by remember { mutableStateOf("") }
    var category     by remember { mutableStateOf("") }
    var college      by remember { mutableStateOf("") }
    var location     by remember { mutableStateOf("") }
    var time         by remember { mutableStateOf("") }
    var description  by remember { mutableStateOf("") }
    var imageUri     by remember { mutableStateOf<android.net.Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitError  by remember { mutableStateOf("") }

    // ── Category dropdown state ──────────────────────────────────────────
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf(
        "💻 Computer / Laptop",
        "📱 Phone",
        "🎧 Earphones / Headphones",
        "🪪 ID Card / Student Card",
        "🎒 Bag / Backpack",
        "📚 Books / Notes",
        "👓 Glasses / Spectacles",
        "🔑 Keys",
        "💳 Wallet / Cards",
        "🧥 Clothing",
        "⌚ Watch / Jewelry",
        "🖊️ Stationery",
        "Other"
    )

    // ── Time picker state ────────────────────────────────────────────────
    var showTimePicker   by remember { mutableStateOf(false) }
    val timePickerState  = rememberTimePickerState(is24Hour = false)

    // ── Image picker ─────────────────────────────────────────────────────
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    val canSubmit = itemName.isNotBlank() && category.isNotBlank() &&
            location.isNotBlank() && time.isNotBlank()

    // ── Time picker dialog ───────────────────────────────────────────────
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour   = timePickerState.hour
                    val minute = timePickerState.minute
                    val amPm   = if (hour < 12) "AM" else "PM"
                    val h      = if (hour % 12 == 0) 12 else hour % 12
                    time = "%d:%02d %s".format(h, minute, amPm)
                    showTimePicker = false
                }) { Text("OK", color = Color(0xFF2C7DA0)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            title = { Text("Select Time Lost", fontWeight = FontWeight.SemiBold) },
            text  = { TimePicker(state = timePickerState) }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Text("Report Lost Item", fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold, color = Color(0xFF0F3B5C))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back", tint = Color(0xFF2C7DA0))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                .padding(bottom = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // ── Image Upload Card ────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                        Text("📷", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Item Photo", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF0F3B5C))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("(Optional)", fontSize = 12.sp, color = Color.Gray)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF0F4FF))
                            .border(2.dp, Color(0xFFDDE3F0), RoundedCornerShape(14.dp))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            androidx.compose.foundation.Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Selected image",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))
                            )
                            // Overlay to re-pick
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Tap to change", color = Color.White,
                                    fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📁", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Tap to upload a photo", fontSize = 14.sp,
                                    color = Color(0xFF2C7DA0), fontWeight = FontWeight.Medium)
                                Text("JPG, PNG supported", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Item Details Card ────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("📋", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Item Details", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF0F3B5C))
                    }

                    // Item Name
                    OutlinedTextField(
                        value = itemName, onValueChange = { itemName = it },
                        label = { Text("Item Name *") },
                        placeholder = { Text("e.g. Black Dell Laptop") },
                        leadingIcon = { Text("🏷️", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2C7DA0),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category *") },
                            placeholder = { Text("Select a category") },
                            leadingIcon = { Text("🗂️", modifier = Modifier.padding(start = 4.dp)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C7DA0),
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 14.sp) },
                                    onClick = { category = option; categoryExpanded = false }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Describe the item — color, brand, markings...") },
                        leadingIcon = { Text("📝", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2C7DA0),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Location & Time Card ─────────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("📍", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Where & When", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF0F3B5C))
                    }

                    // College / Area
                    OutlinedTextField(
                        value = college, onValueChange = { college = it },
                        label = { Text("College / Area") },
                        placeholder = { Text("e.g. College of Engineering") },
                        leadingIcon = { Text("🏛️", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2C7DA0),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Specific Location
                    OutlinedTextField(
                        value = location, onValueChange = { location = it },
                        label = { Text("Specific Location *") },
                        placeholder = { Text("e.g. Library, Room 204, Cafeteria") },
                        leadingIcon = { Text("📌", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2C7DA0),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Time Lost — tapping opens time picker
                    OutlinedTextField(
                        value = time,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Time Lost *") },
                        placeholder = { Text("Tap to select time") },
                        leadingIcon = { Text("⏰", modifier = Modifier.padding(start = 4.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Pick time",
                                    tint = Color(0xFF2C7DA0), modifier = Modifier.size(18.dp))
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2C7DA0),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Error message ────────────────────────────────────────────
            if (submitError.isNotEmpty()) {
                Text(
                    text = submitError,
                    color = Color(0xFFE53935),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // ── Submit Button ────────────────────────────────────────────
            Button(
                onClick = {
                    isSubmitting = true
                    submitError  = ""
                    val db  = FirebaseFirestore.getInstance()
                    val uid = FirebaseAuth.getInstance().currentUser?.uid

                    val lostItem = hashMapOf(
                        "itemName"    to itemName,
                        "category"   to category,
                        "college"    to college,
                        "location"   to location,
                        "timeLost"   to time,
                        "description" to description,
                        "imageUrl"   to (imageUri?.toString() ?: ""),
                        "reportedBy" to uid,
                        "timestamp"  to System.currentTimeMillis(),
                        "status"     to "lost"
                    )

                    db.collection("lost_items")
                        .add(lostItem)
                        .addOnSuccessListener {
                            isSubmitting = false
                            onSubmit()
                        }
                        .addOnFailureListener { e ->
                            isSubmitting = false
                            submitError = "❌ Failed to submit: ${e.message}"
                        }
                },
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C7DA0),
                    contentColor   = Color.White,
                    disabledContainerColor = Color(0xFF2C7DA0).copy(alpha = 0.4f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(22.dp),
                        color     = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text("Submit Lost Report", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "* Required fields",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}// ─── Found Screen ─────────────────────────────────────────────────────────────

// ─── Found Item Data Class ────────────────────────────────────────────────────
data class FoundItemData(
    val id: String = "",
    val itemName: String = "",
    val category: String = "",
    val college: String = "",
    val location: String = "",
    val timeFound: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val reportedBy: String = "",
    val timestamp: Long = 0L,
    val status: String = "found"
)
// ─── Found Screen ─────────────────────────────────────────────────────────────
@Composable
fun FoundScreen(onNavigateToLost: () -> Unit, onNavigateToAccount: () -> Unit, onNavigateToReport: () -> Unit) {
    var search by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var items by remember { mutableStateOf<List<FoundItemData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val filters = listOf("All", "Electronics", "Bags", "Books", "ID Cards", "Keys", "Other")

    DisposableEffect(Unit) {
        val listener = db.collection("found_items")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                isLoading = false
                if (error != null) { errorMessage = "Failed to load items: ${error.message}"; return@addSnapshotListener }
                items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FoundItemData::class.java)?.copy(id = doc.id)
                } ?: emptyList()
            }
        onDispose { listener.remove() }
    }

    val filteredItems = items.filter { item ->
        val matchesSearch = search.isBlank() ||
                item.itemName.contains(search, ignoreCase = true) ||
                item.description.contains(search, ignoreCase = true) ||
                item.location.contains(search, ignoreCase = true) ||
                item.category.contains(search, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "All"         -> true
            "Electronics" -> item.category.contains("Computer", ignoreCase = true) ||
                    item.category.contains("Laptop", ignoreCase = true) ||
                    item.category.contains("Phone", ignoreCase = true) ||
                    item.category.contains("Earphone", ignoreCase = true) ||
                    item.category.contains("Headphone", ignoreCase = true)
            "Bags"        -> item.category.contains("Bag", ignoreCase = true) || item.category.contains("Backpack", ignoreCase = true)
            "Books"       -> item.category.contains("Book", ignoreCase = true) || item.category.contains("Notes", ignoreCase = true)
            "ID Cards"    -> item.category.contains("ID", ignoreCase = true) || item.category.contains("Card", ignoreCase = true)
            "Keys"        -> item.category.contains("Key", ignoreCase = true)
            else          -> true
        }
        matchesSearch && matchesFilter
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0FAF4))) {

        // ── Gradient Header ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF0A3D2B), Color(0xFF1A6B47))))
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Found Items", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold,
                            color = Color.White, letterSpacing = 0.5.sp)
                        Text("${items.size} item${if (items.size != 1) "s" else ""} found",
                            fontSize = 13.sp, color = Color.White.copy(alpha = 0.65f))
                    }
                    Button(
                        onClick = onNavigateToReport,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF82)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("+ Found", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = search, onValueChange = { search = it },
                    placeholder = { Text("Search found items...", color = Color.White.copy(alpha = 0.5f)) },
                    leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 4.dp)) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                        focusedBorderColor = Color(0xFF4CAF82),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        // ── Filter Chips ──────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White)
                .horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isActive = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isActive) Brush.linearGradient(listOf(Color(0xFF0A3D2B), Color(0xFF1A6B47)))
                            else Brush.linearGradient(listOf(Color(0xFFECF8F2), Color(0xFFECF8F2)))
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(filter,
                        color = if (isActive) Color.White else Color(0xFF0A3D2B),
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        // ── Body ──────────────────────────────────────────────────────────
        when {
            isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF1A6B47), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading found items...", fontSize = 14.sp, color = Color(0xFF5B6E8C))
                    }
                }
            }
            errorMessage.isNotEmpty() -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text("⚠️", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage, fontSize = 14.sp, color = Color(0xFFE53935), textAlign = TextAlign.Center)
                    }
                }
            }
            filteredItems.isEmpty() -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text(if (search.isNotBlank()) "🔍" else "📭", fontSize = 52.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            if (search.isNotBlank()) "No results for \"$search\"" else "No found items yet",
                            fontSize = 17.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A3D2B), textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            if (search.isNotBlank()) "Try a different search term"
                            else "Found something? Post it here!",
                            fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center
                        )
                        if (search.isBlank()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onNavigateToReport,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A6B47))
                            ) { Text("Report Found Item", color = Color.White, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredItems) { item -> FoundItemCard(item = item) }
                }
            }
        }

        BottomNavBar(
            activeTab = NavTab.FOUND,
            onNavigateToLost = onNavigateToLost,
            onNavigateToFound = { },
            onNavigateToAccount = { }
        )
    }
}

// ─── Report Found Item Screen ─────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundItemScreen(onSubmit: () -> Unit, onBack: () -> Unit) {
    var itemName     by remember { mutableStateOf("") }
    var category     by remember { mutableStateOf("") }
    var college      by remember { mutableStateOf("") }
    var location     by remember { mutableStateOf("") }
    var time         by remember { mutableStateOf("") }
    var description  by remember { mutableStateOf("") }
    var imageUri     by remember { mutableStateOf<android.net.Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitError  by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showTimePicker   by remember { mutableStateOf(false) }
    val timePickerState  = rememberTimePickerState(is24Hour = false)

    val categories = listOf(
        "💻 Computer / Laptop", "📱 Phone", "🎧 Earphones / Headphones",
        "🪪 ID Card / Student Card", "🎒 Bag / Backpack", "📚 Books / Notes",
        "👓 Glasses / Spectacles", "🔑 Keys", "💳 Wallet / Cards",
        "🧥 Clothing", "⌚ Watch / Jewelry", "🖊️ Stationery", "Other"
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    // Image is mandatory here
    val canSubmit = itemName.isNotBlank() && category.isNotBlank() &&
            location.isNotBlank() && time.isNotBlank() && imageUri != null

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    val amPm = if (hour < 12) "AM" else "PM"
                    val h = if (hour % 12 == 0) 12 else hour % 12
                    time = "%d:%02d %s".format(h, minute, amPm)
                    showTimePicker = false
                }) { Text("OK", color = Color(0xFF1A6B47)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            title = { Text("Select Time Found", fontWeight = FontWeight.SemiBold) },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF0FAF4),
        topBar = {
            TopAppBar(
                title = { Text("Report Found Item", fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold, color = Color(0xFF0A3D2B)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back", tint = Color(0xFF1A6B47))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF0FAF4))
                .verticalScroll(rememberScrollState())
                .padding(paddingValues).padding(horizontal = 16.dp).padding(bottom = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // ── Image Upload Card (Mandatory) ─────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                        Text("📷", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Item Photo", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF0A3D2B))
                        Spacer(modifier = Modifier.width(6.dp))
                        // Mandatory badge
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Required", fontSize = 11.sp,
                                color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (imageUri == null) Color(0xFFF0FAF4) else Color.Transparent)
                            .border(
                                2.dp,
                                if (imageUri == null) Color(0xFF1A6B47).copy(alpha = 0.4f) else Color(0xFF1A6B47),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            androidx.compose.foundation.Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Selected image",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))
                            )
                            Box(
                                modifier = Modifier.fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Tap to change", color = Color.White,
                                    fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📸", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Tap to upload a photo", fontSize = 14.sp,
                                    color = Color(0xFF1A6B47), fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("A photo is required to help identify the item",
                                    fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Item Details Card ─────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("📋", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Item Details", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF0A3D2B))
                    }
                    OutlinedTextField(
                        value = itemName, onValueChange = { itemName = it },
                        label = { Text("Item Name *") },
                        placeholder = { Text("e.g. Black Dell Laptop") },
                        leadingIcon = { Text("🏷️", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category, onValueChange = {}, readOnly = true,
                            label = { Text("Category *") },
                            placeholder = { Text("Select a category") },
                            leadingIcon = { Text("🗂️", modifier = Modifier.padding(start = 4.dp)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 14.sp) },
                                    onClick = { category = option; categoryExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Describe the item — color, brand, markings...") },
                        leadingIcon = { Text("📝", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3, maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Location & Time Card ──────────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("📍", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Where & When", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF0A3D2B))
                    }
                    OutlinedTextField(
                        value = college, onValueChange = { college = it },
                        label = { Text("College / Area") },
                        placeholder = { Text("e.g. College of Engineering") },
                        leadingIcon = { Text("🏛️", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = location, onValueChange = { location = it },
                        label = { Text("Specific Location *") },
                        placeholder = { Text("e.g. Library, Room 204, Cafeteria") },
                        leadingIcon = { Text("📌", modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = time, onValueChange = {}, readOnly = true,
                        label = { Text("Time Found *") },
                        placeholder = { Text("Tap to select time") },
                        leadingIcon = { Text("⏰", modifier = Modifier.padding(start = 4.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Pick time",
                                    tint = Color(0xFF1A6B47), modifier = Modifier.size(18.dp))
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A6B47), unfocusedBorderColor = Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (submitError.isNotEmpty()) {
                Text(text = submitError, color = Color(0xFFE53935), fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp))
            }

            // ── Submit Button ─────────────────────────────────────────────
            Button(
                onClick = {
                    isSubmitting = true
                    submitError = ""
                    val db  = FirebaseFirestore.getInstance()
                    val uid = FirebaseAuth.getInstance().currentUser?.uid

                    val foundItem = hashMapOf(
                        "itemName"    to itemName,
                        "category"   to category,
                        "college"    to college,
                        "location"   to location,
                        "timeFound"  to time,
                        "description" to description,
                        "imageUrl"   to (imageUri?.toString() ?: ""),
                        "reportedBy" to uid,
                        "timestamp"  to System.currentTimeMillis(),
                        "status"     to "found"
                    )

                    db.collection("found_items")
                        .add(foundItem)
                        .addOnSuccessListener { isSubmitting = false; onSubmit() }
                        .addOnFailureListener { e ->
                            isSubmitting = false
                            submitError = "❌ Failed to submit: ${e.message}"
                        }
                },
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A6B47), contentColor = Color.White,
                    disabledContainerColor = Color(0xFF1A6B47).copy(alpha = 0.4f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp),
                        color = Color.White, strokeWidth = 2.5.dp)
                } else {
                    Text("Submit Found Report", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("* Required fields", fontSize = 11.sp, color = Color.Gray,
                modifier = Modifier.align(Alignment.End))
        }
    }
}

// ─── Found Item Card ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundItemCard(item: FoundItemData) {
    val emoji = categoryEmoji(item.category)
    val formattedTime = remember(item.timestamp) {
        if (item.timestamp > 0) {
            val sdf = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
            sdf.format(java.util.Date(item.timestamp))
        } else "Unknown date"
    }

    var showContactSheet by remember { mutableStateOf(false) }
    var contactName      by remember { mutableStateOf("") }
    var contactEmail     by remember { mutableStateOf("") }
    var contactPhone     by remember { mutableStateOf("") }
    var contactStudentId by remember { mutableStateOf("") }
    var contactRole      by remember { mutableStateOf("") }
    var isLoadingContact by remember { mutableStateOf(false) }
    var contactError     by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showContactSheet) {
        ModalBottomSheet(
            onDismissRequest = { showContactSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(64.dp)
                        .background(Brush.linearGradient(listOf(Color(0xFF0A3D2B), Color(0xFF1A6B47))), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contactName.split(" ").filter { it.isNotEmpty() }.take(2)
                            .joinToString("") { it.first().uppercase() }.ifEmpty { "?" },
                        color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(contactName.ifEmpty { "Unknown User" }, fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold, color = Color(0xFF0A3D2B))
                if (contactRole.isNotEmpty()) {
                    Text(contactRole, fontSize = 13.sp, color = Color(0xFF5B6E8C))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier.background(Color(0xFFECF8F2), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Found: ${item.itemName}", fontSize = 12.sp,
                        color = Color(0xFF1A6B47), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(24.dp))

                when {
                    isLoadingContact -> {
                        CircularProgressIndicator(color = Color(0xFF1A6B47), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Fetching contact details...", fontSize = 13.sp, color = Color(0xFF5B6E8C))
                    }
                    contactError.isNotEmpty() -> {
                        Text("⚠️", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(contactError, fontSize = 13.sp, color = Color(0xFFE53935), textAlign = TextAlign.Center)
                    }
                    else -> {
                        if (contactEmail.isNotEmpty()) {
                            ContactDetailRow(icon = "📧", label = "Email Address", value = contactEmail,
                                actionLabel = "Send Email", actionColor = Color(0xFF2C7DA0))
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        if (contactPhone.isNotEmpty()) {
                            ContactDetailRow(icon = "📞", label = "Phone Number", value = contactPhone,
                                actionLabel = "Call", actionColor = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        if (contactStudentId.isNotEmpty()) {
                            ContactDetailRow(icon = "🎓", label = "Student Email", value = contactStudentId,
                                actionLabel = null, actionColor = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        if (contactEmail.isEmpty() && contactPhone.isEmpty()) {
                            Text("⚠️ This user hasn't added contact details yet.",
                                fontSize = 13.sp, color = Color(0xFF5B6E8C), textAlign = TextAlign.Center)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedButton(
                    onClick = { showContactSheet = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, Color(0xFFDDE3F0)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5B6E8C))
                ) { Text("Close", fontWeight = FontWeight.SemiBold) }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                if (item.imageUrl.isNotEmpty()) {
                    androidx.compose.foundation.Image(
                        painter = rememberAsyncImagePainter(item.imageUrl),
                        contentDescription = item.itemName,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(Brush.linearGradient(colors = listOf(Color(0xFFD4EDE1), Color(0xFFECF8F2)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 72.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.category.substringAfter(" ").ifEmpty { item.category },
                                fontSize = 12.sp, color = Color(0xFF1A6B47), fontWeight = FontWeight.Medium)
                        }
                    }
                }
                // FOUND badge
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                        .background(Color(0xFF1A6B47), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("FOUND", color = Color.White, fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(item.itemName.ifEmpty { "Unnamed Item" },
                    fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF0A3D2B))
                if (item.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.description, fontSize = 13.sp, color = Color(0xFF5B6E8C),
                        maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (item.location.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📍", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(item.location, fontSize = 12.sp, color = Color(0xFF5B6E8C),
                                maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 110.dp))
                        }
                    }
                    if (item.timeFound.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏰", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(item.timeFound, fontSize = 12.sp, color = Color(0xFF5B6E8C))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📅", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(formattedTime, fontSize = 12.sp, color = Color(0xFF5B6E8C))
                }
                if (item.college.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏛️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(item.college, fontSize = 12.sp, color = Color(0xFF5B6E8C),
                            maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        showContactSheet = true
                        if (contactName.isEmpty() && contactError.isEmpty()) {
                            isLoadingContact = true
                            if (item.reportedBy.isEmpty()) {
                                isLoadingContact = false
                                contactError = "No finder information linked to this item."
                            } else {
                                db.collection("users").document(item.reportedBy).get()
                                    .addOnSuccessListener { doc ->
                                        isLoadingContact = false
                                        if (doc.exists()) {
                                            contactName      = doc.getString("name")      ?: ""
                                            contactEmail     = doc.getString("email")     ?: ""
                                            contactPhone     = doc.getString("phone")     ?: ""
                                            contactStudentId = doc.getString("studentId") ?: ""
                                            contactRole      = doc.getString("role")      ?: ""
                                        } else { contactError = "Finder profile not found." }
                                    }
                                    .addOnFailureListener { e ->
                                        isLoadingContact = false
                                        contactError = "Failed to load contact: ${e.message}"
                                    }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A3D2B), contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text("Contact Finder", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
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
                        Text(text = name.ifEmpty { "Tap avatar to set Profiles" },
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