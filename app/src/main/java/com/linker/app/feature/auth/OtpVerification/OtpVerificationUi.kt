package com.linker.app.feature.auth.OtpVerification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linker.app.ui.theme.LocalJobFinderTheme

@Composable
fun OtpVerificationUi(
    state: OtpUiState,
    onEvent: (OtpEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "navigate back",
                    tint = Color.Unspecified
                )
            }
            Spacer(Modifier.height(30.dp))

            Text(
                text = "OTP Authentication",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Account Verification",
                style = MaterialTheme.typography.titleSmall,
                fontStyle = FontStyle.Italic,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "An authentication code has been sent to your email",
                color = Color.Gray,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = state.email,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(40.dp))

            // Production-ready OTP Input using BasicTextField
            BasicTextField(
                value = state.otp,
                onValueChange = { onEvent(OtpEvent.OnOtpChanged(it)) },
                enabled = !state.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                decorationBox = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(6) { index ->
                            val char = when {
                                index >= state.otp.length -> ""
                                else -> state.otp[index].toString()
                            }
                            val isFocused = state.otp.length == index

                            Box(
                                modifier = Modifier
                                    .size(width = 52.dp, height = 60.dp)
                                    .border(
                                        width = if (isFocused) 2.dp else 1.dp,
                                        color = if (isFocused) MaterialTheme.colorScheme.primary else Color.LightGray,
                                        shape = MaterialTheme.shapes.medium
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            )

            Spacer(Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Didn't receive code? ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                if (state.timerTicks > 0) {
                    val minutes = state.timerTicks / 60
                    val seconds = state.timerTicks % 60

                    Text(
                        text = "Resend in $minutes:${seconds.toString().padStart(2, '0')}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = "Resend OTP",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onEvent(OtpEvent.OnResendClicked) }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onEvent(OtpEvent.OnVerifyClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB) // Professional Blue
                ),
                enabled = state.otp.length == 6 && !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        text = "Verify",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowOtpVerificationUi() {
    LocalJobFinderTheme {
        OtpVerificationUi(state = OtpUiState(), onEvent = {}, onBack = {})
    }
}
