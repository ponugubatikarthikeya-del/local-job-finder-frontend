package com.linker.app.feature.screens.profile.profile_update

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linker.app.ui.theme.LocalJobFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileUi(
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    // Dummy Initial Data States
    var username by remember { mutableStateOf("nameissanjayy") }
    var phone by remember { mutableStateOf("+91 9876543210") }
    var headline by remember { mutableStateOf("Sai Sanjay...✨") }
    var skills by remember { mutableStateOf("Plumbing, Painting, Cleaning") }
    var dob by remember { mutableStateOf("15-08-1995") }
    var city by remember { mutableStateOf("Vinukonda") }
    var bio by remember { mutableStateOf("Being myself. Extroverted introvert. Music 🎶 🎼 ...") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Update Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                UpdateTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    icon = Icons.Default.Person
                )
            }

            item {
                UpdateTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone Number",
                    icon = Icons.Default.Phone
                )
            }

            item {
                UpdateTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = "Date of Birth",
                    icon = Icons.Default.Cake
                )
            }

            item {
                UpdateTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = "City Name",
                    icon = Icons.Default.LocationOn
                )
            }

            item {
                Text(
                    text = "Professional Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                UpdateTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    label = "Headline",
                    icon = Icons.Default.Star
                )
            }

            item {
                UpdateTextField(
                    value = skills,
                    onValueChange = { skills = it },
                    label = "Skills (comma separated)",
                    icon = Icons.Default.Build
                )
            }

            item {
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Bio") },
                    shape = RoundedCornerShape(16.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSaveSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun UpdateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun UpdateProfilePreview() {
    LocalJobFinderTheme {
        UpdateProfileUi()
    }
}
