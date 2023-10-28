package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.databinding.FragmentChangePasswordBinding
import eu.mcomputing.mobv.zadanie.viewmodels.ChangePasswordViewModel

class ChangePasswordFragment: Fragment(R.layout.fragment_change_password) {

    private lateinit var viewModel: ChangePasswordViewModel
    private var binding: FragmentChangePasswordBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ChangePasswordViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChangePasswordBinding.bind(view).apply {
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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}