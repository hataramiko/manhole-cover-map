package com.mikohatara.manholecovermap

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.mikohatara.manholecovermap.data.ManholeCoverRepository
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapNavGraph

@Composable
fun ManholeCoverMapApp(repository: ManholeCoverRepository) {
    Surface {
        ManholeCoverMapNavGraph(repository)
    }
}