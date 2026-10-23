package com.linker.app.navigation

import android.app.Activity
import android.view.Window
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linker.app.*
import com.linker.app.feature.auth.OtpVerification.OtpLauncher
import com.linker.app.feature.auth.login.LoginLauncher
import com.linker.app.feature.auth.loading.LoadingLauncher
import com.linker.app.feature.auth.reset_password.resetEmailEvent.ResetEmailLauncher
import com.linker.app.feature.auth.reset_password.resetPassword.ResetPasswordLauncher
import com.linker.app.feature.auth.signup.SignupLauncher
import com.linker.app.feature.screens.error.NetworkErrorScreen
import com.linker.app.feature.screens.profile.user_data_receive.UserDataTakenLauncher
import com.linker.app.feature.screens.home.HomeLauncher
import com.linker.app.feature.screens.job.my_jobs.MyJobsLauncher
import com.linker.app.feature.screens.job.post_jobs.PostJobLauncher
import com.linker.app.feature.screens.job.public_job_details.PublicJobDetailsLauncher
import com.linker.app.feature.screens.job.view_applicants.ViewApplicantsLauncher
import com.linker.app.feature.screens.profile.private_profile.PrivateProfileLauncher
import com.linker.app.feature.screens.profile.profile_update.UpdateProfileLauncher
import com.linker.app.feature.screens.profile.profile_update.UpdateProfileUi
import com.linker.app.feature.screens.profile.public_profile.PublicProfileLauncher
import com.linker.app.core.data.models.UserDto
import com.linker.app.main.MainViewModel
import com.linker.app.main.splash.SplashState
import com.linker.app.main.splash.SplashScreen as SplashScreenUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    mainViewModel: MainViewModel = viewModel()
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val activityContext = androidx.compose.ui.platform.LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val splashState by mainViewModel.splashState.collectAsStateWithLifecycle()
    val userProfile by mainViewModel.userProfile.collectAsStateWithLifecycle()
    var isAnimationFinished by remember { mutableStateOf(false) }

    // Collect global internet connectivity state
    val isNetworkAvailable by mainViewModel.isNetworkAvailable.collectAsStateWithLifecycle()
    val isStabilizing by mainViewModel.isStabilizing.collectAsStateWithLifecycle()

    // Track previous offline state to detect when the user comes back online
    var wasOffline by remember { mutableStateOf(false) }

    // Always-Active Network Listener: Detects reconnection globally
    LaunchedEffect(isNetworkAvailable) {
        if (!isNetworkAvailable) {
            wasOffline = true
        } else if (wasOffline) {
            wasOffline = false
            // The stabilization delay is now handled inside MainViewModel.isNetworkAvailable.onEach
            mainViewModel.retryAuthCheck()
        }
    }

    LaunchedEffect(isAnimationFinished, splashState) {
        if (isAnimationFinished) {
            when (splashState) {
                is SplashState.Authenticated -> {
                    navController.navigate(LoadingScreen) {
                        popUpTo(SplashScreen) { inclusive = true }
                    }
                }

                is SplashState.Unauthenticated -> {
                    navController.navigate(LoginScreen) {
                        popUpTo(SplashScreen) { inclusive = true }
                    }
                }

                else -> {} // Still loading or network error
            }
        }
    }

    // Global Logout Observer: Jump to Login if session becomes Unauthenticated while inside the app
    LaunchedEffect(splashState) {
        if (splashState is SplashState.Unauthenticated && 
            currentDestination?.let { it.hasRoute<SplashScreen>() || it.hasRoute<LoginScreen>() } == false
        ) {
            navController.navigate(LoginScreen) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val isNavBar = currentDestination?.let { dest ->
        dest.hasRoute<HomeScreen>() || dest.hasRoute<PrivateProfileScreen>()
    } ?: false


    // Visibility logic for Bars - hide on Auth and Splash screens
    val isMainScreen = currentDestination?.let { dest ->
        !dest.hasRoute<LoginScreen>() &&
                !dest.hasRoute<SignupScreen>() &&
                !dest.hasRoute<SplashScreen>() &&
                !dest.hasRoute<ResetPasswordEmailSendScreen>() &&
                !dest.hasRoute<ResetOtpVerificationScreen>() &&
                !dest.hasRoute<SignupOtpVerificationScreen>() &&
                !dest.hasRoute<ResetPasswordScreen>() &&
                !dest.hasRoute<SkipperScreen>() &&
                !dest.hasRoute<DetailsTakeScreen>() &&
                !dest.hasRoute<LoadingScreen>()
    } ?: false

    // 2. Wrap everything in a Box for top-level overlay support
    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = currentDestination?.hasRoute<HomeScreen>() == true && isNetworkAvailable, // Only enable drawer gestures on Home screen when online
            drawerContent = {
                AppDrawerContent(
                    userProfile = userProfile,
                    navController = navController,
                    currentDestination = currentDestination,
                    drawerState = drawerState,
                    scope = scope,
                    onLogout = {
                        mainViewModel.logout(allDevices = false)
                        navController.navigate(LoginScreen) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets(0.dp),
                bottomBar = {
                    if (isNavBar) {
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
                    val isNetworkError = splashState is SplashState.NetworkError
                    val isRetrying = splashState is SplashState.Loading

                    if (isNetworkError || isRetrying) {
                        NetworkErrorScreen(
                            message = (splashState as? SplashState.NetworkError)?.message
                                ?: "Reconnecting...",
                            isLoading = isRetrying,
                            onRetry = { mainViewModel.retryAuthCheck() }
                        )
                    } else {
                        SplashScreenUi(
                            onFinished = { isAnimationFinished = true }
                        )
                    }
                }


                    composable<LoginScreen> {
                        LoginLauncher(
                            NavigateToSignup = { navController.navigate(SignupScreen) },
                            NavigateToForgotPassword = {
                                navController.navigate(
                                    ResetPasswordEmailSendScreen
                                )
                            },
                            NavigateToOtpVerification = { email ->
                                navController.navigate(
                                    SignupOtpVerificationScreen(email)
                                )
                            },
                            NavigateToLoading = {
                                navController.navigate(LoadingScreen) {
                                    popUpTo(LoginScreen) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<SignupScreen> {
                        SignupLauncher(
                            onNavigateToLogin = { navController.popBackStack() },
//                        onSignupSuccess = {
//                            navController.navigate(HomeScreen) {
//                                popUpTo(SignupScreen) { inclusive = true }
//                            }
//                        },
                            onNavigateToOtpVerification = { email ->
                                navController.navigate(
                                    SignupOtpVerificationScreen(email)
                                )
                            }
                        )
                    }

                    composable<SignupOtpVerificationScreen> {
                        OtpLauncher(
                            isResetFlow = false, // Explicitly false
                            onNavigateBack = { navController.popBackStack() },
                            onVerificationSuccess = {
                                navController.navigate(LoadingScreen) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<ResetPasswordEmailSendScreen> {
                        ResetEmailLauncher(
                            onNavigateBack = { navController.popBackStack() },
                            onSendSuccess = { email ->
                                navController.navigate(ResetOtpVerificationScreen(email))
                            }
                        )
                    }

                    composable<ResetOtpVerificationScreen> { backStackEntry ->
                        val email = backStackEntry.toRoute<ResetOtpVerificationScreen>().email
                        OtpLauncher(
                            isResetFlow = true, // Explicitly true
                            onNavigateBack = { navController.popBackStack() },
                            onVerificationSuccess = { otp ->
                                navController.navigate(ResetPasswordScreen(email, otp?.toIntOrNull() ?: 0))
                            }
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
                        BackHandler {
                            showExitDialog = true
                        }
                        HomeLauncher(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onNavigateToPrivateProfile = { navController.navigate(PrivateProfileScreen) },
                            onNavigateToPublicProfile = { jobId -> 
                                navController.navigate(PublicProfileScreen(jobId)) 
                            },
                            onNavigateToPublicCardDetails = { job ->
                                navController.navigate(PublicJobDetailsScreen(job.id, showApplyButton = true))
                            },
                            onNavigateToNotification = { navController.navigate(NotificationScreen) },
                            onNavigateToChangeAddress = { navController.navigate(DetailsTakeScreen) }
                        )
                    }

                    composable<MyJobsScreen> {
                        MyJobsLauncher(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToJobDetails = { job ->
                                navController.navigate(PublicJobDetailsScreen(job.id, showApplyButton = false, showDeleteButton = true))
                            }
                        )
                    }

                    composable<PrivateProfileScreen> {
                        PrivateProfileLauncher(
                            onNavigateBack = { navController.popBackStack() },
                            onEditProfile = {
                                navController.navigate(ProfileUpdateScreen)
                            },
                            onViewPublicProfile = {
                                navController.navigate(PublicProfileScreen)
                            },
                            onLogoutComplete = {
                                navController.navigate(LoginScreen) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<ProfileUpdateScreen> {
                        UpdateProfileLauncher(
                            onNavigateBack = { navController.popBackStack() },
                            onSaveSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable<PostJobScreen> {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        PostJobLauncher(
                            onNavigateBack = { navController.popBackStack() },
                            onShowToast = { message ->
                                android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    composable<PublicProfileScreen> { backStackEntry ->
                        val route = backStackEntry.toRoute<PublicProfileScreen>()
                        PublicProfileLauncher(
                            jobId = route.jobId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable<PublicJobDetailsScreen> { backStackEntry ->
                        val route = backStackEntry.toRoute<PublicJobDetailsScreen>()
                        PublicJobDetailsLauncher(
                            jobId = route.jobId,
                            showApplyButton = route.showApplyButton,
                            showDeleteButton = route.showDeleteButton,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToViewApplicants = { jobId ->
                                navController.navigate(ViewApplicantsScreen(jobId))
                            },
                            onNavigateToPublicProfile = { jobId ->
                                navController.navigate(PublicProfileScreen(jobId))
                            },
                            onNavigateToPrivateProfile = {
                                navController.navigate(PrivateProfileScreen)
                            }
                        )
                    }
                    composable<ViewApplicantsScreen> { backStackEntry ->
                        val route = backStackEntry.toRoute<ViewApplicantsScreen>()
                        ViewApplicantsLauncher(
                            jobId = route.jobId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable<PrivateJobDetails> { PlaceholderScreen("Private Job Details") }
                    composable<SkipperScreen> { PlaceholderScreen("Skipper Screen") }
                    composable<DetailsTakeScreen> {
                        UserDataTakenLauncher(
                            onNavigateToLoading = {
                                // 🟢 Route back through LoadingScreen so jobs fetch during the 3s loading window
                                navController.navigate(LoadingScreen) {
                                    popUpTo(DetailsTakeScreen) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable<LoadingScreen> {
                        LoadingLauncher(
                            onNavigateToHome = {
                                navController.navigate(HomeScreen) {
                                    popUpTo(LoadingScreen) { inclusive = true }
                                }
                            },
                            onNavigateToDetailsTake = {
                                navController.navigate(DetailsTakeScreen) {
                                    popUpTo(LoadingScreen) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable<NotificationScreen> { PlaceholderScreen("Notification Screen") }
                }
            }

        }
        // GLOBAL NETWORK OVERLAY
        // Refined Logic: Don't show global overlay if we're on Splash, Loading, Login, or Signup.
        // These screens handle their own network error UI to prevent "blinking" and layout fighting
        // during the critical startup and authentication flow.
        val shouldShowOverlay = (!isNetworkAvailable || isStabilizing) &&
                currentDestination?.let { dest ->
                    !dest.hasRoute<SplashScreen>() &&
                    !dest.hasRoute<LoadingScreen>() &&
                    !dest.hasRoute<LoginScreen>() &&
                    !dest.hasRoute<SignupScreen>()
                } ?: false

        if (shouldShowOverlay) {
            NetworkErrorScreen(
                message = if (isStabilizing) "Connection restored! Stabilizing..." else "No Internet Connection",
                isLoading = isStabilizing || splashState is SplashState.Loading,
                onRetry = { mainViewModel.retryAuthCheck() }
            )
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Exit App") },
                text = { Text("Are you sure you want to exit the application?") },
                confirmButton = {
                    TextButton(onClick = { (activityContext as? Activity)?.finish() }) {
                        Text("Exit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}



@Composable
fun AppDrawerContent(
    userProfile: UserDto?,
    navController: NavHostController,
    currentDestination: NavDestination?,
    drawerState: DrawerState,
    scope: CoroutineScope,
    onLogout: () -> Unit
) {
    val username = userProfile?.username?.ifBlank { "User" } ?: "User"
    val email = userProfile?.email?.ifBlank { "No email provided" } ?: "No email provided"

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
                text = username,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = email,
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
                    if (currentDestination?.hasRoute(item.route::class) == false) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    scope.launch { drawerState.close() }
                },
                icon = { Icon(item.icon, null) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Optional App Footer
        Text(
            text = "Version 1.0.0",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Logout") },
            selected = false,
            onClick = {
                onLogout()
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
                    if (currentDestination?.hasRoute(item.route::class) == false) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                        onClick = {
                            if (currentDestination?.hasRoute<PostJobScreen>() == false) {
                                navController.navigate(PostJobScreen) {
                                    popUpTo<HomeScreen> { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                        },
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
