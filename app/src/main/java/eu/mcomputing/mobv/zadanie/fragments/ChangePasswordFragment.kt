package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.databinding.FragmentChangePasswordBinding
import eu.mcomputing.mobv.zadanie.viewmodels.ChangePasswordViewModel

class ChangePasswordFragment: Fragment() {

    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ChangePasswordViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.btResetPassword.setOnClickListener {
                val oldPassword: String = bnd.etOldPassword.text.toString()
                val newPassword: String = bnd.etNewPassword.text.toString()
                val newPasswordAgain: String = bnd.etNewPasswordAgain.text.toString()

                Log.d("A", oldPassword)

                if(oldPassword.isNotEmpty() && newPassword.isNotEmpty() && newPasswordAgain.isNotEmpty()) {
                    // validacie

                    findNavController().navigate(R.id.action_changePass_to_profile)
                } else {
                    // display error message
                }
            }
        }
    }
}