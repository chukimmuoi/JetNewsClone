package com.example.jetnewsclone.ui.article

import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnewsclone.R
import com.example.jetnewsclone.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnewsclone.data.posts.impl.post3
import com.example.jetnewsclone.model.Post
import com.example.jetnewsclone.ui.theme.JetNewsCloneTheme
import com.example.jetnewsclone.ui.utils.BookmarkButton
import com.example.jetnewsclone.ui.utils.FavoriteButton
import com.example.jetnewsclone.ui.utils.ShareButton
import com.example.jetnewsclone.ui.utils.TextSettingsButton
import com.example.jetnewsclone.utils.isScrolled
import kotlinx.coroutines.runBlocking
import com.example.jetnewsclone.data.Result

/**
* Màn hình Bài viết không trạng thái hiển thị một bài đăng duy nhất điều chỉnh giao diện người dùng với các kích thước màn hình khác nhau.
*
* @param post (trạng thái) mục để hiển thị
* @param showNavigationIcon (trạng thái) nếu biểu tượng điều hướng sẽ được hiển thị
* @param onBack (sự kiện) Yêu cầu điều hướng trở lại
* @param isFavorite (trạng thái) có phải mục này hiện đang được yêu thích không
* @param onToggleFavorite (sự kiện) yêu cầu bài đăng này chuyển sang trạng thái yêu thích
* @param lazyListState (trạng thái) [LazyListState] cho nội dung bài viết
*/
@Composable
fun ArticleScreen(
    post: Post,
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {
    var showUnimplementedActionDialog by rememberSaveable { mutableStateOf(false) }
    if (showUnimplementedActionDialog) {
        FunctionalityNotAvailablePopup { showUnimplementedActionDialog = false }
    }

    Row(modifier.fillMaxSize()) {
        val context = LocalContext.current
        ArticleScreenContent(
            post = post,
            // Cho phép mở Drawer nếu màn hình không được mở rộng
            navigationIconContent = if (!isExpandedScreen) {
                {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_up),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            } else {
                null
            },
            // Hiển thị thanh dưới cùng nếu màn hình không được mở rộng
            bottomBarContent = if (!isExpandedScreen) {
                {
                    BottomBar(
                        onUnimplementedAction = { showUnimplementedActionDialog = true },
                        isFavorite = isFavorite,
                        onToggleFavorite = onToggleFavorite,
                        onSharePost = { sharePost(post, context) },
                    )
                }
            } else {
                { }
            },
            lazyListState = lazyListState
        )
    }
}

/**
* Màn hình Bài viết không trạng thái hiển thị một bài đăng.
*
* @param post (trạng thái) mục để hiển thị
* @param navigationIconContent (UI) Nội dung sẽ hiển thị cho biểu tượng điều hướng
* @param bottomBarContent (UI) Nội dung  sẽ hiển thị cho thanh dưới cùng
*/
@Composable
private fun ArticleScreenContent(
    post: Post,
    navigationIconContent: @Composable (() -> Unit)? = null,
    bottomBarContent: @Composable () -> Unit = { },
    lazyListState: LazyListState = rememberLazyListState()
) {
    // https://foso.github.io/Jetpack-Compose-Playground/material/scaffold/
    // Bố cục thiết kế cho giao diện loại material design
    // Có thể thêm những thứ như TopBar, BottomBar, FAB hoặc Drawer.
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(align = Alignment.CenterHorizontally)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_article_background),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(36.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.published_in, post.publication?.name ?: ""),
                            style = MaterialTheme.typography.subtitle2,
                            color = LocalContentColor.current,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .weight(1.5f)
                        )
                    }
                },
                navigationIcon = navigationIconContent,
                elevation = if (!lazyListState.isScrolled) 0.dp else 4.dp,
                backgroundColor = MaterialTheme.colors.surface
            )
        },
        bottomBar = bottomBarContent
    ) { innerPadding ->
        PostContent(
            post = post,
            modifier = Modifier
                // innerPadding có tính đến thanh trên cùng và dưới cùng
                .padding(innerPadding),
            state = lazyListState,
        )
    }
}

/**
 * Bottom bar for Article screen
 *
 * @param onUnimplementedAction (event) called when the user performs an unimplemented action
 * @param isFavorite (state) if this post is currently a favorite
 * @param onToggleFavorite (event) request this post toggle it's favorite status
 * @param onSharePost (event) request this post to be shared
 */
@Composable
private fun BottomBar(
    onUnimplementedAction: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSharePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(elevation = 8.dp, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Vertical))
                .height(56.dp)
                .fillMaxWidth()
        ) {
            FavoriteButton(onClick = onUnimplementedAction)
            BookmarkButton(isBookmarked = isFavorite, onClick = onToggleFavorite)
            ShareButton(onClick = onSharePost)
            Spacer(modifier = Modifier.weight(1f))
            TextSettingsButton(onClick = onUnimplementedAction)
        }
    }
}

/**
 * Display a popup explaining functionality not available.
 *
 * @param onDismiss (event) request the popup be dismissed
 */
@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = stringResource(id = R.string.article_functionality_not_available),
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.close))
            }
        }
    )
}

/**
 * Show a share sheet for a post
 *
 * @param post to share
 * @param context Android context to show the share sheet in
 */
fun sharePost(post: Post, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.article_share_post)))
}

@Preview("Article screen")
@Preview("Article screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Preview("Article screen (big font)", fontScale = 1.5f)
@Composable
fun PreviewArticleDrawer() {
    JetNewsCloneTheme {
        val post = runBlocking {
            (BlockingFakePostsRepository().getPost(post3.id) as Result.Success).data
        }
        ArticleScreen(post, false, {}, false, {})
    }
}

@Preview("Article screen navrail", device = Devices.PIXEL_C)
@Preview(
    "Article screen navrail (dark)",
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_C
)
@Preview("Article screen navrail (big font)", fontScale = 1.5f, device = Devices.PIXEL_C)
@Composable
fun PreviewArticleNavRail() {
    JetNewsCloneTheme {
        val post = runBlocking {
            (BlockingFakePostsRepository().getPost(post3.id) as Result.Success).data
        }
        ArticleScreen(post, true, {}, false, {})
    }
}
