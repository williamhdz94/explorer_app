package com.willian.explorerapp.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.willian.explorerapp.R
import com.willian.explorerapp.data.repository.CharacterRepository
import com.willian.explorerapp.presentation.ui.components.CharacterCard
import com.willian.explorerapp.presentation.ui.components.EmptyStateView
import com.willian.explorerapp.presentation.ui.components.LoadingView
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
    repository: CharacterRepository
) {
    val favoriteIds by viewModel.state.collectAsState()
    val favoriteCharacters by repository.getCachedCharacters().collectAsState(initial = emptyList())

    val filteredFavorites = favoriteCharacters.filter { character ->
        favoriteIds.favoriteIds.contains(character.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.favorites),
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
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
                filteredFavorites.isEmpty() -> {
                    EmptyStateView(
                        message = stringResource(R.string.no_favorites)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = filteredFavorites,
                            key = { character -> character.id }
                        ) { character ->
                            CharacterCard(
                                character = character,
                                onItemClick = { onNavigateToDetail(it) },
                                onFavoriteClick = {
                                    viewModel.removeFavorite(it.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}