package com.example.weatherkt.data

import com.example.weatherkt.data.PostItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationInterface {
    @GET("data/2.5/weather")
    fun getLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String
    ): Call<PostItem>
}
