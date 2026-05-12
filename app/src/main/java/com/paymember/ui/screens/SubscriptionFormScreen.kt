package com.paymember.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paymember.data.model.BillingPeriod
import com.paymember.ui.components.Eyebrow
import com.paymember.ui.components.MoneyText
import com.paymember.ui.components.SectionCard
import com.paymember.ui.components.ServiceLogo
import com.paymember.ui.components.Services
import com.paymember.viewmodel.SubscriptionFormState
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.max

@Composable
fun SubscriptionFormScreen(
    formState: SubscriptionFormState,
    isFormValid: Boolean,
    onFormChange: (SubscriptionFormState.() -> SubscriptionFormState) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    isEdit: Boolean,
    isManual: Boolean
) {
    var splitPeople by remember(formState.serviceName, isEdit, isManual) { mutableIntStateOf(1) }
    var totalPriceInput by remember(formState.serviceName, isEdit, isManual) { mutableStateOf(formState.price) }
    var notesExpanded by remember { mutableStateOf(false) }
    val basePrice = totalPriceInput.replace(',', '.').toDoubleOrNull() ?: 0.0
    val currentPrice = formState.price.replace(',', '.').toDoubleOrNull() ?: 0.0
    val billingDayValue = formState.billingDay.toIntOrNull()
    val billingDayError = formState.billingDay.isNotBlank() &&
        (billingDayValue == null || billingDayValue !in 1..31)

    LaunchedEffect(formState.serviceName, isEdit, isManual) {
        if (!isManual && !isEdit && formState.price.isNotBlank()) {
            totalPriceInput = formState.price
        }
    }

    LaunchedEffect(totalPriceInput, splitPeople) {
        if (!isEdit && basePrice > 0.0) {
            onFormChange {
                copy(price = formatPrice(basePrice / splitPeople))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FormHeader(
            title = when {
                isEdit -> "Editar cobro"
                isManual -> "Suscripción personalizada"
                else -> "Configurar cobro"
            },
            onBackClick = onBackClick
        )
        StepProgress(step = 3)

        if (isManual) {
            ManualPlanCard(
                formState = formState,
                totalPriceInput = totalPriceInput,
                onTotalPriceChange = { value ->
                    if (value.all { it.isDigit() || it == '.' || it == ',' }) {
                        totalPriceInput = value
                        val total = value.replace(',', '.').toDoubleOrNull()
                        onFormChange { copy(price = total?.let { formatPrice(it / splitPeople) } ?: "") }
                    }
                },
                onFormChange = onFormChange
            )
        } else {
            LockedPlanSummary(formState = formState)
        }

        if (!isEdit) {
            SplitPriceCard(
                basePrice = basePrice,
                people = splitPeople,
                currentPrice = currentPrice,
                onPeopleChange = { people -> splitPeople = people.coerceIn(1, 20) }
            )
        }

        BillingCalendarCard(
            selectedDay = billingDayValue,
            isError = billingDayError,
            onDaySelected = { day -> onFormChange { copy(billingDay = day.toString()) } }
        )

        ReminderCard(
            enabled = formState.reminderEnabled,
            daysBefore = formState.reminderDaysBefore,
            onEnabledChange = { enabled -> onFormChange { copy(reminderEnabled = enabled) } },
            onDaysBeforeChange = { days -> onFormChange { copy(reminderDaysBefore = days) } }
        )

        SectionCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Eyebrow("OPCIONAL")
                    Text("Notas", style = MaterialTheme.typography.titleMedium)
                }
                OutlinedButton(onClick = { notesExpanded = !notesExpanded }, shape = CircleShape) {
                    Text(if (notesExpanded) "Ocultar" else "Añadir")
                }
            }
            if (notesExpanded) {
                OutlinedTextField(
                    value = formState.notes,
                    onValueChange = { value -> onFormChange { copy(notes = value) } },
                    label = { Text("Notas") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
        }

        Button(
            onClick = onSaveClick,
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                if (isEdit) "Guardar cambios"
                else "Crear suscripción · ${formatMoney(currentPrice)}/mes"
            )
        }

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Volver")
        }
    }
}

@Composable
private fun FormHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Eyebrow("PASO 3 DE 3")
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun StepProgress(step: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (index < step) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}

@Composable
private fun ManualPlanCard(
    formState: SubscriptionFormState,
    totalPriceInput: String,
    onTotalPriceChange: (String) -> Unit,
    onFormChange: (SubscriptionFormState.() -> SubscriptionFormState) -> Unit
) {
    SectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Eyebrow("MANUAL")
            OutlinedTextField(
                value = formState.serviceName,
                onValueChange = { value -> onFormChange { copy(serviceName = value) } },
                label = { Text("Nombre") },
                placeholder = { Text("Gimnasio, Patreon, periódico...") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = totalPriceInput,
                onValueChange = onTotalPriceChange,
                label = { Text("Precio total") },
                suffix = { Text("EUR") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = formState.period == BillingPeriod.MONTHLY,
                    onClick = { onFormChange { copy(period = BillingPeriod.MONTHLY) } },
                    label = { Text("Mensual") }
                )
                FilterChip(
                    selected = formState.period == BillingPeriod.YEARLY,
                    onClick = { onFormChange { copy(period = BillingPeriod.YEARLY) } },
                    label = { Text("Anual") }
                )
            }
        }
    }
}

