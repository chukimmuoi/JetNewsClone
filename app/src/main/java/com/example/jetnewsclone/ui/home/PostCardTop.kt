package com.example.jetnewsclone.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnewsclone.R
import com.example.jetnewsclone.data.posts.impl.posts
import com.example.jetnewsclone.model.Post
import com.example.jetnewsclone.ui.theme.JetNewsCloneTheme
import com.example.jetnewsclone.utils.CompletePreviews

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
fun PostCardTop(post: Post, modifier: Modifier = Modifier) {
    val typography = MaterialTheme.typography
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val imageModifier = Modifier
            .heightIn(min = 180.dp)
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
        Image(
            painter = painterResource(id = post.imageId),
            contentDescription = null, // decorative
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = post.title,
            style = typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = post.metadata.author.name,
            style = typography.subtitle2,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(
                    id = R.string.home_post_min_read,
                    formatArgs = arrayOf(
                        post.metadata.date,
                        post.metadata.readTimeMinutes
                    )
                ),
                style = typography.subtitle2
            )
        }
    }
}

/**
* Xem trước bản tổng hợp [PostCardTop]. Dữ liệu giả được chuyển vào tệp có thể tổng hợp.
*
* Tìm hiểu thêm về các tính năng Xem trước trong [tài liệu] (https://d.android.com/jetpack/compose/tooling#preview)
*/
@Preview
@Composable
fun PostCardTopPreview() {
    JetNewsCloneTheme {
        Surface {
            PostCardTop(post = posts.highlightedPost)
        }
    }
}

/*
* Các bản xem trước này sẽ chỉ hiển thị trên Android Studio Dolphin trở lên.
* Họ giới thiệu một tính năng được gọi là Chú thích nhiều lần xem.
*
* Đọc thêm trong [tài liệu] (https://d.android.com/jetpack/compose/tooling#preview-multipreview)
*/
@CompletePreviews
@Composable
fun PostCardTopPreviews() {
    JetNewsCloneTheme {
        Surface {
            PostCardTop(post = posts.highlightedPost)
        }
    }
}