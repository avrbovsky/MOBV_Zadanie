package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ChangePasswordFragment: Fragment(R.layout.fragment_change_password) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val oldPasswordInput = view.findViewById<TextInputEditText>(R.id.et_oldPassword)
        val newPasswordInput = view.findViewById<TextInputEditText>(R.id.et_newPassword)
        val newPasswordAgainInput = view.findViewById<TextInputEditText>(R.id.et_newPasswordAgain)
        val passwordResetBtn = view.findViewById<MaterialButton>(R.id.bt_resetPassword)

        val navigation = findNavController()

        passwordResetBtn.setOnClickListener {
            val oldPassword: String = oldPasswordInput.text.toString()
            val newPassword: String = newPasswordInput.text.toString()
            val newPasswordAgain: String = newPasswordAgainInput.text.toString()

            Log.d("A", oldPassword)

            if(oldPassword.isNotEmpty() && newPassword.isNotEmpty() && newPasswordAgain.isNotEmpty()) {
                // validacie

                navigation.navigate(R.id.action_changePass_to_profile)
            } else {
                // display error message
            }
        }
    }
}