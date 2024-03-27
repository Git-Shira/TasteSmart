package com.example.mypostsapp.room


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mypostsapp.entities.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertPost(post: Post)

    @Query("SELECT * FROM posts")
    fun getAll(): List<Post>

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM posts")
    suspend fun clearPostsTable()

    // Add other methods for CRUD operations
}