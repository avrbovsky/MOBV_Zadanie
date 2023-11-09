package eu.mcomputing.mobv.zadanie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.utils.ItemDiffCallback

class FeedAdapter: RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private var items: List<UserEntity> = listOf()

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tv_feedItemText).text = items[position].name
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<UserEntity>) {
        val diffCallback = ItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
}