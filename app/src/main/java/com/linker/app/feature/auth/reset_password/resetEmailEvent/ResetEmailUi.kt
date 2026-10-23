package com.linker.app.feature.auth.reset_password.resetEmailEvent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linker.app.core.util.components.AppTextField
import com.linker.app.ui.theme.LocalJobFinderTheme

@Composable
fun ResetEmailUi(
    state: ResetEmailUiState,
    onEvent: (ResetEmailEvent) -> Unit,
    onBack: () -> Unit
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
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(Modifier.height(24.dp))

                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Navigate back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Forgot Password 🔒",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Enter your registered email address to receive an OTP verification code.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))

                AppTextField(
                    value = state.email,
                    onValueChange = { onEvent(ResetEmailEvent.OnEmailChanged(it)) },
                    label = "Email Address",
                    leadingIcon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { onEvent(ResetEmailEvent.OnSendClicked) },
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
                            text = "Send Reset Code",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowResetEmailPreview() {
    LocalJobFinderTheme {
        ResetEmailUi(state = ResetEmailUiState(), onEvent = {}, onBack = {})
    }
}
