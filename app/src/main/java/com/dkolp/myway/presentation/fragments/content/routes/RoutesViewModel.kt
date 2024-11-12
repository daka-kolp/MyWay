package com.dkolp.myway.presentation.fragments.content.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkolp.myway.core.domain.MapRepository
import com.dkolp.myway.core.domain.entities.Geolocation
import com.dkolp.myway.core.domain.entities.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel @Inject constructor(private val repository: MapRepository) : ViewModel() {
    private val _uiRoutesState = MutableLiveData<UIRoutesState>(UIRoutesState.Empty)
    val uiRoutesState: LiveData<UIRoutesState> = _uiRoutesState

    var origin: Geolocation? = null

    fun getRoutes(destination: Geolocation) {
        val currentLocation = origin ?: return

        _uiRoutesState.value = UIRoutesState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var value: UIRoutesState = UIRoutesState.Processing
                _uiRoutesState.postValue(value)
                try {
                    val result = repository.getRoute(currentLocation, destination)
                    value = UIRoutesState.Result(result)
                } catch (e: Exception) {
                    value = UIRoutesState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _uiRoutesState.postValue(value)
                }
            }
        }
    }

    sealed class UIRoutesState {
        data object Empty : UIRoutesState()
        data object Processing : UIRoutesState()
        class Result(val route: Route) : UIRoutesState()
        class Error(val error: String) : UIRoutesState()
    }
}
