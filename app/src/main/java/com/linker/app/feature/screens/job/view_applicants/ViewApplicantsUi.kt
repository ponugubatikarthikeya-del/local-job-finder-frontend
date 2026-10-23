package com.linker.app.feature.screens.job.view_applicants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.linker.app.core.data.models.ApplicantDto
import com.linker.app.ui.components.AppendError
import com.linker.app.ui.components.AppendLoading
import com.linker.app.ui.components.EmptyListState
import com.linker.app.ui.components.FullScreenError
import com.linker.app.ui.components.FullScreenLoading
import com.linker.app.ui.theme.LocalJobFinderTheme
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewApplicantsUi(
    state: ViewApplicantsUiState,
    onEvent: (ViewApplicantsEvent) -> Unit
) {
    val applicants = state.applicantsPagingDataFlow.collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Applicants", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ViewApplicantsEvent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val refreshState = applicants.loadState.refresh
            val appendState = applicants.loadState.append

            if (refreshState is LoadState.Error && applicants.itemCount == 0) {
                FullScreenError(
                    message = refreshState.error.localizedMessage ?: "Failed to load applicants",
                    onRetry = { applicants.retry() }
                )
            } else if (refreshState is LoadState.Loading && applicants.itemCount == 0) {
                FullScreenLoading(modifier = Modifier.align(Alignment.Center))
            } else if (applicants.itemCount == 0) {
                EmptyListState(
                    title = "No applicants yet",
                    description = "When someone applies, they will appear here.",
                    icon = Icons.Default.Group,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        count = applicants.itemCount,
                        key = { applicants[it]?.uniqueRoomKey ?: it }
                    ) { index ->
                        val applicant = applicants[index]
                        if (applicant != null) {
                            ApplicantCard(
                                applicant = applicant,
                                onAccept = {
                                    onEvent(ViewApplicantsEvent.AcceptApplicant(applicant.targetApplicantId))
                                },
                                onReject = {
                                    onEvent(ViewApplicantsEvent.RejectApplicant(applicant.targetApplicantId))
                                },
                                isProcessing = state.isLoading
                            )
                        }
                    }

                    if (appendState is LoadState.Loading) {
                        item { AppendLoading() }
                    } else if (appendState is LoadState.Error) {
                        item {
                            AppendError(
                                message = appendState.error.localizedMessage ?: "Failed to load more",
                                onRetry = { applicants.retry() }
                            )
                        }
                    }
                }
            }

            NotificationOverlays(state, onEvent)
        }
    }
}

@Composable
fun NotificationOverlays(state: ViewApplicantsUiState, onEvent: (ViewApplicantsEvent) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { onEvent(ViewApplicantsEvent.ClearError) }) {
                        Text("Dismiss", color = Color.White)
                    }
                },
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Text(state.error)
            }
        }

        if (state.successMessage != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { onEvent(ViewApplicantsEvent.ClearSuccessMessage) }) {
                        Text("OK", color = Color.White)
                    }
                },
                containerColor = Color(0xFF2E7D32)
            ) {
                Text(state.successMessage)
            }
        }
    }
}

@Composable
fun ApplicantCard(
    applicant: ApplicantDto,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    isProcessing: Boolean = false
) {
    val status = applicant.status?.uppercase() ?: "PENDING"
    val formatter = try {
        DateTimeFormatter.ofPattern("dd MMM, yyyy").withZone(ZoneId.systemDefault())
    } catch (_: Exception) {
        null
    }
    val dateString = applicant.appliedAt?.let { formatter?.format(it) } ?: "Recently"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Image with Status Indicator Tick Mark
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when (status) {
                                    "ACCEPTED" -> Color(0xFF4CAF50)
                                    "REJECTED" -> Color(0xFFF44336)
                                    else -> Color(0xFFFFC107)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (status == "ACCEPTED") {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Accepted",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        } else if (status == "REJECTED") {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Rejected",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = applicant.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = applicant.headline ?: "No headline provided",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Applied on",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = when (status) {
                        "ACCEPTED" -> Color(0xFFE8F5E9)
                        "REJECTED" -> Color(0xFFFFEBEE)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (status == "ACCEPTED") {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        } else if (status == "REJECTED") {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = status.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium,
                            color = when (status) {
                                "ACCEPTED" -> Color(0xFF2E7D32)
                                "REJECTED" -> Color(0xFFC62828)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (status == "PENDING") {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        enabled = !isProcessing,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828).copy(alpha = 0.5f))
                    ) {
                        Text("Reject", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onAccept,
                        enabled = !isProcessing,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text("Accept", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = when (status) {
                        "ACCEPTED" -> Color(0xFFE8F5E9)
                        "REJECTED" -> Color(0xFFFFEBEE)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (status == "ACCEPTED") Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (status == "ACCEPTED") Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (status == "ACCEPTED") "Application Accepted" else "Application Rejected",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (status == "ACCEPTED") Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewApplicantsPreview() {
    LocalJobFinderTheme {
        ViewApplicantsUi(
            state = ViewApplicantsUiState(totalElements = 3),
            onEvent = {}
        )
    }
}
