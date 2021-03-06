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
 * TabContent cho m???t tab duy nh???t c???a m??n h??nh.
 *
 * ??i???u n??y nh???m m???c ????ch ????ng g??i m???t tab v?? n???i dung c???a n?? nh?? m???t ?????i t?????ng duy nh???t. N?? ???? ???????c th??m v??o ????? tr??nh
 * chuy???n m???t s??? tham s??? tr??n m???i tab t??? tr??nh t???ng h???p tr???ng th??i sang c?? th??? k???t h???p hi???n th???
 * tab hi???n t???i.
 *
 * @param section tab m?? n???i dung n??y d??nh cho
 * @param content n???i dung c???a tab, m???t ph???n c?? th??? t???ng h???p m?? t??? n???i dung
 */
class TabContent(val section: Sections, val content: @Composable () -> Unit)

/**
 * M??n h??nh s??? th??ch kh??ng tr???ng th??i hi???n th??? c??c tab ???????c ch??? ?????nh trong [tabContent] ??i???u ch???nh giao di???n ng?????i d??ng th??nh
 * k??ch th?????c m??n h??nh kh??c nhau.
 *
 * @param tabContent (slot) c??c tab v?? n???i dung c???a ch??ng ????? hi???n th??? tr??n m??n h??nh n??y, ph???i l?? m???t
 * danh s??ch kh??ng tr???ng, c??c tab ???????c hi???n th??? theo th??? t??? ???????c ch??? ?????nh b???i danh s??ch n??y
 * @param currentSection (tr???ng th??i) tab hi???n t???i ????? hi???n th???, ph???i n???m trong [tabContent]
 * @param isExpandedScreen (tr???ng th??i) true n???u m??n h??nh ???????c m??? r???ng
 * @param onTabChange (s??? ki???n) y??u c???u thay ?????i [currentSection] th??nh m???t tab kh??c t??? [tabContent]
 * @param openDrawer (event) y??u c???u m??? ng??n k??o ???ng d???ng
 * @param StamoldState (state) tr???ng th??i cho [Scaffold] c???a m??n h??nh
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
 * Ghi nh??? n???i dung cho t???ng tab tr??n m??n h??nh S??? th??ch
 * thu th???p d??? li???u ???ng d???ng t??? [InterestViewModel]
 */
@Composable
fun rememberTabContent(interestsViewModel: InterestsViewModel): List<TabContent> {
    // UiState c???a m??n h??nh Interests
    val uiState by interestsViewModel.uiState.collectAsState()

    // M?? t??? c??c ph???n m??n h??nh ??? ????y v?? m???i ph???n c???n 2 tr???ng th??i v?? 1 s??? ki???n.
    // Chuy???n ch??ng ?????n M??n h??nh S??? th??ch kh??ng tr???ng th??i b???ng c??ch s??? d???ng N???i dung tab.
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
 * Hi???n th??? h??ng tab v???i [currentSection] ???????c ch???n v?? ph???n n???i dung c???a [tabContent] t????ng ???ng.
 *
 * @param currentSection (state) tab hi???n ???????c ch???n
 * @param isExpandedScreen (state) m??n h??nh c?? ???????c m??? r???ng hay kh??ng
 * @param updateSection (event) y??u c???u thay ?????i l???a ch???n tab
 * @param tabContent (slot) c??c tab v?? n???i dung c???a ch??ng ????? hi???n th???, ph???i l?? m???t danh s??ch kh??ng tr???ng, c??c tab l??
 * hi???n th??? theo th??? t??? c???a danh s??ch n??y
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
            // hi???n th??? n???i dung tab hi???n t???i l?? ????n v??? @Composable () ->
            tabContent[selectedTabIndex].content()
        }
    }
}

/**
 * C??ng c??? s???a ?????i cho c??c v??ng ch???a giao di???n ng?????i d??ng hi???n th??? c??c m???c s??? th??ch
 */
private val tabContainerModifier = Modifier
    .fillMaxWidth()
    .wrapContentWidth(Alignment.CenterHorizontally)
    .navigationBarsPadding()

