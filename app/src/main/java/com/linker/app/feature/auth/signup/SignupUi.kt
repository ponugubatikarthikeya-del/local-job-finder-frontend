package com.linker.app.feature.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
fun SignupUi(
    state: SignupUiState,
    onEvent: (SignupEvent) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(48.dp))

                Text(
                    text = "Create Account ✨\nJoin Local Job Finder",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Fill in your details to start applying or posting local services.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))

                AppTextField(
                    value = state.username,
                    onValueChange = { onEvent(SignupEvent.OnUserNameChanged(it)) },
                    label = "Username",
                    leadingIcon = Icons.Outlined.Person
                )

                Spacer(Modifier.height(16.dp))

                AppTextField(
                    value = state.phone,
                    onValueChange = { onEvent(SignupEvent.OnPhoneChanged(it)) },
                    label = "Phone Number",
                    leadingIcon = Icons.Outlined.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(Modifier.height(16.dp))

                AppTextField(
                    value = state.email,
                    onValueChange = { onEvent(SignupEvent.OnEmailChanged(it)) },
                    label = "Email Address",
                    leadingIcon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )

                Spacer(Modifier.height(16.dp))

                AppTextField(
                    value = state.password,
                    onValueChange = { onEvent(SignupEvent.OnPasswordChanged(it)) },
                    label = "Password",
                    leadingIcon = Icons.Outlined.Lock,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { onEvent(SignupEvent.OnTogglePasswordVisibility) }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { onEvent(SignupEvent.OnSignupBtnClicked) },
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
                            text = "Sign Up",
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
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onEvent(SignupEvent.OnNavigateLoginClicked) }
                    )
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowPreviewSignup() {
    LocalJobFinderTheme {
        SignupUi(
            state = SignupUiState(),
            onEvent = {}
        )
    }
}
