package com.discovery.codingassesment.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.discovery.codingassesment.adapter.NewsHeadlineAdapter
import com.discovery.codingassesment.data.model.NewsHeadlinesData
import com.discovery.codingassesment.databinding.ActivityNewsHomeBinding
import com.discovery.codingassesment.net.APICallStatus
import com.discovery.codingassesment.utils.AppUtils
import com.discovery.codingassesment.viewmodel.NewsHomeActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsHomeActivity : AppCompatActivity() {

    companion object {
        val TAG: String = NewsHomeActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityNewsHomeBinding
    private val viewModel: NewsHomeActivityViewModel by viewModels()
    private lateinit var newsHeadlineAdapter: NewsHeadlineAdapter
    private lateinit var newsHeadlinesData: NewsHeadlinesData
    private var newsArticleList: List<NewsHeadlinesData.Articles> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        buildRecyclerView()

        addNewsAPIStatsObserver()

    }

    private fun buildRecyclerView() {
        newsHeadlineAdapter = NewsHeadlineAdapter(this, newsArticleList)
        binding.newsHeadlinesRv.layoutManager = LinearLayoutManager(this)
        binding.newsHeadlinesRv.adapter = newsHeadlineAdapter

    }

    private fun addNewsAPIStatsObserver() {
        viewModel.initializeNewsAPIState()
        viewModel.newsAPIState.observe(this) {
            Log.d(TAG, "received new headlines API update")
            when (it.APICallStatus) {
                APICallStatus.IDLE -> {
                    Log.d(TAG, "${it.message}")
                    if (AppUtils.isNetworkConnected(this)) {
                        viewModel.startFetchingNewsHeadlines(true)
                    } else {
                        viewModel.startFetchingNewsHeadlines(false)
                    }
                }
                APICallStatus.LOADING -> {
                    Log.d(TAG, "${it.message}")
                    viewModel.getLegalDocs()
                }
                APICallStatus.SUCCESS -> {
                    Log.d(TAG, "news headlines fetched Successfully")
                    //val responseBody: String = it.data.toString()

                    newsHeadlinesData = it.data!!
                    newsHeadlinesData.let {  newsHeadlinesData
                        if (newsHeadlinesData.status == "ok") {
                            Log.d(TAG, "total news size = ${newsHeadlinesData.totalResults}")
                            newsArticleList = newsHeadlinesData.articles
                            newsHeadlineAdapter.refreshList(newsArticleList)
                        }

                    }
                }
                APICallStatus.ERROR -> {
                    Log.d(TAG, "Fetching news headlines failed")
                    Toast.makeText(
                        this@NewsHomeActivity, it.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}
