package com.fct.compose.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fct.compose.GlobalApplication
import com.fct.compose.viewmodels.DataViewModel
import com.fct.compose.viewmodels.DataViewModelFactory
import com.fct.compose.views.compose.LaunchListScreen
import com.google.android.material.composethemeadapter.MdcTheme

private const val TAG = "tcbcLaunchListFragment"

/**
 * Implementing the launch listings using pure compose
 */
class LaunchListFragment : Fragment() {

    private val dataViewModel: DataViewModel by viewModels {
        DataViewModelFactory((activity?.application as GlobalApplication).dataRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {

        // explicitly set the composition strategy to best match fragments lifecycle
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        setContent {
            // MdcTheme is created from the compose theme adapter, which allows us to use our own theme within themes.xml
            MdcTheme {
                LaunchListScreen(dataViewModel) { entity ->
                    activity?.let {
                        startLaunchDetailsActivity(it, entity)
                    }
                }
            }
        }
    }
}