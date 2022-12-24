package com.example.marvelapes.data.rest

import com.example.marvelapes.core.Constants
import com.example.marvelapes.data.models.web.BaseResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface WebService {

    @GET("characters${Constants.API_KEY}")
    suspend fun getCharacters():BaseResponse

}
object RetrofitClient{
    val webService: WebService by lazy {

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }
}