package com.example.jetnewsclone.ui.home

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetnewsclone.R
import com.example.jetnewsclone.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnewsclone.model.Post
import com.example.jetnewsclone.model.PostsFeed
import com.example.jetnewsclone.ui.article.postContentItems
import com.example.jetnewsclone.ui.article.sharePost
import com.example.jetnewsclone.ui.components.JetNewsSnackbarHost
import com.example.jetnewsclone.ui.modifiers.interceptKey
import com.example.jetnewsclone.ui.rememberContentPaddingForScreen
import com.example.jetnewsclone.ui.utils.BookmarkButton
import com.example.jetnewsclone.ui.utils.FavoriteButton
import com.example.jetnewsclone.ui.utils.ShareButton
import com.example.jetnewsclone.ui.utils.TextSettingsButton
import com.example.jetnewsclone.utils.isScrolled
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import com.example.jetnewsclone.data.Result
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
* Màn hình chính hiển thị nguồn cấp dữ liệu cùng với chi tiết bài viết.
*/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeFeedWithArticleDetailsScreen(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    onInteractWithList: () -> Unit,
    onInteractWithDetail: (String) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    articleDetailLazyListStates: Map<String, LazyListState>,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    onSearchInputChanged: (String) -> Unit
) {
    HomeScreenWithList(
        uiState = uiState,
        showTopAppBar = showTopAppBar,
        onRefreshPosts = onRefreshPosts,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        homeListLazyListState = homeListLazyListState,
        scaffoldState = scaffoldState,
        modifier = modifier
    ) { hasPostsUiState, contentModifier ->
        val contentPadding = rememberContentPaddingForScreen(additionalTop = 8.dp)
        Row(contentModifier) {
            PostList(
                postsFeed = hasPostsUiState.postsFeed,
                favorites = hasPostsUiState.favorites,
                showExpandedSearch = !showTopAppBar,
                onArticleTapped = onSelectPost,
                onToggleFavorite = onToggleFavorite,
                contentPadding = contentPadding,
                modifier = Modifier
                    .width(334.dp)
                    .notifyInput(onInteractWithList)
                    .imePadding(), // thêm phần đệm cho bàn phím ảo
                state = homeListLazyListState,
                searchInput = hasPostsUiState.searchInput,
                onSearchInputChanged = onSearchInputChanged,
            )
            // Kết hợp giữa các bài viết chi tiết khác nhau
            Crossfade(targetState = hasPostsUiState.selectedPost) { detailPost ->
                // Lấy trạng thái danh sách lười biếng cho chế độ xem chi tiết này
                val detailLazyListState by derivedStateOf {
                    articleDetailLazyListStates.getValue(detailPost.id)
                }

                // Đánh dấu vào id bài đăng để tránh chia sẻ bất kỳ trạng thái nào giữa các bài đăng khác nhau
                key(detailPost.id) {
                    LazyColumn(
                        state = detailLazyListState,
                        contentPadding = contentPadding,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .notifyInput {
                                onInteractWithDetail(detailPost.id)
                            }
                            .imePadding() // thêm phần đệm cho bàn phím ảo
                    ) {
                        stickyHeader {
                            val context = LocalContext.current
                            PostTopBar(
                                isFavorite = hasPostsUiState.favorites.contains(detailPost.id),
                                onToggleFavorite = { onToggleFavorite(detailPost.id) },
                                onSharePost = {
                                    sharePost(detailPost, context)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.End)
                            )
                        }
                        postContentItems(detailPost)
                    }
                }
            }
        }
    }
}

/**
* Một [Modifier] theo dõi tất cả đầu vào và gọi [block] mỗi khi nhận được đầu vào.
*/
private fun Modifier.notifyInput(block: () -> Unit): Modifier =
    composed {
        val blockState = rememberUpdatedState(block)
        pointerInput(Unit) {
            while (currentCoroutineContext().isActive) {
                awaitPointerEventScope {
                    awaitPointerEvent(PointerEventPass.Initial)
                    blockState.value()
                }
            }
        }
    }

