package eu.mcomputing.mobv.zadanie.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.interfaces.ItemClick
import eu.mcomputing.mobv.zadanie.utils.ItemDiffCallback

class FeedAdapter(private val click: ItemClick): RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private var items: List<UserEntity> = listOf()

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item, parent, false)
        return FeedViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tv_feedItemUsername).text = items[position].name
        holder.itemView.findViewById<TextView>(R.id.tv_feedItemUpdate).text = "Updated at: ${items[position].updated}"
        loadProfilePicture(holder, position)
        holder.itemView.setOnClickListener{
            click.onItemClick(items[position].uid)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<UserEntity>) {
        val diffCallback = ItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    private fun loadProfilePicture(holder: FeedViewHolder, position: Int){
        val imageView:ImageView = holder.itemView.findViewById<ImageView>(R.id.iv_feedItemImage)
        val baseUrl = "https://upload.mcomputing.eu/"
        val photoUrl: String = items[position].photo
        val time = System.currentTimeMillis()

        Picasso.get()
            .load("$baseUrl$photoUrl?time=$time" )
            .placeholder(R.drawable.defualt_profile_picture)
            .resize(60, 60)
            .centerCrop()
            .into(imageView)
    }
}