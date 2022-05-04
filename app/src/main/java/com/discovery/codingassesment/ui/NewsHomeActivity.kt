package com.discovery.codingassesment.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.discovery.codingassesment.R
import com.discovery.codingassesment.adapter.NewsHeadlineAdapter
import com.discovery.codingassesment.data.model.NewsHeadlinesData
import com.discovery.codingassesment.databinding.ActivityNewsHomeBinding
import com.discovery.codingassesment.net.APICallStatus
import com.discovery.codingassesment.utils.AppUtils
import com.discovery.codingassesment.utils.Constants
import com.discovery.codingassesment.viewmodel.NewsHomeActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewsHomeActivity : AppCompatActivity(), NewsHeadlineAdapter.OnItemClickListener {

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

        initSpinner()
        buildRecyclerView()

        addNewsAPIStatsObserver()

        binding.progressBar.visibility = View.GONE
        binding.btnRefresh.setOnClickListener {
            if (AppUtils.isNetworkConnected(this@NewsHomeActivity)) {
                viewModel.startFetchingNewsHeadlines(true)
            } else {
                viewModel.startFetchingNewsHeadlines(false)
            }
        }

    }

    private fun initSpinner() {
        val spinner = binding.selectCountry
        spinner.let {
            val countryList = resources.getStringArray(R.array.Country)
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, countryList)
            it.adapter = adapter

            val sharedPreferences: SharedPreferences = this.getSharedPreferences(
                Constants.sharedPrefFile, Context.MODE_PRIVATE)

            when(sharedPreferences.getString(Constants.countryCode, "us")) {
               "us" -> {
                   it.setSelection(0)
               }
              "ca" -> {
                  it.setSelection(1)
              }
            }
            it.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                    when(position) {
                        0 -> {
                            editor.putString(Constants.countryCode, "us")
                        }
                        1 -> {
                            editor.putString(Constants.countryCode, "ca")
                        }
                    }
                    editor.apply()

                    if (AppUtils.isNetworkConnected(this@NewsHomeActivity)) {
                        viewModel.startFetchingNewsHeadlines(true)
                    } else {
                        viewModel.startFetchingNewsHeadlines(false)
                    }

                    Toast.makeText(this@NewsHomeActivity,
                        getString(R.string.selected_item) + " " +
                                "" + countryList[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }

    private fun buildRecyclerView() {
        newsHeadlineAdapter = NewsHeadlineAdapter(this, newsArticleList,
            this@NewsHomeActivity)
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
                    val sharedPreferences: SharedPreferences = this.getSharedPreferences(
                        Constants.sharedPrefFile, Context.MODE_PRIVATE)
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.fetchNewsHeadlines(sharedPreferences.getString(Constants.countryCode, "us")!!)
                }
                APICallStatus.SUCCESS -> {
                    Log.d(TAG, "news headlines fetched Successfully")
                    binding.progressBar.visibility = View.GONE
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
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@NewsHomeActivity, it.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onItemClick(view: View, url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

}
