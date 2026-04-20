package com.willian.explorerapp.data.remote.datasource

import com.willian.explorerapp.data.remote.api.RetrofitClient
import com.willian.explorerapp.data.remote.models.CharacterDto
import com.willian.explorerapp.data.remote.models.CharacterResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor() {

    suspend fun getCharacters(page: Int): CharacterResponse {
        return RetrofitClient.apiService.getCharacters(page)
    }

    suspend fun getCharacterDetail(id: Int): CharacterDto {
        return RetrofitClient.apiService.getCharacterDetail(id)
    }
}