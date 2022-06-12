package com.example.jetnewsclone.ui.utils

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetnewsclone.R
import com.example.jetnewsclone.ui.theme.JetNewsCloneTheme

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 29/05/2022.
 */
@Composable
fun FavoriteButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.ThumbUpOffAlt, 
            contentDescription = stringResource(id = R.string.cd_add_to_favorites)
        )
    }
}

@Preview
@Composable
fun PreviewFavoriteButton() {
    JetNewsCloneTheme {
        FavoriteButton({})
    }
}

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentAlpha: Float = ContentAlpha.high
) {
    val clickLabel = stringResource(
        if (isBookmarked) R.string.unbookmark else R.string.bookmark
    )
    CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
        IconToggleButton(
            checked = isBookmarked,
            onCheckedChange = { onClick() },
            modifier = modifier.semantics {
                // Sử dụng clickLabel tùy chỉnh mà các dịch vụ trợ năng có thể giao tiếp với người dùng.
                // Chúng tôi chỉ muốn ghi đè nhãn, không phải hành động thực tế, vì vậy đối với hành động, chúng tôi chuyển giá trị null.
                this.onClick(
                    label = clickLabel,
                    action = null
                )
            }
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                contentDescription = null // được xử lý bởi clickLabel của phụ huynh
            )
        }
    }
}

@Preview
@Composable
fun PreviewBookmarkButton() {
    JetNewsCloneTheme {
        BookmarkButton(
            false,
            {}
        )
    }
}

@Composable
fun ShareButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = stringResource(id = R.string.cd_share)
        )
    }
}

@Preview
@Composable
fun PreviewShareButton() {
    JetNewsCloneTheme {
        ShareButton({})
    }
}

@Composable
fun TextSettingsButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            painter = painterResource(id = R.drawable.ic_text_settings), 
            contentDescription = stringResource(id = R.string.cd_text_settings))
    }
}

@Preview
@Composable
fun PreviewTextSettingsButton() {
    JetNewsCloneTheme {
        TextSettingsButton({})
    }
}