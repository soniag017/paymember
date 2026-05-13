package com.paymember.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paymember.data.model.BillingPeriod
import com.paymember.ui.components.Eyebrow
import com.paymember.ui.components.MoneyText
import com.paymember.ui.components.SectionCard
import com.paymember.ui.components.ServiceLogo
import com.paymember.ui.components.Services
import java.util.Locale

@Composable
fun SubscriptionPlansScreen(
    service: SubscriptionTemplate,
    onPlanSelected: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val recommendedPlanId = service.plans.getOrNull(1)?.id ?: service.plans.firstOrNull()?.id
    var selectedPlanId by remember(service.id) { mutableStateOf(recommendedPlanId ?: service.plans.firstOrNull()?.id.orEmpty()) }
    val selectedPlan = service.plans.firstOrNull { it.id == selectedPlanId } ?: service.plans.first()

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
                PlansHeader(onBackClick = onBackClick)
            }

            item {
                StepProgress(step = 2)
            }

            item {
                ServiceHeader(service = service)
            }

            items(service.plans, key = { it.id }) { plan ->
                PlanCard(
                    service = service,
                    plan = plan,
                    recommended = plan.id == recommendedPlanId,
                    selected = plan.id == selectedPlanId,
                    onClick = { selectedPlanId = plan.id }
                )
            }

            item {
                Button(
                    onClick = { onPlanSelected(selectedPlan.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Continuar con ${selectedPlan.name}")
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
            item {
                Text(
                    "Precios orientativos. Algunos servicios cambian tarifas por promociones, impuestos, tiendas de apps o proveedores externos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }
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
private fun PlansHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        }
        Column {
            Eyebrow("PASO 2 DE 3")
            Text("Elige tarifa", style = MaterialTheme.typography.headlineLarge)
        }
    }
}

@Composable
private fun ServiceHeader(service: SubscriptionTemplate) {
    val brand = Services.brandFor(service.id)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = brand.bg,
        contentColor = brand.fg,
        shape = MaterialTheme.shapes.large
    ) {
        Box {
            Text(
                brand.mark,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp),
                color = brand.fg.copy(alpha = 0.13f),
                fontSize = 84.sp,
                fontWeight = FontWeight.Black
            )
            Row(
                modifier = Modifier.padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ServiceLogo(brand, size = 54.dp)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Eyebrow("SERVICIO", color = brand.fg.copy(alpha = 0.70f))
                    Text(service.name, style = MaterialTheme.typography.titleLarge, color = brand.fg)
                    Text(
                        service.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = brand.fg.copy(alpha = 0.74f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlanCard(
    service: SubscriptionTemplate,
    plan: SubscriptionPlanTemplate,
    recommended: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    val brand = Services.brandFor(service.id)

    SectionCard(
        modifier = Modifier.clickable(onClick = onClick),
        padding = PaddingValues(16.dp),
        borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (selected) {
                                Surface(
                                    modifier = Modifier.size(22.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                                    }
                                }
                            }
                            Text(plan.name, style = MaterialTheme.typography.titleMedium)
                        }
                        if (recommended) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ) {
                                Text(
                                    "Recomendado",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        plan.details.take(3).forEach { detail ->
                            PlanTag(text = detail)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .widthIn(min = 58.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    PlanPrice(plan)
                    Text(
                        if (plan.period == BillingPeriod.MONTHLY) "mensual" else "anual",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanTag(text: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}

@Composable
private fun PlanPrice(plan: SubscriptionPlanTemplate) {
    val value = plan.price.replace(',', '.').toDoubleOrNull() ?: 0.0
    if (plan.currencyLabel == "EUR") {
        MoneyText(value, size = 24.sp)
    } else {
        Text(
            String.format(Locale.getDefault(), "%.2f %s", value, plan.currencyLabel),
            style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum")
        )
    }
}
