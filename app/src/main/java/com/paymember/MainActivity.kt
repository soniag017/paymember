package com.paymember

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paymember.data.remote.ApiClient
import com.paymember.data.remote.RemoteAuthManager
import com.paymember.data.remote.SessionStore
import com.paymember.data.reminder.ReminderScheduler
import com.paymember.data.repository.SubscriptionRepository
import com.paymember.data.usage.AppUsageInsight
import com.paymember.data.usage.AppUsageMonitor
import com.paymember.ui.screens.BillingCalendarScreen
import com.paymember.ui.screens.SubscriptionCatalogScreen
import com.paymember.ui.screens.SubscriptionDetailScreen
import com.paymember.ui.screens.SubscriptionFormScreen
import com.paymember.ui.screens.SubscriptionListScreen
import com.paymember.ui.screens.SubscriptionPlansScreen
import com.paymember.ui.screens.LoginScreen
import com.paymember.ui.screens.findSubscriptionPlan
import com.paymember.ui.screens.findSubscriptionTemplate
import com.paymember.ui.theme.PayMemberTheme
import com.paymember.viewmodel.AuthViewModel
import com.paymember.viewmodel.SubscriptionViewModel
import com.paymember.viewmodel.SubscriptionViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

private const val DEV_SKIP_LOGIN = false

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val baseUrl = "http://10.0.2.2:8080/"
        val sessionStore = SessionStore(this)
        val apiService = ApiClient(sessionStore).create(baseUrl)
        val authManager = RemoteAuthManager(apiService, sessionStore)
        val repository = SubscriptionRepository(
            apiService = apiService,
            authManager = authManager,
            demoMode = false
        )

        setContent {
            var darkTheme by rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }
            PayMemberTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = {}
                )
                val viewModel: SubscriptionViewModel = viewModel(
                    factory = SubscriptionViewModelFactory(repository)
                )
                val authViewModel: AuthViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                                @Suppress("UNCHECKED_CAST")
                                return AuthViewModel(authManager) as T
                            }
                            throw IllegalArgumentException("Unknown ViewModel class")
                        }
                    }
                )
                val authState by authViewModel.uiState.collectAsState()

                val subscriptions by viewModel.subscriptions.collectAsState()
                val formState by viewModel.formState.collectAsState()
                val usageMonitor = remember { AppUsageMonitor(this@MainActivity) }
                var usageMonitoringEnabled by rememberSaveable { mutableStateOf(usageMonitor.isEnabled()) }
                var usagePermissionGranted by remember { mutableStateOf(usageMonitor.hasUsageAccess()) }
                var usageInsights by remember { mutableStateOf<List<AppUsageInsight>>(emptyList()) }

                fun refreshUsageInsights() {
                    usagePermissionGranted = usageMonitor.hasUsageAccess()
                    usageInsights = usageMonitor.findDormantSubscriptions(subscriptions)
                }

                val usageSettingsLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { refreshUsageInsights() }
                )

                LaunchedEffect(Unit) {
                    if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val startDestination = if (DEV_SKIP_LOGIN || authManager.isLoggedIn()) "list" else "login"

                LaunchedEffect(authState.isAuthenticated) {
                    if (authState.isAuthenticated) {
                        repository.refreshIfLoggedIn()
                        navController.navigate("list") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                LaunchedEffect(subscriptions, usageMonitoringEnabled, usagePermissionGranted) {
                    refreshUsageInsights()
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    ) {
                        NavHost(navController = navController, startDestination = startDestination) {
                            composable("login") {
                                LoginScreen(
                                    uiState = authState,
                                    onEmailChange = authViewModel::updateEmail,
                                    onPasswordChange = authViewModel::updatePassword,
                                    onSubmitEmailPassword = {
                                        if (
                                            !authState.isRegisterMode &&
                                            authState.email.isBlank() &&
                                            authState.password.isBlank()
                                        ) {
                                            repository.enableDemoMode()
                                            authViewModel.continueAsGuest()
                                        } else {
                                            authViewModel.submitEmailPassword()
                                        }
                                    },
                                    onGoogleToken = authViewModel::submitGoogle,
                                    onToggleMode = authViewModel::toggleMode
                                )
                            }

                            composable("list") {
                                SubscriptionListScreen(
                                    subscriptions = subscriptions,
                                    displayName = authState.email.toDisplayName().ifBlank {
                                        authManager.currentDisplayName() ?: "Invitada"
                                    },
                                    darkTheme = darkTheme,
                                    onToggleDarkTheme = { darkTheme = !darkTheme },
                                    onAddClick = {
                                        viewModel.clearForm()
                                        navController.navigate("catalog")
                                    },
                                    onManualClick = {
                                        viewModel.clearForm()
                                        navController.navigate("form/0?manual=true")
                                    },
                                    onCalendarClick = {
                                        navController.navigate("calendar")
                                    },
                                    usageMonitoringEnabled = usageMonitoringEnabled,
                                    usagePermissionGranted = usagePermissionGranted,
                                    usageInsights = usageInsights,
                                    onUsageMonitoringToggle = { enabled ->
                                        usageMonitor.setEnabled(enabled)
                                        usageMonitoringEnabled = enabled
                                        refreshUsageInsights()
                                        if (enabled && !usageMonitor.hasUsageAccess()) {
                                            usageSettingsLauncher.launch(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                                        }
                                    },
                                    onOpenUsageSettings = {
                                        usageSettingsLauncher.launch(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                                    },
                                    onEditClick = { id -> navController.navigate("detail/$id") },
                                    onDeleteClick = { subscription ->
                                        viewModel.deleteSubscription(subscription) { deletedId ->
                                            ReminderScheduler.cancel(this@MainActivity, deletedId)
                                        }
                                    }
                                )
                            }

                            composable(
                                route = "detail/{id}",
                                arguments = listOf(navArgument("id") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getInt("id") ?: 0
                                SubscriptionDetailScreen(
                                    subscription = subscriptions.firstOrNull { it.id == id },
                                    onBackClick = { navController.popBackStack() },
                                    onEditClick = { navController.navigate("form/$id") }
                                )
                            }

                            composable("calendar") {
                                BillingCalendarScreen(
                                    subscriptions = subscriptions,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable("catalog") {
                                SubscriptionCatalogScreen(
                                    onServiceSelected = { serviceId ->
                                        navController.navigate("plans/$serviceId")
                                    },
                                    onManualClick = {
                                        viewModel.clearForm()
                                        navController.navigate("form/0?manual=true")
                                    },
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = "plans/{serviceId}",
                                arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val serviceId = backStackEntry.arguments?.getString("serviceId").orEmpty()
                                val service = findSubscriptionTemplate(serviceId)

                                if (service == null) {
                                    LaunchedEffect(serviceId) {
                                        navController.popBackStack()
                                    }
                                } else {
                                    SubscriptionPlansScreen(
                                        service = service,
                                        onPlanSelected = { planId ->
                                            navController.navigate("form/0?serviceId=$serviceId&planId=$planId&manual=false")
                                        },
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }
                            }

                            composable(
                                route = "form/{id}?serviceId={serviceId}&planId={planId}&manual={manual}",
                                arguments = listOf(
                                    navArgument("id") { type = NavType.IntType },
                                    navArgument("serviceId") {
                                        type = NavType.StringType
                                        nullable = true
                                        defaultValue = null
                                    },
                                    navArgument("planId") {
                                        type = NavType.StringType
                                        nullable = true
                                        defaultValue = null
                                    },
                                    navArgument("manual") {
                                        type = NavType.BoolType
                                        defaultValue = false
                                    }
                                )
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getInt("id") ?: 0
                                val serviceId = backStackEntry.arguments?.getString("serviceId")
                                val planId = backStackEntry.arguments?.getString("planId")
                                val isManual = backStackEntry.arguments?.getBoolean("manual") ?: false
                                LaunchedEffect(id, serviceId, planId, isManual) {
                                    if (id != 0) {
                                        viewModel.loadSubscription(id)
                                    } else if (isManual) {
                                        viewModel.clearForm()
                                        viewModel.updateForm {
                                            copy(
                                                reminderEnabled = true,
                                                reminderDaysBefore = 1,
                                                notes = ""
                                            )
                                        }
                                    } else {
                                        val service = serviceId?.let { findSubscriptionTemplate(it) }
                                        val plan = if (serviceId != null && planId != null) {
                                            findSubscriptionPlan(serviceId, planId)
                                        } else {
                                            null
                                        }

                                        if (service != null && plan != null) {
                                            viewModel.updateForm {
                                                copy(
                                                    serviceName = "${service.name} - ${plan.name}",
                                                    price = plan.price,
                                                    billingDay = billingDay.ifBlank { "1" },
                                                    period = plan.period,
                                                    reminderEnabled = true,
                                                    reminderDaysBefore = 1,
                                                    notes = ""
                                                )
                                            }
                                        } else {
                                            viewModel.clearForm()
                                        }
                                    }
                                }

                                SubscriptionFormScreen(
                                    formState = formState,
                                    isFormValid = viewModel.isFormValid(),
                                    onFormChange = viewModel::updateForm,
                                    onSaveClick = {
                                        viewModel.saveSubscription { saved ->
                                            ReminderScheduler.schedule(this@MainActivity, saved)
                                            if (id == 0) {
                                                navController.navigate("list") {
                                                    popUpTo("list") { inclusive = false }
                                                    launchSingleTop = true
                                                }
                                            } else {
                                                navController.popBackStack()
                                            }
                                        }
                                    },
                                    onBackClick = { navController.popBackStack() },
                                    isEdit = id != 0,
                                    isManual = isManual
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun String.toDisplayName(): String {
    return substringBefore("@")
        .replace('.', ' ')
        .replace('_', ' ')
        .replace('-', ' ')
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
        }
}
