package com.linker.app.feature.screens.job.post_jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Post a Job", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(PostJobEvent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Job Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Fill in the details below to post a new job opportunity.",
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
                            label = "Salary",
                            placeholder = "e.g. 500",
                            icon = Icons.Default.CurrencyRupee
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PostJobTextField(
                            value = state.address,
                            onValueChange = { onEvent(PostJobEvent.OnAddressChanged(it)) },
                            label = "Location",
                            placeholder = "e.g. Vinukonda",
                            icon = Icons.Default.LocationOn
                        )
                    }
                }
            }

            item {
                Text(text = "Job Type", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    JobTypeEnum.values().forEach { type ->
                        FilterChip(
                            selected = state.jobType == type,
                            onClick = { onEvent(PostJobEvent.OnJobTypeChanged(type)) },
                            label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            item {
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a").withZone(ZoneId.systemDefault())
                PostJobTextField(
                    value = formatter.format(state.jobTime),
                    onValueChange = { /* Handle time picker in future */ },
                    label = "Job Time",
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
                    label = { Text("Job Description") },
                    placeholder = { Text("Describe the job requirements and responsibilities...") },
                    shape = RoundedCornerShape(12.dp),
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onEvent(PostJobEvent.OnPostClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Post Job Opportunity", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
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
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
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
