package com.willian.explorerapp.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.willian.explorerapp.domain.models.Character

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val image: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): Character {
        return Character(
            id = id,
            name = name,
            status = status,
            species = species,
            type = "",
            gender = "",
            origin = "",
            location = "",
            image = image,
            episode = emptyList(),
            url = "",
            created = "",
            isFavorite = false
        )
    }

    companion object {
        fun fromDomain(character: Character): CharacterEntity {
            return CharacterEntity(
                id = character.id,
                name = character.name,
                status = character.status,
                species = character.species,
                image = character.image
            )
        }
    }
}