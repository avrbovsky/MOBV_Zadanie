package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class ChangePasswordFragment: Fragment(R.layout.fragment_change_password) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val oldPasswordInput = view.findViewById<EditText>(R.id.et_oldPass)
        val newPasswordInput = view.findViewById<EditText>(R.id.et_newPass)
        val newPasswordAgainInput = view.findViewById<EditText>(R.id.et_newPassAgain)
        val passwordResetBtn = view.findViewById<MaterialButton>(R.id.bt_resetPassword)

        val navigation = findNavController()

        passwordResetBtn.setOnClickListener {
            val oldPassword: String = oldPasswordInput.text.toString()
            val newPassword: String = newPasswordInput.text.toString()
            val newPasswordAgain: String = newPasswordAgainInput.text.toString()

            if(oldPassword.isNotEmpty() && newPassword.isNotEmpty() && newPasswordAgain.isNotEmpty()) {
                // validacie

                navigation.navigate(R.id.action_changePass_to_profile)
            } else {
                // display error message
            }
        }
    }
}