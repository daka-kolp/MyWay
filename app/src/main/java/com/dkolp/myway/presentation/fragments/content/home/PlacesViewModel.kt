package com.dkolp.myway.presentation.fragments.content.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkolp.myway.core.domain.MapRepository
import com.dkolp.myway.core.domain.entities.Geolocation
import com.dkolp.myway.core.domain.entities.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(private val repository: MapRepository) : ViewModel() {
    private val _state = MutableLiveData<UIPlacesState>(UIPlacesState.Empty)
    val uiPlacesState: LiveData<UIPlacesState> = _state

    fun findPlaceByText(text: String) {
        _state.value = UIPlacesState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var value: UIPlacesState = UIPlacesState.Processing
                _state.postValue(value)
                try {
                    val result = repository.findPlaceByText(text)
                    value = UIPlacesState.ResultOnSearch(result)
                } catch (e: Exception) {
                    value = UIPlacesState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _state.postValue(value)
                }
            }
        }
    }

    fun findPlaceByGeolocation(geolocation: Geolocation) {
        _state.value = UIPlacesState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var value: UIPlacesState = UIPlacesState.Processing
                _state.postValue(value)
                try {
                    val result = repository.findPlaceByGeolocation(geolocation)
                    value = if (result.isNotEmpty()) {
                        UIPlacesState.ResultOnCurrentLocation(result.first())
                    } else {
                        UIPlacesState.Error("MyWay can't find place by your location")
                    }
                } catch (e: Exception) {
                    value = UIPlacesState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _state.postValue(value)
                }
            }
        }
    }

    sealed class UIPlacesState {
        data object Empty : UIPlacesState()
        data object Processing : UIPlacesState()
        class ResultOnSearch(val places: List<Place>) : UIPlacesState()
        class ResultOnCurrentLocation(val place: Place) : UIPlacesState()
        class Error(val error: String) : UIPlacesState()
    }
}
