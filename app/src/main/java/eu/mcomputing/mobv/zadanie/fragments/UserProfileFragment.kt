package eu.mcomputing.mobv.zadanie.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.databinding.FragmentUserProfileBinding
import eu.mcomputing.mobv.zadanie.viewmodels.UserProfileViewModel

class UserProfileFragment:  Fragment() {
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[UserProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("userId")?.let{
            viewModel.loadUserById(it)
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also {bnd ->
            viewModel.userResult.observe(viewLifecycleOwner) {
                loadProfile(bnd, it)

                it?.let {
                    bnd.tvUserProfileText.text = getString(R.string.otherUserProfile, it.name)
                    bnd.tvLastSeen.text = getString(R.string.lastSeen, it.updated)
                }
            }

            viewModel.geofences.observe(viewLifecycleOwner){
                drawLastGeofence(it)
            }
        }
    }

    private fun loadProfile(binding: FragmentUserProfileBinding, user: UserEntity?){
        val baseUrl = "https://upload.mcomputing.eu/"
        val photoUrl: String = user?.photo ?: ""
        val time = System.currentTimeMillis()

        Picasso.get()
            .load("$baseUrl$photoUrl?time=$time")
            .placeholder(R.drawable.defualt_profile_picture)
            .resize(60, 60)
            .centerCrop()
            .into(binding.ivUserPicture)
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
                        .strokeWidth(2.0F)
                )
            }
        }
    }
}