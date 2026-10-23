package com.linker.app.feature.screens.job.post_jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linker.app.core.util.enums.JobTypeEnum
import com.linker.app.ui.theme.LocalJobFinderTheme
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobUi(
    state: PostJobUiState,
    onEvent: (PostJobEvent) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Post a Job", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(PostJobEvent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Job Information 🛠️",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Provide clear details to attract the best local workers for your task.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                PostJobTextField(
                    value = state.title,
                    onValueChange = { onEvent(PostJobEvent.OnTitleChanged(it)) },
                    label = "Job Title",
                    placeholder = "e.g. Experienced Plumber Needed",
                    icon = Icons.Default.Work
                )
            }

            item {
                PostJobTextField(
                    value = state.category,
                    onValueChange = { onEvent(PostJobEvent.OnCategoryChanged(it)) },
                    label = "Category",
                    placeholder = "e.g. Plumbing",
                    icon = Icons.Default.Category
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        PostJobTextField(
                            value = state.payAmount,
                            onValueChange = { onEvent(PostJobEvent.OnPayAmountChanged(it)) },
                            label = "Salary (₹)",
                            placeholder = "e.g. 500",
                            icon = Icons.Default.CurrencyRupee
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PostJobTextField(
                            value = state.address,
                            onValueChange = { onEvent(PostJobEvent.OnAddressChanged(it)) },
                            label = "Location",
                            placeholder = "e.g. Guntur",
                            icon = Icons.Default.LocationOn
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Job Payment Type",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    JobTypeEnum.entries.forEach { type ->
                        FilterChip(
                            selected = state.jobType == type,
                            onClick = { onEvent(PostJobEvent.OnJobTypeChanged(type)) },
                            label = { 
                                Text(
                                    text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelMedium
                                ) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            item {
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a").withZone(ZoneId.systemDefault())
                PostJobTextField(
                    value = formatter.format(state.jobTime),
                    onValueChange = { },
                    label = "Job Scheduled Time",
                    placeholder = "",
                    icon = Icons.Default.Schedule,
                    readOnly = true
                )
            }

            item {
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { onEvent(PostJobEvent.OnDescriptionChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Job Description / Bio") },
                    placeholder = { Text("Describe the work required, tools needed, and timing...") },
                    shape = MaterialTheme.shapes.large,
                    minLines = 3,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onEvent(PostJobEvent.OnPostClicked) },
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
                            text = "Post Job Listing",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun PostJobTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        readOnly = readOnly,
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        shape = MaterialTheme.shapes.large,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PostJobPreview() {
    LocalJobFinderTheme {
        PostJobUi(
            state = PostJobUiState(),
            onEvent = {}
        )
    }
}
