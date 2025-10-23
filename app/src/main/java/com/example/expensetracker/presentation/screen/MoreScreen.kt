// presentation/screen/MoreScreen.kt
package com.example.expensetracker.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expensetracker.presentation.navigation.BottomNavItem

@Composable
fun MoreScreen(
    navController: NavController
) {
    val DataManagementOptions = listOf(
        SettingsOption(
            title = "Categories",
            description = "Manage expense categories",
            icon = Icons.Default.Category,
            onClick = {
                navController.navigate("category_management")
            }
        ),
        SettingsOption(
            title = "Export Data",
            description = "Export expenses and budgets",
            icon = Icons.Default.ImportExport,
            onClick = { navController.navigate("export") }
        ),
        SettingsOption(
            title = "Analytics",
            description = "View detailed spending analytics",
            icon = Icons.Default.Analytics,
            onClick = { navController.navigate(BottomNavItem.Analytics.route) }
        )
    )

    val AppSettingsOptions = listOf(
        SettingsOption(
            title = "App Settings",
            description = "Customize app preferences",
            icon = Icons.Default.Settings,
            onClick = { /* Navigate to settings */ }
        ),
        SettingsOption(
            title = "Privacy & Security",
            description = "Manage data privacy settings",
            icon = Icons.Default.Shield,
            onClick = { /* Navigate to privacy */ }
        )
    )

    val AboutOptions = listOf(
        SettingsOption(
            title = "About",
            description = "App information and version",
            icon = Icons.Default.Info,
            onClick = { /* Navigate to about */ }
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "More",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsCategory(title = "Data Management")
            }
            items(DataManagementOptions) { option ->
                SettingsOptionItem(option = option)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsCategory(title = "App Settings")
            }
            items(AppSettingsOptions) { option ->
                SettingsOptionItem(option = option)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsCategory(title = "About")
            }
            items(AboutOptions) { option ->
                SettingsOptionItem(option = option)
            }
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsOptionItem(option: SettingsOption) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = option.onClick,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class SettingsOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

// Options for More Screen
