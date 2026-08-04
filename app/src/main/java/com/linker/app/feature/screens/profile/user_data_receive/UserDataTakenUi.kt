package com.linker.app.feature.screens.profile.user_data_receive

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserDataTakenUi(
    state: UserDataTakenUiState,
    onEvent: (UserDataTakenEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Welcome! Please enter your city name to find jobs nearby.",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = state.cityName,
            onValueChange = { onEvent(UserDataTakenEvent.OnCityNameChange(it)) },
            label = { Text("City Name") },
            placeholder = { Text("e.g. Guntur") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.error != null,
            singleLine = true
        )

        if (state.error != null) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onEvent(UserDataTakenEvent.OnSubmit) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.medium,
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Submit",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowUserDataTakenUiPreview() {
    UserDataTakenUi(
        state = UserDataTakenUiState(),
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ShowUserDataTakenUiErrorPreview() {
    UserDataTakenUi(
        state = UserDataTakenUiState(
            cityName = "guntur",
            error = "Server encountered an error. Please try again later."
        ),
        onEvent = {}
    )
}
