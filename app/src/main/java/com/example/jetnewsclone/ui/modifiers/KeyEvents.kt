package com.example.jetnewsclone.ui.modifiers

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 29/05/2022.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.interceptKey(key: Key, onKeyEvent: () -> Unit): Modifier {

    return this.onPreviewKeyEvent {
        if (it.key == key && it.type == KeyEventType.KeyUp) { // kích hoạt onKeyEvent trên KeyUp để ngăn các bản sao
            onKeyEvent()
            true
        } else {
            it.key == key // chỉ chuyển sự kiện khóa cho con nếu nó không phải là khóa đã chọn
        }
    }
}