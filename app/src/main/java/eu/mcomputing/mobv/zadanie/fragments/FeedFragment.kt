package eu.mcomputing.mobv.zadanie.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.adapters.FeedAdapter
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.databinding.FragmentFeedBinding
import eu.mcomputing.mobv.zadanie.interfaces.ItemClick
import eu.mcomputing.mobv.zadanie.utils.GeofencingHelper
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel
import eu.mcomputing.mobv.zadanie.viewmodels.ProfileViewModel
import eu.mcomputing.mobv.zadanie.widgets.bottomBar.BottomBar

class FeedFragment: Fragment() {

    private lateinit var viewModel: FeedViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentFeedBinding
    private lateinit var geofencingHelper: GeofencingHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]

        profileViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        geofencingHelper = GeofencingHelper(requireContext(), profileViewModel, requireActivity())

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.FEED)

            displayDialog()

            bnd.feedRecyclerview.layoutManager = LinearLayoutManager(context)
            val feedAdapter = FeedAdapter(object: ItemClick {
                override fun onItemClick(userId: String) {
                    val userBundle = Bundle().apply {
                        putString("userId", userId)
                    }

                    findNavController().navigate(R.id.action_feed_to_userProfile, userBundle)
                }
            })
            bnd.feedRecyclerview.adapter = feedAdapter

            viewModel.feed_items.observe(viewLifecycleOwner) { items ->
                feedAdapter.updateItems(items ?: emptyList())
            }

            bnd.pullRefresh.setOnRefreshListener {
                viewModel.updateItems()
            }

            viewModel.loading.observe(viewLifecycleOwner) {
                bnd.pullRefresh.isRefreshing = it
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayDialog(){
        val isSharing = PreferenceData.getInstance().getSharing(requireContext())

        if(!isSharing) {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())

            alertDialogBuilder
                .setTitle("Sharing is Disabled")
                .setMessage("Would you like to enable sharing?")
                .setPositiveButton("Enable") { _, _ ->
                    // Ask for permissions if not granted
                    PreferenceData.getInstance().putSharing(requireContext(), true)

                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                    fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) {
                        if (it == null) {
                        } else {
                            geofencingHelper.setupGeofence(it)
                        }
                    }
                    viewModel.updateItems()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
}