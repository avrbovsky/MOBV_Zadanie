package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import eu.mcomputing.mobv.zadanie.R

class ForgotPasswordCodeFragment:  Fragment(R.layout.fragment_forgot_password_code) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val codeInput = view.findViewById<TextInputEditText>(R.id.et_code)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.et_passwordReset)
        val passwordRepeatInput = view.findViewById<TextInputEditText>(R.id.et_passwordAgainReset)
        val passwordResetBtn = view.findViewById<MaterialButton>(R.id.bt_resetPasswordCode)

        val navController = findNavController()

        passwordResetBtn.setOnClickListener {
            val code: String = codeInput.text.toString()
            val password: String = passwordInput.text.toString()
            val passwordRepeat: String = passwordRepeatInput.text.toString()

            if(code.isNotEmpty() && password.isNotEmpty() && passwordRepeat.isNotEmpty()) {
                // api requests for code validation, password validations...
                navController.navigate(R.id.action_passwordReset_to_login)
            }else {
                // display error message
            }
        }
    }
}