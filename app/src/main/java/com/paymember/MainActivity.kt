package com.paymember

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paymember.data.db.AppDatabase
import com.paymember.data.reminder.ReminderScheduler
import com.paymember.data.repository.SubscriptionRepository
import com.paymember.ui.screens.SubscriptionCatalogScreen
import com.paymember.ui.screens.SubscriptionFormScreen
import com.paymember.ui.screens.SubscriptionListScreen
import com.paymember.ui.screens.SubscriptionPlansScreen
import com.paymember.ui.screens.findSubscriptionPlan
import com.paymember.ui.screens.findSubscriptionTemplate
import com.paymember.ui.theme.PayMemberTheme
import com.paymember.viewmodel.SubscriptionViewModel
import com.paymember.viewmodel.SubscriptionViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = SubscriptionRepository(AppDatabase.getInstance(this).subscriptionDao())

        setContent {
            PayMemberTheme {
                val navController = rememberNavController()
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = {}
                )
                val viewModel: SubscriptionViewModel = viewModel(
                    factory = SubscriptionViewModelFactory(repository)
                )

                val subscriptions by viewModel.subscriptions.collectAsState()
                val formState by viewModel.formState.collectAsState()

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

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        SubscriptionListScreen(
                            subscriptions = subscriptions,
                            onAddClick = {
                                viewModel.clearForm()
                                navController.navigate("catalog")
                            },
                            onEditClick = { id -> navController.navigate("form/$id") },
                            onDeleteClick = { subscription ->
                                viewModel.deleteSubscription(subscription) { deletedId ->
                                    ReminderScheduler.cancel(this@MainActivity, deletedId)
                                }
                            }
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
