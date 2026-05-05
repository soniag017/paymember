package com.paymember

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paymember.data.db.AppDatabase
import com.paymember.data.repository.SubscriptionRepository
import com.paymember.ui.screens.SubscriptionFormScreen
import com.paymember.ui.screens.SubscriptionListScreen
import com.paymember.viewmodel.SubscriptionViewModel
import com.paymember.viewmodel.SubscriptionViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = SubscriptionRepository(AppDatabase.getInstance(this).subscriptionDao())

        setContent {
            val navController = rememberNavController()
            val viewModel: SubscriptionViewModel = viewModel(
                factory = SubscriptionViewModelFactory(repository)
            )

            val subscriptions by viewModel.subscriptions.collectAsState()
            val formState by viewModel.formState.collectAsState()

            NavHost(navController = navController, startDestination = "list") {
                composable("list") {
                    SubscriptionListScreen(
                        subscriptions = subscriptions,
                        onAddClick = {
                            viewModel.clearForm()
                            navController.navigate("form/0")
                        },
                        onEditClick = { id -> navController.navigate("form/$id") },
                        onDeleteClick = viewModel::deleteSubscription
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
                        onFormChange = viewModel::updateForm,
                        onSaveClick = {
                            viewModel.saveSubscription {
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
