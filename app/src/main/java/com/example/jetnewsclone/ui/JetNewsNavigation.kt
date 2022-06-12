package com.example.jetnewsclone.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 25/05/2022.
 */
/**
 * Destinations used in the [JetnewsApp].
 */
object JetNewsDestinations {
    const val HOME_ROUTE = "home"
    const val INTERESTS_ROUTE = "interests"
}

/**
 * Models the navigation actions in the app.
 */
class JetNewsNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(JetNewsDestinations.HOME_ROUTE) {
            // Bật lên điểm đến bắt đầu của biểu đồ để
            // tránh tạo ra một lượng lớn các điểm đến
            // ở ngăn xếp phía sau khi người dùng chọn các mục
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Tránh nhiều bản sao của cùng một điểm đến khi
            // chọn lại cùng một mục
            launchSingleTop = true
            // Khôi phục trạng thái khi chọn lại một mục đã chọn trước đó
            restoreState = true
        }
    }
    val navigateToInterests: () -> Unit = {
        navController.navigate(JetNewsDestinations.INTERESTS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}