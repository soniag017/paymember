package com.paymember.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.paymember.data.model.BillingPeriod
import com.paymember.viewmodel.SubscriptionFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionFormScreen(
    formState: SubscriptionFormState,
    isFormValid: Boolean,
    onFormChange: (SubscriptionFormState.() -> SubscriptionFormState) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    isEdit: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val normalizedPrice = formState.price.replace(',', '.')
    val priceValue = normalizedPrice.toDoubleOrNull()
    val billingDayValue = formState.billingDay.toIntOrNull()
    val showServiceNameError = formState.serviceName.isBlank() &&
        (formState.price.isNotBlank() || formState.billingDay.isNotBlank() || formState.notes.isNotBlank())
    val priceError = formState.price.isNotBlank() && (priceValue == null || priceValue <= 0.0)
    val billingDayError = formState.billingDay.isNotBlank() &&
        (billingDayValue == null || billingDayValue !in 1..31)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(if (isEdit) "Editar suscripcion" else "Nueva suscripcion")
                        Text(
                            "Completa los datos del servicio",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Datos principales", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = formState.serviceName,
                        onValueChange = { value -> onFormChange { copy(serviceName = value) } },
                        label = { Text("Nombre del servicio") },
                        isError = showServiceNameError,
                        supportingText = {
                            if (showServiceNameError) Text("El nombre es obligatorio")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = formState.price,
                        onValueChange = { value ->
                            if (value.all { it.isDigit() || it == '.' || it == ',' }) {
                                onFormChange { copy(price = value) }
                            }
                        },
                        label = { Text("Precio") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = priceError,
                        supportingText = {
                            if (priceError) Text("Introduce un precio valido mayor que 0")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = formState.billingDay,
                        onValueChange = { value ->
                            if (value.all { it.isDigit() }) {
                                onFormChange { copy(billingDay = value) }
                            }
                        },
                        label = { Text("Dia de cobro (1-31)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = billingDayError,
                        supportingText = {
                            if (billingDayError) Text("El dia debe estar entre 1 y 31")
                        },
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
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Recordatorio", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Te avisaremos antes del cobro",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
                }
            }

            Button(
                onClick = onSaveClick,
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEdit) "Guardar cambios" else "Crear suscripcion")
            }

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}
