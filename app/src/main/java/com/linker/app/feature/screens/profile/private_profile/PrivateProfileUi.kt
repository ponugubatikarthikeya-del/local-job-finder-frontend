package com.linker.app.feature.screens.profile.private_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linker.app.ui.theme.LocalJobFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateProfileUi(
    state: PrivateProfileUiState,
    onEvent: (PrivateProfileEvent) -> Unit
) {
    val user = state.user

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(PrivateProfileEvent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(PrivateProfileEvent.OnEditProfileClicked) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onEvent(PrivateProfileEvent.OnRefresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (user == null && !state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Failed to load profile.")
                }
            } else if (user != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        // Profile Header
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(70.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = user.username,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = user.headline ?: "No headline provided",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

//                        Button(
//                            onClick = { onEvent(PrivateProfileEvent.OnViewPublicProfileClicked) },
//                            shape = RoundedCornerShape(12.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
//                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
//                            )
//                        ) {
//                            Text("View Public Profile", fontWeight = FontWeight.Bold)
//                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem(label = "Completed", value = user.totalJobsCompleted.toString())
                            ProfileStatItem(label = "Rating", value = "★ ${user.rating}")
                            ProfileStatItem(label = "City", value = user.cityName ?: "Not specified")
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Details Section
                        SectionHeader(title = "Contact Information")
                        DetailCard(Icons.Default.Email, "Email", user.email ?: "Not provided")
                        DetailCard(Icons.Default.Phone, "Phone", user.phone ?: "Not provided")

                        Spacer(modifier = Modifier.height(16.dp))

                        SectionHeader(title = "Professional Info")
                        DetailCard(
                            Icons.Default.Build,
                            "Skills",
                            user.skills?.joinToString(", ")?.ifEmpty { "None" } ?: "None"
                        )
                        DetailCard(
                            Icons.Default.Description,
                            "Bio",
                            user.userDescription ?: "No bio provided"
                        )
                        DetailCard(Icons.Default.Cake, "Birth Date", user.dateOfBirth ?: "Not provided")

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onEvent(PrivateProfileEvent.OnLogoutClicked) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !state.isLoading
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Logout", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun DetailCard(icon: ImageVector, label: String, value: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrivateProfilePreview() {
    LocalJobFinderTheme {
        PrivateProfileUi(
            state = PrivateProfileUiState(),
            onEvent = {}
        )
    }
}


