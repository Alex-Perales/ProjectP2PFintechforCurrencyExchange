package com.example.p2p.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

@Composable
fun NavGraph(startDestination: String = Screen.Login.route) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = TokenManager.getInstance(context)
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = startDestination) {

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
                onNavigateToPublish = { navController.navigate(Screen.Publish.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
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
