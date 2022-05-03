package com.discovery.codingassesment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.discovery.codingassesment.data.model.NewsHeadlinesData
import com.discovery.codingassesment.net.NewsAPIState
import com.discovery.codingassesment.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsHomeActivityViewModel @Inject constructor(
    private var newsRepository: NewsRepository,
    application: Application
) : AndroidViewModel(application) {

    companion object {
        val TAG: String = NewsHomeActivityViewModel::class.java.simpleName
    }

    /*private lateinit var job: Job

    val newsHeadlinesState = MutableStateFlow<NewsAPIState<NewsHeadlinesData>>(
        NewsAPIState(
            Status.LOADING,
            null, ""
        )
    )

    init {
        fetchData()
    }*/

    private val _newsAPIState = MutableLiveData<NewsAPIState<NewsHeadlinesData>>()
        .apply {
            value = NewsAPIState.initialize("Initialize news API call")
        }
    var newsAPIState: LiveData<NewsAPIState<NewsHeadlinesData>> = _newsAPIState

    fun initializeNewsAPIState() {
        newsAPIState = _newsAPIState
    }

    fun startFetchingNewsHeadlines(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) {
            _newsAPIState.value = NewsAPIState.loading("Loading news headlines")
        } else {
            _newsAPIState.value = NewsAPIState.error("Internet not available")
        }
    }

    fun getLegalDocs() {
        viewModelScope.launch {
            newsRepository.fetchNewsHeadlines("us", "14126501a792437fb4d9be3b72db39f8")
                .catch {
                    it.printStackTrace()
                    _newsAPIState.value =
                        NewsAPIState.error(it.message.toString())
                }
                .collect {
                    _newsAPIState.value =
                        NewsAPIState.success(it.data)
                }
        }
    }

}