package com.mikohatara.manholecovermap.ui.navigation

import androidx.navigation.NavHostController
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapDestinationArgs.ITEM_ID
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapDestinations.ENTRY_NEW_ROUTE
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapDestinations.HOME_ROUTE
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapScreen.ENTRY_SCREEN
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapScreen.HOME_SCREEN

object ManholeCoverMapScreen {
    const val HOME_SCREEN = "home"
    const val ENTRY_SCREEN = "entry"
}

object ManholeCoverMapDestinationArgs {
    const val ITEM_ID = "itemId"
}

object ManholeCoverMapDestinations {
    const val HOME_ROUTE = HOME_SCREEN
    const val ENTRY_NEW_ROUTE = ENTRY_SCREEN
    const val ENTRY_OLD_ROUTE = "$ENTRY_SCREEN/{$ITEM_ID}"
}

class ManholeCoverMapNavActions(private val navController: NavHostController) {

    fun navigateToHomeScreen() {
        navController.navigate(HOME_ROUTE)
    }

    fun navigateToEntryScreen(itemId: Int? = null) {
        navController.navigate(
            if (itemId == null) {
                "$ENTRY_SCREEN/$itemId"
            } else {
                ENTRY_NEW_ROUTE
            }
        )
    }
}
