package com.example.jetnewsclone.ui.interests

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jetnewsclone.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainWidth
import com.example.jetnewsclone.data.interests.InterestSection
import com.example.jetnewsclone.data.interests.TopicSelection
import kotlin.math.max
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.unit.constrainHeight
import com.example.jetnewsclone.data.Result
import com.example.jetnewsclone.data.interests.impl.FakeInterestsRepository
import com.example.jetnewsclone.ui.theme.JetNewsCloneTheme
import kotlinx.coroutines.runBlocking

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 07/06/2022.
 */
enum class Sections(@StringRes val titleResId: Int) {
    Topics(R.string.interests_section_topics),
    People(R.string.interests_section_people),
    Publications(R.string.interests_section_publications)
}

/**
 * TabContent cho một tab duy nhất của màn hình.
 *
 * Điều này nhằm mục đích đóng gói một tab và nội dung của nó như một đối tượng duy nhất. Nó đã được thêm vào để tránh
 * chuyển một số tham số trên mỗi tab từ trình tổng hợp trạng thái sang có thể kết hợp hiển thị
 * tab hiện tại.
 *
 * @param section tab mà nội dung này dành cho
 * @param content nội dung của tab, một phần có thể tổng hợp mô tả nội dung
 */
class TabContent(val section: Sections, val content: @Composable () -> Unit)

/**
 * Màn hình sở thích không trạng thái hiển thị các tab được chỉ định trong [tabContent] điều chỉnh giao diện người dùng thành
 * kích thước màn hình khác nhau.
 *
 * @param tabContent (slot) các tab và nội dung của chúng để hiển thị trên màn hình này, phải là một
 * danh sách không trống, các tab được hiển thị theo thứ tự được chỉ định bởi danh sách này
 * @param currentSection (trạng thái) tab hiện tại để hiển thị, phải nằm trong [tabContent]
 * @param isExpandedScreen (trạng thái) true nếu màn hình được mở rộng
 * @param onTabChange (sự kiện) yêu cầu thay đổi [currentSection] thành một tab khác từ [tabContent]
 * @param openDrawer (event) yêu cầu mở ngăn kéo ứng dụng
 * @param StamoldState (state) trạng thái cho [Scaffold] của màn hình
 */
@Composable
fun InterestsScreen(
    tabContent: List<TabContent>,
    currentSection: Sections,
    isExpandedScreen: Boolean,
    onTabChange: (Sections) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.cd_interests),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = if(!isExpandedScreen) {
                    {
                        IconButton(onClick = openDrawer) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_jetnews_logo),
                                contentDescription = stringResource(id = R.string.cd_open_navigation_drawer),
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }
                } else {
                    null
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.cd_search)
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp
            )
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        InterestScreenContent(currentSection, isExpandedScreen, onTabChange, tabContent, screenModifier)
    }
}

/**
 * Ghi nhớ nội dung cho từng tab trên màn hình Sở thích
 * thu thập dữ liệu ứng dụng từ [InterestViewModel]
 */
@Composable
fun rememberTabContent(interestsViewModel: InterestsViewModel): List<TabContent> {
    // UiState của màn hình Interests
    val uiState by interestsViewModel.uiState.collectAsState()

    // Mô tả các phần màn hình ở đây vì mỗi phần cần 2 trạng thái và 1 sự kiện.
    // Chuyển chúng đến Màn hình Sở thích không trạng thái bằng cách sử dụng Nội dung tab.
    val topicsSection = TabContent(Sections.Topics) {
        val selectedTopics by interestsViewModel.selectedTopics.collectAsState()
        TabWithSections(
            sections = uiState.topics,
            selectedTopics = selectedTopics,
            onTopicSelect = { interestsViewModel.toggleTopicSelection(it) }
        )
    }

    val peopleSection = TabContent(Sections.People) {
        val selectedPeople by interestsViewModel.selectedPeople.collectAsState()
        TabWithTopics(
            topics = uiState.people,
            selectedTopics = selectedPeople,
            onTopicSelect = { interestsViewModel.togglePersonSelected(it)}
        )
    }

    val publicationSection = TabContent(Sections.Publications) {
        val selectedPublications by interestsViewModel.selectedPublications.collectAsState()
        TabWithTopics(
            topics = uiState.publications,
            selectedTopics = selectedPublications,
            onTopicSelect = { interestsViewModel.togglePublicationSelected(it)}
        )
    }

    return listOf(topicsSection, peopleSection, publicationSection)
}

