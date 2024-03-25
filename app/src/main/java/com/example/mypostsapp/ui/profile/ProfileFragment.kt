package com.example.mypostsapp.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mypostsapp.AlertDialogUtils
import com.example.mypostsapp.ui.posts.PostsActivity
import com.example.mypostsapp.R
import com.example.mypostsapp.databinding.FragmentProfileBinding
import com.example.mypostsapp.ui.Login.LoginActivity
import com.example.mypostsapp.ui.posts.PostsScreenType

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding = FragmentProfileBinding.inflate(inflater)
        binding.edit.setOnClickListener {
            val navController = findNavController()
            val action = ProfileFragmentDirections.actionOpenProfileActiviy(viewModel.userLD.value)
            navController.navigate(action)
        }
        binding.logOut.setOnClickListener {
            AlertDialogUtils.showAlertWithButtons(requireContext(), getString(R.string.alarm), getString(R.string.are_you_sure), { dialog, p1 ->
                signOut()
            }) { dialog, p1 ->
                dialog.dismiss()
            }
        }

        binding.myPosts.setOnClickListener {
            val navController = findNavController()
            val action = ProfileFragmentDirections.actionPostsActiviy(PostsScreenType.MY_POSTS)
            navController.navigate(action)
        }
        binding.likedPosts.setOnClickListener {
            val navController = findNavController()
            val action = ProfileFragmentDirections.actionPostsActiviy(PostsScreenType.LIKED)
            navController.navigate(action)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userLD.observe(viewLifecycleOwner) {
            it?.let { user ->
                binding.name.text = user.name
                binding.email.text = user.email
                Glide.with(requireContext())
                    .load(it.image)
                    .placeholder(R.drawable.baseline_person_24)
                    .circleCrop()
                    .into(binding.imageView)
            }
        }
    }

    private fun signOut() {
        viewModel.signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        activity?.finish()
    }


}