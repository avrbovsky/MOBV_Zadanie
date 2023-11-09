package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.adapters.FeedAdapter
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentFeedBinding
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel
import eu.mcomputing.mobv.zadanie.widgets.bottomBar.BottomBar

class FeedFragment: Fragment(R.layout.fragment_feed) {

    private lateinit var viewModel: FeedViewModel

    private var binding: FragmentFeedBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFeedBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.FEED)

            bnd.feedRecyclerview.layoutManager = LinearLayoutManager(context)
            val feedAdapter = FeedAdapter()
            bnd.feedRecyclerview.adapter = feedAdapter

            viewModel.feed_items.observe(viewLifecycleOwner) { items ->
                Log.d("FeedFragment", "nove hodnoty $items")
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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}