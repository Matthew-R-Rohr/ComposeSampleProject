package com.fct.compose.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fct.compose.data.LaunchEntity

@Dao
abstract class LaunchDao {

    //region SQL methods (non threaded!)

    /**
     * Return the latest [LaunchEntity]
     */
    @Query(
        """
        SELECT * FROM launch_entity 
        WHERE upcoming = 0
        ORDER BY dateUnix desc
        LIMIT 1
    """
    )
   abstract fun getLatestLaunch(): LaunchEntity?

    /**
     * Return a list of all upcoming [LaunchEntity]
     */
    @Query(
        """
        SELECT * FROM launch_entity 
        WHERE upcoming = 1  
        ORDER BY dateUnix asc
    """
    )
    abstract fun getUpcomingLaunches(): List<LaunchEntity>

    /**
     * Return a list of the past [LaunchEntity]
     */
    @Query(
        """
        SELECT * FROM launch_entity 
        WHERE upcoming = 0  
        ORDER BY dateUnix desc
    """
    )
    abstract fun getPastLaunches(): List<LaunchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(launchEntity: LaunchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(launchCollection: List<LaunchEntity>)

    @Query("DELETE FROM launch_entity")
    abstract suspend fun deleteAll()

    //endregion
}