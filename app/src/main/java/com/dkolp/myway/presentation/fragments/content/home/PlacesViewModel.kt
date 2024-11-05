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
    private val _uiPlacesState = MutableLiveData<UIPlacesState>(UIPlacesState.Empty)
    val uiPlacesState: LiveData<UIPlacesState> = _uiPlacesState

    fun findPlaceByText(text: String) {
        _uiPlacesState.value = UIPlacesState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var value: UIPlacesState = UIPlacesState.Processing
                _uiPlacesState.postValue(value)
                try {
                    val result = repository.findPlaceByText(text)
                    value = UIPlacesState.Result(result)
                } catch (e: Exception) {
                    value = UIPlacesState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _uiPlacesState.postValue(value)
                }
            }
        }
    }

    fun findPlaceByGeolocation(geolocation: Geolocation) {
        _uiPlacesState.value = UIPlacesState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var value: UIPlacesState = UIPlacesState.Processing
                _uiPlacesState.postValue(value)
                try {
                    val result = repository.findPlaceByGeolocation(geolocation)
                    value = UIPlacesState.Result(result)
                } catch (e: Exception) {
                    value = UIPlacesState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _uiPlacesState.postValue(value)
                }
            }
        }
    }

    sealed class UIPlacesState {
        data object Empty : UIPlacesState()
        data object Processing : UIPlacesState()
        class Result(val places: List<Place>) : UIPlacesState()
        class Error(val error: String) : UIPlacesState()
    }
}
