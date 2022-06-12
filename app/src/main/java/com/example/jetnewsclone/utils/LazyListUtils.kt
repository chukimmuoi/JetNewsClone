package com.example.jetnewsclone.utils

import androidx.compose.foundation.lazy.LazyListState

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 29/05/2022.
 */
val LazyListState.isScrolled: Boolean
    get() = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0