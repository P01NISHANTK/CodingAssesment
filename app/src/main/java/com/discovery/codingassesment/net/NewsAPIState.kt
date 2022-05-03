package com.discovery.codingassesment.net

data class NewsAPIState <out T>(val APICallStatus: APICallStatus, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): NewsAPIState<T> {
            return NewsAPIState(APICallStatus.SUCCESS, data, null)
        }

        fun <T> error(msg: String): NewsAPIState<T> {
            return NewsAPIState(APICallStatus.ERROR, null, msg)
        }

        fun <T> loading(msg: String): NewsAPIState<T> {
            return NewsAPIState(APICallStatus.LOADING, null, msg)
        }

        fun <T> initialize(msg: String): NewsAPIState<T> {
            return  NewsAPIState(APICallStatus.IDLE, null, msg)
        }
    }
}

enum class APICallStatus {
    IDLE,
    SUCCESS,
    ERROR,
    LOADING
}
