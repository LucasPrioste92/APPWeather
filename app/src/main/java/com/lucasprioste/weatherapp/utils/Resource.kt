package com.lucasprioste.weatherapp.utils

data class Resource<out T>(val status: Status, val data: T? = null, val message: String? = null) {
    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(message: String?): Resource<T> {
            return Resource(Status.ERROR, null, message)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data)
        }
    }
}

enum class Status {
    LOADING,
    ERROR,
    SUCCESS,
}
