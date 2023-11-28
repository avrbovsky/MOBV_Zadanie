package eu.mcomputing.mobv.zadanie.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentProfilePictureChangeBinding
import eu.mcomputing.mobv.zadanie.viewmodels.ProfilePictureViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfilePictureChangeFragment: Fragment() {
    private lateinit var viewModel: ProfilePictureViewModel
    private lateinit var binding: FragmentProfilePictureChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfilePictureViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfilePictureViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePictureChangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            val navController = findNavController()

            val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    val image = createTempImageFileFromUri(requireContext(), uri)
                    viewModel.uploadPicture(image)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                    Snackbar.make(
                        view.findViewById(R.id.bt_removeProfilePicture),
                        "No image selected",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            bnd.btChangeProfilePicture.setOnClickListener{
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            viewModel.messageResult.observe(viewLifecycleOwner){
                if (it.isNotEmpty()) {
                    Snackbar.make(
                        view.findViewById(R.id.bt_removeProfilePicture),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            viewModel.statusResult.observe(viewLifecycleOwner){
                if(it){
                    viewModel.resetState()
                    navController.navigate(R.id.action_changePicture_to_profile)
                }
            }
        }
    }

    private fun createTempImageFileFromUri(context: Context, uri: Uri): File? {
        var tempFile: File? = null
        try {
            val inputStream = context.contentResolver.openInputStream(uri)

            tempFile = File(requireContext().cacheDir, "temp_image.jpg")
            tempFile.createNewFile()

            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)

            outputStream.flush()

            inputStream?.close()
            outputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return tempFile
    }
}