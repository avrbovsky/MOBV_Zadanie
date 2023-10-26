package eu.mcomputing.mobv.zadanie.widgets.bottomBar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import eu.mcomputing.mobv.zadanie.R

class BottomBar : ConstraintLayout {
    private var active = -1
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    fun setActive(index: Int) {
        active = index
    }
    fun init() {
        val layout =
            LayoutInflater.from(context)
                .inflate(R.layout.bottom_navigation_layout, this, false)
        addView(layout)
        layout.findViewById<ImageView>(R.id.iv_map).setOnClickListener {
            if (active != MAP) {
                it.findNavController().navigate(R.id.action_to_map)
            }
        }
        layout.findViewById<ImageView>(R.id.iv_feed).setOnClickListener {
            if (active != FEED) {
                it.findNavController().navigate(R.id.action_to_feed)
            }
        }
        layout.findViewById<ImageView>(R.id.iv_account).setOnClickListener {
            if (active != PROFILE) {
                it.findNavController().navigate(R.id.action_to_profile)
            }
        }
    }
    companion object {
        const val MAP = 0
        const val FEED = 1
        const val PROFILE = 2
    }
}