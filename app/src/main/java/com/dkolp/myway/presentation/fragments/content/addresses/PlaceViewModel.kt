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
class PlaceViewModel @Inject constructor(private val repository: PlacesRepository) : ViewModel() {
    private val _state = MutableLiveData<UIAddressesState>(UIAddressesState.Empty)
    val uiAddressesState: LiveData<UIAddressesState> = _state

    fun getPlaces() {
        _state.value = UIAddressesState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var value: UIAddressesState = UIAddressesState.Processing
                _state.postValue(value)
                try {
                    val result = repository.getPlaces()
                    value = if (result.isEmpty()) {
                        UIAddressesState.Empty
                    } else {
                        UIAddressesState.Result(result)
                    }
                } catch (e: Exception) {
                    value = UIAddressesState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _state.postValue(value)
                }
            }
        }
    }

    sealed class UIAddressesState {
        data object Empty : UIAddressesState()
        data object Processing : UIAddressesState()
        class Result(val places: List<Place>) : UIAddressesState()
        class Error(val error: String) : UIAddressesState()
    }
}