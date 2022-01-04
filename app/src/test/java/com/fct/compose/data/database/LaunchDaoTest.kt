package com.fct.compose.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fct.compose.createLaunchEntity
import com.fct.compose.validateEntity
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LaunchDaoTest {

    private lateinit var launchDao: LaunchDao
    private lateinit var database: SpaceXDatabase

    // unix testing dates
    private val entityLatest =
        createLaunchEntity(upcoming = false, unixDate = 1612137600L, flightNumber = 2) // latest - jan 31
    private val entityPast =
        createLaunchEntity(upcoming = false, unixDate = 1611137600L, flightNumber = 1) // past - jan 20
    private val entityUpcoming =
        createLaunchEntity(upcoming = true, unixDate = 1613137600L, flightNumber = 3) // upcoming - feb 12

    @Before
    fun setup() {

        // setup test in memory db
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), SpaceXDatabase::class.java
        )
            .setQueryExecutor { it.run() }
            .allowMainThreadQueries()
            .build()

        launchDao = database.launchDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun `test getting Latest Launch`() {

        // arrange
        launchDao.insertAll(
            listOf(
                entityLatest,
                entityUpcoming,
                entityPast,
            )
        )

        // assert
        validateEntity(entityLatest, launchDao.getLatestLaunch())
    }

    @Test
    fun `test getting Upcoming Launches`() {

        // arrange
        launchDao.insertAll(
            listOf(
                entityLatest,
                entityUpcoming,
                entityPast,
            )
        )

        // assert
        validateEntity(entityUpcoming, launchDao.getUpcomingLaunches().take(1)[0])
    }

    @Test
    fun `test getting Past Launches`() {

        // arrange
        launchDao.insertAll(
            listOf(
                entityLatest,
                entityUpcoming,
                entityPast,
            )
        )

        // assert
        val results = launchDao.getPastLaunches()
        assertEquals(2, results.size)
        validateEntity(entityLatest, results[0])
        validateEntity(entityPast, results[1])
    }
}