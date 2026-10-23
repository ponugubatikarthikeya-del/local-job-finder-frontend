package com.linker.app.feature.auth.loading

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linker.app.ui.theme.LocalJobFinderTheme

@Composable
fun LoadingUi(
    state: LoadingUiState
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // Quote Section
                Text(
                    text = "\"${state.quote}\"",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 32.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Loading Indicator
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Preparing your dashboard...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowLoadingUi(){
    LocalJobFinderTheme {
        LoadingUi(state = LoadingUiState())
    }
}
