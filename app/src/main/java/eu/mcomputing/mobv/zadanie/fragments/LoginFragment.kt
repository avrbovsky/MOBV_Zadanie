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
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.databinding.FragmentLoginBinding
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel

class LoginFragment:  Fragment() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            val navController = findNavController()

            viewModel.loginResult.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    Snackbar.make(
                        view.findViewById(R.id.bt_login),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            bnd.tvForgotPassword.setOnClickListener{
                navController.navigate(R.id.action_login_to_reset)
            }

            viewModel.userResult.observe(viewLifecycleOwner) {
                it?.let { user ->
                    PreferenceData.getInstance().putUser(requireContext(), user)
                    navController.navigate(R.id.action_login_to_feed)
                } ?: PreferenceData.getInstance().putUser(requireContext(), null)
            }
        }
    }
}