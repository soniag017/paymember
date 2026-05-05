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
import com.paymember.ui.screens.SubscriptionFormScreen
import com.paymember.ui.screens.SubscriptionListScreen
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
                                navController.navigate("form/0")
                            },
                            onEditClick = { id -> navController.navigate("form/$id") },
                            onDeleteClick = { subscription ->
                                viewModel.deleteSubscription(subscription) { deletedId ->
                                    ReminderScheduler.cancel(this@MainActivity, deletedId)
                                }
                            }
                        )
                    }

                    composable(
                        route = "form/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        LaunchedEffect(id) {
                            viewModel.loadSubscription(id)
                        }

                        SubscriptionFormScreen(
                            formState = formState,
                            isFormValid = viewModel.isFormValid(),
                            onFormChange = viewModel::updateForm,
                            onSaveClick = {
                                viewModel.saveSubscription { saved ->
                                    ReminderScheduler.schedule(this@MainActivity, saved)
                                    navController.popBackStack()
                                }
                            },
                            onBackClick = { navController.popBackStack() },
                            isEdit = id != 0
                        )
                    }
                }
            }
        }
    }
}
