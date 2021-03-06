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
* M??n h??nh ch??nh hi???n th??? ngu???n c???p d??? li???u c??ng v???i chi ti???t b??i vi???t.
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
                    .imePadding(), // th??m ph???n ?????m cho b??n ph??m ???o
                state = homeListLazyListState,
                searchInput = hasPostsUiState.searchInput,
                onSearchInputChanged = onSearchInputChanged,
            )
            // K???t h???p gi???a c??c b??i vi???t chi ti???t kh??c nhau
            Crossfade(targetState = hasPostsUiState.selectedPost) { detailPost ->
                // L???y tr???ng th??i danh s??ch l?????i bi???ng cho ch??? ????? xem chi ti???t n??y
                val detailLazyListState by derivedStateOf {
                    articleDetailLazyListStates.getValue(detailPost.id)
                }

                // ????nh d???u v??o id b??i ????ng ????? tr??nh chia s??? b???t k??? tr???ng th??i n??o gi???a c??c b??i ????ng kh??c nhau
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
                            .imePadding() // th??m ph???n ?????m cho b??n ph??m ???o
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
* M???t [Modifier] theo d??i t???t c??? ?????u v??o v?? g???i [block] m???i khi nh???n ???????c ?????u v??o.
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
* M??n h??nh ch??nh ch??? hi???n th??? ngu???n c???p b??i vi???t.
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
* M??n h??nh ch??nh hi???n th??? danh s??ch.
*
* Thao t??c n??y thi???t l???p khung v???i thanh ???ng d???ng tr??n c??ng v?? bao quanh [hasPostsContent] b???ng c??ch l??m m???i,
* t???i v?? x??? l?? l???i.
*
* Ch???c n??ng tr??? gi??p n??y t???n t???i v?? [HomeFeedWithArticleDetailsScreen] v?? [HomeFeedScreen] l??
* c???c k??? gi???ng nhau, ngo???i tr??? n???i dung k???t xu???t khi c?? b??i ????ng ????? hi???n th???.
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
                            // n???u kh??ng c?? b??i ????ng n??o v?? kh??ng c?? l???i, h??y ????? ng?????i d??ng l??m m???i theo c??ch th??? c??ng
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
                            // hi???n c?? l???i hi???n th???, kh??ng hi???n th??? b???t k??? n???i dung n??o
                            Box(contentModifier.fillMaxSize()) {
                                /* empty screen */
                            }
                        }
                    }
                }
            }
        )
    }

    // X??? l?? m???t th??ng b??o l???i t???i m???t th???i ??i???m v?? hi???n th??? ch??ng d?????i d???ng Snackbars trong giao di???n ng?????i d??ng
    if (uiState.errorMessages.isNotEmpty()) {
        // Ghi nh??? errorMessage ????? hi???n th??? tr??n m??n h??nh
        val errorMessage = remember(uiState) {
            uiState.errorMessages[0]
        }

        // L???y v??n b???n hi???n th??? tr??n tin nh???n t??? c??c t??i nguy??n
        val errorMessageText: String = stringResource(id = errorMessage.messageId)
        val retryMessageText = stringResource(id = R.string.retry)

        // N???u onRefreshPosts ho???c onErrorDismiss thay ?????i trong khi LaunchedEffect ??ang ch???y,
        // kh??ng kh???i ?????ng l???i hi???u ???ng v?? s??? d???ng c??c gi?? tr??? lambda m???i nh???t.
        val onRefreshPostsState by rememberUpdatedState(newValue = onRefreshPosts)
        val onErrorDismissState by rememberUpdatedState(newValue = onErrorDismiss)

        // Hi???u ???ng ch???y trong m???t quy tr??nh hi???n th??? Snackbar tr??n m??n h??nh
        // N???u c?? s??? thay ?????i ?????i v???i errorMessageText, retryMessageText ho???c Scate,
        // hi???u ???ng tr?????c ???? s??? b??? h???y v?? m???t hi???u ???ng m???i s??? b???t ?????u v???i c??c gi?? tr??? m???i
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
            // Sau khi th??ng b??o ???????c hi???n th??? v?? b??? lo???i b???, h??y th??ng b??o cho ViewModel
            onErrorDismissState(errorMessage.id)
        }
    }
}

/**
* Hi???n th??? tr???ng th??i tr???ng ban ?????u ho???c vu???t ????? l??m m???i n???i dung.
*
* @param empty (tr???ng th??i) khi ????ng, hi???n th??? [N???i dung tr???ng]
* @param emptyContent (v??? tr??) n???i dung hi???n th??? cho tr???ng th??i tr???ng
* @param loading (tr???ng th??i) khi ????ng, hi???n th??? v??ng xoay ??ang t???i tr??n [n???i dung]
* @param onRefresh S??? ki???n (s??? ki???n) ????? y??u c???u l??m m???i
* @param content (slot) n???i dung ch??nh s??? hi???n th???
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
* TopAppBar cho m??n h??nh ch??nh
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
* Ch??? b??o ti???n tr??nh h??nh tr??n to??n m??n h??nh
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
* Hi???n th??? m???t ngu???n c???p d??? li???u c??c b??i ????ng.
*
* Khi m???t b??i ????ng ???????c nh???p v??o, [onArticleTapped] s??? ???????c g???i.
*
* @param postsFeed (tr???ng th??i) ngu???n c???p d??? li???u s??? hi???n th???
* @param onArticleTapped (s??? ki???n) y??u c???u ??i???u h?????ng ?????n m??n h??nh B??i vi???t
* @param modifier modifier cho ph???n t??? g???c
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
* Giao di???n ng?????i d??ng t??m ki???m m??? r???ng - bao g???m h??? tr??? nh???p ????? g???i v?? tho??t ????? lo???i b??? tr??n tr?????ng t??m ki???m
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
                    // keyboardOptions thay ?????i ph??m d??ng m???i th??nh ph??m t??m ki???m tr??n b??n ph??m m???m
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    // keyboardActions g???i truy v???n t??m ki???m khi nh???n ph??m t??m ki???m
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            submitSearch(onSearchInputChanged, context)
                            keyboardControler?.hide()
                        }
                    ),
                    modifier = Modifier
                        .interceptKey(Key.Enter) { // g???i truy v???n t??m ki???m khi nh???n Enter
                            submitSearch(onSearchInputChanged, context)
                        }
                        .interceptKey(Key.Escape) { // lo???i b??? ti??u ??i???m khi nh???n Escape
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
* Ch???c n??ng tr??? gi??p tr??nh b??y ????? g???i truy v???n t??m ki???m c???a ng?????i d??ng
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
* Thanh tr??n c??ng cho m???t B??i ????ng khi ???????c hi???n th??? b??n c???nh Ngu???n c???p d??? li???u Trang ch???
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
* Ph???n tr??n c??ng c???a [PostList]
*
* @param post ???????c ????nh d???u  (tr???ng th??i) ???????c ????nh d???u ????? hi???n th???
* @param navigateToArticle (s??? ki???n) y??u c???u ??i???u h?????ng ?????n m??n h??nh B??i vi???t
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
 * D???i ph??n c??ch c?? chi???u r???ng ?????y ????? v???i ph???n ?????m cho [Danh s??ch ????ng]
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


