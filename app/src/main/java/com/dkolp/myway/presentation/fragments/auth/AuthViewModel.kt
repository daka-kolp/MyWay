package com.dkolp.myway.presentation.fragments.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkolp.myway.infrastructure.utils.SignInProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.RefreshFailureCause
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val signInProvider: SignInProvider) : ViewModel() {
    private val _state = MutableLiveData<UIAuthState>()
    val uiAuthState: LiveData<UIAuthState> = _state

    init {
        viewModelScope.launch {
            signInProvider.sessionStatusFlow.collect {
                when (it) {
                    is SessionStatus.Initializing -> _state.postValue(UIAuthState.Initial)
                    is SessionStatus.Authenticated -> _state.postValue(UIAuthState.LoggedIn)
                    is SessionStatus.NotAuthenticated -> _state.postValue(UIAuthState.LoggedOut)
                    is SessionStatus.RefreshFailure -> {
                        when (val cause = it.cause) {
                            is RefreshFailureCause.InternalServerError -> _state.postValue(UIAuthState.Error(cause.exception))
                            is RefreshFailureCause.NetworkError -> _state.postValue(UIAuthState.Error(cause.exception))
                        }
                    }
                }
            }
        }
    }

    fun login(context: Context) {
        _state.postValue(UIAuthState.Processing)
        viewModelScope.launch {
            try {
                signInProvider.login(context)
            } catch (e: Exception) {
                _state.postValue(UIAuthState.Error(e))
            }
        }
    }

    fun logout() {
        _state.postValue(UIAuthState.Processing)
        viewModelScope.launch {
            try {
                signInProvider.logout()
            } catch (e: Exception) {
                _state.postValue(UIAuthState.Error(e))
            }
        }
    }

    sealed class UIAuthState {
        data object Initial : UIAuthState()
        data object Processing : UIAuthState()
        data object LoggedIn : UIAuthState()
        data object LoggedOut : UIAuthState()
        class Error(error: Throwable) : UIAuthState() {
            val message: String = error.localizedMessage ?: error.toString()
        }
    }
}
