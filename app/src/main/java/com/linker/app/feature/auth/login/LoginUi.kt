package com.linker.app.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linker.app.core.util.components.AppTextField
import com.linker.app.ui.theme.LocalJobFinderTheme

@Composable
fun LoginUi(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(48.dp))

                Text(
                    text = "Welcome Back! 👋\nLog In to Local Jobs",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Sign in to discover nearby gig opportunities or manage your posted jobs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))

                AppTextField(
                    value = state.email,
                    onValueChange = { onEvent(LoginEvent.OnEmailChanged(it)) },
                    label = "Email Address",
                    leadingIcon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )

                Spacer(Modifier.height(16.dp))

                AppTextField(
                    value = state.password,
                    onValueChange = { onEvent(LoginEvent.OnPasswordChanged(it)) },
                    label = "Password",
                    leadingIcon = Icons.Outlined.Lock,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { onEvent(LoginEvent.OnTogglePasswordVisibility) }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(Modifier.height(8.dp))

                TextButton(
                    onClick = { onEvent(LoginEvent.OnNavigateForgotPasswordClicked) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { onEvent(LoginEvent.OnLoginBtnClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onEvent(LoginEvent.OnNavigateSignupClicked) }
                    )
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowLoginPreview() {
    LocalJobFinderTheme {
        LoginUi(
            state = LoginUiState(),
            onEvent = {}
        )
    }
}
