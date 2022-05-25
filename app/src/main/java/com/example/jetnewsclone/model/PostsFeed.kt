package com.example.jetnewsclone.model

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
 * A container of [Post]s, partitioned into different categories.
 */
data class PostsFeed(
    val highlightedPost: Post,
    val recommendedPosts: List<Post>,
    val popularPosts: List<Post>,
    val recentPosts: List<Post>
) {
    /**
     * Returns a flattened list of all posts contained in the feed.
     */
    val allPosts: List<Post> = listOf(highlightedPost) + recommendedPosts + popularPosts + recentPosts
}