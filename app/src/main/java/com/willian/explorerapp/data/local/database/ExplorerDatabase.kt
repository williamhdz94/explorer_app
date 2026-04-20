package com.willian.explorerapp.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.willian.explorerapp.data.local.database.dao.CharacterDao
import com.willian.explorerapp.data.local.database.dao.FavoriteDao
import com.willian.explorerapp.data.local.database.entities.CharacterEntity
import com.willian.explorerapp.data.local.database.entities.FavoriteEntity

@Database(
    entities = [CharacterEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ExplorerDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: ExplorerDatabase? = null

        fun getDatabase(context: Context): ExplorerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExplorerDatabase::class.java,
                    "explorer_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}