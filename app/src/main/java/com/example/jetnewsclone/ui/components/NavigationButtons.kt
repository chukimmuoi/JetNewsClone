package com.example.jetnewsclone.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
 * Created by chukimmuoi on 28/05/2022.
 */
@Composable
fun JetNewsIcon(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_jetnews_logo),
        contentDescription = null,
        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
        modifier = modifier
    )
}

@Composable
fun NavigationIcon(
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tintColor: Color? = null
) {
    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }

    val iconTintColor = tintColor ?: if (isSelected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    }

    Image(
        modifier = modifier,
        imageVector = icon,
        contentDescription = contentDescription,
        contentScale = ContentScale.Inside,
        colorFilter = ColorFilter.tint(iconTintColor),
        alpha = imageAlpha
    )
}

@Preview("JetNews icon")
@Preview("jetNews icon (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewJetNewsIcon() {
    JetNewsCloneTheme {
        Surface {
            JetNewsIcon()
        }
    }
}

@Preview("Navigation icon")
@Preview("Navigation (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewNavigationIcon() {
    JetNewsCloneTheme {
        Surface {
            NavigationIcon(icon = Icons.Filled.Home, isSelected = true)
        }
    }
}