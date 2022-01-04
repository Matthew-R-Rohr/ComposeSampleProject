package com.fct.compose.data.repository

import android.util.Log
import com.fct.compose.api.SpaceXApi
import com.fct.compose.data.LaunchEntity
import com.fct.compose.data.database.LaunchDao

private const val TAG = "tcbcDataRepo"

/**
 * Example repository implementation using Coroutines + Retrofit + ROOM DB cache
 *
 * Methods return cached data using [LaunchDao], otherwise will get fresh data through the [SpaceXApi]
 */
class DataRepository(
    private val launchDao: LaunchDao
) {

    private val spaceXApi = SpaceXApi()
    private val dtoTransformer = LaunchDtoTransformer()

    /**
     * Returns latest SpaceX launch
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    suspend fun getLatestLaunch(): LaunchEntity? {

        // check ROOM DB cache
        val cached = launchDao.getLatestLaunch()
        return if (cached != null) cached
        else {

            // return and update cache if empty
            Log.d(TAG, "empty cache, getting data")
            val response = spaceXApi.spaceXService.getLatestLaunch()
            if (response.isSuccessful) {
                response.body()?.let {
                    val entity = dtoTransformer.transformToLaunchEntity(it)

                    // update cache
                    launchDao.insert(entity)

                    // return result
                    return entity
                }
            } else {
                throw Exception("getLatestLaunch() was not successful: ${response.code()}") // error state)
            }
        }
    }

    /**
     * Returns all upcoming SpaceX launches
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    suspend fun getUpcomingLaunches(): List<LaunchEntity> {

        // check ROOM DB cache
        val cachedList = launchDao.getUpcomingLaunches()
        return if (cachedList.size > 1) cachedList
        else {

            // return and update cache if empty
            Log.d(TAG, "empty cache, getting data")
            val response = spaceXApi.spaceXService.getUpcomingLaunches()
            if (response.isSuccessful) {
                response.body()?.let { results ->
                    val entityList = dtoTransformer
                        .transformToLaunchEntityCollection(results)
                        .sortedBy { it.dateUnix }

                    // update cache
                    launchDao.insertAll(entityList)

                    // return results
                    return entityList
                }
            } else {
                throw Exception("getUpcomingLaunches() was not successful: ${response.code()}") // error state)
            }
            emptyList()
        }
    }

    /**
     * Returns all past SpaceX launches
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    suspend fun getPastLaunches(): List<LaunchEntity> {

        // check ROOM DB cache
        val cachedList = launchDao.getPastLaunches()
        return if (cachedList.size > 1) cachedList
        else {

            // return and update cache if empty
            Log.d(TAG, "empty cache, getting data")
            val response = spaceXApi.spaceXService.getPastLaunches()
            if (response.isSuccessful) {
                response.body()?.let { results ->
                    val entityList = dtoTransformer
                        .transformToLaunchEntityCollection(results)
                        .sortedByDescending { it.dateUnix }

                    // update cache
                    launchDao.insertAll(entityList)

                    // return results
                    return entityList
                }
            } else {
                throw Exception("getPastLaunches() was not successful: ${response.code()}") // error state)
            }
            emptyList()
        }
    }

    suspend fun deleteCaches() {
        launchDao.deleteAll()
    }
}

