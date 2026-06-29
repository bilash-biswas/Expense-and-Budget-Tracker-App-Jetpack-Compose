package com.example.expensetracker.presentation.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────
// Core Shimmer Brush
// ─────────────────────────────────────────────

@Composable
fun shimmerBrush(
    targetValue: Float = 1000f,
    showShimmer: Boolean = true,
    shimmerColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
    highlightColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f)
): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(shimmerColor, highlightColor, shimmerColor)
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnimation by transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_translate"
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation, y = translateAnimation)
        )
    } else {
        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }
}

// ─────────────────────────────────────────────
// Generic Shimmer Box
// ─────────────────────────────────────────────

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp)
) {
    val brush = shimmerBrush()
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

// ─────────────────────────────────────────────
// Expense List Item Shimmer
// ─────────────────────────────────────────────

@Composable
fun ExpenseItemShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon circle
        ShimmerBox(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(50)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            // Title line
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Subtitle line
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(11.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        // Amount
        ShimmerBox(
            modifier = Modifier
                .width(64.dp)
                .height(16.dp)
        )
    }
}

@Composable
fun ExpenseListShimmer(itemCount: Int = 7) {
    Column {
        repeat(itemCount) {
            ExpenseItemShimmer()
        }
    }
}

// ─────────────────────────────────────────────
// Budget Card Shimmer
// ─────────────────────────────────────────────

@Composable
fun BudgetCardShimmer() {
    val brush = shimmerBrush()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(brush)
            .height(130.dp)
    )
}

@Composable
fun BudgetListShimmer(itemCount: Int = 4) {
    Column {
        // Header stat card
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        repeat(itemCount) {
            BudgetCardShimmer()
        }
    }
}

// ─────────────────────────────────────────────
// Dashboard Shimmer (Summary Card + Items)
// ─────────────────────────────────────────────

@Composable
fun DashboardShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero summary card
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(24.dp)
        )
        // Quick stats row
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ShimmerBox(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                shape = RoundedCornerShape(16.dp)
            )
            ShimmerBox(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                shape = RoundedCornerShape(16.dp)
            )
        }
        // Section title
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(14.dp)
        )
        // Recent transactions
        repeat(4) {
            ExpenseItemShimmer()
        }
    }
}

// ─────────────────────────────────────────────
// Analytics Shimmer (Chart Skeleton + Legend)
// ─────────────────────────────────────────────

@Composable
fun AnalyticsShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chart placeholder
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(20.dp)
        )
        // Legend row items
        repeat(3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(
                    modifier = Modifier.size(12.dp),
                    shape = RoundedCornerShape(50)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(12.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                ShimmerBox(
                    modifier = Modifier
                        .width(48.dp)
                        .height(12.dp)
                )
            }
        }
        // Second chart
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(20.dp)
        )
    }
}
