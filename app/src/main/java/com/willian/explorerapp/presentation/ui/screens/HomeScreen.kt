package com.willian.explorerapp.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.willian.explorerapp.R
import com.willian.explorerapp.domain.models.Character
import com.willian.explorerapp.presentation.ui.components.CharacterCard
import com.willian.explorerapp.presentation.ui.components.EmptyStateView
import com.willian.explorerapp.presentation.ui.components.ErrorView
import com.willian.explorerapp.presentation.ui.components.LoadingView
import com.willian.explorerapp.presentation.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToFavorites: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !state.isLoading && !state.isRefreshing) {
            viewModel.loadMore()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.explorer_app),
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.favorites)
                        )
                    }
                    IconButton(onClick = { /* TODO: Show device info */ }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.device_info)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.error != null && state.characters.isEmpty() -> {
                    ErrorView(
                        errorMessage = state.error ?: stringResource(R.string.unknown_error),
                        onRetry = { viewModel.retry() }
                    )
                }
                state.isLoading && state.characters.isEmpty() -> {
                    LoadingView()
                }
                state.characters.isEmpty() -> {
                    EmptyStateView()
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.characters,
                            key = { character -> character.id }
                        ) { character ->
                            CharacterCard(
                                character = character,
                                onItemClick = { onNavigateToDetail(it) },
                                onFavoriteClick = { viewModel.toggleFavorite(it) }
                            )
                        }

                        if (state.isLoading) {
                            item {
                                LoadingView(
                                    modifier = Modifier.padding(32.dp),
                                    message = stringResource(R.string.loading_more)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}