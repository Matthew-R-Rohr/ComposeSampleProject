package com.fct.compose

import android.app.Application
import com.fct.compose.data.database.SpaceXDatabase
import com.fct.compose.data.repository.DataRepository

class GlobalApplication : Application() {

    // database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { SpaceXDatabase.getDatabase(this) }
    val dataRepository by lazy { DataRepository(database.launchDao()) }
}