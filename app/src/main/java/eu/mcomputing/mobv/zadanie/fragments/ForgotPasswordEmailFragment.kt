package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentForgotPasswordEmailBinding
import eu.mcomputing.mobv.zadanie.viewmodels.PasswordResetViewModel

class ForgotPasswordEmailFragment: Fragment(R.layout.fragment_forgot_password_email) {
    private lateinit var viewModel: PasswordResetViewModel
    private lateinit var binding: FragmentForgotPasswordEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PasswordResetViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[PasswordResetViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            val navController = findNavController()

            viewModel.passwordResetMessageResult.observe(viewLifecycleOwner) {
                if(it.isNotEmpty()){
                    Snackbar.make(
                        view.findViewById(R.id.bt_reset_password_email),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            viewModel.passwordResetSuccessResult.observe(viewLifecycleOwner) {
                if(it) {
                    navController.navigate(R.id.action_passwordReset_to_passwordResetCode)
                }
            }
        }
    }
}