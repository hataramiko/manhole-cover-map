package com.mikohatara.manholecovermap.ui.home

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.manholecovermap.R
import com.mikohatara.manholecovermap.data.ManholeCover

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNewItem: () -> Unit,
    onItemClick: (ManholeCover) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    HomeScreen(
        uiState = uiState,
        context = context,
        onBack = onBack,
        onNewItem = onNewItem,
        onItemClick = onItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    context: Context,
    onBack: () -> Unit,
    onNewItem: () -> Unit,
    onItemClick: (ManholeCover) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults
        .enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                onNavigation = onBack,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewItem
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_add_24),
                    contentDescription = null
                )
            }
        },
        content = { innerPadding ->
            HomeScreenContent(
                uiState = uiState,
                context = context,
                onItemClick = onItemClick,
                modifier = modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    context: Context,
    onItemClick: (ManholeCover) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val itemList = uiState.items

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        if (uiState.isLoading) {
            item { Loading() }
        } else if (itemList.isEmpty()) {
            item { Empty() }
        } else {
            items(items = itemList) { item ->
                val onClickModifier = Modifier.clickable { onItemClick(item) }
                Item(
                    context = context,
                    modifier = onClickModifier,
                    country = item.country,
                    region = item.region,
                    city = item.city
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    title: String = "Manhole Cover Map",
    onNavigation: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigation,
                enabled = false
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_menu_24),
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = {/*TODO*/},
                enabled = false
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_more_vert_24),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun Loading() {
    Text(
        text = "${stringResource(R.string.loading)}...",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 128.dp)
    )
}

@Composable
private fun Empty() {
    Text(
        text = "${stringResource(R.string.empty)}.",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 256.dp)
    )
}

@Composable
private fun Item(
    context: Context,
    modifier: Modifier = Modifier,
    imagePath: String? = null,
    country: String? = null,
    region: String? = null,
    city: String? = null,
) {
    val label = listOfNotNull(country, region).joinToString(separator = ", ")

    Card(
        colors = CardDefaults.cardColors(colorScheme.surfaceContainerLow),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            // Applying "modifier" only after ".clip" makes for a better onClick visual effect
            .then(modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Card(
                modifier = Modifier.size(72.dp)
            ) {
                if (imagePath != null) {
                    //TODO
                } else {
                    //NoImage()
                }
            }
            Column(
                modifier = Modifier
                    .heightIn(max = 80.dp)
                    .padding(horizontal = 16.dp)
            ) {
                if (label.isNotBlank()) {
                    Text(
                        text = label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!city.isNullOrBlank()) {
                    Text(
                        text = city,
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
