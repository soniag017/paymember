package com.paymember.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import com.paymember.ui.components.Eyebrow
import com.paymember.ui.components.SectionCard
import com.paymember.ui.components.ServiceLogo
import com.paymember.ui.components.Services
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun BillingCalendarScreen(
    subscriptions: List<SubscriptionEntity>,
    onBackClick: () -> Unit
) {
    var visibleMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val chargesByDay = remember(subscriptions, visibleMonth) {
        subscriptions
            .mapNotNull { item -> chargeDateForMonth(item, visibleMonth)?.let { date -> date.dayOfMonth to item } }
            .groupBy({ it.first }, { it.second })
    }
    val selectedCharges = chargesByDay[selectedDate.dayOfMonth].orEmpty()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CalendarHeader(onBackClick = onBackClick)
            }

            item {
                MonthSelector(
                    month = visibleMonth,
                    onPrevious = {
                        val newMonth = visibleMonth.minusMonths(1)
                        visibleMonth = newMonth
                        selectedDate = newMonth.atDay(selectedDate.dayOfMonth.coerceAtMost(newMonth.lengthOfMonth()))
                    },
                    onNext = {
                        val newMonth = visibleMonth.plusMonths(1)
                        visibleMonth = newMonth
                        selectedDate = newMonth.atDay(selectedDate.dayOfMonth.coerceAtMost(newMonth.lengthOfMonth()))
                    }
                )
            }

            item {
                CalendarGrid(
                    month = visibleMonth,
                    selectedDate = selectedDate,
                    chargesByDay = chargesByDay,
                    onDaySelected = { day -> selectedDate = visibleMonth.atDay(day) }
                )
            }

            item {
                DayChargeDetails(
                    date = selectedDate,
                    charges = selectedCharges
                )
            }
        }
    }
}

@Composable
private fun CalendarHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            Eyebrow("CALENDARIO")
            Text("Cobros programados", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun MonthSelector(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    SectionCard(padding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior", onClick = onPrevious)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(month.monthName(), style = MaterialTheme.typography.titleMedium)
                Text("${month.year}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            HeaderIconButton(icon = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente", onClick = onNext)
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(38.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    chargesByDay: Map<Int, List<SubscriptionEntity>>,
    onDaySelected: (Int) -> Unit
) {
    val leadingBlanks = month.atDay(1).dayOfWeek.value - 1
    val cells = List(leadingBlanks) { null } + (1..month.lengthOfMonth()).map { it }
    val today = LocalDate.now()

    SectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        CalendarDay(
                            day = day,
                            charges = day?.let { chargesByDay[it] }.orEmpty(),
                            selected = day != null && selectedDate.year == month.year && selectedDate.month == month.month && selectedDate.dayOfMonth == day,
                            today = day != null && today.year == month.year && today.month == month.month && today.dayOfMonth == day,
                            onClick = { if (day != null) onDaySelected(day) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(7 - week.size) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int?,
    charges: List<SubscriptionEntity>,
    selected: Boolean,
    today: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = when {
        selected -> MaterialTheme.colorScheme.primaryContainer
        today -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val border = when {
        selected -> MaterialTheme.colorScheme.primary
        charges.isNotEmpty() -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Surface(
        modifier = modifier
            .height(74.dp)
            .then(if (day != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = MaterialTheme.shapes.small,
        color = if (day == null) MaterialTheme.colorScheme.background else background,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        border = if (day == null) null else BorderStroke(1.dp, border)
    ) {
        if (day != null) {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(day.toString(), style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum"))
                    if (charges.size > 2) {
                        Text("+${charges.size - 2}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    charges.take(2).forEach { item ->
                        ServiceLogo(Services.brandFor(inferBrandKey(item.serviceName)), size = 18.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DayChargeDetails(
    date: LocalDate,
    charges: List<SubscriptionEntity>
) {
    SectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Eyebrow("COBROS DEL DIA")
                    Text(date.dayLabel(), style = MaterialTheme.typography.titleMedium)
                }
                Surface(color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer, shape = CircleShape) {
                    Text(
                        "${charges.size} cobros",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            if (charges.isEmpty()) {
                Text(
                    "No hay cobros programados para este dia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                charges.sortedBy { it.serviceName }.forEach { item ->
                    ChargeRow(item = item)
                }
            }
        }
    }
}

@Composable
private fun ChargeRow(item: SubscriptionEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f), MaterialTheme.shapes.small)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ServiceLogo(Services.brandFor(inferBrandKey(item.serviceName)), size = 40.dp)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(item.cleanName(), style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "${item.planName()} · ${if (item.period == BillingPeriod.MONTHLY) "Mensual" else "Anual"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                if (item.reminderEnabled) "Aviso ${reminderText(item.reminderDaysBefore)}" else "Sin recordatorio",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatMoney(item.price), style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"))
            Text(if (item.period == BillingPeriod.MONTHLY) "/MES" else "/A\u00d1O", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun chargeDateForMonth(item: SubscriptionEntity, month: YearMonth): LocalDate? {
    val safeDay = item.billingDay.coerceIn(1, 31)
    if (item.period == BillingPeriod.YEARLY && month.month != LocalDate.now().month) return null
    return month.atDay(safeDay.coerceAtMost(month.lengthOfMonth()))
}

private fun YearMonth.monthName(): String {
    return month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        .replaceFirstChar { it.uppercase(Locale("es", "ES")) }
}

private fun LocalDate.dayLabel(): String {
    val monthLabel = month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        .replaceFirstChar { it.uppercase(Locale("es", "ES")) }
    return "$dayOfMonth de $monthLabel"
}

private fun SubscriptionEntity.cleanName(): String = serviceName.substringBefore(" - ").ifBlank { serviceName }

private fun SubscriptionEntity.planName(): String = serviceName.substringAfter(" - ", if (period == BillingPeriod.MONTHLY) "Mensual" else "Anual")

private fun reminderText(daysBefore: Int): String {
    return when (daysBefore) {
        0 -> "el mismo dia"
        1 -> "1 dia antes"
        else -> "$daysBefore dias antes"
    }
}

private fun inferBrandKey(name: String): String {
    val cleaned = name.lowercase(Locale.getDefault())
    return PopularSubscriptionTemplates.firstOrNull { template ->
        cleaned.startsWith(template.name.lowercase(Locale.getDefault()).take(5)) ||
            cleaned.contains(template.id.replace("-", " "))
    }?.id ?: cleaned.take(2)
}

private fun formatMoney(value: Double): String {
    return String.format(Locale.getDefault(), "€%.2f", value)
}