/**
* Màn hình chính chỉ hiển thị nguồn cấp bài viết.
*/
@Composable
fun HomeFeedScreen(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    searchInput: String = "",
    onSearchInputChanged: (String) -> Unit,
) {
    HomeScreenWithList(
        uiState = uiState,
        showTopAppBar = showTopAppBar,
        onRefreshPosts = onRefreshPosts,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        homeListLazyListState = homeListLazyListState,
        scaffoldState = scaffoldState,
        modifier = modifier
    ) { hasPostsUiState, contentModifier ->
        PostList(
            postsFeed = hasPostsUiState.postsFeed,
            favorites = hasPostsUiState.favorites,
            showExpandedSearch = !showTopAppBar,
            onArticleTapped = onSelectPost,
            onToggleFavorite = onToggleFavorite,
            contentPadding = rememberContentPaddingForScreen(
                additionalTop = if (showTopAppBar) 0.dp else 8.dp
            ),
            modifier = contentModifier,
            state = homeListLazyListState,
            searchInput = searchInput,
            onSearchInputChanged = onSearchInputChanged
        )
    }
}

/**
* Màn hình chính hiển thị danh sách.
*
* Thao tác này thiết lập khung với thanh ứng dụng trên cùng và bao quanh [hasPostsContent] bằng cách làm mới,
* tải và xử lý lỗi.
*
* Chức năng trợ giúp này tồn tại vì [HomeFeedWithArticleDetailsScreen] và [HomeFeedScreen] là
* cực kỳ giống nhau, ngoại trừ nội dung kết xuất khi có bài đăng để hiển thị.
*/
@Composable
private fun HomeScreenWithList(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasPostsContent: @Composable (
        uiState: HomeUiState.HasPosts,
        modifier: Modifier
    ) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { JetNewsSnackbarHost(hostState = it) },
        topBar = {
            if (showTopAppBar) {
                HomeTopAppBar(
                    openDrawer = openDrawer,
                    elevation = if (!homeListLazyListState.isScrolled) 0.dp else 4.dp
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        LoadingContent(
            empty = when(uiState) {
                is HomeUiState.HasPosts -> false
                is HomeUiState.NoPosts -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = onRefreshPosts,
            content = {
                when(uiState) {
                    is HomeUiState.HasPosts -> hasPostsContent(uiState, contentModifier)
                    is HomeUiState.NoPosts -> {
                        if (uiState.errorMessages.isEmpty()) {
                            // nếu không có bài đăng nào và không có lỗi, hãy để người dùng làm mới theo cách thủ công
                            TextButton(
                                onClick = onRefreshPosts,
                                modifier = modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.home_tap_to_load_content),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // hiện có lỗi hiển thị, không hiển thị bất kỳ nội dung nào
                            Box(contentModifier.fillMaxSize()) {
                                /* empty screen */
                            }
                        }
                    }
                }
            }
        )
    }

    // Xử lý một thông báo lỗi tại một thời điểm và hiển thị chúng dưới dạng Snackbars trong giao diện người dùng
    if (uiState.errorMessages.isNotEmpty()) {
        // Ghi nhớ errorMessage để hiển thị trên màn hình
        val errorMessage = remember(uiState) {
            uiState.errorMessages[0]
        }

        // Lấy văn bản hiển thị trên tin nhắn từ các tài nguyên
        val errorMessageText: String = stringResource(id = errorMessage.messageId)
        val retryMessageText = stringResource(id = R.string.retry)

        // Nếu onRefreshPosts hoặc onErrorDismiss thay đổi trong khi LaunchedEffect đang chạy,
        // không khởi động lại hiệu ứng và sử dụng các giá trị lambda mới nhất.
        val onRefreshPostsState by rememberUpdatedState(newValue = onRefreshPosts)
        val onErrorDismissState by rememberUpdatedState(newValue = onErrorDismiss)

        // Hiệu ứng chạy trong một quy trình hiển thị Snackbar trên màn hình
        // Nếu có sự thay đổi đối với errorMessageText, retryMessageText hoặc Scate,
        // hiệu ứng trước đó sẽ bị hủy và một hiệu ứng mới sẽ bắt đầu với các giá trị mới
        LaunchedEffect(
            errorMessageText,
            retryMessageText,
            scaffoldState
        ) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = retryMessageText
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
            }
            // Sau khi thông báo được hiển thị và bị loại bỏ, hãy thông báo cho ViewModel
            onErrorDismissState(errorMessage.id)
        }
    }
}

/**
* Hiển thị trạng thái trống ban đầu hoặc vuốt để làm mới nội dung.
*
* @param empty (trạng thái) khi đúng, hiển thị [Nội dung trống]
* @param emptyContent (vị trí) nội dung hiển thị cho trạng thái trống
* @param loading (trạng thái) khi đúng, hiển thị vòng xoay đang tải trên [nội dung]
* @param onRefresh Sự kiện (sự kiện) để yêu cầu làm mới
* @param content (slot) nội dung chính sẽ hiển thị
*/
@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = loading),
            onRefresh = onRefresh,
            content = content
        )
    }
}

