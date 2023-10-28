package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.databinding.FragmentIntroBinding

class IntroFragment : Fragment(R.layout.fragment_intro) {
    private var binding: FragmentIntroBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentIntroBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            val navController = findNavController()

            bnd.btPrihlasenie.setOnClickListener {
                navController.navigate(R.id.action_intro_to_login)
            }

            bnd.btRegistracia.setOnClickListener {
                navController.navigate(R.id.action_intro_to_registration)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}