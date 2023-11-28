package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.model.User
import eu.mcomputing.mobv.zadanie.databinding.FragmentUserProfileBinding
import eu.mcomputing.mobv.zadanie.viewmodels.ProfileViewModel

class UserProfileFragment:  Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("userId")?.let{
            viewModel.loadUser(it)
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also {bnd ->
            viewModel.userResult.observe(viewLifecycleOwner) {
                loadProfile(bnd, it)
            }

            viewModel.profileResult.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    Snackbar.make(
                        bnd.mapView,
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun loadProfile(binding: FragmentUserProfileBinding, user: User?){
        val baseUrl = "https://upload.mcomputing.eu/"
        val photoUrl: String = user?.photo ?: ""

        Picasso.get()
            .load(baseUrl + photoUrl)
            .placeholder(R.drawable.ic_account_box)
            .resize(60, 60)
            .centerCrop()
            .into(binding.ivUserPicture)
    }
}