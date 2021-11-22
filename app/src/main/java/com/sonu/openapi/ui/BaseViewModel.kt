package com.sonu.openapi.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel


abstract class BaseViewModel<StateEvent, ViewState> : ViewModel() {

    val TAG = "AppDebug"
    private val _stateEvent: MutableLiveData<StateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    val viewState: LiveData<ViewState>
        get() = _viewState

    val dataState: LiveData<DataState<ViewState>> = Transformations
        .switchMap(_viewState) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(stateEvent)
            }
        }

    fun setStateEvent(event: StateEvent) {
        _stateEvent.value = event!!
    }

    fun getCurrentViewStateOrNew(): ViewState {
        return viewState.value ?: initNewViewState()
    }

    abstract fun initNewViewState(): ViewState

    abstract fun handleStateEvent(stateEvent: ViewState): LiveData<DataState<ViewState>>

}