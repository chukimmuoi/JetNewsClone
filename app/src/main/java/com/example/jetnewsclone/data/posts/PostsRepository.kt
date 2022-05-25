package com.example.jetnewsclone.data.posts

import com.example.jetnewsclone.model.Post
import com.example.jetnewsclone.model.PostsFeed
import kotlinx.coroutines.flow.Flow
import com.example.jetnewsclone.data.Result

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 24/05/2022.
 */
/**
 * Interface to the Posts data layer.
 */
interface PostsRepository {

    /**
     * Get a specific JetNews post.
     */
    suspend fun getPost(postId: String?): Result<Post>

    /**
     * Get JetNews posts.
     */
    suspend fun getPostsFeed(): Result<PostsFeed>

    fun observeFavorites(): Flow<Set<String>>

    suspend fun toggleFavorite(postId: String)
}