package com.example.expensetracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.presentation.screen.AnalyticsScreen
import com.example.expensetracker.presentation.screen.BudgetScreen
import com.example.expensetracker.presentation.screen.DashboardScreen
import com.example.expensetracker.presentation.screen.MoreScreen
import com.example.expensetracker.presentation.screen.SettingsScreen
import com.example.expensetracker.presentation.screen.SplashScreen
import com.example.expensetracker.presentation.screen.OnboardingScreen
import com.example.expensetracker.presentation.screen.auth.UnlockScreen
import com.example.expensetracker.presentation.screen.budget.AddBudgetScreen
import com.example.expensetracker.presentation.screen.budget.EditBudgetScreen
import com.example.expensetracker.presentation.screen.category.CategoryManagementScreen
import com.example.expensetracker.presentation.screen.category.EditCategoryScreen
import com.example.expensetracker.presentation.screen.expense.AddExpenseScreen
import com.example.expensetracker.presentation.screen.expense.EditExpenseScreen
import com.example.expensetracker.presentation.screen.expense.ExpenseListScreen
import com.example.expensetracker.presentation.screen.export.ExportScreen

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        // Splash Screen
        composable("splash") {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(BottomNavItem.Expenses.route) {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToUnlock = {
                    navController.navigate("unlock") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // Unlock Screen
        composable("unlock") {
            UnlockScreen(
                onUnlockSuccess = {
                    navController.navigate(BottomNavItem.Expenses.route) {
                        popUpTo("unlock") { inclusive = true }
                    }
                }
            )
        }

        // Onboarding Screen
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(BottomNavItem.Expenses.route) {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // Dashboard Screen
        composable(BottomNavItem.Dashboard.route) {
            DashboardScreen(
                navController = navController,
            )
        }

        // Expenses Screen (Main Screen)
        composable(BottomNavItem.Expenses.route) {
            ExpenseListScreen(
                onNavigateToAddExpense = { navController.navigate("add_expense") },
                onNavigateToEditExpense = { expenseId ->
                    navController.navigate("edit_expense/$expenseId")
                },
                onNavigateToAnalytics = { navController.navigate(BottomNavItem.Analytics.route) },
                onNavigateToBudget = { navController.navigate("add_budget") },
                onNavigateToExport = { navController.navigate("export") },
                onNavigateToCategoryManagement = { navController.navigate("category_management") }
            )
        }

        // Budget Screen
        composable(BottomNavItem.Budget.route) {
            BudgetScreen(
                onNavigateToAddBudget = { navController.navigate("add_budget") },
                onNavigateToEditBudget = { budgetId ->
                    navController.navigate("edit_budget/$budgetId")
                }
            )
        }

        // Analytics Screen
        composable(BottomNavItem.Analytics.route) {
            AnalyticsScreen()
        }

        // More Screen
        composable(BottomNavItem.More.route) {
            MoreScreen(navController)
        }

        // Additional screens that are not in bottom nav

        // Expense Management
        composable("add_expense") {
            AddExpenseScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("edit_expense/{expenseId}") { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")?.toLongOrNull() ?: 0L
            EditExpenseScreen(
                expenseId = expenseId,
                onBack = { navController.popBackStack() }
            )
        }

        // Budget Management
        composable("add_budget") {
            AddBudgetScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("edit_budget/{budgetId}") { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getString("budgetId")?.toLongOrNull() ?: 0L
            EditBudgetScreen(
                budgetId = budgetId,
                onBack = { navController.popBackStack() }
            )
        }

        // Category Management
        composable("category_management") {
            CategoryManagementScreen(navController = navController)
        }

        composable("edit-category/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            EditCategoryScreen(
                categoryName = categoryName,
                navController = navController
            )
        }

        // Export
        composable("export") {
            ExportScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Settings (placeholder for now)
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}