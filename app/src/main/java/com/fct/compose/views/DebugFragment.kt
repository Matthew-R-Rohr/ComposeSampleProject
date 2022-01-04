package com.fct.compose.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fct.compose.GlobalApplication
import com.fct.compose.data.UIState
import com.fct.compose.databinding.FragmentDebugBinding
import com.fct.compose.viewmodels.DataViewModel
import com.fct.compose.viewmodels.DataViewModelFactory
import kotlin.system.exitProcess

private const val TAG = "tcbcDebugFragment"

/**
 * Debugging Fragment used for development & manual testing purposes
 */
class DebugFragment : Fragment() {

    private lateinit var binding: FragmentDebugBinding

    private val dataViewModel: DataViewModel by viewModels {
        DataViewModelFactory((activity?.application as GlobalApplication).dataRepository)
    }

    //region LifeCycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDebugBinding.inflate(inflater, container, false)
        setupUIComponents()
        setupObservables()
        return binding.root
    }

    //endregion

    //region UI

    private fun setupUIComponents() {
        with(binding) {

            // ensure log out [TextView] is manually scrollable
            txtDebugOutput.movementMethod = ScrollingMovementMethod()

            // event to delete all caches
            btnDeleteCaches.setOnClickListener {
                deleteAllCaches()
            }

            // event to restart the app
            btnRestartApp.setOnClickListener {
                deleteAllCaches()
                context?.startActivity(
                    Intent(context, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                exitProcess(0)
            }

            btnTestLatest.setOnClickListener {
                dataViewModel.filterSelection = "Latest"
                updateLog("\n--------------------------------")
                dataViewModel.fetchLatestLaunch()
            }
            btnTestUpcoming.setOnClickListener {
                dataViewModel.filterSelection = "Upcoming"
                updateLog("\n--------------------------------")
                dataViewModel.fetchUpcomingLaunches()
            }
            btnTestPast.setOnClickListener {
                dataViewModel.filterSelection = "Past"
                updateLog("\n--------------------------------")
                dataViewModel.fetchPastLaunches()
            }
        }
    }

    //endregion

    //region Data Testing Methods

    private fun setupObservables() {
        dataViewModel.launchUIState.observe(viewLifecycleOwner) {
            when (it.status) {
                UIState.Status.SUCCESS -> updateLog("Success: ${dataViewModel.filterSelection} - Results:${it.data?.size}")
                UIState.Status.ERROR -> {
                    Log.e(TAG, it.error?.message, it.error)
                    updateLog("Error: ${it.error?.message}")
                }
                UIState.Status.LOADING -> updateLog("Loading: ${dataViewModel.filterSelection}")
                UIState.Status.EMPTY -> updateLog("Empty: ${dataViewModel.filterSelection}")
                UIState.Status.INIT -> {}
            }
        }
    }

    //endregion

    //region Helper Methods

    private fun deleteAllCaches() {
        updateLog("\n--------------------------------")
        updateLog("Deleting All Caches")
        dataViewModel.deleteAllCaches()
    }

    @SuppressLint("SetTextI18n")
    private fun updateLog(message: String) {

        // update visual log
        with(binding.txtDebugOutput) {
            post {
                append("\n$message")
            }
        }

        // console log
        Log.d(TAG, message)
    }

    //endregion
}