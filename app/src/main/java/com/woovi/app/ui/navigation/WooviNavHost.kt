package com.woovi.app.ui.navigation
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.woovi.app.ui.detail.DetailScreen
import com.woovi.app.ui.form.ItemFormScreen
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.woovi.app.WooviApp
import com.woovi.app.ui.auth.AuthViewModel
import com.woovi.app.ui.auth.LoginScreen
import com.woovi.app.ui.auth.RegisterScreen
import com.woovi.app.ui.components.LoadingBox
import com.woovi.app.ui.library.LibraryScreen
import com.woovi.app.ui.welcome.WelcomeScreen
import io.github.jan.supabase.auth.status.SessionStatus

private val publicRoutes = setOf(Route.Welcome.path, Route.Login.path, Route.Register.path)

private data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem(Route.Home.path, "Inicio", Icons.Default.Home),
    TabItem(Route.Library.path, "Biblioteca", Icons.Default.Favorite),
    TabItem(Route.AddItem.path, "Agregar", Icons.Default.Add),
    TabItem(Route.Profile.path, "Perfil", Icons.Default.Person)
)

@Composable
fun WooviNavHost() {
    val context = LocalContext.current
    val authRepository = remember {
        (context.applicationContext as WooviApp).container.authRepository
    }
    val sessionStatus by authRepository.sessionStatus.collectAsState()
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = tabs.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                WooviBottomBar(
                    currentRoute = currentRoute,
                    onTabClick = { navController.navigateToTab(it) }
                )
            }
        }
    ) { innerPadding ->
        if (sessionStatus is SessionStatus.Initializing) {
            LoadingBox(modifier = Modifier.padding(innerPadding))
        } else {
            val startDestination = remember {
                if (sessionStatus is SessionStatus.Authenticated) Route.Home.path else Route.Welcome.path
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Welcome.path) {
                    WelcomeScreen(
                        onRegisterClick = { navController.navigate(Route.Register.path) },
                        onLoginClick = { navController.navigate(Route.Login.path) }
                    )
                }
                composable(Route.Login.path) {
                    LoginScreen(
                        onGoRegister = {
                            navController.navigate(Route.Register.path) { popUpTo(Route.Welcome.path) }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Route.Register.path) {
                    RegisterScreen(
                        onGoLogin = {
                            navController.navigate(Route.Login.path) { popUpTo(Route.Welcome.path) }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Route.Home.path) {
                    PlaceholderTab(
                        title = "Inicio",
                        message = "Aquí verás tu saludo, tus contadores y tus obras recientes."
                    )
                }
                composable(Route.Library.path) {
                    LibraryScreen(
                        onAddClick = { navController.navigateToTab(Route.AddItem.path) },
                        onItemClick = { id -> navController.navigate(Route.Detail.create(id)) }
                    )
                }
                composable(Route.AddItem.path) {
                    ItemFormScreen(
                        itemId = null,
                        onFinished = { navController.navigateToTab(Route.Library.path) }
                    )
                }
                composable(
                    route = Route.Detail.path,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("id").orEmpty()
                    DetailScreen(
                        itemId = id,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Route.Edit.create(it)) },
                        onDeleted = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Route.Edit.path,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("id").orEmpty()
                    ItemFormScreen(
                        itemId = id,
                        onFinished = { navController.popBackStack() },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Route.Profile.path) {
                    ProfilePlaceholder(email = authRepository.currentEmail())
                }
            }

            LaunchedEffect(sessionStatus) {
                val current = navController.currentDestination?.route
                when (sessionStatus) {
                    is SessionStatus.Authenticated -> {
                        if (current == null || current in publicRoutes) {
                            navController.navigate(Route.Home.path) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        if (current != null && current !in publicRoutes) {
                            navController.navigate(Route.Welcome.path) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun WooviBottomBar(currentRoute: String?, onTabClick: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { onTabClick(tab.route) },
                icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Route.Home.path) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun PlaceholderTab(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ProfilePlaceholder(
    email: String?,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text("Perfil", style = MaterialTheme.typography.headlineMedium)
        Text(email ?: "", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = { viewModel.signOut() }, shape = MaterialTheme.shapes.large) {
            Text("Cerrar sesión")
        }
    }
}