/**
 * Hiển thị hàng tab với [currentSection] được chọn và phần nội dung của [tabContent] tương ứng.
 *
 * @param currentSection (state) tab hiện được chọn
 * @param isExpandedScreen (state) màn hình có được mở rộng hay không
 * @param updateSection (event) yêu cầu thay đổi lựa chọn tab
 * @param tabContent (slot) các tab và nội dung của chúng để hiển thị, phải là một danh sách không trống, các tab là
 * hiển thị theo thứ tự của danh sách này
 */
@Composable
private fun InterestScreenContent(
    currentSection: Sections,
    isExpandedScreen: Boolean,
    updateSection: (Sections) -> Unit,
    tabContent: List<TabContent>,
    modifier: Modifier = Modifier
) {
    val selectedTabIndex = tabContent.indexOfFirst { it.section == currentSection }
    Column(modifier) {
        InterestsTabRow(selectedTabIndex, updateSection, tabContent, isExpandedScreen)
        Divider(
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        )
        Box(modifier = Modifier.weight(1f)) {
            // hiển thị nội dung tab hiện tại là đơn vị @Composable () ->
            tabContent[selectedTabIndex].content()
        }
    }
}

/**
 * Công cụ sửa đổi cho các vùng chứa giao diện người dùng hiển thị các mục sở thích
 */
private val tabContainerModifier = Modifier
    .fillMaxWidth()
    .wrapContentWidth(Alignment.CenterHorizontally)
    .navigationBarsPadding()

/**
 * Hiển thị một danh sách các chủ đề đơn giản
 *
 * @param topics (trạng thái) chủ đề  để hiển thị
 * @param selectedTopics (trạng thái) các chủ đề hiện được chọn
 * @param onTopicSelect (sự kiện) yêu cầu thay đổi lựa chọn chủ đề
 */
@Composable
private fun TabWithTopics(
    topics: List<String>,
    selectedTopics: Set<String>,
    onTopicSelect: (String) -> Unit
) {
    InterestsAdaptiveContentLayout(
        topPadding = 16.dp,
        modifier = tabContainerModifier.verticalScroll(rememberScrollState())
    ) {
        topics.forEach { topic ->
            TopicItem(
                itemTitle = topic,
                selected = selectedTopics.contains(topic),
                onToggle = { onTopicSelect(topic) }
            )
        }
    }
}

/**
 * Hiển thị danh sách các chủ đề theo từng phần
 *
 * @param sections (state) các chủ đề để hiển thị, được nhóm theo các phần
 * @param selectedTopics (state) chủ đề hiện được chọn
 * @param onTopicSelect (event) yêu cầu thay đổi chủ đề + lựa chọn phần
 */
@Composable
private fun TabWithSections(
    sections: List<InterestSection>,
    selectedTopics: Set<TopicSelection>,
    onTopicSelect: (TopicSelection) -> Unit
) {
    Column(tabContainerModifier.verticalScroll(rememberScrollState())) {
        sections.forEach { (section, topics) ->
            Text(
                text = section,
                modifier = Modifier
                    .padding(16.dp)
                    .semantics { heading() },
                style = MaterialTheme.typography.subtitle1
            )
            InterestsAdaptiveContentLayout {
                topics.forEach { topic ->
                    TopicItem(
                        itemTitle = topic,
                        selected = selectedTopics.contains(TopicSelection(section, topic)),
                        onToggle = { onTopicSelect(TopicSelection(section, topic)) })
                }
            }
        }
    }
}


/**
 * Hiển thị một mục chủ đề có chiều rộng đầy đủ
 *
 * @param itemTitle (state) Tên chủ đề
 * @param selected (state) chủ đề hiện đang được chọn
 * @param onToggle (event) chuyển đổi lựa chọn cho chủ đề
 */
@Composable
private fun TopicItem(
    itemTitle: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = modifier.toggleable(
                value = selected,
                onValueChange = { onToggle() }
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val image = painterResource(id = R.drawable.placeholder_1_1)
            Image(
                painter = image,
                contentDescription = null, // decorative
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Text(
                text = itemTitle,
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f), // Ngắt dòng nếu tiêu đề quá dài
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.weight(0.01f))
            SelectTopicButton(selected = selected)
        }
        Divider(
            modifier = modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        )
    }
}

/**
 * TabRow cho màn hình sở thích
 */
