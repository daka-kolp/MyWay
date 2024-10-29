package com.dkolp.myway.presentation.fragments.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkolp.myway.infrastructure.utils.SignInProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val signInProvider: SignInProvider) : ViewModel() {
    private val _state = MutableLiveData<UIAuthState>()
    val uiAuthState: LiveData<UIAuthState> = _state

    fun getAccount(): UserInfo? {
        return signInProvider.getAccount()
    }

    fun login(context: Context, onSuccess: () -> Unit, onFailure: (error: String) -> Unit) {
        viewModelScope.launch {
            try {
                signInProvider.login(context)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.localizedMessage ?: e.toString())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            signInProvider.logout()
        }
    }

    sealed class UIAuthState {
        data object Processing : UIAuthState()
        data object Result : UIAuthState()
        class Error(val error: String) : UIAuthState()
    }
}
