package com.example.jetnewsclone.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnewsclone.R
import com.example.jetnewsclone.ui.components.JetNewsIcon
import com.example.jetnewsclone.ui.components.NavigationIcon
import com.example.jetnewsclone.ui.theme.JetNewsCloneTheme

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 25/05/2022.
 */
@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToInterests: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        JetNewsLogo(modifier = Modifier.padding(16.dp))
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
        DrawerButton(
            icon = Icons.Filled.Home,
            label = stringResource(id = R.string.home_title),
            isSelected = currentRoute == JetNewsDestinations.HOME_ROUTE,
            action = {
                navigateToHome()
                closeDrawer()
            }
        )
        DrawerButton(
            icon = Icons.Filled.ListAlt,
            label = stringResource(id = R.string.interests_title),
            isSelected = currentRoute == JetNewsDestinations.INTERESTS_ROUTE,
            action = {
                navigateToInterests()
                closeDrawer()
            }
        )
    }
}

@Composable
private fun JetNewsLogo(modifier: Modifier) {
    Row(modifier = modifier) {
        JetNewsIcon()
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_jetnews_wordmark),
            contentDescription = stringResource(id = R.string.app_name),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
        )
    }
}

@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colors
    val textIconColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val surfaceModifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()
    Surface(
        modifier = surfaceModifier,
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationIcon(
                    icon = icon,
                    isSelected = isSelected,
                    contentDescription = null,
                    tintColor = textIconColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = textIconColor
                )
            }
        }
    }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    JetNewsCloneTheme() {
        Surface {
            AppDrawer(
                currentRoute = JetNewsDestinations.HOME_ROUTE,
                navigateToHome = {},
                navigateToInterests = {},
                closeDrawer = { }
            )
        }
    }
}