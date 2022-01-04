package com.fct.compose.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fct.compose.data.LaunchEntity

class LaunchDetailsViewModel : ViewModel() {

    var launchEntity: LaunchEntity? = null
    var currentPhotoIndex = 0
        private set

    val photoUrlLiveData: MutableLiveData<String> = MutableLiveData()
    val photoLoadingError: MutableLiveData<String> = MutableLiveData()

    /**
     * triggers the next photo in the list of [LaunchEntity.flickrImages] list to [photoUrlLiveData]
     *
     * If the list is blank, returns a null
     */
    fun triggerNextLaunchPhoto() {
        photoLoadingError.value = ""
        launchEntity?.flickrImages.takeIf { !it.isNullOrEmpty() }?.let {

            // reset loop if needed
            if (currentPhotoIndex == it.size) currentPhotoIndex = 0

            photoUrlLiveData.value = it[currentPhotoIndex++]
        }
    }

    fun prepLaunchPhoto() {
        if (photoUrlLiveData.value == null) triggerNextLaunchPhoto()
    }

    fun setPhotoLoadingError(errorString: String?) {
        photoLoadingError.value = errorString ?: ""
    }
}