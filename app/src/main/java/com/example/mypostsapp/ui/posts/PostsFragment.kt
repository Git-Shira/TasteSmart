package com.example.mypostsapp.ui.posts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mypostsapp.CreateOrUpdatePostActivity
import com.example.mypostsapp.R
import com.example.mypostsapp.databinding.FragmentHomeBinding
import com.example.mypostsapp.entities.Post
import com.example.mypostsapp.entities.User

class PostsFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter = PostsAdapter(updatePostListener = object : OnItemClickListener {
        override fun onItemClicked(position: Int) {
            openUpdatePostActivity(position)
        }
    }, deletePostListener = object : OnItemClickListener {
        override fun onItemClicked(position: Int) {
            deletePost(position)
        }
    }, likePostListener = object : OnItemClickListener{
        override fun onItemClicked(position: Int) {
            onLikeClicked(position)
        }
    })


    var postsViewModel : PostsViewModel ?= null

    private val createPostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val post = result.data?.getSerializableExtra("post") as? Post
            val user = result.data?.getSerializableExtra("user") as? User
            post?.let {
                user?.let {
                    postsViewModel?.addPost(post, user)
                }
            }
        }
    }

    private val updatePostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val post = result.data?.getSerializableExtra("post") as? Post
            val position = result.data?.getIntExtra("position", -1) ?: -1
            val user = result.data?.getSerializableExtra("user") as? User
            post?.let {
                user?.let {
                    postsViewModel?.updatePost(position, post, user)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        postsViewModel =
            ViewModelProvider(this).get(PostsViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.list.layoutManager  = LinearLayoutManager(requireContext())
        return root
    }

    private fun updateTopBanner(user: User) {
        user.let {
            Glide.with(requireContext())
                .load(it.image)
                .placeholder(R.drawable.baseline_person_24)
                .circleCrop()
                .into(binding.profileImage)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postsViewModel?.posts?.observe(viewLifecycleOwner) {
            adapter.setItems(it)
        }

        postsViewModel?.currentUser?.observe(viewLifecycleOwner) {
            updateTopBanner(it)
        }

        binding.list.adapter = adapter

        binding.title.setOnClickListener { openCreatePostActivity() }
    }

    private fun openCreatePostActivity() {
        val intent = Intent(requireContext(), CreateOrUpdatePostActivity::class.java)
        getCurrentUser()?.let {
            intent.putExtra("user",  it)
        }
        createPostLauncher.launch(intent)
    }

    private fun openUpdatePostActivity(position: Int) {
        val intent = Intent(requireContext(), CreateOrUpdatePostActivity::class.java)
        getCurrentUser()?.let {
            intent.putExtra("user",  it)
        }
        intent.putExtra("position", position)
        intent.putExtra("post", postsViewModel?.posts?.value?.get(position))
        updatePostLauncher.launch(intent)
    }

    private fun getCurrentUser() : User? = postsViewModel?.currentUser?.value


    private fun deletePost(position: Int) {
        postsViewModel?.onDeleteClicked(position)
    }

    private fun onLikeClicked(position: Int) {
        postsViewModel?.onLikeClicked(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}