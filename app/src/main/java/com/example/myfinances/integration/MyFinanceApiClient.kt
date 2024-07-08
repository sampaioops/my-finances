package com.example.myfinances.integration

import com.example.myfinances.model.Summary
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.Month
import java.time.Year

interface MyFinanceApiClient {
    @GET("/entries/summary")
    fun summary(@Query("month") month: Month,
                @Query("year") year: Int): Call<Summary>

}