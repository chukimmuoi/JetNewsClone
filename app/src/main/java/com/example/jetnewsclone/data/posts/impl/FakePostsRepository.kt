package com.example.jetnewsclone.data.posts.impl

import com.example.jetnewsclone.data.posts.PostsRepository
import com.example.jetnewsclone.model.Post
import com.example.jetnewsclone.model.PostsFeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import com.example.jetnewsclone.data.Result
import com.example.jetnewsclone.utils.addOrRemove
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.withLock

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
 * Việc triển khai PostsRepository trả về danh sách các bài đăng được mã hóa cứng
 * với các tài nguyên sau một số thời gian trì hoãn trong một chuỗi nền.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FakePostsRepository: PostsRepository {

    // Bây giờ, hãy lưu trữ những thứ này trong bộ nhớ
    private val favorites = MutableStateFlow<Set<String>>(setOf())

    // Được sử dụng để tạm ngừng các chức năng đọc và cập nhật trạng thái an toàn để gọi từ bất kỳ chuỗi nào
    private val mutex = Mutex()

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = posts.allPosts.find { it.id == postId }
            if (post == null) {
               Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPostsFeed(): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            delay(800)
            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                Result.Success(posts)
            }
        }
    }

    override fun observeFavorites(): Flow<Set<String>> {
        return favorites
    }

    override suspend fun toggleFavorite(postId: String) {
        mutex.withLock {
            val set = favorites.value.toMutableSet()
            set.addOrRemove(postId)
            favorites.value = set.toSet()
        }
    }

    // được sử dụng để thúc đẩy thất bại "ngẫu nhiên" theo một mô hình có thể dự đoán được,
    // làm cho yêu cầu đầu tiên luôn thành công
    private var requestCount = 0

    /**
     * Ngẫu nhiên không thành công một số tải để mô phỏng một mạng thực.
     *
     * Điều này sẽ không thành công một cách xác định sau mỗi 5 yêu cầu
     */
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0
}