@Composable
private fun LockedPlanSummary(formState: SubscriptionFormState) {
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ServiceLogo(Services.brandFor(inferBrandKey(formState.serviceName)), size = 44.dp)
            Column(modifier = Modifier.weight(1f)) {
                Eyebrow("TARIFA SELECCIONADA")
                Text(formState.serviceName, style = MaterialTheme.typography.titleMedium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${formState.price.replace('.', ',')} €",
                    style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum")
                )
                AssistChip(
                    onClick = {},
                    label = { Text(if (formState.period == BillingPeriod.MONTHLY) "Mensual" else "Anual") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }
    }
}

@Composable
private fun SplitPriceCard(
    basePrice: Double,
    people: Int,
    currentPrice: Double,
    onPeopleChange: (Int) -> Unit
) {
    SectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Dividir entre personas", style = MaterialTheme.typography.titleMedium)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onPeopleChange(max(1, people - 1)) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Quitar persona")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        repeat(people.coerceAtMost(8)) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                    Text(
                        "$people",
                        style = MaterialTheme.typography.displayMedium.copy(fontFeatureSettings = "tnum")
                    )
                    Text(if (people == 1) "persona" else "personas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { onPeopleChange(people + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir persona")
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Eyebrow("TU PARTE", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f))
                        Text("${formatMoney(basePrice)} ÷ $people", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f))
                    }
                    MoneyText(currentPrice, size = 32.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
private fun BillingCalendarCard(
    selectedDay: Int?,
    isError: Boolean,
    onDaySelected: (Int) -> Unit
) {
    val month = remember { YearMonth.now() }
    val today = LocalDate.now()
    val monthName = month.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        .replaceFirstChar { it.uppercase(Locale("es", "ES")) }
    val leadingBlanks = month.atDay(1).dayOfWeek.value - 1
    val cells = List(leadingBlanks) { null } + (1..month.lengthOfMonth()).map { it }

    SectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column {
                        Text("Día de cobro", style = MaterialTheme.typography.titleMedium)
                        Text("$monthName ${month.year}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    Text(
                        selectedDay?.let { "Día $it" } ?: "Elegir",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            cells.chunked(7).forEach { week ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    week.forEach { day ->
                        CalendarDayCell(
                            day = day,
                            selected = day != null && day == selectedDay,
                            today = day != null && day == today.dayOfMonth,
                            onClick = { if (day != null) onDaySelected(day) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(7 - week.size) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }

            if (isError || selectedDay == null) {
                Text(
                    if (isError) "Elige un día válido del calendario." else "Selecciona el día en el que se cobra la suscripción.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int?,
    selected: Boolean,
    today: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = when {
        selected -> MaterialTheme.colorScheme.primary
        today -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val foreground = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .then(if (day != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(if (day == null) MaterialTheme.colorScheme.surface else background),
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(day.toString(), color = foreground, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                if (today && !selected) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    enabled: Boolean,
    daysBefore: Int,
    onEnabledChange: (Boolean) -> Unit,
    onDaysBeforeChange: (Int) -> Unit
) {
    val options = listOf(0, 1, 3, 7)

    SectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column {
                        Text("Aviso antes del cobro", style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (enabled) reminderLabel(daysBefore) else "Sin aviso",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(checked = enabled, onCheckedChange = onEnabledChange)
            }

            if (enabled) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { option ->
                        ReminderSegment(
                            days = option,
                            selected = daysBefore == option,
                            onClick = { onDaysBeforeChange(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                AssistChip(onClick = { onEnabledChange(true) }, label = { Text("Activar recordatorio") })
            }
        }
    }
}

@Composable
private fun ReminderSegment(
    days: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(if (days == 0) "El día" else "${days}d", style = MaterialTheme.typography.labelLarge)
        }
    }
}

private fun reminderLabel(daysBefore: Int): String {
    return when (daysBefore) {
        0 -> "Te avisamos el mismo día"
        1 -> "Te avisamos 1 día antes"
        else -> "Te avisamos $daysBefore días antes"
    }
}

private fun inferBrandKey(name: String): String {
    val cleaned = name.lowercase(Locale.getDefault())
    return PopularSubscriptionTemplates.firstOrNull { template ->
        cleaned.startsWith(template.name.lowercase(Locale.getDefault()).take(5)) ||
            cleaned.contains(template.id.replace("-", " "))
    }?.id ?: cleaned.take(2)
}

private fun formatPrice(value: Double): String {
    return String.format(Locale.US, "%.2f", value)
}

private fun formatMoney(value: Double): String {
    return String.format(Locale.getDefault(), "%.2f €", value)
}
