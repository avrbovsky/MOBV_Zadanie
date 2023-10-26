package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel

class RegistrationFragment: Fragment(R.layout.fragment_signup) {
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance()) as T
            }
        })[AuthViewModel::class.java]

        viewModel.registrationResult.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                requireView().findNavController().navigate(R.id.action_registration_to_login)
            } else {
                Snackbar.make(
                    view.findViewById(R.id.bt_sign_up),
                    it,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        val emailInput = view.findViewById<TextInputEditText>(R.id.et_registrationEmail)
        val usernameInput = view.findViewById<TextInputEditText>(R.id.et_registrationUsername)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.et_registrationPassword)
        val passwordRepeatInput = view.findViewById<TextInputEditText>(R.id.et_registrationPasswordRepeat)
        val registerBtn = view.findViewById<MaterialButton>(R.id.bt_sign_up)

        registerBtn.setOnClickListener {
            val email: String = emailInput.text.toString()
            val username: String = usernameInput.text.toString()
            val password: String = passwordInput.text.toString()
            val repeatPassword: String = passwordRepeatInput.text.toString()

            if(email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty()){
                if(password == repeatPassword){
                    viewModel.registerUser(username,email,password)
                }
                else{
                    // display error message
                }
            } else {
                // display error message
            }
        }
    }
}