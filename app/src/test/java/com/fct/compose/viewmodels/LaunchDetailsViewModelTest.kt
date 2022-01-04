package com.fct.compose.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fct.compose.createLaunchEntity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class LaunchDetailsViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val viewModel: LaunchDetailsViewModel = LaunchDetailsViewModel()

    @Test
    fun `ensure getNextLaunchPhoto cycles properly`() {

        // arrange
        viewModel.launchEntity = createLaunchEntity()

        // cycle through all four image url's, ensure index increases and current photo url is correct
        assertEquals(0, viewModel.currentPhotoIndex) // starting index

        viewModel.triggerNextLaunchPhoto()
        assertEquals(
            "https://live.staticflickr.com/65535/50814482042_476d87b020_o.jpg",
            viewModel.photoUrlLiveData.value
        )
        assertEquals(1, viewModel.currentPhotoIndex)

        viewModel.triggerNextLaunchPhoto()
        assertEquals(
            "https://live.staticflickr.com/65535/50813630408_d98c2215f8_o.jpg",
            viewModel.photoUrlLiveData.value
        )
        assertEquals(2, viewModel.currentPhotoIndex)

        viewModel.triggerNextLaunchPhoto()
        assertEquals(
            "https://live.staticflickr.com/65535/50814379121_8834b5362d_o.jpg",
            viewModel.photoUrlLiveData.value
        )
        assertEquals(3, viewModel.currentPhotoIndex)

        viewModel.triggerNextLaunchPhoto()
        assertEquals(
            "https://live.staticflickr.com/65535/50814379056_f032a23955_o.jpg",
            viewModel.photoUrlLiveData.value
        )
        assertEquals(4, viewModel.currentPhotoIndex)

        // ensure cycle restarts with the first photo
        viewModel.triggerNextLaunchPhoto()
        assertEquals(
            "https://live.staticflickr.com/65535/50814482042_476d87b020_o.jpg",
            viewModel.photoUrlLiveData.value
        )
        assertEquals(1, viewModel.currentPhotoIndex)
    }

    @Test
    fun `ensure getNextLaunchPhoto cycles handles an empty lists`() {

        // arrange
        viewModel.launchEntity = createLaunchEntity(flickrImages = emptyList())

        // assert
        assertEquals(null, viewModel.photoUrlLiveData.value)

        viewModel.triggerNextLaunchPhoto()
        assertEquals(null, viewModel.photoUrlLiveData.value)
    }

    @Test
    fun `ensure getNextLaunchPhoto cycles handles a null list`() {

        // arrange
        viewModel.launchEntity = createLaunchEntity(flickrImages = null)

        // assert
        assertEquals(null, viewModel.photoUrlLiveData.value)

        viewModel.triggerNextLaunchPhoto()
        assertEquals(null, viewModel.photoUrlLiveData.value)
    }
}