package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.databinding.FragmentIntroBinding

class IntroFragment : Fragment() {
    private lateinit var binding: FragmentIntroBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
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

        val user = PreferenceData.getInstance().getUser(requireContext())
        if (user != null) {
            requireView().findNavController().navigate(R.id.action_intro_to_feed)
        }
    }
}