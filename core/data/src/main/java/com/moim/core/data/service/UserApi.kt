package com.moim.core.data.service

import com.moim.core.datamodel.UserResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

internal interface UserApi {

    @GET("user/info")
    suspend fun getUser(): UserResponse

    @PATCH("user/info")
    suspend fun updateUser(@Body params: JsonObject): UserResponse

    @DELETE("user/remove")
    suspend fun deleteUser()

    @GET("user/nickname/duplicate")
    suspend fun checkedNickname(@Query("nickname") nickname: String): Boolean
}