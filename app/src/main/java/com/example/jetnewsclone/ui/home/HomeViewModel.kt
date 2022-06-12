package com.example.jetnewsclone.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnewsclone.R
import com.example.jetnewsclone.data.Result
import com.example.jetnewsclone.data.posts.PostsRepository
import com.example.jetnewsclone.model.Post
import com.example.jetnewsclone.model.PostsFeed
import com.example.jetnewsclone.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 28/05/2022.
 */
/**
* Trạng thái giao diện người dùng cho Tuyến đường chính.
*
* Điều này có nguồn gốc từ [HomeViewModelState], nhưng được chia thành hai lớp con có thể có để nhiều hơn
* đại diện chính xác trạng thái có sẵn để hiển thị giao diện người dùng.
*/
sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    /**
    * Không có bài viết nào để kết xuất.
    *
    * Điều này có thể là do chúng vẫn đang tải hoặc chúng không tải được và chúng tôi
    * đang chờ tải lại chúng.
    */
    data class NoPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ): HomeUiState

    /**
    * Có các bài viết để hiển thị, như có trong [postsFeed].
    *
    * Được đảm bảo là một [selectedPost], là một trong những bài đăng từ [postsFeed].
    */
    data class HasPosts(
        val postsFeed: PostsFeed,
        val selectedPost: Post,
        val isArticleOpen: Boolean,
        val favorites: Set<String>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ): HomeUiState
}

/**
* Bản trình bày nội bộ của trạng thái Lộ trình nhà, ở dạng thô
*/
private data class HomeViewModelState(
    val postsFeed: PostsFeed? = null,
    val selectedPostId: String? = null,
    val isArticleOpen: Boolean = false,
    val favorites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = ""
) {

    /**
    * Chuyển đổi [HomeViewModelState] này thành [HomeUiState] được gõ mạnh hơn để lái xe
    * ui.
    */
    fun toUiState(): HomeUiState =
        if (postsFeed == null) {
            HomeUiState.NoPosts(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            HomeUiState.HasPosts(
                postsFeed = postsFeed,
                // Xác định bài đã chọn. Đây sẽ là bài đăng mà người dùng chọn lần cuối.
                // Nếu không có bài nào (hoặc bài đăng đó không có trong nguồn cấp dữ liệu hiện tại), hãy mặc định
                // bài đăng được đánh dấu
                selectedPost = postsFeed.allPosts.find {
                    it.id == selectedPostId
                } ?: postsFeed.highlightedPost,
                isArticleOpen = isArticleOpen,
                favorites = favorites,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

/**
* ViewModel xử lý logic nghiệp vụ của Màn hình chính
*/
class HomeViewModel(
    private val postsRepository: PostsRepository
): ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    // Trạng thái giao diện người dùng được hiển thị với giao diện người dùng
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshPosts()
    }

    /**
    * Làm mới bài đăng và cập nhật trạng thái giao diện người dùng cho phù hợp
    */
    fun refreshPosts() {
        // Trạng thái Ui đang làm mới
        viewModelState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            val result = postsRepository.getPostsFeed()
            viewModelState.update {
                when(result) {
                    is Result.Success -> it.copy(postsFeed = result.data, isLoading = false)
                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }

    /**
    * Chuyển đổi mục yêu thích của một bài đăng
    */
    fun toggleFavorite(postId: String) {
        viewModelScope.launch {
            postsRepository.toggleFavorite(postId)
        }
    }

    /**
    * Chọn bài báo đã cho để xem thêm thông tin về nó.
    */
    fun selectArticle(postId: String) {
        // Coi việc chọn một chi tiết đơn giản là tương tác với nó
        interactedWithArticleDetails(postId)
    }

    /**
    * Thông báo rằng một lỗi đã được hiển thị trên màn hình
    */
    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    /**
    * Thông báo rằng người dùng đã tương tác với nguồn cấp dữ liệu
    */
    fun interactedWithFeed() {
        viewModelState.update {
            it.copy(isArticleOpen = false)
        }
    }

    /**
    * Thông báo rằng người dùng đã tương tác với chi tiết bài viết
    */
    fun interactedWithArticleDetails(postId: String) {
        viewModelState.update {
            it.copy(
                selectedPostId = postId,
                isArticleOpen = true
            )
        }
    }

    /**
    * Thông báo rằng người dùng đã cập nhật truy vấn tìm kiếm
    */
    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
    }

    /**
    * Factory cho HomeViewModel lấy PostsRepository làm phần phụ thuộc
    */
    companion object {

        fun provideFactory(
            postsRepository: PostsRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return HomeViewModel(postsRepository) as T
            }
        }
    }
}

