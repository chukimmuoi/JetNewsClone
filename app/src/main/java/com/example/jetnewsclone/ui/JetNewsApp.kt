package com.example.jetnewsclone.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetnewsclone.data.AppContainer
import com.example.jetnewsclone.ui.components.AppNavRail
import com.example.jetnewsclone.ui.theme.JetNewsCloneTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

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
fun JetNewsApp(
    appContainer: AppContainer,
    widthSizeClass: WindowWidthSizeClass
) {
    JetNewsCloneTheme {
        val systemUiController = rememberSystemUiController()
        val darkIcons = MaterialTheme.colors.isLight
        SideEffect {
            systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
        }

        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            JetNewsNavigationActions(navController)
        }

        val coroutineScope = rememberCoroutineScope()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.destination?.route ?: JetNewsDestinations.HOME_ROUTE

        val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded
        val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen = isExpandedScreen)
        ModalDrawer(
            drawerContent = {
                AppDrawer(
                    currentRoute = currentRoute,
                    navigateToHome = navigationActions.navigateToHome,
                    navigateToInterests = navigationActions.navigateToInterests,
                    closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } },
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                )
            },
            drawerState = sizeAwareDrawerState,
            // Chỉ cho phép mở ngăn thông qua cử chỉ nếu màn hình không được mở rộng
            gesturesEnabled = !isExpandedScreen
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .windowInsetsPadding(
                        WindowInsets
                            .navigationBars
                            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    )
            ) {
                if (isExpandedScreen) {
                    AppNavRail(
                        currentRoute = currentRoute,
                        navigateToHome = navigationActions.navigateToHome,
                        navigateToInterests = navigationActions.navigateToInterests
                    )
                }
                JetNewsNavGraph(
                    appContainer = appContainer,
                    isExpandedScreen = isExpandedScreen,
                    navController = navController,
                    openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() }}
                )
            }
        }
    }
}

/**
 * Determine the drawer state to pass to the modal drawer.
 */
@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        // Nếu chúng ta muốn cho phép hiển thị ngăn kéo, chúng ta sử dụng một ngăn kéo thực sự, được ghi nhớ
        // trạng thái được xác định ở trên
        drawerState
    } else {
        // Nếu chúng tôi không muốn cho phép ngăn kéo được hiển thị, chúng tôi cung cấp trạng thái ngăn
        // cái đó bị khóa đóng lại. Điều này cố tình không được ghi nhớ, bởi vì chúng tôi
        // không muốn theo dõi bất kỳ thay đổi nào và luôn đóng
        DrawerState(DrawerValue.Closed)
    }
}

/**
* Xác định phần đệm nội dung để áp dụng cho các màn hình khác nhau của ứng dụng
*/
@Composable
fun rememberContentPaddingForScreen(additionalTop: Dp = 0.dp) =
    WindowInsets.systemBars
        .only(WindowInsetsSides.Bottom)
        .add(WindowInsets(top = additionalTop))
        .asPaddingValues()