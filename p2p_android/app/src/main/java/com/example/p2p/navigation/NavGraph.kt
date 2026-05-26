package com.example.p2p.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.p2p.core.security.TokenManager
import com.example.p2p.data.repository.AuthRepositoryImpl
import com.example.p2p.presentation.about.AboutScreen
import com.example.p2p.presentation.admin.AdminScreen
import com.example.p2p.presentation.auth.ForgotPasswordScreen
import com.example.p2p.presentation.auth.LoginScreen
import com.example.p2p.presentation.auth.LoginViewModel
import com.example.p2p.presentation.auth.RegisterScreen
import com.example.p2p.presentation.cards.BankAccountsScreen
import com.example.p2p.presentation.complaints.ComplaintsScreen
import com.example.p2p.presentation.dispute.MyDisputesScreen
import com.example.p2p.presentation.dispute.RegisterDisputeScreen
import com.example.p2p.presentation.help.HelpScreen
import com.example.p2p.presentation.history.HistoryScreen
import com.example.p2p.presentation.kyc.KycScreen
import com.example.p2p.presentation.legal.PrivacyScreen
import com.example.p2p.presentation.legal.TermsScreen
import com.example.p2p.presentation.market.MarketScreen
import com.example.p2p.presentation.notifications.NotificationsScreen
import com.example.p2p.presentation.offer.MyOffersScreen
import com.example.p2p.presentation.offer.PublishScreen
import com.example.p2p.presentation.profile.EditProfileScreen
import com.example.p2p.presentation.profile.ProfileScreen
import com.example.p2p.presentation.rating.RatingScreen
import com.example.p2p.presentation.receipt.ReceiptScreen
import com.example.p2p.presentation.reviews.ReviewsScreen
import com.example.p2p.presentation.transaction.TransactionDetailScreen
import com.example.p2p.presentation.transaction.TransactionScreen
import com.example.p2p.ui.theme.Primary
import com.example.p2p.ui.theme.SurfaceColor
import kotlinx.coroutines.launch

// Rutas donde NO se muestra la barra inferior (auth)
private val authRoutes = setOf(
    Screen.Login.route,
    Screen.Register.route,
    Screen.ForgotPass.route,
    Screen.Kyc.route
)

@Composable
fun NavGraph(startDestination: String = Screen.Login.route) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = TokenManager.getInstance(context)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute != null && currentRoute !in authRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(currentRoute = currentRoute) { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(Screen.Market.route) { saveState = true }
                        restoreState = true
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {

            // ── Auth ─────────────────────────────────────────────────────────────
            composable(Screen.Login.route) {
                val authRepo = AuthRepositoryImpl(tokenManager)
                val vm: LoginViewModel = viewModel(factory = LoginViewModel.Factory(authRepo))
                LoginScreen(
                    viewModel = vm,
                    onLoginSuccess = {
                        navController.navigate(Screen.Market.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ForgotPass.route) {
                ForgotPasswordScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Kyc.route) {
                KycScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ── Main ─────────────────────────────────────────────────────────────
            composable(Screen.Market.route) {
                MarketScreen(
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
                )
            }

            composable(Screen.Publish.route) {
                PublishScreen()
            }

            composable(Screen.History.route) {
                HistoryScreen()
            }

            composable(Screen.Rating.route) {
                RatingScreen()
            }

            composable(
                route = Screen.Transaction.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
            ) { backStack ->
                TransactionScreen(transactionId = backStack.arguments?.getString("transactionId"))
            }

            composable(
                route = Screen.Receipt.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
            ) { backStack ->
                ReceiptScreen(transactionId = backStack.arguments?.getString("transactionId"))
            }

            composable(
                route = Screen.TransactionDetail.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
            ) { backStack ->
                TransactionDetailScreen(transactionId = backStack.arguments?.getString("transactionId"))
            }

            // ── Profile ──────────────────────────────────────────────────────────
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onLogout = {
                        scope.launch {
                            tokenManager.clearSession()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen()
            }

            composable(Screen.BankAccounts.route) {
                BankAccountsScreen()
            }

            composable(Screen.Notifications.route) {
                NotificationsScreen()
            }

            composable(Screen.Reviews.route) {
                ReviewsScreen()
            }

            composable(Screen.MyOffers.route) {
                MyOffersScreen()
            }

            composable(Screen.Complaints.route) {
                ComplaintsScreen()
            }

            // ── Disputes ─────────────────────────────────────────────────────────
            composable(Screen.MyDisputes.route) {
                MyDisputesScreen()
            }

            composable(Screen.RegisterDispute.route) {
                RegisterDisputeScreen()
            }

            // ── Admin ─────────────────────────────────────────────────────────────
            composable(Screen.Admin.route) {
                AdminScreen()
            }

            // ── Legal / Info ──────────────────────────────────────────────────────
            composable(Screen.Terms.route) {
                TermsScreen()
            }

            composable(Screen.Privacy.route) {
                PrivacyScreen()
            }

            composable(Screen.About.route) {
                AboutScreen()
            }

            composable(Screen.Help.route) {
                HelpScreen()
            }
        }
    }
}

@Composable
private fun AppBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = SurfaceColor, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = currentRoute == Screen.Market.route,
            onClick = { onNavigate(Screen.Market.route) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Mercado") },
            label = { Text("Mercado", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                indicatorColor = Primary.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Publish.route,
            onClick = { onNavigate(Screen.Publish.route) },
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Publicar") },
            label = { Text("Publicar", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                indicatorColor = Primary.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route,
            onClick = { onNavigate(Screen.Profile.route) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                indicatorColor = Primary.copy(alpha = 0.12f)
            )
        )
    }
}
