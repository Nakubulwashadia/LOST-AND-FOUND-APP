package com.kayzwilson.retrace.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kayzwilson.retrace.R
import com.kayzwilson.retrace.ui.components.RetraceScreenHeader
import com.kayzwilson.retrace.ui.components.RetraceTextField
import com.kayzwilson.retrace.ui.theme.*



@Composable
fun SignUpScreen(onNavigateToLogin: () -> Unit, onSignUpSuccess: () -> Unit) {
    val focusManager = LocalFocusManager.current
    var studentId              by remember { mutableStateOf("") }
    var email                  by remember { mutableStateOf("") }
    var password               by remember { mutableStateOf("") }
    var confirmPassword        by remember { mutableStateOf("") }
    var passwordVisible        by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading              by remember { mutableStateOf(false) }

    val studentIdError       = studentId.isNotEmpty() && studentId.length < 4
    val emailError           = email.isNotEmpty() &&
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passwordError        = password.isNotEmpty() && password.length < 6
    val confirmPasswordError = confirmPassword.isNotEmpty() && confirmPassword != password
    val canSubmit = studentId.isNotEmpty() && email.isNotEmpty() &&
            password.isNotEmpty() && confirmPassword.isNotEmpty() &&
            !studentIdError && !emailError && !passwordError && !confirmPasswordError

    val auth = FirebaseAuth.getInstance()
    val db   = FirebaseFirestore.getInstance()

    // ← RetraceLightBg comes from Color.kt, NOT defined as null here
    Box(modifier = Modifier.fillMaxSize().background(RetraceLightBg)) {
        RetraceScreenHeader()  // ← imported from ui/components/RetraceScreenHeader.kt
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            Image(
                painter = painterResource(id = R.drawable.retrace_logo),
                contentDescription = "Retrace Logo",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "RETRACE", fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RetraceWhite, letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Find what's lost. Return what matters.",
                fontSize = 13.sp,
                color = RetraceWhite.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(36.dp))

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
                        "Create Account", fontSize = 22.sp,
                        fontWeight = FontWeight.Bold, color = RetraceNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Join Retrace on your campus",
                        fontSize = 14.sp, color = RetraceGrey
                    )
                    Spacer(modifier = Modifier.height(28.dp))

                    RetraceTextField(
                        value = studentId, onValueChange = { studentId = it },
                        label = "Student Email",
                        placeholder = "e.g. student@campus.ac.ug",
                        isError = studentIdError,
                        errorMessage = "Enter a valid student email",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Badge, null,
                                tint = if (studentIdError) RetraceError else RetraceMidBlue
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RetraceTextField(
                        value = email, onValueChange = { email = it },
                        label = "Email address",
                        placeholder = "you@campus.ac.ug",
                        isError = emailError,
                        errorMessage = "Enter a valid email",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email, null,
                                tint = if (emailError) RetraceError else RetraceMidBlue
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RetraceTextField(
                        value = password, onValueChange = { password = it },
                        label = "Password",
                        placeholder = "Min. 6 characters",
                        isError = passwordError,
                        errorMessage = "Password must be at least 6 characters",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock, null,
                                tint = if (passwordError) RetraceError else RetraceMidBlue
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide" else "Show",
                                    tint = RetraceGrey
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RetraceTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm password",
                        placeholder = "Re-enter your password",
                        isError = confirmPasswordError,
                        errorMessage = "Passwords do not match",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock, null,
                                tint = if (confirmPasswordError) RetraceError else RetraceMidBlue
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                            ) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide" else "Show",
                                    tint = RetraceGrey
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
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
                                        userId?.let {
                                            db.collection("users").document(it).set(userMap)
                                        }
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
                        if (isLoading)
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = RetraceWhite,
                                strokeWidth = 2.5.dp
                            )
                        else Text(
                            "Create Account", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToLogin() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFDDE3F0)
                        )
                        Text(
                            "  Already have an account? Sign In  ",
                            fontSize = 12.sp,
                            color = RetraceMidBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFDDE3F0)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}