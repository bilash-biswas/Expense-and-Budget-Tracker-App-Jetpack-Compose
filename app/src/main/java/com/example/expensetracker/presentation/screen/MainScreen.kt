package com.example.expensetracker.presentation.screen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.presentation.navigation.BottomNavItem
import com.example.expensetracker.presentation.navigation.NavigationHost
import com.example.expensetracker.presentation.navigation.bottomNavItems

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavItems = remember { bottomNavItems }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != "splash" && currentRoute != "onboarding" && currentRoute != "unlock"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    items = bottomNavItems
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            modifier = if (showBottomBar) Modifier.padding(innerPadding) else Modifier
        )
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentDestination?.isRouteInHierarchy(item.route) == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon, 
                        contentDescription = item.title,
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            )
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}