@Composable
private fun InterestsTabRow(
    selectedTabIndex: Int,
    updateSection: (Sections) -> Unit,
    tabContent: List<TabContent>,
    isExpandedScreen: Boolean
) {
    when (isExpandedScreen) {
        false -> {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = MaterialTheme.colors.onPrimary,
                contentColor = MaterialTheme.colors.primary
            ) {
                InterestsTabRowContent(
                    selectedTabIndex,
                    updateSection,
                    tabContent
                )
            }
        }
        true -> {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = MaterialTheme.colors.onPrimary,
                contentColor = MaterialTheme.colors.primary,
                edgePadding = 0.dp
            ) {
                InterestsTabRowContent(
                    selectedTabIndex = selectedTabIndex,
                    updateSection = updateSection,
                    tabContent = tabContent,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun InterestsTabRowContent(
    selectedTabIndex: Int,
    updateSection: (Sections) -> Unit,
    tabContent: List<TabContent>,
    modifier: Modifier = Modifier
) {
    tabContent.forEachIndexed { index, content ->  
        val colorText = if (selectedTabIndex == index) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
        }
        Tab(
            selected = selectedTabIndex == index, 
            onClick = { updateSection(content.section) },
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text(
                text = stringResource(id = content.section.titleResId),
                color = colorText,
                style = MaterialTheme.typography.subtitle2,
                modifier = modifier.paddingFromBaseline(top = 20.dp)
            )
        }
    }
}

/**
 * Bố cục tùy chỉnh cho màn hình Sở thích đặt các mục trên màn hình với kích thước có sẵn.
 *
 * Ví dụ: Đưa ra một danh sách các mặt hàng (A, B, C, D, E) và kích thước màn hình cho phép 2 cột,
 * các mục sẽ được hiển thị trên màn hình như sau:
 *     A B
 *     C D
 *     E
 */
@Composable
private fun InterestsAdaptiveContentLayout(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    itemSpacing: Dp = 4.dp,
    itemMaxWidth: Dp = 450.dp,
    multipleColumnsBreakPoint: Dp = 600.dp,
    content: @Composable () -> Unit,
){
    Layout(modifier = modifier, content = content) { measurables, outerConstraints ->
        // Chuyển đổi tham số thành Px. An toàn để thực hiện vì khối đo lường `Layout` chạy trong phạm vi `Density`
        val multipleColumnsBreakPointPx = multipleColumnsBreakPoint.roundToPx()
        val topPaddingPx = topPadding.roundToPx()
        val itemSpacingPx = itemSpacing.roundToPx()
        val itemMaxWidthPx = itemMaxWidth.roundToPx()

        // Số cột hiển thị trên màn hình. Điều này được mã hóa thành 2 do
        // thiết kế chế nhạo, nhưng logic này có thể thay đổi trong tương lai.
        val columns = if (outerConstraints.maxWidth < multipleColumnsBreakPointPx) 1 else 2
        // Chiều rộng tối đa cho mỗi mục có tính đến không gian khả dụng, khoảng cách và `itemMaxWidth`
        val itemWidth = if (columns == 1) {
            outerConstraints.maxWidth
        } else {
            val maxWidthWithSpaces = outerConstraints.maxWidth - (columns - 1) * itemSpacingPx
            (maxWidthWithSpaces / columns).coerceIn(0, itemMaxWidthPx)
        }
        val itemConstraints = outerConstraints.copy(maxWidth = itemWidth)

        // Theo dõi chiều cao của mỗi hàng để tính toán kích thước cuối cùng của bố cục
        val rowHeights = IntArray(measurables.size / columns + 1)
        // Đo lường các phần tử với chiều rộng tối đa của chúng và theo dõi chiều cao
        val placeaables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(itemConstraints)
            // Cập nhật chiều cao cho từng hàng
            val row = index.floorDiv(columns)
            rowHeights[row] = max(rowHeights[row], placeable.height)
            placeable
        }

        // Tính toán maxHeight của bố cục Sở thích. Chiều cao của hàng + phần đệm trên cùng
        val layoutHeight = topPaddingPx + rowHeights.sum()
        // Tính toán chiều rộng tối đa của bố cục Sở thích
        val layoutWidth = itemWidth * columns + (itemSpacingPx * (columns - 1))

        // Bố cục có chiều rộng và chiều cao tối đa
        layout(
            width = outerConstraints.constrainWidth(layoutWidth),
            height = outerConstraints.constrainHeight(layoutHeight)
        ) {
            // Theo dõi tọa độ y mà chúng tôi đã sắp xếp con lên
            var yPosition = topPaddingPx
            // Tách các vị trí trong danh sách không vượt quá số cột
            // và đặt chúng có tính đến chiều rộng và khoảng cách của chúng
            placeaables.chunked(columns).forEachIndexed { rowIndex, row ->
                var xPosition = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x = xPosition, y = yPosition)
                    xPosition += placeable.width + itemSpacingPx
                }
                yPosition += rowHeights[rowIndex]
            }
        }
    }
}

