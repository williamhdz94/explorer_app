package com.willian.explorerapp.data.repository

import com.willian.explorerapp.data.local.database.dao.CharacterDao
import com.willian.explorerapp.data.local.database.dao.FavoriteDao
import com.willian.explorerapp.data.local.database.entities.CharacterEntity
import com.willian.explorerapp.data.local.database.entities.FavoriteEntity
import com.willian.explorerapp.data.remote.datasource.RemoteDataSource
import com.willian.explorerapp.domain.models.Character
import com.willian.explorerapp.domain.repository.ICharacterRepository
import com.willian.explorerapp.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val characterDao: CharacterDao,
    private val favoriteDao: FavoriteDao,
    private val networkUtils: NetworkUtils
) : ICharacterRepository {

    override suspend fun getCharacters(page: Int): Result<List<Character>> {
        return try {
            if (networkUtils.isNetworkAvailable()) {

                val response = remoteDataSource.getCharacters(page)
                val characters = response.results.map { it.toDomain() }

                saveCharactersToLocal(characters)

                val favoriteIds = getFavoriteIdsFromLocal()
                val charactersWithFavorites = characters.map {
                    it.copy(isFavorite = it.id in favoriteIds)
                }

                Result.success(charactersWithFavorites)
            } else {
                val cachedCharacters = getCachedCharactersFromLocal()
                if (cachedCharacters.isNotEmpty()) {
                    Result.success(cachedCharacters)
                } else {
                    Result.failure(Exception("No internet connection and no cached data"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCharacterDetail(id: Int): Result<Character> {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                val detail = remoteDataSource.getCharacterDetail(id)
                val character = detail.toDomain()
                val isFavorite = favoriteDao.isFavorite(id)
                Result.success(character.copy(isFavorite = isFavorite))
            } else {
                val cached = characterDao.getCharacterById(id)
                if (cached != null) {
                    val isFavorite = favoriteDao.isFavorite(id)
                    Result.success(cached.toDomain().copy(isFavorite = isFavorite))
                } else {
                    Result.failure(Exception("Character not available offline"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToFavorites(character: Character) {
        favoriteDao.insert(FavoriteEntity(characterId = character.id))
    }

    override suspend fun removeFromFavorites(characterId: Int) {
        favoriteDao.delete(characterId)
    }

    override fun getFavoriteIds(): Flow<Set<Int>> {
        return favoriteDao.getFavoriteIds()
    }

    override fun getCachedCharacters(): Flow<List<Character>> {
        return characterDao.getAllCharacters().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private suspend fun saveCharactersToLocal(characters: List<Character>) {
        val entities = characters.map { CharacterEntity.fromDomain(it) }
        characterDao.insertAll(entities)
    }

    private suspend fun getCachedCharactersFromLocal(): List<Character> {
        val entities = characterDao.getAllCharacters().let { flow ->
            var result = emptyList<CharacterEntity>()
            kotlinx.coroutines.flow.first {
                result = it
                true
            }
            result
        }

        val favoriteIds = getFavoriteIdsFromLocal()

        return entities.map { entity ->
            entity.toDomain().copy(isFavorite = entity.id in favoriteIds)
        }
    }

    private suspend fun getFavoriteIdsFromLocal(): Set<Int> {
        var ids = emptySet<Int>()
        favoriteDao.getFavoriteIds().let { flow ->
            kotlinx.coroutines.flow.first {
                ids = it
                true
            }
        }
        return ids
    }
}