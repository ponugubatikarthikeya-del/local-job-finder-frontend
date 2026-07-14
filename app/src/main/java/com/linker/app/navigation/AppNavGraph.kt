package com.linker.app.navigation

import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.linker.app.*
import com.linker.app.feature.auth.OtpVerification.OtpLauncher
import com.linker.app.feature.auth.login.LoginLauncher
import com.linker.app.feature.auth.reset_password.resetEmailEvent.ResetEmailLauncher
import com.linker.app.feature.auth.reset_password.resetPassword.ResetPasswordLauncher
import com.linker.app.feature.auth.signup.SignupLauncher
import com.linker.app.feature.screens.home.HomeUi
import com.linker.app.feature.screens.job.post_jobs.PostJobUi
import com.linker.app.feature.screens.profile.private_profile.PrivateProfileUi
import com.linker.app.feature.screens.profile.profile_update.UpdateProfileUi
import com.linker.app.feature.screens.profile.public_profile.PublicProfileUi
import com.linker.app.main.splash.SplashScreen as SplashScreenUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Visibility logic for Bars - hide on Auth and Splash screens
    val isMainScreen = currentDestination?.let { dest ->
        !dest.hasRoute<LoginScreen>() &&
        !dest.hasRoute<SignupScreen>() &&
        !dest.hasRoute<SplashScreen>() &&
        !dest.hasRoute<ResetPasswordEmailSendScreen>() &&
        !dest.hasRoute<ResetOtpVerificationScreen>() &&
        !dest.hasRoute<ResetPasswordScreen>() &&
        !dest.hasRoute<SkipperScreen>() &&
        !dest.hasRoute<DetailsTakeScreen>() &&
        !dest.hasRoute<LoadingScreen>()
    } ?: false

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isMainScreen,
        drawerContent = {
            AppDrawerContent(
                navController = navController,
                currentDestination = currentDestination,
                drawerState = drawerState,
                scope = scope
            )
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0.dp),
            bottomBar = {
                if (isMainScreen) {
                    AppBottomBar(
                        navController = navController,
                        currentDestination = currentDestination
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = SplashScreen,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable<SplashScreen> {
                    SplashScreenUi(
                        onFinished = {
                            navController.navigate(LoginScreen) {
                                popUpTo(SplashScreen) { inclusive = true }
                            }
                        }
                    )
                }

                composable<LoginScreen> {
                    LoginLauncher(
                        NavigateToSignup = { navController.navigate(SignupScreen) },
                        NavigateToForgotPassword = { navController.navigate(ResetPasswordEmailSendScreen) },
                        NavigateToOtpVerification = { navController.navigate(ResetOtpVerificationScreen) },
                        NavigateToHome = {
                            navController.navigate(HomeScreen) {
                                popUpTo(LoginScreen) { inclusive = true }
                            }
                        }
                    )
                }

                composable<SignupScreen> {
                    SignupLauncher(
                        onNavigateToLogin = { navController.popBackStack() },
                        onSignupSuccess = {
                            navController.navigate(HomeScreen) {
                                popUpTo(SignupScreen) { inclusive = true }
                            }
                        },
                        onNavigateToOtpVerification = { navController.navigate(ResetOtpVerificationScreen) }
                    )
                }

                composable<ResetPasswordEmailSendScreen> {
                    ResetEmailLauncher(
                        onNavigateBack = { navController.popBackStack() },
                        onSendSuccess = { navController.navigate(ResetOtpVerificationScreen) }
                    )
                }

                composable<ResetOtpVerificationScreen> {
                    OtpLauncher(
                        onNavigateBack = { navController.popBackStack() },
                        onVerificationSuccess = { navController.navigate(ResetPasswordScreen) }
                    )
                }

                composable<ResetPasswordScreen> {
                    ResetPasswordLauncher(
                        onNavigateBack = { navController.popBackStack() },
                        onResetSuccess = {
                            navController.navigate(LoginScreen) {
                                popUpTo(ResetPasswordEmailSendScreen) { inclusive = true }
                            }
                        }
                    )
                }

                composable<HomeScreen> { 
                    HomeUi(onOpenDrawer = { scope.launch { drawerState.open() } },
                        OnNavigateToPublicProfile = { navController.navigate(PublicProfileScreen) })
                }

                composable<MyJobsScreen> { PlaceholderScreen("My Jobs") }

                composable<PrivateProfileScreen> { 
                    PrivateProfileUi(
                        onNavigateBack = { navController.popBackStack() },
                        onEditProfile = { 
                            navController.navigate(ProfileUpdateScreen)
                        },
                    ) 
                }

                composable<ProfileUpdateScreen> {
                    UpdateProfileUi(
                        onNavigateBack = { navController.popBackStack() },
                        onSaveSuccess = { 
                            navController.popBackStack()
                        }
                    ) 
                }

                composable<PostJobScreen> { 
                    PostJobUi(
                        onNavigateBack = { navController.popBackStack() },
                        onPostSuccess = { navController.popBackStack() }
                    ) 
                }

                composable<PublicProfileScreen> {
                    PublicProfileUi(
                        onNavigateBack = {navController.popBackStack()}
                    )
                }
                composable<PublicJobDetailsScreen> { PlaceholderScreen("Public Job Details") }
                composable<PrivateJobDetails> { PlaceholderScreen("Private Job Details") }
                composable<SkipperScreen> { PlaceholderScreen("Skipper Screen") }
                composable<DetailsTakeScreen> { PlaceholderScreen("Details Take Screen") }
                composable<LoadingScreen> { PlaceholderScreen("Loading Screen") }
            }
        }
    }
}

@Composable
fun AppDrawerContent(
    navController: NavHostController,
    currentDestination: NavDestination?,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    ModalDrawerSheet(
        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
        modifier = Modifier.width(320.dp)
    ) {
        // Drawer Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Karthikeya",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "karthikeya@gmail.com",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }

        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        // Navigation Items
        drawerNavItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = currentDestination?.hasRoute(item.route::class) == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    scope.launch { drawerState.close() }
                },
                icon = { Icon(item.icon, null) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Logout") },
            selected = false,
            onClick = {
                navController.navigate(LoginScreen) {
                    popUpTo(0) { inclusive = true }
                }
                scope.launch { drawerState.close() }
            },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AppBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    NavigationBar {
        bottomNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = currentDestination?.hasRoute(item.route::class) == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, item.label) },
                label = { Text(item.label) }
            )
            
            // Add FAB in between the two bottom items
            if (index == 0) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = { navController.navigate(PostJobScreen) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.Default.Add, "Post Job")
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Build, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(16.dp))
            Text(text = name, style = MaterialTheme.typography.headlineMedium)
            Text("Screen under development", style = MaterialTheme.typography.bodySmall)
        }
    }
}
