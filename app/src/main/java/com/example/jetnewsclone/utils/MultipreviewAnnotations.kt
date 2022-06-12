package com.example.jetnewsclone.utils

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 29/05/2022.
 */
/**
* Thêm chú thích nhiều chế độ xem này vào một bản có thể tổng hợp để hiển thị bản có thể tổng hợp cực kỳ nhỏ và
* cỡ chữ cực lớn.
*
* Đọc thêm trong [tài liệu] (https://d.android.com/jetpack/compose/tooling#preview-multipreview)
*/
@Preview(
    name = "small font",
    group = "font scales",
    fontScale = 0.5f
)
@Preview(
    name = "large font",
    group = "font scales",
    fontScale = 1.5f
)
annotation class FontScalePreviews

/**
* Thêm chú thích nhiều chế độ xem này vào một bản có thể tổng hợp để hiển thị bản có thể tổng hợp trên nhiều thiết bị khác nhau
* kích thước: điện thoại, có thể gập lại và máy tính bảng.
*
* Đọc thêm trong [tài liệu] (https://d.android.com/jetpack/compose/tooling#preview-multipreview)
*/
@Preview(
    name = "phone",
    group = "devices",
    device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480"
)
@Preview(
    name = "foldable",
    group = "devices",
    device = "spec:shape=Normal,width=673,height=841,unit=dp,dpi=480"
)
@Preview(
    name = "tablet",
    group = "devices",
    device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"
)
annotation class DevicePreviews

/**
* Thêm chú thích nhiều chế độ xem này vào một bản có thể tổng hợp để hiển thị bản có thể kết hợp theo nhiều điểm chung khác nhau
* cấu hình:
* - Chủ đề tối
* - Cỡ chữ nhỏ và lớn
* - kích thước thiết bị khác nhau
*
* Đọc thêm trong [tài liệu] (https://d.android.com/jetpack/compose/tooling#preview-multipreview)
*
* _Lưu ý: Việc kết hợp các chú thích nhiều chế độ xem không có nghĩa là tất cả các kết hợp khác nhau đều được hiển thị.
* Thay vào đó, mỗi chú thích nhiều chế độ xem tự hoạt động và chỉ hiển thị các biến thể của chính nó._
*/
@Preview(
    name = "dark theme",
    group = "themes",
    uiMode = UI_MODE_NIGHT_YES
)
@FontScalePreviews
@DevicePreviews
annotation class CompletePreviews