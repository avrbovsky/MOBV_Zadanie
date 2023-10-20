package eu.mcomputing.mobv.zadanie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class IntroFragment : Fragment(R.layout.fragment_intro) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginBtn: MaterialButton = view.findViewById(R.id.bt_prihlasenie)
        val registrationBtn: MaterialButton = view.findViewById(R.id.bt_registracia)
        val navController = findNavController()

        loginBtn.setOnClickListener {
            navController.navigate(R.id.action_intro_to_login)
        }

        registrationBtn.setOnClickListener {
            navController.navigate(R.id.action_intro_to_registration)
        }
    }
}