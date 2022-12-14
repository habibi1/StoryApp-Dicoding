package com.habibi.storyapp.features.story.presentation.add

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.habibi.core.data.Resource
import com.habibi.core.utils.createCustomTempFile
import com.habibi.core.utils.getAddressName
import com.habibi.core.utils.showSnackBar
import com.habibi.core.utils.toFile
import com.habibi.storyapp.BuildConfig
import com.habibi.storyapp.R
import com.habibi.storyapp.databinding.FragmentStoryAddBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class StoryAddFragment : Fragment() {

    private var _binding: FragmentStoryAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StoryAddViewModel by viewModels()

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = selectedImg.toFile(requireActivity())
            checkFieldValidation(myFile)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val myFile = File(viewModel.currentPhotoPath)
            checkFieldValidation(myFile)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initObserver()
        initEditTextChangeListener()
        getLastLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient?.lastLocation
            ?.addOnSuccessListener { location : Location? ->
                location?.let {
                    viewModel.setCurrentLatitude(it.latitude.toFloat())
                    viewModel.setCurrentLongitude(it.longitude.toFloat())
                    showLocation(it.latitude, it.longitude)
                }
            }
    }

    private fun showLocation(latitude: Double, longitude: Double) {
        val address = requireActivity().getAddressName(latitude, longitude)
        binding.tvAddLocationTitle.text = getString(R.string.location_format, address, viewModel.currentLatitude, viewModel.currentLongitude)
        binding.cvAddLocation.visibility = View.VISIBLE
    }

    private fun initEditTextChangeListener() {

        binding.edAddDescription.doOnTextChanged { _, _, _, _ ->
            checkFieldValidation()
        }

    }

    private fun checkFieldValidation(file: File? = null) {
        viewModel.checkFieldValidation(
            file,
            binding.edWrapStoryAddDescription.error?.toString(),
            binding.edAddDescription.text.toString()
        )
    }

    private fun initListener() {

        binding.buttonAdd.setOnClickListener {
            viewModel.postNewStory(
                binding.edAddDescription.text.toString()
            )
        }

        binding.buttonAddGallery.setOnClickListener {
            goToGallery()
        }

        binding.buttonAddCamera.setOnClickListener {
            goToCamera()
        }
    }

    private fun initObserver() {

        viewModel.photoFile.observe(viewLifecycleOwner) {
            binding.ivAddPreview.setImageBitmap(
                BitmapFactory.decodeFile(it.path)
            )
        }

        viewModel.fieldValid.observe(viewLifecycleOwner) {
            binding.buttonAdd.isEnabled = it
        }

        viewModel.newStory.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    onLoading(true)
                }
                is Resource.Success -> {
                    onSuccess(it.message)
                }
                is Resource.Failed -> {
                    onFailed(it.message)
                }
                is Resource.Error -> {
                    onError(it.messageResource)
                }
            }
        }

    }

    private fun onLoading(isLoading: Boolean) {
        binding.apply {
            edAddDescription.isEnabled = !isLoading
            buttonAddCamera.isEnabled = !isLoading
            buttonAddGallery.isEnabled = !isLoading
            if (isLoading) {
                buttonAdd.visibility = View.INVISIBLE
                pbStoryAdd.visibility = View.VISIBLE
            } else {
                buttonAdd.visibility = View.VISIBLE
                pbStoryAdd.visibility = View.GONE
            }
        }
    }

    private fun onSuccess(message: String) {

        requireActivity().showSnackBar(message)

        goToStoryList()
    }

    private fun onFailed(message: String) {
        onLoading(false)
        requireActivity().showSnackBar(message)
    }

    private fun onError(messageResource: Int) {
        onLoading(false)
        requireActivity().showSnackBar(
            getString(messageResource)
        )
    }

    private fun goToStoryList() {
        findNavController().navigate(R.id.action_storyAddFragment_to_StoryListFragment)
    }

    private fun goToGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun goToCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)
        createCustomTempFile(requireActivity()).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity(),
                BuildConfig.APPLICATION_ID,
                it
            )
            viewModel.setCurrentPhotoPath(it.absolutePath)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}