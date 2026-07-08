package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.backend.api.TradeFitApi
import com.example.backend.service.ResourceState
import com.example.ui.components.TradeFitButton
import com.example.ui.components.TradeFitTextField
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp)
                .verticalScroll(rememberScrollState())
                .clip(RoundedCornerShape(24.dp))
                .background(PureWhite)
                .border(1.dp, DarkSlateBorder, RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Brand Logo Placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ExecutiveBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BusinessCenter,
                    contentDescription = "TradeFit ERP Logo",
                    tint = PureWhite,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "TradeFit ERP",
                style = MaterialTheme.typography.displaySmall,
                color = MatteBlack,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Fabric Supplier Business Management",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateTextLight,
                fontWeight = FontWeight.Normal
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email Field
            TradeFitTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMsg = ""
                },
                label = "Username / Email",
                placeholder = "",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Mail, contentDescription = null, tint = SlateTextDark)
                },
                isError = errorMsg.isNotEmpty(),
                testTagStr = "login_email_input"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMsg = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                label = { Text("Password") },
                placeholder = { Text("") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = SlateTextDark)
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = errorMsg.isNotEmpty(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ExecutiveBlue,
                    unfocusedBorderColor = DarkSlateBorder,
                    focusedLabelColor = ExecutiveBlue,
                    unfocusedLabelColor = SlateTextDark,
                    focusedContainerColor = PureWhite,
                    unfocusedContainerColor = OffWhite,
                    errorContainerColor = Color(0xFFFFF5F5)
                )
            )
            
            // Error Message
            AnimatedVisibility(
                visible = errorMsg.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = errorMsg,
                    color = WarningAlert,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Submit Button
            TradeFitButton(
                text = if (isLoading) "Signing In..." else "Secure Login",
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMsg = "Please enter both credentials"
                    } else if (!email.contains("@")) {
                        errorMsg = "Please enter a valid work email"
                    } else if (password.length < 6) {
                        errorMsg = "Password must be at least 6 characters"
                    } else {
                        scope.launch {
                            isLoading = true
                            when (val result = TradeFitApi.auth.login(email.trim(), password)) {
                                is ResourceState.Success -> {
                                    TradeFitApi.activeCompanyId = result.data.companyId.ifBlank { "demo_company" }
                                    onLoginSuccess()
                                }
                                is ResourceState.Error -> errorMsg = result.message
                                else -> Unit
                            }
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                testTagStr = "submit_login_button"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Authorized Personnel Only",
                style = MaterialTheme.typography.labelSmall,
                color = SlateTextLight,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
