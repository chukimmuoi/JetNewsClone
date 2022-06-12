package com.example.jetnewsclone.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetnewsclone.data.AppContainer
import com.example.jetnewsclone.ui.home.HomeRoute
import com.example.jetnewsclone.ui.home.HomeViewModel
import com.example.jetnewsclone.ui.interests.InterestsRoute
import com.example.jetnewsclone.ui.interests.InterestsViewModel

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
fun JetNewsNavGraph(
    appContainer: AppContainer,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = JetNewsDestinations.HOME_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(JetNewsDestinations.HOME_ROUTE) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(appContainer.postsRepository)
            )
            HomeRoute(
                homeViewModel = homeViewModel,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer)
        }
        composable(JetNewsDestinations.INTERESTS_ROUTE) {
            val interestsViewModel: InterestsViewModel = viewModel(
                factory = InterestsViewModel.providerFactory(
                    appContainer.interestsRepository
                )
            )
            InterestsRoute(
                interestsViewModel = interestsViewModel,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer
            )
        }
    }
}