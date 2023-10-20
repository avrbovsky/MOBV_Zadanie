package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExampleFragment: Fragment(R.layout.fragment_example) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myDataList: List<CustomItem> = listOf(CustomItem(R.drawable.ic_feed, "aa"), CustomItem(R.drawable.ic_feed, "bb"))

        val recyclerView: RecyclerView = view.findViewById(R.id.my_recycler_view)
        recyclerView.adapter = CustomAdapter(myDataList)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }
}