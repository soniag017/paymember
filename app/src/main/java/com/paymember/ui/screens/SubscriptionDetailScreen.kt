package com.paymember.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paymember.data.analysis.BillingCharge
import com.paymember.data.analysis.BillingHistoryCalculator
import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import com.paymember.ui.components.Eyebrow
import com.paymember.ui.components.MoneyText
import com.paymember.ui.components.SectionCard
import com.paymember.ui.components.ServiceLogo
import com.paymember.ui.components.Services
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SubscriptionDetailScreen(
    subscription: SubscriptionEntity?,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        if (subscription == null) {
            MissingSubscription(onBackClick = onBackClick)
            return@Surface
        }

        val history = remember(subscription) { BillingHistoryCalculator.calculate(subscription) }
        val charges = history.charges.take(24)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                DetailHeader(
                    title = subscription.cleanName(),
                    onBackClick = onBackClick,
                    onEditClick = onEditClick
                )
            }

            item {
                SpendingSummaryCard(
                    subscription = subscription,
                    totalSpent = history.totalSpent,
                    chargeCount = history.chargeCount
                )
            }

            item {
                NextChargeCard(subscription = subscription)
            }

            item {
                Text("Historial de cobros", style = MaterialTheme.typography.titleMedium)
            }

            if (charges.isEmpty()) {
                item {
                    SectionCard {
                        Text(
                            "Aun no hay cobros registrados desde la fecha de inicio.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(charges, key = { "${it.date}-${it.amount}" }) { charge ->
                    ChargeHistoryRow(charge = charge, period = subscription.period)
                }
            }
        }
    }
}

@Composable
private fun MissingSubscription(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailHeader(title = "Suscripcion", onBackClick = onBackClick, onEditClick = {})
        SectionCard {
            Text(
                "No se ha encontrado esta suscripcion.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailHeader(
    title: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
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
                Eyebrow("DETALLE")
                Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
        }
    }
}

@Composable
private fun SpendingSummaryCard(
    subscription: SubscriptionEntity,
    totalSpent: Double,
    chargeCount: Int
) {
    SectionCard(padding = PaddingValues(18.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                ServiceLogo(Services.brandFor(inferBrandKey(subscription.serviceName)), size = 48.dp)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Eyebrow("TOTAL GASTADO")
                    Text(subscription.planName(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            MoneyText(totalSpent, size = 44.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricPill(label = "Cobros", value = chargeCount.toString(), modifier = Modifier.weight(1f))
                MetricPill(
                    label = "Desde",
                    value = subscription.startLabel(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NextChargeCard(subscription: SubscriptionEntity) {
    val nextCharge = remember(subscription) { BillingHistoryCalculator.nextChargeDate(subscription) }
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Eyebrow("PROXIMO COBRO")
                Text(nextCharge.longLabel(), style = MaterialTheme.typography.titleMedium)
                Text(
                    if (subscription.period == BillingPeriod.MONTHLY) "Renovacion mensual" else "Renovacion anual",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatMoney(subscription.price), style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"))
                Text(if (subscription.period == BillingPeriod.MONTHLY) "/MES" else "/ANO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Eyebrow(label)
            Text(value, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ChargeHistoryRow(
    charge: BillingCharge,
    period: BillingPeriod
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(charge.date.longLabel(), style = MaterialTheme.typography.titleMedium)
            Text(
                if (period == BillingPeriod.MONTHLY) "Cobro mensual" else "Cobro anual",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(formatMoney(charge.amount), style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"))
    }
}

private fun SubscriptionEntity.cleanName(): String = serviceName.substringBefore(" - ").ifBlank { serviceName }

private fun SubscriptionEntity.planName(): String = serviceName.substringAfter(" - ", if (period == BillingPeriod.MONTHLY) "Mensual" else "Anual")

private fun SubscriptionEntity.startLabel(): String {
    return runCatching { LocalDate.parse(startDate) }
        .getOrNull()
        ?.shortLabel()
        ?: "Hoy"
}

private fun LocalDate.shortLabel(): String {
    val monthLabel = month.getDisplayName(TextStyle.SHORT, Locale("es", "ES")).lowercase(Locale("es", "ES"))
    return "$dayOfMonth $monthLabel $year"
}

private fun LocalDate.longLabel(): String {
    val monthLabel = month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        .replaceFirstChar { it.uppercase(Locale("es", "ES")) }
    return "$dayOfMonth de $monthLabel de $year"
}

private fun inferBrandKey(name: String): String {
    val cleaned = name.lowercase(Locale.getDefault())
    return PopularSubscriptionTemplates.firstOrNull { template ->
        cleaned.startsWith(template.name.lowercase(Locale.getDefault()).take(5)) ||
            cleaned.contains(template.id.replace("-", " "))
    }?.id ?: cleaned.take(2)
}

private fun formatMoney(value: Double): String {
    return String.format(Locale.getDefault(), "%.2f EUR", value)
}
