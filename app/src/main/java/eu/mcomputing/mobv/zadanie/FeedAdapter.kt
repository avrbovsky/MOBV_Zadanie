package eu.mcomputing.mobv.zadanie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

data class FeedItem(val id: Int, val imageResource: Int, val text: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FeedItem

        if (id != other.id) return false
        if (imageResource != other.imageResource) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + imageResource
        result = 31 * result + text.hashCode()
        return result
    }
}

class FeedAdapter: RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private var items: List<FeedItem> = listOf()

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.itemView.findViewById<ImageView>(R.id.iv_feedItemImage).setImageResource(items[position].imageResource)
        holder.itemView.findViewById<TextView>(R.id.tv_feedItemText).text = items[position].text
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<FeedItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}


class MyItemDiffCallback(
    private val oldList: List<FeedItem>,
    private val newList: List<FeedItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
