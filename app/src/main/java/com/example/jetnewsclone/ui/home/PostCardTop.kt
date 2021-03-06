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
* Xem tr?????c b???n t???ng h???p [PostCardTop]. D??? li???u gi??? ???????c chuy???n v??o t???p c?? th??? t???ng h???p.
*
* T??m hi???u th??m v??? c??c t??nh n??ng Xem tr?????c trong [t??i li???u] (https://d.android.com/jetpack/compose/tooling#preview)
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
* C??c b???n xem tr?????c n??y s??? ch??? hi???n th??? tr??n Android Studio Dolphin tr??? l??n.
* H??? gi???i thi???u m???t t??nh n??ng ???????c g???i l?? Ch?? th??ch nhi???u l???n xem.
*
* ?????c th??m trong [t??i li???u] (https://d.android.com/jetpack/compose/tooling#preview-multipreview)
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