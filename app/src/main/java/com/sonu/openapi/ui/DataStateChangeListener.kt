package com.sonu.openapi.ui

interface DataStateChangeListener {
    fun onDataStateChange(dataState: DataState<*>?)
}