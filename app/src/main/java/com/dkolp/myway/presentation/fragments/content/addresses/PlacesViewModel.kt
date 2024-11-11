package com.dkolp.myway.presentation.fragments.content.addresses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkolp.myway.core.domain.PlacesRepository
import com.dkolp.myway.core.domain.entities.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(private val repository: PlacesRepository) : ViewModel() {
    private val _state = MutableLiveData<UIPlacesState>(UIPlacesState.Empty)
    val uiPlacesState: LiveData<UIPlacesState> = _state

    fun getPlaces() {
        _state.value = UIPlacesState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var value: UIPlacesState = UIPlacesState.Processing
                _state.postValue(value)
                try {
                    val result = repository.getPlaces()
                    value = if (result.isEmpty()) {
                        UIPlacesState.Empty
                    } else {
                        UIPlacesState.Result(result)
                    }
                } catch (e: Exception) {
                    value = UIPlacesState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _state.postValue(value)
                }
            }
        }
    }

    fun addPlace(newPlace: Place) {
        val value = _state.value
        if (value is UIPlacesState.Empty) {
            _state.postValue(UIPlacesState.Result(listOf(newPlace)))
        } else if (value is UIPlacesState.Result) {
            _state.postValue(UIPlacesState.Result(value.places.plus(newPlace)))
        }
    }

    sealed class UIPlacesState {
        data object Empty : UIPlacesState()
        data object Processing : UIPlacesState()
        class Result(val places: List<Place>) : UIPlacesState()
        class Error(val error: String) : UIPlacesState()
    }
}