package com.fct.compose.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fct.compose.R
import com.fct.compose.data.LaunchEntity
import com.fct.compose.databinding.FragmentLaunchDetailsBinding
import com.fct.compose.viewmodels.LaunchDetailsViewModel
import com.fct.compose.views.compose.LaunchDetailsScreen
import com.google.android.material.composethemeadapter.MdcTheme

/**
 * Example of XML -> Compose conversion, where a ComposeView is utilized within existing XML
 */
class LaunchDetailsFragment(
    private val launchEntity: LaunchEntity? = null
) : Fragment() {

    private val detailsViewModel: LaunchDetailsViewModel by viewModels()
    private lateinit var binding: FragmentLaunchDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaunchDetailsBinding.inflate(inflater, container, false)

        // add [LaunchEntity] to the class view model
        launchEntity?.let { detailsViewModel.launchEntity = it }
        setupUIComponents()
        return binding.root
    }

    private fun setupUIComponents() {

        detailsViewModel.launchEntity?.let { entity ->

            // screen title
            (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.launch_name, entity.name)

            detailsViewModel.prepLaunchPhoto()

            // xml into compose world
            with(binding.composeView) {

                // important to set this so the [composeView] follows the fragment onDestroy lifecycle,
                // helps keep navigation animation fully intact, among other things
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {

                    // MdcTheme is created from the compose theme adapter, which allows us to use our own theme within themes.xml
                    MdcTheme {
                        LaunchDetailsScreen(viewModel = detailsViewModel, entity = entity) {
                            processExternalLink(it)
                        }
                    }
                }
            }
        }

        // if for some reason the [LaunchEntity] is missing, send the user back to the listing
        if (detailsViewModel.launchEntity == null) activity?.onBackPressed()
    }

    /**
     * If this url exist, set the OnClickListener, else hide the button
     */
    private fun processExternalLink(urlString: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(urlString)
        startActivity(intent)

    }
}