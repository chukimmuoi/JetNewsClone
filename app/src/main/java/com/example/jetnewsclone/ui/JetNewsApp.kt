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
            // Ch??? cho ph??p m??? ng??n th??ng qua c??? ch??? n???u m??n h??nh kh??ng ???????c m??? r???ng
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
        // N???u ch??ng ta mu???n cho ph??p hi???n th??? ng??n k??o, ch??ng ta s??? d???ng m???t ng??n k??o th???c s???, ???????c ghi nh???
        // tr???ng th??i ???????c x??c ?????nh ??? tr??n
        drawerState
    } else {
        // N???u ch??ng t??i kh??ng mu???n cho ph??p ng??n k??o ???????c hi???n th???, ch??ng t??i cung c???p tr???ng th??i ng??n
        // c??i ???? b??? kh??a ????ng l???i. ??i???u n??y c??? t??nh kh??ng ???????c ghi nh???, b???i v?? ch??ng t??i
        // kh??ng mu???n theo d??i b???t k??? thay ?????i n??o v?? lu??n ????ng
        DrawerState(DrawerValue.Closed)
    }
}

/**
* X??c ?????nh ph???n ?????m n???i dung ????? ??p d???ng cho c??c m??n h??nh kh??c nhau c???a ???ng d???ng
*/
@Composable
fun rememberContentPaddingForScreen(additionalTop: Dp = 0.dp) =
    WindowInsets.systemBars
        .only(WindowInsetsSides.Bottom)
        .add(WindowInsets(top = additionalTop))
        .asPaddingValues()