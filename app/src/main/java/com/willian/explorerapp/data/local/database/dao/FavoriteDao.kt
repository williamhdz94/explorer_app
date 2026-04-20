package com.willian.explorerapp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.willian.explorerapp.data.local.database.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT characterId FROM favorites")
    fun getFavoriteIds(): Flow<Set<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE characterId = :characterId")
    suspend fun delete(characterId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE characterId = :characterId)")
    suspend fun isFavorite(characterId: Int): Boolean
}