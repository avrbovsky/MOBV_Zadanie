package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginFragment:  Fragment(R.layout.fragment_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameInput = view.findViewById<TextInputEditText>(R.id.et_loginUsername)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.et_loginHeslo)
        val loginBtn = view.findViewById<MaterialButton>(R.id.bt_login)

        val navController = findNavController()

        loginBtn.setOnClickListener {
            val username: String = usernameInput.text.toString()
            val password: String = passwordInput.text.toString()

            if(username.isNotEmpty() && password.isNotEmpty()){
                // api requests, validations ...

                navController.navigate(R.id.action_login_to_map)
            } else{
                // some error message
            }
        }
    }
}