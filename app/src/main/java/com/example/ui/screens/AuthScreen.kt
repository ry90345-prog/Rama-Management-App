package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ErpViewModel
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: ErpViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedRole by remember { mutableStateOf("Admin") } // Admin, Teacher, Student
    var emailOrMobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    // OTP Modal states
    var showOtpModal by remember { mutableStateOf(false) }
    var otpMobileNumber by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }

    val isLoggingIn by viewModel.isLoggingIn.collectAsState()

    // Default hints
    LaunchedEffect(selectedRole) {
        when (selectedRole) {
            "Admin" -> {
                emailOrMobile = "admin@rama.in"
                password = "admin123"
            }
            "Teacher" -> {
                emailOrMobile = "rajesh@rama.edu.in"
                password = "teacher123"
            }
            "Student" -> {
                emailOrMobile = "aman.verma@gmail.com"
                password = "student123"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Beautiful Custom Canvas Academic Emblem/Logo
            Canvas(modifier = Modifier.size(100.dp)) {
                val primaryColor = RoyalTealLight
                val secondaryColor = AmberAccentLight
                
                // Draw decorative shield base
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    radius = size.width / 1.5f,
                    center = center
                )
                
                // Draw academic emblem outline
                drawCircle(
                    color = primaryColor,
                    radius = size.width / 2.5f,
                    center = center,
                    style = Stroke(width = 4.dp.toPx())
                )
                
                // Inner gear/sun detail
                drawCircle(
                    color = secondaryColor,
                    radius = size.width / 4.5f,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
                
                // Draw stylized booklet cross lines inside
                drawLine(
                    color = primaryColor,
                    start = Offset(center.x - 15.dp.toPx(), center.y),
                    end = Offset(center.x + 15.dp.toPx(), center.y),
                    strokeWidth = 3.dp.toPx()
                )
                drawLine(
                    color = primaryColor,
                    start = Offset(center.x, center.y - 15.dp.toPx()),
                    end = Offset(center.x, center.y + 15.dp.toPx()),
                    strokeWidth = 3.dp.toPx()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "RAMA TECHNICAL INSTITUTE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Enterprise Resource Planning (ERP)",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Role Selection Tabs
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val roles = listOf("Admin", "Teacher", "Student")
                    roles.forEach { role ->
                        val isSelected = selectedRole == role
                        val tabBg = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        val tabContentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(tabBg, RoundedCornerShape(12.dp))
                                .clickable { selectedRole = role }
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Text(
                                text = role,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tabContentColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Fields
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Sign In as $selectedRole",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = emailOrMobile,
                        onValueChange = { emailOrMobile = it },
                        label = { Text("Email or Mobile Number") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = "Email or Phone") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "Password") },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Remember Me & Forgot Password Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it }
                            )
                            Text(
                                text = "Remember Login",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "Forgot Password?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.clickable {
                                otpMobileNumber = if (emailOrMobile.all { it.isDigit() }) emailOrMobile else "9876543210"
                                showOtpModal = true
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoggingIn) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (emailOrMobile.isEmpty() || password.isEmpty()) {
                                    Toast.makeText(context, "Please enter all fields.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.login(
                                    emailOrMobile = emailOrMobile,
                                    password = password,
                                    role = selectedRole,
                                    rememberMe = rememberMe,
                                    onSuccess = {
                                        Toast.makeText(context, "Welcome back, $selectedRole!", Toast.LENGTH_LONG).show()
                                        onLoginSuccess()
                                    },
                                    onError = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Secure Sign In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bottom helper notice
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Standard demo password is: ${selectedRole.lowercase()}123",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // OTP Verification Sheet Modal
        if (showOtpModal) {
            AlertDialog(
                onDismissRequest = { showOtpModal = false },
                title = { Text("Secure OTP Reset") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("We will send an OTP verification code to secure access to this account.")
                        OutlinedTextField(
                            value = otpMobileNumber,
                            onValueChange = { otpMobileNumber = it },
                            label = { Text("Registered Mobile Number") },
                            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = enteredOtp,
                            onValueChange = { enteredOtp = it },
                            label = { Text("Enter OTP Code (Simulated)") },
                            placeholder = { Text("Hint: 123456") },
                            leadingIcon = { Icon(Icons.Filled.LockOpen, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (enteredOtp == "123456" || enteredOtp.isNotEmpty()) {
                                Toast.makeText(context, "OTP verified successfully. Please log in using standard password.", Toast.LENGTH_LONG).show()
                                showOtpModal = false
                            } else {
                                Toast.makeText(context, "Invalid OTP code. Enter 123456 to bypass.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Verify & Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showOtpModal = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
