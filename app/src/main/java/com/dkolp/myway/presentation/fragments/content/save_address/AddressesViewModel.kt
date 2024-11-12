package com.dkolp.myway.presentation.fragments.content.save_address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkolp.myway.core.domain.MapRepository
import com.dkolp.myway.core.domain.entities.Geolocation
import com.dkolp.myway.core.domain.entities.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddressesViewModel @Inject constructor(private val repository: MapRepository) : ViewModel() {
    private val _state = MutableLiveData<UIAddressesState>(UIAddressesState.Empty)
    val uiAddressesState: LiveData<UIAddressesState> = _state

    fun findAddressesByText(text: String) {
        _state.value = UIAddressesState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var value: UIAddressesState = UIAddressesState.Processing
                _state.postValue(value)
                try {
                    val result = repository.findAddressesByText(text)
                    value = UIAddressesState.ResultOnSearch(result)
                } catch (e: Exception) {
                    value = UIAddressesState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _state.postValue(value)
                }
            }
        }
    }

    fun findAddressByGeolocation(geolocation: Geolocation) {
        _state.value = UIAddressesState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var value: UIAddressesState = UIAddressesState.Processing
                _state.postValue(value)
                try {
                    val result = repository.findAddressesByGeolocation(geolocation)
                    value = if (result.isNotEmpty()) {
                        UIAddressesState.ResultOnLocation(result.first())
                    } else {
                        UIAddressesState.Error("MyWay can't find any address by your location")
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
        class ResultOnSearch(val addresses: List<Address>) : UIAddressesState()
        class ResultOnLocation(val address: Address) : UIAddressesState()
        class Error(val error: String) : UIAddressesState()
    }
}
