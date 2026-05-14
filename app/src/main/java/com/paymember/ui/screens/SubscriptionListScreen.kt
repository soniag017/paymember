package com.paymember.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import com.paymember.ui.components.Eyebrow
import com.paymember.ui.components.MoneyText
import com.paymember.ui.components.SectionCard
import com.paymember.ui.components.ServiceLogo
import com.paymember.ui.components.Services
import com.paymember.ui.theme.Butter
import com.paymember.ui.theme.Coral
import com.paymember.ui.theme.Lavender
import com.paymember.ui.theme.Moss
import com.paymember.ui.theme.Rose
import com.paymember.ui.theme.Sky
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SubscriptionListScreen(
    subscriptions: List<SubscriptionEntity>,
    displayName: String,
    darkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onAddClick: () -> Unit,
    onManualClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (SubscriptionEntity) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 26.dp, top = 38.dp, end = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { HomeHeader(displayName = displayName, darkTheme = darkTheme, onToggleDarkTheme = onToggleDarkTheme) }
            item { CreateSubscriptionPanel(onCatalogClick = onAddClick, onManualClick = onManualClick) }
            item { MonthSectionHeader() }
            item { MonthlyHeroCard(subscriptions = subscriptions) }
            item { UpcomingStrip(subscriptions = subscriptions, onCalendarClick = onCalendarClick) }
            item { SubscriptionsHeader(count = subscriptions.size) }

            if (subscriptions.isEmpty()) {
                item { EmptyState(onAddClick = onAddClick) }
            } else {
                items(subscriptions, key = { it.id }) { item ->
                    SubscriptionItem(
                        item = item,
                        onClick = { onEditClick(item.id) },
                        onDeleteClick = { onDeleteClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    displayName: String,
    darkTheme: Boolean,
    onToggleDarkTheme: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("p", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Eyebrow("HOLA, ${displayName.uppercase(Locale.getDefault())}")
                Text(
                    "PayMember",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HeaderIconButton(icon = Icons.Default.Search, contentDescription = "Buscar")
            HeaderIconButton(
                icon = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (darkTheme) "Activar modo claro" else "Activar modo oscuro",
                onClick = onToggleDarkTheme
            )
            HeaderIconButton(icon = Icons.Default.Notifications, contentDescription = "Avisos")
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun CreateSubscriptionPanel(
    onCatalogClick: () -> Unit,
    onManualClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(214.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = MaterialTheme.shapes.large,
        shadowElevation = 8.dp
    ) {
        Box {
            DecorativeRings()
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Eyebrow("NUEVA SUSCRIPCIÓN", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.66f))
                Text(
                    "Añade un servicio\ndel catálogo",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 23.sp),
                    lineHeight = 25.sp
                )
                Text(
                    "${PopularSubscriptionTemplates.size}+ servicios listos, o créala\nmanualmente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f),
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Button(
                        onClick = onCatalogClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Catálogo", modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.labelLarge)
                    }
                    OutlinedButton(
                        onClick = onManualClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.42f)),
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Manual", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.DecorativeRings() {
    Box(
        modifier = Modifier
            .padding(top = 22.dp, end = 26.dp)
            .size(112.dp)
            .align(Alignment.TopEnd),
        contentAlignment = Alignment.Center
    ) {
        listOf(112.dp, 76.dp, 38.dp).forEach { size ->
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = if (size == 38.dp) 0.18f else 0.04f))
            )
        }
    }
}

@Composable
private fun MonthSectionHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Este mes", style = MaterialTheme.typography.titleMedium)
        Text(monthLabel(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MonthlyHeroCard(subscriptions: List<SubscriptionEntity>) {
    val totalMonthly = subscriptions.sumOf { it.monthlyEquivalent() }
    val annualProjection = totalMonthly * 12
    val month = Month.from(LocalDate.now()).getDisplayName(TextStyle.SHORT, Locale("es", "ES")).uppercase(Locale("es", "ES"))

    SectionCard(
        padding = PaddingValues(18.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Eyebrow("GASTO MENSUAL · $month")
                    MoneyText(totalMonthly, size = 42.sp)
                    Text(
                        "≈ ${formatMoney(annualProjection)} al año · ${subscriptions.size} activas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape
                ) {
                    Text(
                        "~ -€4,12",
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            DistributionBar(subscriptions.sortedByDescending { it.monthlyEquivalent() }.map { it.serviceName to it.monthlyEquivalent() })
            LegendRows(subscriptions.sortedByDescending { it.monthlyEquivalent() }.take(4))
        }
    }
}

@Composable
private fun DistributionBar(items: List<Pair<String, Double>>) {
    val colors = listOf(MaterialTheme.colorScheme.primary, Coral, Lavender, Butter, Sky, Moss, Rose)
    val total = items.sumOf { it.second }.coerceAtLeast(0.01)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.outlineVariant),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (items.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(MaterialTheme.colorScheme.outlineVariant))
        } else {
            items.take(7).forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .weight((item.second / total).toFloat().coerceAtLeast(0.03f))
                        .fillMaxHeight()
                        .background(colors[index % colors.size])
                )
            }
        }
    }
}

@Composable
private fun LegendRows(items: List<SubscriptionEntity>) {
    val colors = listOf(MaterialTheme.colorScheme.primary, Coral, Lavender, Butter)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEachIndexed { index, item ->
                    val color = colors[(items.indexOf(item)).coerceAtLeast(0) % colors.size]
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(MaterialTheme.shapes.extraSmall).background(color))
                        Text(
                            "${item.shortName()} · ${formatMoney(item.monthlyEquivalent())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (row.size == 1) Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun UpcomingStrip(
    subscriptions: List<SubscriptionEntity>,
    onCalendarClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Próximos cobros", style = MaterialTheme.typography.titleMedium)
            Surface(
                onClick = onCalendarClick,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    "Ver calendario",
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        if (subscriptions.isEmpty()) {
            SectionCard { Text("Añade una suscripción para ver tus próximos cargos.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            val upcoming = subscriptions
                .sortedWith(compareBy<SubscriptionEntity> { nextChargeDate(it) }.thenBy { it.serviceName })
                .take(8)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(upcoming, key = { it.id }) { item ->
                    UpcomingCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun UpcomingCard(item: SubscriptionEntity) {
    val nextCharge = nextChargeDate(item)
    val month = nextCharge.month.getDisplayName(TextStyle.SHORT, Locale("es", "ES")).uppercase(Locale("es", "ES"))

    SectionCard(modifier = Modifier.width(96.dp), padding = PaddingValues(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${nextCharge.dayOfMonth}", style = MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = "tnum"))
                Text(" $month", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                ServiceLogo(Services.brandFor(inferBrandKey(item.serviceName)), size = 22.dp)
                Text(formatMoney(item.price), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SubscriptionsHeader(count: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Tus suscripciones", style = MaterialTheme.typography.titleMedium)
        Text("$count activas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyState(onAddClick: () -> Unit) {
    SectionCard {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Aún no tienes suscripciones", style = MaterialTheme.typography.titleMedium)
            Text("Crea la primera y PayMember calculará tu gasto mensual automáticamente.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Surface(onClick = onAddClick, color = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary, shape = MaterialTheme.shapes.small) {
                Text("Crear suscripción", modifier = Modifier.padding(horizontal = 18.dp, vertical = 13.dp), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun SubscriptionItem(
    item: SubscriptionEntity,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    SectionCard(modifier = Modifier.clickable(onClick = onClick), padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            ServiceLogo(Services.brandFor(inferBrandKey(item.serviceName)), size = 40.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(item.cleanName(), style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${item.planName()} · día ${item.billingDay}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AssistChip(onClick = {}, enabled = false, label = { Text(reminderText(item.reminderEnabled, item.reminderDaysBefore)) })
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatMoney(item.price), style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"))
                Text(if (item.period == BillingPeriod.MONTHLY) "/MES" else "/A\u00d1O", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

private fun SubscriptionEntity.monthlyEquivalent(): Double {
    return if (period == BillingPeriod.MONTHLY) price else price / 12.0
}

private fun nextChargeDate(item: SubscriptionEntity): LocalDate {
    val today = LocalDate.now()
    val safeDay = item.billingDay.coerceIn(1, 31)

    if (item.period == BillingPeriod.MONTHLY) {
        val thisMonthDate = today.withDayOfMonth(safeDay.coerceAtMost(today.lengthOfMonth()))
        if (!thisMonthDate.isBefore(today)) return thisMonthDate

        val nextMonth = today.plusMonths(1)
        return nextMonth.withDayOfMonth(safeDay.coerceAtMost(nextMonth.lengthOfMonth()))
    }

    val annualMonth = today.month
    val thisYearMonth = YearMonth.of(today.year, annualMonth)
    val thisYearDate = thisYearMonth.atDay(safeDay.coerceAtMost(thisYearMonth.lengthOfMonth()))
    if (!thisYearDate.isBefore(today)) return thisYearDate

    val nextYearMonth = YearMonth.of(today.year + 1, annualMonth)
    return nextYearMonth.atDay(safeDay.coerceAtMost(nextYearMonth.lengthOfMonth()))
}

private fun SubscriptionEntity.cleanName(): String = serviceName.substringBefore(" - ").ifBlank { serviceName }

private fun SubscriptionEntity.planName(): String = serviceName.substringAfter(" - ", if (period == BillingPeriod.MONTHLY) "Mensual" else "Anual")

private fun SubscriptionEntity.shortName(): String = cleanName().take(12)

private fun reminderText(enabled: Boolean, daysBefore: Int): String {
    if (!enabled) return "Sin aviso"
    return when (daysBefore) {
        0 -> "Aviso hoy"
        1 -> "+1d"
        else -> "+${daysBefore}d"
    }
}

private fun inferBrandKey(name: String): String {
    val cleaned = name.lowercase(Locale.getDefault())
    return PopularSubscriptionTemplates.firstOrNull { template ->
        cleaned.startsWith(template.name.lowercase(Locale.getDefault()).take(5)) ||
            cleaned.contains(template.id.replace("-", " "))
    }?.id ?: cleaned.take(2)
}

private fun monthLabel(): String {
    val today = LocalDate.now()
    val month = today.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase(Locale("es", "ES")) }
    return "$month ${today.year}"
}

private fun formatMoney(value: Double): String {
    return String.format(Locale.getDefault(), "€%.2f", value)
}
