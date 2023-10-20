package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class RegistrationFragment: Fragment(R.layout.fragment_signup) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput = view.findViewById<EditText>(R.id.et_registrationEmail)
        val usernameInput = view.findViewById<EditText>(R.id.et_registrationUsername)
        val passwordInput = view.findViewById<EditText>(R.id.et_registrationPassword)
        val passwordRepeatInput = view.findViewById<EditText>(R.id.et_registrationPasswordRepeat)
        val registerBtn: MaterialButton = view.findViewById(R.id.bt_sign_up)

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