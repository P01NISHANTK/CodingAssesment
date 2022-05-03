package com.discovery.codingassesment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.discovery.codingassesment.R
import com.discovery.codingassesment.data.model.NewsHeadlinesData
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewsHeadlineAdapter(
    private var context: Context,
    private var mDataSet: List<NewsHeadlinesData.Articles>,
    private val itemClickListener: OnItemClickListener
    )
    : RecyclerView.Adapter<NewsHeadlineAdapter.ViewHolder>(){

    companion object {
        val TAG: String = NewsHeadlineAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_news_headline_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder -> data : {${this.mDataSet[position]}")

        holder.bind(this.mDataSet[position].urlToImage)
        val source = context.resources.getString(
            R.string.news_source) + this.mDataSet[position].source.name
        holder.tvNewsSource.text = source

        val publishedAt = this.mDataSet[position].publishedAt
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val output = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
        var date: Date? = null
        try {
            date = input.parse(publishedAt)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val formatted = date?.let { output.format(it) }
        Log.i("DATE", "" + formatted)

        Log.d("Nishant", "published at  $formatted")
        holder.tvPublishedAt.text = formatted

        holder.tvNewsTitle.text = this.mDataSet[position].title

        holder.parentRow.setOnClickListener {
            itemClickListener.onItemClick(it, mDataSet[position].newsURL)
        }

    }

    override fun getItemCount(): Int {
        Log.d(TAG, "item count is : ${this.mDataSet.size}")
        return this.mDataSet.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parentRow: LinearLayout = itemView.findViewById(R.id.parent_row)
        private val ivNewsImageView: ImageView = itemView.findViewById(R.id.iv_row_news_image)
        val tvNewsTitle: TextView = itemView.findViewById(R.id.tv_row_news_title)
        val tvNewsSource: TextView = itemView.findViewById(R.id.tv_row_news_source)
        val tvPublishedAt: TextView = itemView.findViewById(R.id.tv_row_news_time)

        fun bind(photoUrl: String?) {
            val url = if (photoUrl != null) "$photoUrl?w=360" else null //1
            Glide.with(itemView)  //2
                .load(url) //3
                .centerCrop() //4
                .placeholder(R.drawable.ic_image_place_holder) //5
                .error(R.drawable.ic_broken_image) //6
                .fallback(R.drawable.ic_no_image) //7
                .into(ivNewsImageView) //8
        }

    }

    // method for refreshing recyclerview items.
    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(filterList: List<NewsHeadlinesData.Articles>) {
        this.mDataSet = filterList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, url: String)
    }
}