package com.paymember.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.paymember.data.model.BillingPeriod
import com.paymember.ui.components.Eyebrow
import com.paymember.ui.components.SectionCard
import com.paymember.ui.components.ServiceLogo
import com.paymember.ui.components.Services
import java.util.Locale

private val ScreenSidePadding = 24.dp

@Composable
fun SubscriptionCatalogScreen(
    onServiceSelected: (String) -> Unit,
    onManualClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("all") }
    var query by remember { mutableStateOf("") }
    val categories = remember(PopularSubscriptionTemplates.size, SubscriptionCategories.size) {
        listOf(CatalogCategory("all", "Todas", "Todas")) +
            SubscriptionCategories.sortedBy { it.title.lowercase(Locale("es", "ES")) }.map { category ->
                CatalogCategory(category.id, category.title, category.title)
            }
    }
    val selectedBaseServices = remember(selectedCategory) {
        if (selectedCategory == "all") {
            PopularSubscriptionTemplates
        } else {
            templatesForCategory(selectedCategory)
        }
    }
    val visibleServices = remember(selectedCategory, selectedBaseServices, query) {
        val base = if (selectedCategory == "all") {
            selectedBaseServices.sortedBy { it.displayName().lowercase(Locale("es", "ES")) }
        } else {
            selectedBaseServices.sortedBy { it.displayName().lowercase(Locale("es", "ES")) }
        }
        base.filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
    }
    val selectedMeta = categories.first { it.id == selectedCategory }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = ScreenSidePadding, top = 38.dp, end = ScreenSidePadding, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CatalogHeader(onBackClick = onBackClick)
            }

            item {
                SearchField(
                    value = query,
                    onValueChange = { query = it }
                )
            }

            item {
                ManualSubscriptionCard(onClick = onManualClick)
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Eyebrow("CATEGOR\u00cdAS")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp)
                    ) {
                        items(categories, key = { it.id }) { category ->
                            CategoryPill(
                                label = category.label,
                                selected = selectedCategory == category.id,
                                onClick = { selectedCategory = category.id }
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedMeta.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        countLabel(visibleServices.size, selectedBaseServices.size, query),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            visibleServices.chunked(2).forEach { row ->
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { service ->
                            ServiceCatalogCard(
                                service = service,
                                onClick = { onServiceSelected(service.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 1.dp
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", modifier = Modifier.size(20.dp))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Eyebrow("PASO 1 DE 3")
                Text("Elige un servicio", style = MaterialTheme.typography.titleLarge)
            }
        }
        Icon(Icons.Default.MoreVert, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.small,
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingIcon = {
            Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        },
        placeholder = {
            Text("Buscar Netflix, Spotify, gimnasio...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        singleLine = true
    )
}

@Composable
private fun ManualSubscriptionCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.24f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("\u00bfNo la encuentras?", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Crea una suscripci\u00f3n a mano",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Manual", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun CategoryPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = if (selected) 0.dp else 1.dp
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun ServiceCatalogCard(
    service: SubscriptionTemplate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SectionCard(
        modifier = modifier
            .height(154.dp)
            .clickable(onClick = onClick),
        padding = PaddingValues(14.dp),
        color = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outlineVariant
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    ServiceLogo(Services.brandFor(service.id), size = 44.dp)
                    PlanCountPill(count = service.plans.size)
                }
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        service.displayName(),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        service.categoryLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text("desde ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(service.lowestPriceLabel(), style = MaterialTheme.typography.titleSmall.copy(fontFeatureSettings = "tnum"))
                Text(" /mes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PlanCountPill(count: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = CircleShape
    ) {
        Text(
            "$count tarifas",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

private data class CatalogCategory(
    val id: String,
    val label: String,
    val title: String
)

private fun countLabel(visibleCount: Int, totalCount: Int, query: String): String {
    return if (query.isBlank() || visibleCount == totalCount) {
        "$totalCount servicios"
    } else {
        "$visibleCount de $totalCount servicios"
    }
}

private fun SubscriptionTemplate.displayName(): String {
    return when (id) {
        "icloud" -> "iCloud+"
        "disney" -> "Disney+"
        else -> name
    }
}

private fun SubscriptionTemplate.categoryLabel(): String {
    return when (id) {
        "spotify" -> "M\u00fasica"
        "chatgpt" -> "IA"
        "icloud" -> "Nube"
        else -> categoryTitleFor(categoryId)
    }
}

private fun categoryTitleFor(categoryId: String): String {
    return SubscriptionCategories.firstOrNull { it.id == categoryId }?.title.orEmpty()
}

private fun SubscriptionTemplate.lowestPriceLabel(): String {
    val lowest = plans.minOfOrNull { it.monthlyEquivalentPrice() ?: Double.MAX_VALUE }
    return if (lowest == null || lowest == Double.MAX_VALUE) {
        "-"
    } else {
        String.format(Locale.getDefault(), "\u20ac%.2f", lowest)
    }
}

private fun SubscriptionPlanTemplate.monthlyEquivalentPrice(): Double? {
    val value = price.replace(',', '.').toDoubleOrNull() ?: return null
    return if (period == BillingPeriod.YEARLY) value / 12.0 else value
}
