/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnewsclone.data.posts.impl

import com.example.jetnewsclone.data.posts.PostsRepository
import com.example.jetnewsclone.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import com.example.jetnewsclone.data.Result
import com.example.jetnewsclone.model.PostsFeed
import com.example.jetnewsclone.utils.addOrRemove

/**
 * Triển khai PostsRepository trả về danh sách các bài đăng được mã hóa cứng
 * với các tài nguyên một cách đồng bộ.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BlockingFakePostsRepository : PostsRepository {

    // Bây giờ, hãy giữ các mục yêu thích trong bộ nhớ
    private val favorites = MutableStateFlow<Set<String>>(setOf())

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = posts.allPosts.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Unable to find post"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPostsFeed(): Result<PostsFeed> {
        return Result.Success(posts)
    }

    override fun observeFavorites(): Flow<Set<String>> = favorites

    override suspend fun toggleFavorite(postId: String) {
        val set = favorites.value.toMutableSet()
        set.addOrRemove(postId)
        favorites.value = set
    }
}
