package com.example.mypostsapp.ui.main

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.mypostsapp.AlertDialogUtils
import com.example.mypostsapp.R
import com.example.mypostsapp.databinding.CreateOrUpdatePostLayoutBinding
import com.example.mypostsapp.entities.Post
import com.example.mypostsapp.entities.PostLocation
import com.example.mypostsapp.entities.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class CreateOrUpdatePostFragment : Fragment() {

    private var myLocation: Location? = null
    private var loadingDialog: ProgressDialog? = null
    private var currentUser: User? = null
    private var imageBitmap: Bitmap? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val isAllGranted = permissions.all { it.value }
        if (isAllGranted) {
            findMyLocation()
        } else {
        }
    }

    private val cameraActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = (result.data!!.extras!!["data"] as Bitmap?)!!
            imageBitmap = photo
            binding.postDataContainer.postImage.setImageBitmap(photo)
        }
    }

    private val galleryActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val uri = result.data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(this.context?.contentResolver, uri)
                imageBitmap = bitmap
                binding.postDataContainer.postImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
            }
        }
    }

    companion object {
        fun newInstance(position: Int?, post: Post?, user: User?): CreateOrUpdatePostFragment {
            val bundle: Bundle = Bundle()
            position?.let {
                bundle.putInt("position", it)
            }
            post?.let {
                bundle.putSerializable("post", it)
            }
            bundle.putSerializable("user", user)
            return CreateOrUpdatePostFragment().apply {
                arguments = bundle
            }
        }
    }

    private var _binding: CreateOrUpdatePostLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateOrUpdateViewModel
    private var position: Int? = null
    private var post: Post? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateOrUpdateViewModel::class.java)
        position = arguments?.getInt("position", -1)
        position = if (position == -1) null else position
        post = arguments?.getSerializable("post") as? Post
        currentUser = arguments?.getSerializable("user") as? User
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateOrUpdatePostLayoutBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        findMyLocationIfHasAccess()
        loadingDialog = ProgressDialog(requireContext())
        loadingDialog?.setMessage(getString(R.string.please_wait))
        viewModel.onSuccess.observe(viewLifecycleOwner) {
            onSuccess(it.first, it.second)
        }
        viewModel.onError.observe(viewLifecycleOwner) {
            loadingDialog?.dismiss()
            AlertDialogUtils.showAlert(requireContext(), getString(R.string.error), it)
        }

        Glide.with(requireContext())
            .load(currentUser?.image)
            .placeholder(R.drawable.baseline_person_24)
            .circleCrop()
            .into(binding.postDataContainer.profileImage)

        binding.postDataContainer.name.text = currentUser?.name
        binding.postDataContainer.camera.setOnClickListener { openCamera() }
        binding.postDataContainer.gallery.setOnClickListener { openGallery() }
        post?.let {
            Glide.with(this)
                .load(it.image)
                .circleCrop()
                .into(binding.postDataContainer.postImage)
            binding.postDataContainer.description.setText(it.description)
        }

        binding.save.setText(if (post == null) R.string.save else R.string.update)
        binding.save.setOnClickListener { savePost() }
        binding.postDataContainer.description.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.postDataContainer.title.visibility = if (p0?.length == 0) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.postDataContainer.title.visibility = if (binding.postDataContainer.description.text?.length == 0) View.VISIBLE else View.GONE
        binding.close.setOnClickListener { activity?.finish() }
        binding.postDataContainer.email.setText(post?.createdUser?.email ?: FirebaseAuth.getInstance().currentUser?.email)
    }

    private fun onSuccess(post: Post?, user: User?) {
        loadingDialog?.dismiss()
        val intent = Intent()
        intent.putExtra("post", post)
        intent.putExtra("position", position)
        user?.let {
            intent.putExtra("user", it)
        }
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    private fun savePost() {
        if (binding.postDataContainer.description.text.toString().isNullOrEmpty()) {
            binding.postDataContainer.description.error = getString(R.string.please_enter_description)
        } else {
            loadingDialog?.show()
            var address: String? = ""
            myLocation?.let {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                try {
                    address = geocoder.getFromLocation(it.latitude, it.longitude, 1)?.get(0)?.getAddressLine(0)
                } catch (e: Exception) {
                }
            }
            viewModel.savePost(post?.uid,
                currentUser, binding.postDataContainer.description.text.toString(),
                imageBitmap, post?.image, post?.likeUserIds, PostLocation(
                    myLocation?.latitude ?: 0.0, myLocation?.longitude ?: 0.0,
                    address ?: "")
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityResultLauncher.launch(takePictureIntent)
    }

    private fun openGallery() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        galleryActivityResultLauncher.launch(intent)
    }

    private fun findMyLocationIfHasAccess() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        } else {
            findMyLocation()
        }
    }

    private fun findMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener(
                requireActivity(),
                OnSuccessListener<Location> { location -> // Got last known location. In some rare situations this can be null.
                    myLocation = location
                })
        }
    }

}