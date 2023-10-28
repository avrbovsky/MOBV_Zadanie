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
import eu.mcomputing.mobv.zadanie.databinding.FragmentSignupBinding
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel

class RegistrationFragment: Fragment(R.layout.fragment_signup) {
    private lateinit var viewModel: AuthViewModel
    private var binding: FragmentSignupBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance()) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignupBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.btSignUp.setOnClickListener {
                val email: String = bnd.etRegistrationEmail.text.toString()
                val username: String = bnd.etRegistrationUsername.text.toString()
                val password: String = bnd.etRegistrationPassword.text.toString()
                val repeatPassword: String = bnd.etRegistrationPasswordRepeat.text.toString()

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
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}