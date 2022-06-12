package com.example.jetnewsclone.ui.interests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnewsclone.data.interests.InterestSection
import com.example.jetnewsclone.data.interests.InterestsRepository
import com.example.jetnewsclone.data.interests.TopicSelection
import com.example.jetnewsclone.data.successOr
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
* Trạng thái giao diện người dùng cho màn hình Interests
*/
data class InterestsUiState(
    val topics: List<InterestSection> = emptyList(),
    val people: List<String> = emptyList(),
    val publications: List<String> = emptyList(),
    val loading: Boolean = false,
)

class InterestsViewModel(
    private val interestsRepository: InterestsRepository
) : ViewModel() {
    // Trạng thái giao diện người dùng được hiển thị với giao diện người dùng
    private val _uiState = MutableStateFlow(InterestsUiState(loading = true))
    val uiState: StateFlow<InterestsUiState> = _uiState.asStateFlow()

    val selectedTopics =
        interestsRepository.observeTopicsSelected().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptySet()
        )

    val selectedPeople =
        interestsRepository.observePeopleSelected().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptySet()
        )

    val selectedPublications =
        interestsRepository.observePublicationSelected().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptySet()
        )

    init {
        refreshAll()
    }

    fun toggleTopicSelection(topic: TopicSelection) {
        viewModelScope.launch {
            interestsRepository.toggleTopicSelection(topic)
        }
    }

    fun togglePersonSelected(person: String) {
        viewModelScope.launch {
            interestsRepository.togglePersonSelection(person)
        }
    }

    fun togglePublicationSelected(publication: String) {
        viewModelScope.launch {
            interestsRepository.togglePublicationSelected(publication)
        }
    }

    /**
     * Refresh topics, people, and publications
     */
    private fun refreshAll() {
        _uiState.update {
            it.copy(loading = true)
        }

        viewModelScope.launch {
            // Kích hoạt các yêu cầu kho lưu trữ song song
            val topicsDeferred = async { interestsRepository.getTopics() }
            val peopleDeferred = async { interestsRepository.getPeople() }
            val publicationsDeferred = async { interestsRepository.getPublications() }

            // Chờ tất cả các yêu cầu kết thúc
            val topics = topicsDeferred.await().successOr(emptyList())
            val people = peopleDeferred.await().successOr(emptyList())
            val publications = publicationsDeferred.await().successOr(emptyList())

            _uiState.update {
                it.copy(
                    loading = false,
                    topics = topics,
                    people = people,
                    publications = publications
                )
            }
        }
    }

    /**
    * Factory for InterestViewModel lấy PostsRepository làm phụ thuộc
    */
    companion object {
        fun providerFactory(
            interestsRepository: InterestsRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return InterestsViewModel(interestsRepository) as T
            }
        }
    }
}