package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class ForgotPasswordEmailFragment: Fragment(R.layout.fragment_forgot_password_email) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput = view.findViewById<EditText>(R.id.et_passwordResetEmail)
        val resetBtn = view.findViewById<MaterialButton>(R.id.bt_reset_password_email)

        val navigation = findNavController()

        resetBtn.setOnClickListener {
            val email: String = emailInput.text.toString()

            if(email.isNotEmpty()) {
                // api request
                navigation.navigate(R.id.action_passwordReset_to_passwordResetCode)
            } else {
                // display error message
            }
        }
    }
}