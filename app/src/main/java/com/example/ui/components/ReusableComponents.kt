package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * TradeFit ERP Reusable Components Library
 * Strictly styled to represent the White, Black, and Blue premium executive aesthetic.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeFitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    testTagStr: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTagStr.ifEmpty { "input_${label.lowercase().replace(" ", "_")}" }),
        label = { Text(text = label, fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ExecutiveBlue,
            unfocusedBorderColor = DarkSlateBorder,
            focusedLabelColor = ExecutiveBlue,
            unfocusedLabelColor = SlateTextDark,
            focusedContainerColor = PureWhite,
            unfocusedContainerColor = OffWhite,
            errorContainerColor = Color(0xFFFFF5F5)
        )
    )
}

@Composable
fun TradeFitButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTagStr: String = ""
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(52.dp)
            .testTag(testTagStr.ifEmpty { "button_${text.lowercase().replace(" ", "_")}" }),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ExecutiveBlue,
            contentColor = PureWhite,
            disabledContainerColor = SlateTextLight,
            disabledContentColor = PureWhite
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    trend: String? = null,
    isPositive: Boolean = true,
    testTagStr: String = "",
    containerColor: Color = PureWhite,
    contentColor: Color = MatteBlack,
    iconBgColor: Color = LightBlueAccent,
    iconColor: Color = ExecutiveBlue
) {
    Card(
        modifier = modifier
            .testTag(testTagStr.ifEmpty { "stat_card_${title.lowercase().replace(" ", "_")}" }),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = if (containerColor == PureWhite) CardBorder() else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (containerColor == PureWhite) SlateTextDark else contentColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )

            if (trend != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (isPositive) PositiveGrowth else WarningAlert,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trend,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPositive) PositiveGrowth else WarningAlert,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "vs last month",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (containerColor == PureWhite) SlateTextLight else contentColor.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun TradeFitSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Elegant vertical blue indicator bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ExecutiveBlue)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MatteBlack,
                fontWeight = FontWeight.Bold
            )
        }
        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                colors = ButtonDefaults.textButtonColors(contentColor = ExecutiveBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = actionText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CanvasStockChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(PureWhite)
            .border(1.dp, DarkSlateBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        val maxVal = dataPoints.maxOrNull() ?: 100f
        val minVal = dataPoints.minOrNull() ?: 0f
        val range = (maxVal - minVal).coerceAtLeast(1f)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val spacing = width / (dataPoints.size - 1)

            val points = dataPoints.mapIndexed { idx, value ->
                val x = idx * spacing
                // Subtract from height because Canvas y-coords increase downwards
                val y = height - ((value - minVal) / range) * height
                Offset(x, y)
            }

            // Draw area gradient under the line
            val fillPath = Path().apply {
                moveTo(0f, height)
                points.forEach { offset ->
                    lineTo(offset.x, offset.y)
                }
                lineTo(width, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(ExecutiveBlue.copy(alpha = 0.25f), Color.Transparent)
                )
            )

            // Draw line
            val strokePath = Path().apply {
                points.forEachIndexed { idx, offset ->
                    if (idx == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
                }
            }
            drawPath(
                path = strokePath,
                color = ExecutiveBlue,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw points
            points.forEach { offset ->
                drawCircle(
                    color = PureWhite,
                    radius = 5.dp.toPx(),
                    center = offset
                )
                drawCircle(
                    color = ExecutiveBlue,
                    radius = 3.dp.toPx(),
                    center = offset
                )
            }
        }
    }
}

@Composable
fun CardBorder(): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(1.dp, DarkSlateBorder)
}

// --------------------------------------------------------------------
// TRADEFIT ADDITIONAL PREMIUM REUSABLE COMPONENTS
// --------------------------------------------------------------------

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTagStr: String = ""
) {
    TradeFitButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        testTagStr = testTagStr
    )
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTagStr: String = ""
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(52.dp)
            .testTag(testTagStr.ifEmpty { "button_${text.lowercase().replace(" ", "_")}" }),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ExecutiveBlue,
            disabledContentColor = SlateTextLight
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, ExecutiveBlue),
        elevation = null
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeFitModal(
    title: String,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp),
        containerColor = PureWhite,
        titleContentColor = MatteBlack,
        textContentColor = SlateTextDark,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                content()
            }
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String = "Confirm",
    dismissLabel: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = PureWhite,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = WarningAlert,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = SlateTextDark)
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = WarningAlert)
            ) {
                Text(confirmLabel, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissLabel, color = SlateTextDark)
            }
        }
    )
}

@Composable
fun SearchComponent(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    testTagStr: String = ""
) {
    TradeFitTextField(
        value = query,
        onValueChange = onQueryChange,
        label = "Search",
        placeholder = placeholder,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
                tint = SlateTextDark
            )
        },
        testTagStr = testTagStr
    )
}

@Composable
fun FilterComponent(
    selectedFilter: String,
    availableFilters: List<String>,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        availableFilters.forEach { filter ->
            val isSelected = selectedFilter == filter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) ExecutiveBlue else PureWhite)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) ExecutiveBlue else DarkSlateBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) PureWhite else SlateTextDark,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSlateBorder.copy(alpha = alpha))
    )
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = null,
            tint = SlateTextLight,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MatteBlack,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = SlateTextLight,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun NotificationToast(
    message: String,
    onDismiss: () -> Unit
) {
    Snackbar(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        containerColor = ExecutiveBlue,
        contentColor = PureWhite,
        action = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = PureWhite)
            ) {
                Text("DISMISS", fontWeight = FontWeight.Bold)
            }
        }
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DataTable(
    headers: List<String>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OffWhite)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                headers.forEach { header ->
                    Text(
                        text = header.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateTextDark,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Divider(color = DarkSlateBorder, thickness = 1.dp)
            content()
        }
    }
}
