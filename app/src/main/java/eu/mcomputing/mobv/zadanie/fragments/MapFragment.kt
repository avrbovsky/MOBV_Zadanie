package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.databinding.FragmentMapBinding

class MapFragment: Fragment(R.layout.fragment_map) {
    private var binding: FragmentMapBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMapBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}