package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import eu.mcomputing.mobv.zadanie.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {
//    private lateinit var viewModel: FeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel = ViewModelProvider(requireActivity())[FeedViewModel::class.java]
//
//        viewModel.feed_items.observe(viewLifecycleOwner) { items ->
//            // Tu môžete aktualizovať UI podľa hodnoty stringValue
//            Log.d("Profile", "prvky su $items")
//        }
    }
}