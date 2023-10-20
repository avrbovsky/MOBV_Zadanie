package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class LoginFragment:  Fragment(R.layout.fragment_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameInput: EditText = view.findViewById(R.id.et_loginUsername)
        val passwordInput: EditText = view.findViewById(R.id.et_loginHeslo)
        val loginBtn: MaterialButton = view.findViewById(R.id.bt_login)

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