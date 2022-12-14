package com.habibi.core.data.source.remote.network

import com.habibi.core.data.source.remote.response.NewStoryResponse
import com.habibi.core.data.source.remote.response.ListStoryResponse
import com.habibi.core.data.source.remote.response.LoginResponse
import com.habibi.core.data.source.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @Multipart
    @POST("stories")
    suspend fun postNewStory(
        @HeaderMap header: Map<String, String>,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Response<NewStoryResponse>

    @GET("stories")
    suspend fun getListStories(
        @HeaderMap header: Map<String, String>,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): Response<ListStoryResponse>

}