package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import eu.mcomputing.mobv.zadanie.R

class RegistrationFragment: Fragment(R.layout.fragment_signup) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput = view.findViewById<TextInputEditText>(R.id.et_registrationEmail)
        val usernameInput = view.findViewById<TextInputEditText>(R.id.et_registrationUsername)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.et_registrationPassword)
        val passwordRepeatInput = view.findViewById<TextInputEditText>(R.id.et_registrationPasswordRepeat)
        val registerBtn = view.findViewById<MaterialButton>(R.id.bt_sign_up)

        val navController = findNavController()

        registerBtn.setOnClickListener {
            val email: String = emailInput.text.toString()
            val username: String = usernameInput.text.toString()
            val password: String = passwordInput.text.toString()
            val repeatPassword: String = passwordRepeatInput.text.toString()

            if(email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty()){
                // validations, post request...

                navController.navigate(R.id.action_registration_to_login)
            } else {
                // display error message
            }
        }
    }
}