package eu.mcomputing.mobv.zadanie

import androidx.recyclerview.widget.DiffUtil
import eu.mcomputing.mobv.zadanie.adapters.FeedItem

class ItemDiffCallback(
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