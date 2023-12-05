package eu.mcomputing.mobv.zadanie.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
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
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.broadcastReceivers.GeofenceBroadcastReceiver
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.model.User
import eu.mcomputing.mobv.zadanie.databinding.FragmentProfileBinding
import eu.mcomputing.mobv.zadanie.utils.PicassoUtils
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel
import eu.mcomputing.mobv.zadanie.viewmodels.ProfileViewModel
import eu.mcomputing.mobv.zadanie.workers.MyWorker
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mapFragment: SupportMapFragment

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
        Log.d("ProfileFragment", "turnOnSharing")
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
                setupGeofence(it)
            }
        }
    }

    private fun turnOffSharing() {
        Log.d("ProfileFragment", "turnOffSharing")
        PreferenceData.getInstance().putSharing(requireContext(), false)
        removeGeofence()
    }

    @SuppressLint("MissingPermission")
    private fun setupGeofence(location: Location) {

        val geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        val geofence = Geofence.Builder()
            .setRequestId("my-geofence")
            .setCircularRegion(location.latitude, location.longitude, 100f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(requireActivity(), GeofenceBroadcastReceiver::class.java)
        val geofencePendingIntent =
            PendingIntent.getBroadcast(
                requireActivity(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                viewModel.updateGeofence(location.latitude, location.longitude, 100.0)
                runWorker()
            }
            addOnFailureListener {
                it.printStackTrace()
                binding.swToggleLocation.isChecked = false
                PreferenceData.getInstance().putSharing(requireContext(), false)
            }
        }

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
        Log.d("ProfileFragment", "geofence zruseny")
        val geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        geofencingClient.removeGeofences(listOf("my-geofence"))
        viewModel.removeGeofence()
        cancelWorker()
    }

    private fun runWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<MyWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("geo-notification")
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "geoLocationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    private fun cancelWorker() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("myworker")
    }

    private fun drawLastGeofence(geofences: List<GeofenceEntity>?){
        geofences?.let {
            mapFragment.getMapAsync {
                it.clear()
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