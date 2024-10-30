package com.dkolp.myway.presentation.fragments.content.home

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CurrentLocationViewModel @Inject constructor(private val client: FusedLocationProviderClient) : ViewModel() {
    private val _state = MutableLiveData<UICurrentLocationState>(UICurrentLocationState.Empty)
    val uiCurrentLocationState: LiveData<UICurrentLocationState> = _state

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        _state.postValue(UICurrentLocationState.Processing)
        client.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    _state.postValue(UICurrentLocationState.Result(location))
                } else {
                    _state.postValue(UICurrentLocationState.Error("We can not define your location"))
                }
            }
            .addOnFailureListener { e ->
                _state.postValue(UICurrentLocationState.Error(e.localizedMessage ?: e.toString()))
            }
    }

    sealed class UICurrentLocationState {
        data object Empty : UICurrentLocationState()
        data object Processing : UICurrentLocationState()
        class Result(val location: Location) : UICurrentLocationState()
        class Error(val error: String) : UICurrentLocationState()
    }
}
