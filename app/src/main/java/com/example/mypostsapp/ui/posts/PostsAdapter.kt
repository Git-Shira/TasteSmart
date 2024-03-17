package com.example.mypostsapp.ui.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.mypostsapp.R
import com.example.mypostsapp.databinding.PostItemLayoutBinding
import com.example.mypostsapp.entities.Post
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class PostsAdapter(
    private val posts: ArrayList<Post> = arrayListOf(),
    private val updatePostListener: OnItemClickListener,
    private val deletePostListener: OnItemClickListener,
    private val likePostListener: OnItemClickListener

) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(PostItemLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[holder.adapterPosition]
        holder.binding.description.text = post.description
        holder.binding.userName.text = post.createdUser?.name
        holder.binding.likesCount.text = "${post.likeUserIds?.size ?: 0}"
        holder.binding.time.text = formatter.format(post.created)
        Glide.with(holder.itemView.context)
            .load(post.createdUser?.image)
            .placeholder(R.drawable.baseline_person_24)
            .circleCrop()
            .into(holder.binding.userImage)
        post.image?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .into(holder.binding.postImage)
        }

        holder.binding.delete.setOnClickListener { deletePostListener.onItemClicked(holder.adapterPosition) }
        holder.binding.edit.setOnClickListener { updatePostListener.onItemClicked(holder.adapterPosition) }
        holder.binding.actionContainer.visibility = if (post.createdUser?.uid == FirebaseAuth.getInstance().uid) View.VISIBLE else View.GONE
        holder.binding.location.text = post.location?.address ?: ""
        holder.binding.postImage.visibility = if (post.image != null) View.VISIBLE else View.GONE
        post.likeUserIds?.firstOrNull { it == FirebaseAuth.getInstance().uid }?.let {
           holder.binding.likeImage.rotation = 180f
        } ?: run {
            holder.binding.likeImage.rotation = 0f
        }
        holder.binding.likeImage.setOnClickListener { likePostListener.onItemClicked(holder.adapterPosition) }
    }

    fun setItems(posts: ArrayList<Post>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(val binding: PostItemLayoutBinding) : ViewHolder(binding.root) {

    }
}


