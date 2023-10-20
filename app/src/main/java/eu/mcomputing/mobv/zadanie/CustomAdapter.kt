package eu.mcomputing.mobv.zadanie

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class CustomItem(val imageResource: Int, val text: String)

class CustomAdapter(private val data: List<CustomItem>) : RecyclerView.Adapter<CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.itemView.findViewById<ImageView>(R.id.item_image).setImageResource(data[position].imageResource)
        holder.itemView.findViewById<TextView>(R.id.item_text).text = data[position].text
    }

    override fun getItemCount(): Int = data.size
}