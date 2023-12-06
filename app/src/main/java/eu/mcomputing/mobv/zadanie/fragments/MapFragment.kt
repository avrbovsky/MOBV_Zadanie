package eu.mcomputing.mobv.zadanie.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.databinding.FragmentMapBinding
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel
import kotlin.random.Random


class MapFragment: Fragment(), OnMarkerClickListener, OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var feedViewModel: FeedViewModel

    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private var users: List<UserEntity>? = null
    private var geofence: GeofenceEntity?= null

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        feedViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment!!.getMapAsync(this)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.myLocation.setOnClickListener {
                if (!hasPermissions(requireContext())) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } else {
                    centerLocation()
                }
            }

            feedViewModel.feed_items.observe(viewLifecycleOwner) { items ->
                users = items
                drawEntitiesOnMap()
            }

            feedViewModel.geofences.observe(viewLifecycleOwner) { geofences ->
                if(geofences != null){
                    geofence = geofences.last()
                    drawEntitiesOnMap()
                }
            }
        }
    }

    private fun centerLocation() {
        mapFragment.getMapAsync{
            if(geofence != null) {
                it.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            geofence!!.lat,
                            geofence!!.lon
                        ), 17.0F
                    )
                )
            }
        }
    }

    private fun drawEntitiesOnMap() {
        mapFragment.getMapAsync{
            it.clear()
            if(geofence != null){
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(geofence!!.lat, geofence!!.lon), 17.0F))
                it.addCircle(
                    CircleOptions()
                    .center(LatLng(geofence!!.lat, geofence!!.lon))
                    .radius(100.0)
                    .fillColor(Color.argb(100, 200, 150, 255))
                    .strokeColor(Color.rgb(150, 100, 200))
                )
            }
            users?.let{ list ->
                for(user in list){
                    val userPosition = getRandomLocationInRadius()
                    val marker = MarkerOptions()
                    marker.position(userPosition)
                    marker.title(user.uid)
//                    marker.loadIcon(requireContext(), "https://upload.mcomputing.eu/" + user.photo )
                    it.addMarker(marker)
                }
            }
        }
    }

    private fun getRandomLocationInRadius():LatLng {
        var x0 = 0.0
        var y0 = 0.0
        var radius = 100.0

        geofence?.let {
            x0 = it.lat
            y0 = it.lon
            radius = it.radius
        }

        val radiusInDegrees = (radius / 111000f).toDouble()

        val u: Double = Random.nextDouble()
        val v: Double = Random.nextDouble()
        val w = radiusInDegrees * Math.sqrt(u)
        val t = 2 * Math.PI * v
        val x = w * Math.cos(t)
        val y = w * Math.sin(t)

        val new_x = x / Math.cos(Math.toRadians(y0))

        val foundLongitude = new_x + x0
        val foundLatitude = y + y0
        return LatLng(foundLongitude, foundLatitude)
    }

    override fun onMapReady(map: GoogleMap) {
        map.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val userId = p0.title
        val userBundle = Bundle().apply {
            putString("userId", userId)
        }
        findNavController().navigate(R.id.action_map_to_userProfile, userBundle)
        return true
    }


}