/**
* TopAppBar cho màn hình chính
*/
@Composable
private fun HomeTopAppBar(
    elevation: Dp,
    openDrawer: () -> Unit
) {
    val title = stringResource(id = R.string.app_name)
    TopAppBar(
        title = {
            Icon(
                painter = painterResource(id = R.drawable.ic_jetnews_wordmark),
                contentDescription = title,
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp, top = 10.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_jetnews_logo),
                    contentDescription = stringResource(id = R.string.cd_open_navigation_drawer),
                    tint = MaterialTheme.colors.primary
                )
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.cd_search))
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = elevation
    )
}

/**
* Chỉ báo tiến trình hình tròn toàn màn hình
*/
@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

/**
* Hiển thị một nguồn cấp dữ liệu các bài đăng.
*
* Khi một bài đăng được nhấp vào, [onArticleTapped] sẽ được gọi.
*
* @param postsFeed (trạng thái) nguồn cấp dữ liệu sẽ hiển thị
* @param onArticleTapped (sự kiện) yêu cầu điều hướng đến màn hình Bài viết
* @param modifier modifier cho phần tử gốc
*/
@Composable
private fun PostList(
    postsFeed: PostsFeed,
    favorites: Set<String>,
    showExpandedSearch: Boolean,
    onArticleTapped: (postId: String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    searchInput: String = "",
    onSearchInputChanged: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        state = state
    ) {
        if (showExpandedSearch) {
            item {
                HomeSearch(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    searchInput = searchInput,
                    onSearchInputChanged = onSearchInputChanged,
                )
            }
        }
        item { PostListTopSection(postsFeed.highlightedPost, onArticleTapped) }
        if (postsFeed.recommendedPosts.isNotEmpty()) {
            item {
                PostListSimpleSection(
                    postsFeed.recommendedPosts,
                    onArticleTapped,
                    favorites,
                    onToggleFavorite
                )
            }
        }
        if (postsFeed.popularPosts.isNotEmpty() && !showExpandedSearch) {
            item {
                PostListPopularSection(
                    postsFeed.popularPosts, onArticleTapped
                )
            }
        }
        if (postsFeed.recentPosts.isNotEmpty()) {
            item { PostListHistorySection(postsFeed.recentPosts, onArticleTapped) }
        }
    }
}

/**
* Giao diện người dùng tìm kiếm mở rộng - bao gồm hỗ trợ nhập để gửi và thoát để loại bỏ trên trường tìm kiếm
*/
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HomeSearch(
    modifier: Modifier = Modifier,
    searchInput: String = "",
    onSearchInputChanged: (String) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(Dp.Hairline, MaterialTheme.colors.onSurface.copy(alpha = .6f)),
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.cd_search)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                val context = LocalContext.current
                val focusManager = LocalFocusManager.current
                val keyboardControler = LocalSoftwareKeyboardController.current
                TextField(
                    value = searchInput,
                    onValueChange = { onSearchInputChanged(it) },
                    placeholder = { Text(text = stringResource(id = R.string.home_search)) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    // keyboardOptions thay đổi phím dòng mới thành phím tìm kiếm trên bàn phím mềm
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    // keyboardActions gửi truy vấn tìm kiếm khi nhấn phím tìm kiếm
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            submitSearch(onSearchInputChanged, context)
                            keyboardControler?.hide()
                        }
                    ),
                    modifier = Modifier
                        .interceptKey(Key.Enter) { // gửi truy vấn tìm kiếm khi nhấn Enter
                            submitSearch(onSearchInputChanged, context)
                        }
                        .interceptKey(Key.Escape) { // loại bỏ tiêu điểm khi nhấn Escape
                            focusManager.clearFocus()
                        }
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(id = R.string.cd_more_actions)
                    )
                }
            }
        }
    }
}

/**
* Chức năng trợ giúp trình bày để gửi truy vấn tìm kiếm của người dùng
*/
private fun submitSearch(
    onSearchInputChanged: (String) -> Unit,
    context: Context
) {
    onSearchInputChanged("")
    Toast.makeText(
        context,
        "Search is not yet implemented",
        Toast.LENGTH_SHORT
    ).show()
}

