package com.willian.explorerapp.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val characterId: Int,
    val timestamp: Long = System.currentTimeMillis()
)