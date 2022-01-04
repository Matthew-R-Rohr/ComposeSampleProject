package com.fct.compose.api

import com.fct.compose.api.model.LaunchDto
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val PAST_LAUNCHES_ENDPOINT = "launches/past"
private const val UPCOMING_LAUNCHES_ENDPOINT = "launches/upcoming"
private const val LATEST_LAUNCH_ENDPOINT = "launches/latest"

/**
 * SpaceX Service API
 *
 * link in constructor is for testability
 */
class SpaceXApi(baseUrl: String = "https://api.spacexdata.com/v4/") {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var spaceXService: SpaceXService = retrofit.create(SpaceXService::class.java)

    interface SpaceXService {

        @GET(PAST_LAUNCHES_ENDPOINT)
        suspend fun getPastLaunches(): Response<List<LaunchDto>>

        @GET(LATEST_LAUNCH_ENDPOINT)
        suspend fun getLatestLaunch(): Response<LaunchDto>

        @GET(UPCOMING_LAUNCHES_ENDPOINT)
        suspend fun getUpcomingLaunches(): Response<List<LaunchDto>>

    }
}