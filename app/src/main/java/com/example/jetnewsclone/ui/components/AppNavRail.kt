package com.example.jetnewsclone.ui.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.NavigationRail
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jetnewsclone.R
import com.example.jetnewsclone.ui.JetNewsDestinations
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
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
fun JetNewsNavRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    NavigationRail(
        modifier = modifier,
        elevation = 0.dp,
        header = header
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
fun AppNavRail(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToInterests: () -> Unit,
    modifier: Modifier = Modifier
) {
    JetNewsNavRail(
        header = {
            JetNewsIcon(Modifier.padding(top = 8.dp))
        },
        modifier = modifier
    ) {
        NavRailIcon(
            icon = Icons.Filled.Home,
            contentDescription = stringResource(id = R.string.cd_navigate_home),
            isSelected = currentRoute == JetNewsDestinations.HOME_ROUTE,
            action = navigateToHome
        )
        Spacer(modifier = Modifier.height(16.dp))
        NavRailIcon(
            icon = Icons.Filled.ListAlt,
            contentDescription = stringResource(id = R.string.cd_navigate_home) ,
            isSelected = currentRoute == JetNewsDestinations.INTERESTS_ROUTE,
            action = navigateToInterests)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NavRailIcon(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colors.primary.copy(alpha = 0.12f)
        } else {
            Color.Transparent
        }
    )

    Surface(
        selected = isSelected,
        color = backgroundColor,
        onClick = action,
        shape = CircleShape,
        modifier = modifier.size(48.dp)
    ) {
        NavigationIcon(
            icon = icon,
            isSelected = isSelected,
            contentDescription = contentDescription,
        modifier = Modifier.size(32.dp))
    }
}

@Preview("Drawer contents")
@Preview("Drawer contents(dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppNavRail() {
    JetNewsCloneTheme {
        AppNavRail(
            currentRoute = JetNewsDestinations.HOME_ROUTE,
            navigateToHome = { /*TODO*/ },
            navigateToInterests = { /*TODO*/ })
    }
}