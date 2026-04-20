package com.willian.explorerapp.domain.repository

import com.willian.explorerapp.domain.models.Character
import kotlinx.coroutines.flow.Flow

interface ICharacterRepository {
    suspend fun getCharacters(page: Int): Result<List<Character>>
    suspend fun getCharacterDetail(id: Int): Result<Character>
    suspend fun addToFavorites(character: Character)
    suspend fun removeFromFavorites(characterId: Int)
    fun getFavoriteIds(): Flow<Set<Int>>
    fun getCachedCharacters(): Flow<List<Character>>
}