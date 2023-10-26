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

class LoginFragment:  Fragment(R.layout.fragment_login) {
    private lateinit var viewModel: AuthViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance()) as T
            }
        })[AuthViewModel::class.java]

        viewModel.loginResult.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                requireView().findNavController().navigate(R.id.action_login_to_map)
            } else {
                Snackbar.make(
                    view.findViewById(R.id.bt_login),
                    it,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        val usernameInput = view.findViewById<TextInputEditText>(R.id.et_loginUsername)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.et_loginHeslo)
        val loginBtn = view.findViewById<MaterialButton>(R.id.bt_login)

        val navController = findNavController()

        loginBtn.setOnClickListener {
            val username: String = usernameInput.text.toString()
            val password: String = passwordInput.text.toString()

            if(username.isNotEmpty() && password.isNotEmpty()){
                viewModel.loginUser(username, password)
            } else{
                // display error message
            }
        }
    }
}