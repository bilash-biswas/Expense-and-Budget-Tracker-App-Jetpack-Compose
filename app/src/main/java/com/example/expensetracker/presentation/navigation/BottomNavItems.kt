package com.example.expensetracker.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard: BottomNavItem(
        route = "dashboard",
        title = "Dashboard",
        icon = Icons.Default.Home
    )

    object Expenses: BottomNavItem(
        route = "expenses",
        title = "Expenses",
        icon = Icons.Default.Payments
    )

    object Budget: BottomNavItem(
        route = "budget",
        title = "Budget",
        icon = Icons.Default.AccountBalanceWallet
    )

    object Analytics: BottomNavItem(
        route = "analytics",
        title = "Analytics",
        icon = Icons.Default.Analytics
    )

    object More: BottomNavItem(
        route = "more",
        title = "More",
        icon = Icons.Default.Menu
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.Expenses,
    BottomNavItem.Budget,
    BottomNavItem.Analytics,
    BottomNavItem.More
)