@Preview("Interests screen", "Interests")
@Preview("Interests screen (dark)", "Interests", uiMode = UI_MODE_NIGHT_YES)
@Preview("Interests screen (big font)", "Interests", fontScale = 1.5f)
@Composable
fun PreviewInterestsScreenDrawer() {
    JetNewsCloneTheme {
        val tabContent = getFakeTabsContent()
        val (currentSection, updateSection) = rememberSaveable {
            mutableStateOf(tabContent.first().section)
        }

        InterestsScreen(
            tabContent = tabContent,
            currentSection = currentSection,
            isExpandedScreen = false,
            onTabChange = updateSection,
            openDrawer = {  },
            scaffoldState = rememberScaffoldState()
        )
    }
}


@Preview("Interests screen navrail", "Interests", device = Devices.PIXEL_C)
@Preview(
    "Interests screen navrail (dark)", "Interests",
    uiMode = UI_MODE_NIGHT_YES, device = Devices.PIXEL_C
)
@Preview(
    "Interests screen navtrail (big font)", "Interests",
    fontScale = 1.5f, device = Devices.PIXEL_C
)
@Composable
fun PreviewInterestsScreenNavRail(){
    JetNewsCloneTheme {
        val tabContent = getFakeTabsContent()
        val (currentSection, updateSection) = rememberSaveable {
            mutableStateOf(tabContent.first().section)
        }

        InterestsScreen(
            tabContent = tabContent,
            currentSection = currentSection,
            isExpandedScreen = true,
            onTabChange = updateSection,
            openDrawer = { /*TODO*/ },
            scaffoldState = rememberScaffoldState()
        )
    }
}

@Preview("Interests screen topics tab", "Topics")
@Preview("Interests screen topics tac (dark)", "Topics", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewTopicsTab() {
    val topics = runBlocking {
        (FakeInterestsRepository().getTopics() as Result.Success).data
    }
    JetNewsCloneTheme {
        Surface {
            TabWithSections(
                sections = topics,
                selectedTopics = setOf(),
                onTopicSelect = {

                }
            )
        }

    }
}

@Preview("Interests screen people tab", "People")
@Preview("Interests screen people tab (dark)", "People", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPeopleTab(){
    val people = runBlocking {
        (FakeInterestsRepository().getPeople() as Result.Success).data
    }
    JetNewsCloneTheme {
        Surface {
            TabWithTopics(
                topics = people,
                selectedTopics = setOf(),
                onTopicSelect = {}
            )
        }
    }
}

@Preview("Interests screen publications tab", "Publications")
@Preview("Interests screen publication tab (dark)", "Publications", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPublicationTab() {
    val publications = runBlocking {
        (FakeInterestsRepository().getPublications() as Result.Success).data
    }
    JetNewsCloneTheme {
        Surface {
            TabWithTopics(
                topics = publications,
                selectedTopics = setOf(),
                onTopicSelect = {}
            )
        }
    }
}

private fun getFakeTabsContent(): List<TabContent> {
    val interestsRepository = FakeInterestsRepository()
    val topicSelection = TabContent(Sections.Topics) {
        TabWithSections(
            runBlocking {
                (interestsRepository.getTopics() as Result.Success).data
            },
            emptySet()
        ) {  }
    }
    val peopleSection = TabContent(Sections.People) {
        TabWithTopics(
            runBlocking {
                (interestsRepository.getPeople() as Result.Success).data
            },
            emptySet()
        ) {  }
    }
    val publicationSection = TabContent(Sections.Publications) {
        TabWithTopics(
            runBlocking {
                (interestsRepository.getPublications() as Result.Success).data
            },
            emptySet()
        ) {  }
    }

    return listOf(topicSelection, peopleSection, publicationSection)
}