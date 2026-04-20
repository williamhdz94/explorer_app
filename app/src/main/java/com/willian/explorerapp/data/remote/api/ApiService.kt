package com.willian.explorerapp.data.remote.api

import com.willian.explorerapp.data.remote.models.CharacterDetailResponse
import com.willian.explorerapp.data.remote.models.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1
    ): CharacterResponse

    @GET("api/character/{id}")
    suspend fun getCharacterDetail(
        @Path("id") id: Int
    ): CharacterDetailResponse
}