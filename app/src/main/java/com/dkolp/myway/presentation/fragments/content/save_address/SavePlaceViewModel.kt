package com.dkolp.myway.presentation.fragments.content.save_address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkolp.myway.core.domain.PlacesRepository
import com.dkolp.myway.core.domain.entities.Address
import com.dkolp.myway.core.domain.entities.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SavePlaceViewModel @Inject constructor(private val repository: PlacesRepository) : ViewModel() {
    val address: MutableLiveData<Address> by lazy { MutableLiveData<Address>(Address.nullable()) }
    val placeType: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val _state = MutableLiveData<UISavePlaceState>(UISavePlaceState.Initial)
    val uiSaveAddressState: LiveData<UISavePlaceState> = _state

    fun saveAddress(onSuccess: (newPlace: Place) -> Unit) {
        _state.value = UISavePlaceState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var value: UISavePlaceState = UISavePlaceState.Processing
                _state.postValue(value)
                try {
                    val address = address.value
                    val placeType = placeType.value
                    if (address != null && !placeType.isNullOrEmpty()) {
                        val place = Place(placeType, address)
                        repository.savePlace(place)
                        onSuccess(place)
                        value = UISavePlaceState.Success
                    } else {
                        value = UISavePlaceState.Error("Data is null")
                    }
                } catch (e: Exception) {
                    value = UISavePlaceState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _state.postValue(value)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    val formValid: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        val validator = Validator(::postValue)
        addSource(address, validator as Observer<Address>)
        addSource(placeType, validator as Observer<String>)
    }

    private inner class Validator(private val validationConsumer: (Boolean) -> Unit) : Observer<Any> {
        override fun onChanged(value: Any) {
            val address = address.value
            val placeType = placeType.value
            validationConsumer(
                when {
                    address != Address.nullable() && !placeType.isNullOrEmpty() -> true
                    else -> false
                }
            )
        }
    }

    sealed class UISavePlaceState {
        data object Initial : UISavePlaceState()
        data object Processing : UISavePlaceState()
        data object Success : UISavePlaceState()
        class Error(val error: String) : UISavePlaceState()
    }
}
