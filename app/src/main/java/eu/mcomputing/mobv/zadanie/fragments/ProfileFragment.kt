package eu.mcomputing.mobv.zadanie.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.model.User
import eu.mcomputing.mobv.zadanie.databinding.FragmentProfileBinding
import eu.mcomputing.mobv.zadanie.utils.GeofencingHelper
import eu.mcomputing.mobv.zadanie.utils.PicassoUtils
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel
import eu.mcomputing.mobv.zadanie.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var geofencingHelper: GeofencingHelper

    private val PERMISSIONS_REQUIRED = when {
        Build.VERSION.SDK_INT >= 33 -> {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        Build.VERSION.SDK_INT >= 29 -> {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        else -> {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]

        authViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment_profile) as SupportMapFragment

        geofencingHelper = GeofencingHelper(requireContext(), viewModel, requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = PreferenceData.getInstance().getUser(requireContext())
        user?.let {
            viewModel.loadUser(it.id)
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            val navController = findNavController()

            val isSharing = PreferenceData.getInstance().getSharing(requireContext())
            bnd.swToggleLocation.isChecked = isSharing

            viewModel.geofences.observe(viewLifecycleOwner) {
                drawLastGeofence(it)
            }

            bnd.ivPicture.setOnClickListener{
                navController.navigate(R.id.action_profile_to_changePicture)
            }

            bnd.btLogout.setOnClickListener {
                PreferenceData.getInstance().clearData(requireContext())
                authViewModel.logout()
                it.findNavController().navigate(R.id.action_profile_to_intro)
            }

            bnd.btChangePassword.setOnClickListener{
                it.findNavController().navigate(R.id.action_profile_to_changePass)
            }

            viewModel.userResult.observe(viewLifecycleOwner) {
                loadProfile(bnd, it)
            }

            viewModel.profileResult.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    Snackbar.make(
                        bnd.btChangePassword,
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            bnd.swToggleLocation.isChecked = PreferenceData.getInstance().getSharing(requireContext())
            bnd.swToggleLocation.setOnCheckedChangeListener { _, checked ->
                Log.d("ProfileFragment", "sharing je $checked")
                if (checked) {
                    turnOnSharing()
                } else {
                    turnOffSharing()
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun turnOnSharing() {
        if (!hasPermissions(requireContext())) {
            binding.swToggleLocation.isChecked = false
            for (p in PERMISSIONS_REQUIRED) {
                requestPermissionLauncher.launch(p)
            }
            return
        }
        PreferenceData.getInstance().putSharing(requireContext(), true)

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) {
            if (it == null) {
            } else {
                geofencingHelper.setupGeofence(it)
            }
        }
    }

    private fun turnOffSharing() {
        PreferenceData.getInstance().putSharing(requireContext(), false)
        viewModel.removeUsers()
        removeGeofence()
    }

    private fun loadProfile(binding: FragmentProfileBinding, user: User?){
        val baseUrl = "https://upload.mcomputing.eu/"
        val photoUrl: String = user?.photo ?: ""

        Picasso.get()
            .load(baseUrl + photoUrl)
            .placeholder(R.drawable.ic_account_box)
            .transform(PicassoUtils.circleTransformation)
            .resize(60, 60)
            .centerCrop()
            .into(binding.ivPicture)
    }

    private fun removeGeofence() {
        val geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        geofencingClient.removeGeofences(listOf("my-geofence"))
        viewModel.removeGeofence()
        geofencingHelper.cancelWorker()
    }

    private fun drawLastGeofence(geofences: List<GeofenceEntity>?){
        geofences?.let {
            mapFragment.getMapAsync {
                it.clear()
                if(geofences.size > 0){
                    val geofence = geofences.last()

                    it.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                geofence.lat,
                                geofence.lon
                            ), 16.0F
                        )
                    )
                    it.addCircle(
                        CircleOptions()
                            .center(LatLng(geofence.lat, geofence.lon))
                            .radius(100.0)
                            .fillColor(Color.argb(100, 200, 150, 255))
                            .strokeColor(Color.rgb(150, 100, 200))
                    )
                }
            }
        }
    }
}