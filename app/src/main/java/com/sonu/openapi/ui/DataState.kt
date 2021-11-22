package com.sonu.openapi.ui

data class DataState<T>(
    var loading: Loading? = null,
    var data: Data<T>? = null,
    var error: Event<StateError>? = null
) {

    fun <T> error(
        response: Response
    ): DataState<T> {
        return DataState(
            error = Event(
                StateError(response)
            )
        )
    }

    fun <T> loading(
        isLoading: Boolean,
        cachedData: T?
    ): DataState<T> {
        return DataState(
            loading = Loading(isLoading),
            data = Data(
                data = Event.dataEvent(cachedData),
                response = null
            )
        )
    }

    fun <T> data(
        data: T? = null,
        response: Response? = null
    ): DataState<T> {
        return DataState(
            data = Data(
                Event.dataEvent(data),
                Event.responseEvent(response)
            )
        )
    }

}