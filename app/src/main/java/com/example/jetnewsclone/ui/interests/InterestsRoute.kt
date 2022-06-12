package com.example.jetnewsclone.ui.interests

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable


/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 06/06/2022.
 */
/**
 * Có thể tổng hợp trạng thái hiển thị lộ trình Điều hướng cho màn hình Sở thích.
 *
 * @param interestsViewModel ViewModel xử lý logic nghiệp vụ của màn hình này
 * @param isExpandedScreen (state) true nếu màn hình được mở rộng
 * @param openDrawer (event) yêu cầu mở ngăn kéo ứng dụng
 * @param scaffoldState (state) trạng thái cho màn hình Scaffold
 */
@Composable
fun InterestsRoute(
    interestsViewModel: InterestsViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {

    val tabContent = rememberTabContent(interestsViewModel = interestsViewModel)
    val (currentSection, updateSection) = rememberSaveable {
        mutableStateOf(tabContent.first().section)
    }

    InterestsScreen(
        tabContent = tabContent,
        currentSection = currentSection,
        isExpandedScreen = isExpandedScreen,
        onTabChange = updateSection,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState
    )
}