/**
 * Hi???n th??? m???t danh s??ch c??c ch??? ????? ????n gi???n
 *
 * @param topics (tr???ng th??i) ch??? ?????  ????? hi???n th???
 * @param selectedTopics (tr???ng th??i) c??c ch??? ????? hi???n ???????c ch???n
 * @param onTopicSelect (s??? ki???n) y??u c???u thay ?????i l???a ch???n ch??? ?????
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
 * Hi???n th??? danh s??ch c??c ch??? ????? theo t???ng ph???n
 *
 * @param sections (state) c??c ch??? ????? ????? hi???n th???, ???????c nh??m theo c??c ph???n
 * @param selectedTopics (state) ch??? ????? hi???n ???????c ch???n
 * @param onTopicSelect (event) y??u c???u thay ?????i ch??? ????? + l???a ch???n ph???n
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
 * Hi???n th??? m???t m???c ch??? ????? c?? chi???u r???ng ?????y ?????
 *
 * @param itemTitle (state) T??n ch??? ?????
 * @param selected (state) ch??? ????? hi???n ??ang ???????c ch???n
 * @param onToggle (event) chuy???n ?????i l???a ch???n cho ch??? ?????
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
                    .weight(1f), // Ng???t d??ng n???u ti??u ????? qu?? d??i
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
 * TabRow cho m??n h??nh s??? th??ch
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
 * B??? c???c t??y ch???nh cho m??n h??nh S??? th??ch ?????t c??c m???c tr??n m??n h??nh v???i k??ch th?????c c?? s???n.
 *
 * V?? d???: ????a ra m???t danh s??ch c??c m???t h??ng (A, B, C, D, E) v?? k??ch th?????c m??n h??nh cho ph??p 2 c???t,
 * c??c m???c s??? ???????c hi???n th??? tr??n m??n h??nh nh?? sau:
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
        // Chuy???n ?????i tham s??? th??nh Px. An to??n ????? th???c hi???n v?? kh???i ??o l?????ng `Layout` ch???y trong ph???m vi `Density`
        val multipleColumnsBreakPointPx = multipleColumnsBreakPoint.roundToPx()
        val topPaddingPx = topPadding.roundToPx()
        val itemSpacingPx = itemSpacing.roundToPx()
        val itemMaxWidthPx = itemMaxWidth.roundToPx()

        // S??? c???t hi???n th??? tr??n m??n h??nh. ??i???u n??y ???????c m?? h??a th??nh 2 do
        // thi???t k??? ch??? nh???o, nh??ng logic n??y c?? th??? thay ?????i trong t????ng lai.
        val columns = if (outerConstraints.maxWidth < multipleColumnsBreakPointPx) 1 else 2
        // Chi???u r???ng t???i ??a cho m???i m???c c?? t??nh ?????n kh??ng gian kh??? d???ng, kho???ng c??ch v?? `itemMaxWidth`
        val itemWidth = if (columns == 1) {
            outerConstraints.maxWidth
        } else {
            val maxWidthWithSpaces = outerConstraints.maxWidth - (columns - 1) * itemSpacingPx
            (maxWidthWithSpaces / columns).coerceIn(0, itemMaxWidthPx)
        }
        val itemConstraints = outerConstraints.copy(maxWidth = itemWidth)

        // Theo d??i chi???u cao c???a m???i h??ng ????? t??nh to??n k??ch th?????c cu???i c??ng c???a b??? c???c
        val rowHeights = IntArray(measurables.size / columns + 1)
        // ??o l?????ng c??c ph???n t??? v???i chi???u r???ng t???i ??a c???a ch??ng v?? theo d??i chi???u cao
        val placeaables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(itemConstraints)
            // C???p nh???t chi???u cao cho t???ng h??ng
            val row = index.floorDiv(columns)
            rowHeights[row] = max(rowHeights[row], placeable.height)
            placeable
        }

        // T??nh to??n maxHeight c???a b??? c???c S??? th??ch. Chi???u cao c???a h??ng + ph???n ?????m tr??n c??ng
        val layoutHeight = topPaddingPx + rowHeights.sum()
        // T??nh to??n chi???u r???ng t???i ??a c???a b??? c???c S??? th??ch
        val layoutWidth = itemWidth * columns + (itemSpacingPx * (columns - 1))

        // B??? c???c c?? chi???u r???ng v?? chi???u cao t???i ??a
        layout(
            width = outerConstraints.constrainWidth(layoutWidth),
            height = outerConstraints.constrainHeight(layoutHeight)
        ) {
            // Theo d??i t???a ????? y m?? ch??ng t??i ???? s???p x???p con l??n
            var yPosition = topPaddingPx
            // T??ch c??c v??? tr?? trong danh s??ch kh??ng v?????t qu?? s??? c???t
            // v?? ?????t ch??ng c?? t??nh ?????n chi???u r???ng v?? kho???ng c??ch c???a ch??ng
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