/**
* Thanh trên cùng cho một Bài đăng khi được hiển thị bên cạnh Nguồn cấp dữ liệu Trang chủ
*/
@Composable
private fun PostTopBar(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSharePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(Dp.Hairline, MaterialTheme.colors.onSurface.copy(alpha = .6f)),
        modifier = modifier.padding(end = 16.dp)
    ) {
        Row(Modifier.padding(horizontal = 8.dp)) {
            FavoriteButton {

            }
            BookmarkButton(isBookmarked = isFavorite, onClick = onToggleFavorite)
            ShareButton {
                onSharePost
            }
            TextSettingsButton {

            }
        }
    }
}

/**
* Phần trên cùng của [PostList]
*
* @param post được đánh dấu  (trạng thái) được đánh dấu để hiển thị
* @param navigateToArticle (sự kiện) yêu cầu điều hướng đến màn hình Bài viết
*/
@Composable
private fun PostListTopSection(
    post: Post,
    navigateToArticle: (String) -> Unit
) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        text = stringResource(id = R.string.home_top_section_title),
        style = MaterialTheme.typography.subtitle1
    )
    PostCardTop(
        post = post,
        modifier = Modifier.clickable(onClick = { navigateToArticle(post.id) })
    )
    PostListDivider()
}

/**
 * Dải phân cách có chiều rộng đầy đủ với phần đệm cho [Danh sách đăng]
 */
@Composable
private fun PostListDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}



@Preview("Home list drawer screen")
@Preview("Home list drawer screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Home list drawer screen (big font)", fontScale = 1.5f)
@Composable
fun PreviewHomeListDrawerScreen() {
    val postsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    JetNewsCloneTheme {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                selectedPost = postsFeed.highlightedPost,
                isArticleOpen = false,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = ""
            ),
            showTopAppBar = false,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            scaffoldState = rememberScaffoldState(),
            onSearchInputChanged = {}
        )
    }
}

@Preview("Home list navrail screen", device = Devices.NEXUS_7_2013)
@Preview(
    "Home list navrail screen (dark)",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.NEXUS_7_2013
)
@Preview("Home list navrail screen (big font)", fontScale = 1.5f, device = Devices.NEXUS_7_2013)
@Composable
fun PreviewHomeListNavRailScreen() {
    val postsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    JetNewsCloneTheme() {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                selectedPost = postsFeed.highlightedPost,
                isArticleOpen = false,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = ""
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            scaffoldState = rememberScaffoldState(),
            onSearchInputChanged = {}
        )
    }
}

@Preview("Home list detail screen", device = Devices.PIXEL_C)
@Preview("Home list detail screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_C)
@Preview("Home list detail screen (big font)", fontScale = 1.5f, device = Devices.PIXEL_C)
@Composable
fun PreviewHomeListDetailScreen() {
    val postsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    JetNewsCloneTheme {
        HomeFeedWithArticleDetailsScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                selectedPost = postsFeed.highlightedPost,
                isArticleOpen = false,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = ""
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            onInteractWithList = {},
            onInteractWithDetail = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            articleDetailLazyListStates = postsFeed.allPosts.associate { post ->
                key(post.id) {
                    post.id to rememberLazyListState()
                }
            },
            scaffoldState = rememberScaffoldState(),
            onSearchInputChanged = {}
        )
    }
}

/**
 * Full-width list items for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToArticle (event) request navigation to Article screen
 */
@Composable
private fun PostListSimpleSection(
    posts: List<Post>,
    navigateToArticle: (String) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit
) {
    Column {
        posts.forEach { post ->
            PostCardSimple(
                post = post,
                navigateToArticle = navigateToArticle,
                isFavorite = favorites.contains(post.id),
                onToggleFavorite = { onToggleFavorite(post.id) }
            )
            PostListDivider()
        }
    }
}

/**
 * Horizontal scrolling cards for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToArticle (event) request navigation to Article screen
 */
@Composable
private fun PostListPopularSection(
    posts: List<Post>,
    navigateToArticle: (String) -> Unit
) {
    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_popular_section_title),
            style = MaterialTheme.typography.subtitle1
        )

        LazyRow(modifier = Modifier.padding(end = 16.dp)) {
            items(posts) { post ->
                PostCardPopular(
                    post,
                    navigateToArticle,
                    Modifier.padding(start = 16.dp, bottom = 16.dp)
                )
            }
        }
        PostListDivider()
    }
}

/**
 * Full-width list items that display "based on your history" for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToArticle (event) request navigation to Article screen
 */
@Composable
private fun PostListHistorySection(
    posts: List<Post>,
    navigateToArticle: (String) -> Unit
) {
    Column {
        posts.forEach { post ->
            PostCardHistory(post, navigateToArticle)
            PostListDivider()
        }
    }
}


