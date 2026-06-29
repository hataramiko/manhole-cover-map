package com.mikohatara.manholecovermap.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mikohatara.manholecovermap.data.ManholeCoverRepository
import com.mikohatara.manholecovermap.ui.home.HomeScreen
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapDestinationArgs.ITEM_ID
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapDestinations.ENTRY_NEW_ROUTE
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapDestinations.ENTRY_OLD_ROUTE
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapDestinations.HOME_ROUTE
import kotlinx.coroutines.CoroutineScope

@Composable
fun ManholeCoverMapNavGraph(
    repository: ManholeCoverRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = HOME_ROUTE,
    navActions: ManholeCoverMapNavActions = remember(navController) {
        ManholeCoverMapNavActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination
    val onBack = {
        if (navController.previousBackStackEntry != null) { navController.popBackStack() }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = HOME_ROUTE
        ) {
            HomeScreen(
                onBack = onBack,
                onNewItem = {
                    navActions.navigateToEntryScreen(null)
                },
                onItemClick = { /*item -> TODO
                    val itemId = item.id
                    navActions.navigateToEntryScreen(itemId)*/
                }
            )
        }
        composable(
            route = ENTRY_NEW_ROUTE
        ) {
            //EntryScreen(onBack = onBack) TODO
        }
        composable(
            route = ENTRY_OLD_ROUTE,
            arguments = listOf(navArgument(ITEM_ID) { type = NavType.StringType })
        ) {
            //EntryScreen(onBack = onBack) TODO
        }
    }
}
