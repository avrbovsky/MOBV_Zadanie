package eu.mcomputing.mobv.zadanie

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

class BottomNavigationLayout(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.bottom_navigation_layout, this, true)

//        val mapButton: ImageView = findViewById(R.id.iv_map)
//        val feedButton: ImageView = findViewById(R.id.iv_feed)
//        val accountButton: ImageView = findViewById(R.id.iv_account)
//        val navController = findNavController()
//
//        mapButton.setOnClickListener{
//            navController.navigate(R.id.action_intro_to_map)
//        }
//
//        feedButton.setOnClickListener{
//            navController.navigate(R.id.action_intro_to_feed)
//        }
//
//        accountButton.setOnClickListener{
//            navController.navigate(R.id.action_intro_to_account)
//        }
    }

    // pridanie metod, ktore dokazu menit stav widgetu
    // .. napriklad zvyraznenie, aktivnej ikony
}