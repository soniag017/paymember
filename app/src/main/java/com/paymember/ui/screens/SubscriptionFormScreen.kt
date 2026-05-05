package com.paymember.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paymember.data.model.BillingPeriod
import com.paymember.viewmodel.SubscriptionFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionFormScreen(
    formState: SubscriptionFormState,
    onFormChange: (SubscriptionFormState.() -> SubscriptionFormState) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    isEdit: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (isEdit) "Editar suscripción" else "Nueva suscripción") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = formState.serviceName,
                onValueChange = { value -> onFormChange { copy(serviceName = value) } },
                label = { Text("Nombre del servicio") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.price,
                onValueChange = { value -> onFormChange { copy(price = value) } },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.billingDay,
                onValueChange = { value -> onFormChange { copy(billingDay = value) } },
                label = { Text("Día de cobro (1-31)") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = if (formState.period == BillingPeriod.MONTHLY) "Mensual" else "Anual",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Periodicidad") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Mensual") },
                        onClick = {
                            onFormChange { copy(period = BillingPeriod.MONTHLY) }
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Anual") },
                        onClick = {
                            onFormChange { copy(period = BillingPeriod.YEARLY) }
                            expanded = false
                        }
                    )
                }
            }

            Column {
                Text("Recordatorio")
                Switch(
                    checked = formState.reminderEnabled,
                    onCheckedChange = { checked -> onFormChange { copy(reminderEnabled = checked) } }
                )
            }

            OutlinedTextField(
                value = formState.notes,
                onValueChange = { value -> onFormChange { copy(notes = value) } },
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEdit) "Guardar cambios" else "Crear suscripción")
            }

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}
