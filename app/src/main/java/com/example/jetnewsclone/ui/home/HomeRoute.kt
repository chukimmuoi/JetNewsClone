package com.example.jetnewsclone.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetnewsclone.ui.article.ArticleScreen
import com.example.jetnewsclone.ui.theme.JetNewsCloneTheme

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
* Hiển thị lộ trình Trang chủ.
*
* Lưu ý: AAC ViewModels hiện không hoạt động với Soạn xem trước.
*
* @param homeViewModel ViewModel xử lý logic nghiệp vụ của màn hình này
* @param isExpandedScreen (trạng thái) cho dù màn hình có được mở rộng hay không
* @param openDrawer (sự kiện) yêu cầu mở ngăn kéo ứng dụng
* Trạng thái @param StamoldState (trạng thái) cho thành phần [Scaffold] trên màn hình này
*/
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    // UiState of the HomeScreen
    val uiState by homeViewModel.uiState.collectAsState()

    HomeRoute(
        uiState = uiState,
        isExpandedScreen = isExpandedScreen,
        onToggleFavorite = { homeViewModel.toggleFavorite(it) },
        onSelectPost = { homeViewModel.selectArticle(it) },
        onRefreshPosts = { homeViewModel.refreshPosts() },
        onErrorDismiss = { homeViewModel.errorShown(it) },
        onInteractWithFeed = { homeViewModel.interactedWithFeed() },
        onInteractWithArticleDetails = { homeViewModel.interactedWithArticleDetails(it)},
        onSearchInputChanged = {homeViewModel.onSearchInputChanged(it) },
        openDrawer = openDrawer,
        scaffoldState = scaffoldState
    )
}

/**
* Hiển thị lộ trình Trang chủ.
*
* Bản tổng hợp này không liên quan đến bất kỳ cơ quan quản lý nhà nước cụ thể nào.
*
* @param uiState (trạng thái) dữ liệu hiển thị trên màn hình
* @param isExpandedScreen (trạng thái) cho dù màn hình có được mở rộng hay không
* @param onToggleFavorite (sự kiện) chuyển đổi mục yêu thích cho một bài đăng
* @param onSelectPost (sự kiện) cho biết rằng một bài đăng đã được chọn
* @param onRefreshPosts (sự kiện) yêu cầu làm mới bài đăng
* Thông báo lỗi @param onErrorDismiss (sự kiện) được hiển thị
* @param onInteractWithFeed (sự kiện) cho biết rằng nguồn cấp dữ liệu đã được tương tác với
* @param onInteractWithArticleDetails (sự kiện) cho biết chi tiết bài viết đã được tương tác
* với
* @param openDrawer (sự kiện) yêu cầu mở ngăn kéo ứng dụng
* Trạng thái @param StamoldState (trạng thái) cho thành phần [Scaffold] trên màn hình này
*/
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeRoute(
    uiState: HomeUiState,
    isExpandedScreen: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    onInteractWithFeed: () -> Unit,
    onInteractWithArticleDetails: (String) -> Unit,
    onSearchInputChanged: (String) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    // Xây dựng trạng thái danh sách lười biếng cho danh sách và các chi tiết bên ngoài việc quyết định cái nào
    // buổi bieu diễn. Điều này cho phép trạng thái liên quan tồn tại ngoài quyết định đó, và do đó
    // chúng ta có thể giữ nguyên cuộn trong suốt bất kỳ thay đổi nào đối với nội dung.
    val homeListLazyListState = rememberLazyListState()
    val articleDetailLazyListStates = when(uiState) {
        is HomeUiState.HasPosts -> uiState.postsFeed.allPosts
        is HomeUiState.NoPosts -> emptyList()
    } .associate { post ->
        key(post.id) {
            post.id to rememberLazyListState()
        }
    }

    val homeScreenType = getHomeScreenType(isExpandedScreen, uiState)
    when(homeScreenType) {
        HomeScreenType.FeedWithArticleDetails -> {
            HomeFeedWithArticleDetailsScreen(
                uiState = uiState,
                showTopAppBar = !isExpandedScreen,
                onToggleFavorite = onToggleFavorite,
                onSelectPost = onSelectPost,
                onRefreshPosts = onRefreshPosts,
                onErrorDismiss = onErrorDismiss,
                onInteractWithList = onInteractWithFeed,
                onInteractWithDetail = onInteractWithArticleDetails,
                openDrawer = openDrawer,
                homeListLazyListState = homeListLazyListState,
                articleDetailLazyListStates = articleDetailLazyListStates,
                scaffoldState = scaffoldState,
                onSearchInputChanged = onSearchInputChanged
            )
        }
        HomeScreenType.Feed -> {
            HomeFeedScreen(
                uiState = uiState,
                showTopAppBar = !isExpandedScreen,
                onToggleFavorite = onToggleFavorite,
                onSelectPost = onSelectPost,
                onRefreshPosts = onRefreshPosts,
                onErrorDismiss = onErrorDismiss,
                openDrawer = openDrawer,
                homeListLazyListState = homeListLazyListState,
                scaffoldState = scaffoldState,
                onSearchInputChanged = onSearchInputChanged,
            )
        }
        HomeScreenType.ArticleDetails -> {
            // Được đảm bảo bởi điều kiện trên cho loại màn hình chính
            check(uiState is HomeUiState.HasPosts)

            ArticleScreen(
                post = uiState.selectedPost,
                isExpandedScreen = isExpandedScreen,
                onBack = onInteractWithFeed,
                isFavorite = uiState.favorites.contains(uiState.selectedPost.id),
                onToggleFavorite = {
                    onToggleFavorite(uiState.selectedPost.id)
                },
                lazyListState = articleDetailLazyListStates.getValue(
                    uiState.selectedPost.id
                )
            )

            // Nếu chúng ta chỉ hiển thị chi tiết, hãy nhấn nút quay lại để chuyển sang danh sách.
            // Điều này không cần gì hơn ngoài việc thông báo rằng chúng tôi "đã tương tác với danh sách"
            // vì đó là thứ thúc đẩy việc hiển thị nguồn cấp dữ liệu
            BackHandler {
                onInteractWithFeed()
            }
        }
    }
}

/**
* Liệt kê chính xác loại màn hình nào sẽ hiển thị trên tuyến đường chính.
*
* Có 3 lựa chọn:
* - [FeedWithArticleDetails], hiển thị cả danh sách tất cả các bài báo và một bài báo cụ thể.
* - [Nguồn cấp dữ liệu], chỉ hiển thị danh sách tất cả các bài báo
* - [ArticleDetails], chỉ hiển thị một bài báo cụ thể.
*/
private enum class HomeScreenType {
    FeedWithArticleDetails,
    Feed,
    ArticleDetails
}

/**
* Trả về [HomeScreenType] hiện tại để hiển thị, dựa trên việc màn hình có được mở rộng hay không
* và [HomeUiState].
*/
@Composable
private fun getHomeScreenType(
    isExpandedScreen: Boolean,
    uiState: HomeUiState
): HomeScreenType = when(isExpandedScreen) {
    false -> {
        when (uiState) {
            is HomeUiState.HasPosts -> {
                if (uiState.isArticleOpen) {
                    HomeScreenType.ArticleDetails
                } else {
                    HomeScreenType.Feed
                }
            }
            is HomeUiState.NoPosts -> HomeScreenType.Feed
        }
    }
    true -> HomeScreenType.FeedWithArticleDetails
}