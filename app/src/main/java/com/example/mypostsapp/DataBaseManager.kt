package com.example.mypostsapp

import com.example.mypostsapp.entities.Post
import com.example.mypostsapp.entities.User
import com.example.mypostsapp.room.RoomManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.EventListener
import java.util.function.Consumer

class DataBaseManager {

    companion object {
        private const val USERS = "users"
        private const val POSTS = "posts"

        fun createUser(user: User, listener: OnCompleteListener<Void>) {
            FirebaseFirestore.getInstance()
                .collection(USERS)
                .document(user.uid)
                .set(user)
                .addOnCompleteListener(listener)
        }

        fun fetchPosts(viewModelScope: CoroutineScope, loadFromRoom: Boolean, posts: ArrayList<Post>, onDone: (saveToRoom: Boolean) -> Unit) {
            posts.clear()
            if (loadFromRoom) {
                viewModelScope.launch(Dispatchers.IO) {
                    posts.addAll(RoomManager.database.postDao().getAll())
                    onDone.invoke(false)
                    fetchPostsFromFireBase(posts, onDone)
                }
            } else {
                fetchPostsFromFireBase(posts, onDone)
            }
        }

        private fun fetchPostsFromFireBase(posts: ArrayList<Post>, onDone: (saveToRoom: Boolean) -> Unit) {
            FirebaseFirestore.getInstance()
                .collection(POSTS)
                .get()
                .addOnSuccessListener {
                    val size = it.documents.size
                    posts.clear()
                    it.documents.forEachIndexed { index, documentSnapshot ->
                        val post = documentSnapshot.toObject(Post::class.java)
                        FirebaseFirestore.getInstance()
                            .collection(USERS)
                            .document(post?.createdUser?.uid!!)
                            .get()
                            .addOnSuccessListener {userSnapshot->
                                post.createdUser = userSnapshot.toObject(User::class.java)
                                posts.add(post)
                                if (posts.size == size) {
                                    onDone.invoke(true)
                                }
                            }
                    }
                }
        }

        fun getCurrentUser(uid: String, listener: OnCompleteListener<DocumentSnapshot>) {
            FirebaseFirestore.getInstance()
                .collection(USERS)
                .document(uid)
                .get()
                .addOnCompleteListener{
                    listener.onComplete(it)
                }
        }

        fun savePost(post: Post, user: User? = null, onComplete: OnCompleteListener<Void>? = null) {
            FirebaseFirestore.getInstance()
                .collection(POSTS)
                .document(post.uid!!)
                .set(post)
                .addOnCompleteListener {
                    onComplete?.onComplete(it)
                }
        }

        fun deletePost(selectedPost: Post, user: User ?) {
            FirebaseFirestore.getInstance()
                .collection(POSTS)
                .document(selectedPost.uid!!)
                .delete()
        }
    }
}