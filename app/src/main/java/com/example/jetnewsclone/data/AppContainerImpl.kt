package com.example.jetnewsclone.data

import android.content.Context
import com.example.jetnewsclone.data.interests.InterestsRepository
import com.example.jetnewsclone.data.interests.impl.FakeInterestsRepository
import com.example.jetnewsclone.data.posts.PostsRepository
import com.example.jetnewsclone.data.posts.impl.FakePostsRepository

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 24/05/2022.
 */
interface AppContainer {
    val postsRepository: PostsRepository
    val interestsRepository: InterestsRepository
}

class AppContainerImpl(
    private val applicationContext: Context
): AppContainer {

    override val postsRepository: PostsRepository by lazy {
        FakePostsRepository()
    }

    override val interestsRepository: InterestsRepository by lazy {
        FakeInterestsRepository()
    }
}