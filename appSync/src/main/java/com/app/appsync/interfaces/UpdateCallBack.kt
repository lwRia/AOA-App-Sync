package com.app.appsync.interfaces

interface UpdateCallBack {
    fun onSuccess(response: String?)
    fun onFailure(message: String?)
}