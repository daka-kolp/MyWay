package com.dkolp.myway.presentation.fragments.content.addresses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dkolp.myway.core.domain.map.entities.Geolocation
import com.dkolp.myway.core.domain.map.entities.Place
import javax.inject.Inject

class AddressesViewModel @Inject constructor() : ViewModel() {
    private val _uiAddressesState = MutableLiveData<UIAddressesState>(UIAddressesState.Empty)
    val uiAddressesState: LiveData<UIAddressesState> = _uiAddressesState

    fun getAddresses() {
        val value = listOf(
            Place(
                "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. ",
                Geolocation.nullable()
            ),
            Place(
                "Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim.",
                Geolocation.nullable()
            ),
        )
        if (value.isEmpty()) {
            _uiAddressesState.postValue(UIAddressesState.Empty)
        } else {
            _uiAddressesState.postValue(UIAddressesState.Result(value))
        }
    }

    sealed class UIAddressesState {
        data object Empty : UIAddressesState()
        data object Processing : UIAddressesState()
        class Result(val places: List<Place>) : UIAddressesState()
        class Error(val error: String) : UIAddressesState()
    }
}