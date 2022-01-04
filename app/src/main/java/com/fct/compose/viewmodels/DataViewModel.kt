package com.fct.compose.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fct.compose.data.LaunchEntity
import com.fct.compose.data.UIState
import com.fct.compose.data.UIState.Companion.empty
import com.fct.compose.data.UIState.Companion.error
import com.fct.compose.data.UIState.Companion.loading
import com.fct.compose.data.UIState.Companion.success
import com.fct.compose.data.repository.DataRepository
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch

/**
 * SpaceX Launch ViewModel
 *
 * This ViewModel handles deciphering data from the repository and wrapping the results in a [UIState].
 *
 * The data is stored and managed within the repository.
 *
 * Additional ViewModel information:
 * @see "https://developer.android.com/topic/libraries/architecture/viewmodel"
 */
class DataViewModel(
    private val repository: DataRepository
) : ViewModel() {

    val launchUIState: MutableLiveData<UIState<List<LaunchEntity>>> = MutableLiveData()

    // stores the launch filter selection
    var filterSelection: String = ""

    /**
     * Updates [launchUIState] with Latest Launch data
     */
    fun fetchLatestLaunch() {
        launchUIState.postValue(loading())
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.getLatestLaunch()
            }.onSuccess {
                launchUIState.postValue(if (it == null) empty() else success(listOf(it)))
            }.onFailure {
                launchUIState.postValue(error(it as Exception))
            }
        }
    }

    /**
     * Updates [launchUIState] with Past Launch data
     */
    fun fetchPastLaunches() {
        launchUIState.postValue(loading())
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.getPastLaunches()
            }.onSuccess {
                launchUIState.postValue(if (it.isEmpty()) empty() else success(it))
            }.onFailure {
                launchUIState.postValue(error(it as Exception))
            }
        }
    }

    /**
     * Updates [launchUIState] with Upcoming Launch data
     */
    fun fetchUpcomingLaunches() {
        launchUIState.postValue(loading())
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.getUpcomingLaunches()
            }.onSuccess {
                launchUIState.postValue(if (it.isEmpty()) empty() else success(it))
            }.onFailure {
                launchUIState.postValue(error(it as Exception))
            }
        }
    }

    /**
     * removes both ROOM and Repository in-memory caches
     */
    fun deleteAllCaches() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCaches() // deletes all DB data
        }
    }
}

class DataViewModelFactory(private val repository: DataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DataViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
