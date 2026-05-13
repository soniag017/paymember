package com.paymember.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(16.dp),
    color: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(Modifier.padding(padding), content = content)
    }
}

@Composable
fun Eyebrow(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = text.uppercase(Locale.getDefault()),
        color = color,
        style = MaterialTheme.typography.labelMedium
    )
}

@Composable
fun MoneyText(
    value: Double,
    size: TextUnit = 40.sp,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val txt = String.format(Locale.US, "%.2f", value)
    val parts = txt.split(".")
    val whole = parts.firstOrNull().orEmpty()
    val cents = parts.getOrNull(1) ?: "00"

    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = "€",
            color = color.copy(alpha = 0.58f),
            fontSize = (size.value * 0.55f).sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = whole,
            color = color,
            fontSize = size,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.displayMedium.copy(fontFeatureSettings = "tnum")
        )
        Text(
            text = ",$cents",
            color = color.copy(alpha = 0.55f),
            fontSize = (size.value * 0.55f).sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum")
        )
    }
}

data class ServiceBrand(
    val key: String,
    val bg: Color,
    val fg: Color,
    val mark: String,
    val drawable: Int? = null
)

@Composable
fun ServiceLogo(
    service: ServiceBrand,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.30f))
            .background(service.bg),
        contentAlignment = Alignment.Center
    ) {
        if (service.drawable != null) {
            Icon(
                painter = painterResource(service.drawable),
                contentDescription = null,
                tint = service.fg,
                modifier = Modifier.size(size * 0.58f)
            )
        } else {
            Text(
                text = service.mark,
                color = service.fg,
                fontWeight = FontWeight.Bold,
                fontSize = (if (service.mark.length <= 2) size.value * 0.42f else size.value * 0.26f).sp,
                maxLines = 1
            )
        }
    }
}
