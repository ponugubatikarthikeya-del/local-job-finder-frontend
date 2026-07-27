package com.linker.app.feature.screens.profile.public_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linker.app.ui.theme.LocalJobFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileUi(
    onNavigateBack: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF050505) // Very dark background like the image
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Image
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                                .background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            // In real app, use AsyncImage for the user's photo
                            Text("👤", fontSize = 40.sp)
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        // User Info & Stats
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "nameissanjayy",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Text(
                                text = "Sai Sanjay...✨",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatItem(count = "1", label = "post")
                                StatItem(count = "4.5", label = "rating")
                                StatItem(count = "185", label = "completed")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Bio Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "✨ ✨ ✨", color = Color.White)
                        Text(text = "Being myself", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Extroverted introvert", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Music 🎶 🎼 🧐 😳 ...", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626))
                        ) {
                            Text("Connect", color = Color.White)
                        }
//                        Button(
//                            onClick = { },
//                            modifier = Modifier.weight(1f),
//                            shape = RoundedCornerShape(8.dp),
//                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626))
//                        ) {
//                            Text("Message", color = Color.White)
//                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

//                    // Mutual Followers
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        // Tiny avatars stack
//                        Box {
//                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.Gray).border(1.dp, Color.Black, CircleShape))
//                            Box(modifier = Modifier.padding(start = 12.dp).size(24.dp).clip(CircleShape).background(Color.LightGray).border(1.dp, Color.Black, CircleShape))
//                        }
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Followed by royalstag_raghava, __sai._27 and 10 more",
//                            style = MaterialTheme.typography.labelSmall,
//                            color = Color.LightGray
//                        )
//                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(text = label, color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview
@Composable
fun PublicProfilePreview() {
    LocalJobFinderTheme {
        PublicProfileUi()
    }
}
