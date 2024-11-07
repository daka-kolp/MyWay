package com.dkolp.myway.presentation.fragments.content.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.dkolp.myway.core.domain.map.entities.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SaveAddressViewModel @Inject constructor() : ViewModel() {
    val place: MutableLiveData<Place> by lazy { MutableLiveData<Place>(Place.nullable()) }
    val placeType: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    @Suppress("UNCHECKED_CAST")
    val formValid: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        val validator = Validator(::postValue)
        addSource(place, validator as Observer<Place>)
        addSource(placeType, validator as Observer<String>)
    }

    private inner class Validator(private val validationConsumer: (Boolean) -> Unit) : Observer<Any> {
        override fun onChanged(value: Any) {
            val place = place.value
            val placeType = placeType.value
            validationConsumer(
                when {
                    place != Place.nullable() && !placeType.isNullOrEmpty() -> true
                    else -> false
                }
            )
        }
    }
}
