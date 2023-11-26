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
import eu.mcomputing.mobv.zadanie.databinding.FragmentChangePasswordBinding
import eu.mcomputing.mobv.zadanie.viewmodels.ChangePasswordViewModel

class ChangePasswordFragment: Fragment() {

    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChangePasswordViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ChangePasswordViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            val navController = findNavController()

            viewModel.messageResult.observe(viewLifecycleOwner){
                if (it.isNotEmpty()) {
                    Snackbar.make(
                        view.findViewById(R.id.bt_changePassword),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            viewModel.statusResult.observe(viewLifecycleOwner){
                if(it){
                    viewModel.resetState()
                    navController.navigate(R.id.action_changePass_to_profile)
                }
            }
        }
    }
}