package com.linker.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.linker.app.*

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val route: Any
)

val bottomNavItems = listOf(
    NavigationItem("Home", Icons.Default.Home, HomeScreen),
    NavigationItem("Profile", Icons.Default.Person, PrivateProfileScreen)
)

val drawerNavItems = listOf(
    NavigationItem("Home", Icons.Default.Home, HomeScreen),
    NavigationItem("My Jobs", Icons.Default.Work, MyJobsScreen),
    NavigationItem("Profile", Icons.Default.Person, PrivateProfileScreen),
    NavigationItem("Notifications", Icons.Default.Notifications, NotificationScreen),
    NavigationItem("Post a Job", Icons.Default.Add, PostJobScreen),
    NavigationItem("Account", Icons.Default.AccountCircle, PublicJobDetailsScreen)
)
