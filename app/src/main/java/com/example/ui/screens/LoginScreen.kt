package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppViewModel

@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val isDark = viewModel.darkThemeOverride

    val bgGradient = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF04120D), Color(0xFF0D1E19), Color(0xFF060A08))
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Color(0xFFE8F5EE), Color(0xFFF2FAF6), Color(0xFFD6EDE0))
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
            .systemBarsPadding()
    ) {
        // Language Toggle floating in the upper right
        IconButton(
            onClick = { viewModel.toggleLanguage() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    RoundedCornerShape(50)
                )
                .testTag("lang_toggle")
        ) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "Language",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App Brand Display
            Card(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(26.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.cholobd_logo),
                        contentDescription = "CholoBD Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "CholoBD",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = viewModel.translate("tagline"),
                fontSize = 14.sp,
                color = if (isDark) Color(0xFFA0B5A7) else Color(0xFF557761),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login / Signup Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Sign In or Switch
                    Text(
                        text = if (isRegisterMode) viewModel.translate("sign_up") else viewModel.translate("sign_in"),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (isRegisterMode) {
                        // Full Name input
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(viewModel.translate("name")) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .testTag("name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(viewModel.translate("email")) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Phone input
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(viewModel.translate("phone")) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("phone_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(viewModel.translate("password")) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (errorMessage.isNotBlank()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Authenticate Exec Button
                    Button(
                        onClick = {
                            val formName = if (isRegisterMode) name else "Subornbho traveler"
                            if (email.isBlank() || phone.isBlank() || password.isBlank() || (isRegisterMode && name.isBlank())) {
                                errorMessage = if (viewModel.isBangla) "অনুগ্রহ করে সব তথ্য পূরণ করুন" else "Please complete all fields"
                            } else {
                                val success = viewModel.loginUser(formName, email, phone)
                                if (!success) {
                                    errorMessage = if (viewModel.isBangla) "ভুল ডেটা ইনপুট।" else "Invalid credentials."
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isRegisterMode) viewModel.translate("sign_up") else viewModel.translate("sign_in"),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Toggle mode
                    TextButton(
                        onClick = {
                            isRegisterMode = !isRegisterMode
                            errorMessage = ""
                        }
                    ) {
                        Text(
                            text = if (isRegisterMode) {
                                if (viewModel.isBangla) "ইতিমধ্যেই অ্যাকাউন্ট আছে? সাইন ইন" else "Already have an account? Sign In"
                            } else {
                                if (viewModel.isBangla) "কোন অ্যাকাউন্ট নেই? নিবন্ধন করুন" else "Don't have an account? Register"
                            },
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Quick bypass mock indicator
            TextButton(
                onClick = {
                    viewModel.loginUser("Ashraful Islam", "ashraf@cholobd.bd", "+8801700998877")
                },
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    text = if (viewModel.isBangla) "অজ্ঞাতনামা ডেমো প্রবেশ (Bypass)" else "Skip Auth / Demo Quick Logon ➔",
                    color = if (isDark) Color(0xFFC4ECE0) else MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
