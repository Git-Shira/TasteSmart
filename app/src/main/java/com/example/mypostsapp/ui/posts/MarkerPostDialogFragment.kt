package com.example.mypostsapp.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.GONE
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewGroup.VISIBLE
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.mypostsapp.R
import com.example.mypostsapp.databinding.PostMarkerBinding
import com.example.mypostsapp.entities.Post
import com.google.firebase.auth.FirebaseAuth


class MarkerPostDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(post: Post) :MarkerPostDialogFragment {
            val fragment = MarkerPostDialogFragment()
            fragment.arguments =  Bundle().apply {
                putSerializable("post", post)
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = PostMarkerBinding.inflate(inflater, container, false)
        val post = arguments?.get("post") as Post
        Glide.with(requireContext())
            .load(post.createdUser?.image)
            .placeholder(R.drawable.baseline_person_24)
            .circleCrop()
            .into(binding.container.profileImage)

        binding.container.name.text = post.createdUser?.name
        Glide.with(this)
            .load(post.image)
            .circleCrop()
            .into(binding.container.postImage)
        binding.container.description.setText(post.description)
        binding.container.camera.visibility = View.GONE
        binding.container.gallery.visibility = View.GONE
        binding.container.title.visibility = GONE
        binding.container.postImage.visibility = if (post.image != null) VISIBLE else GONE
        binding.location.text = post.location?.address
        binding.container.email.text = post.createdUser?.email

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)
    }


}