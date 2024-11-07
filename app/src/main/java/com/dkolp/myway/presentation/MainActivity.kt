package com.dkolp.myway.presentation

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import com.dkolp.myway.R
import com.dkolp.myway.presentation.fragments.auth.AuthFragment
import com.dkolp.myway.presentation.fragments.auth.AuthViewModel
import com.dkolp.myway.presentation.fragments.EmptyFragment
import com.dkolp.myway.presentation.fragments.content.addresses.AddressesFragment
import com.dkolp.myway.presentation.helpers.OnAuthLaunch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnAuthLaunch {
    private val viewModel by viewModels<AuthViewModel>()
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        onResultCallback()
    }
    private var progressLayout: FrameLayout? = null

    override fun login() {
        startForResult.launch(intent)
    }

    override fun logout() {
        viewModel.logout()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        progressLayout = findViewById(R.id.loading_layout)
        viewModel.uiAuthState.observe(this) { onAuthStatusChanged(it) }
    }

    private fun onResultCallback() {
        viewModel.login(this)
    }

    private fun onAuthStatusChanged(it: AuthViewModel.UIAuthState) {
        progressLayout?.isVisible = it is AuthViewModel.UIAuthState.Processing
        when (it) {
            is AuthViewModel.UIAuthState.Initial -> showEmptyContent()
            is AuthViewModel.UIAuthState.LoggedIn -> showContent()
            is AuthViewModel.UIAuthState.LoggedOut -> showAuth()
            is AuthViewModel.UIAuthState.Error -> showError(it)
            else -> Unit
        }
    }

    private fun showEmptyContent() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, EmptyFragment())
            .commit()
    }

    private fun showAuth() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AuthFragment())
            .commit()
    }


    private fun showContent() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AddressesFragment())
            .commit()
    }

    private fun showError(error: AuthViewModel.UIAuthState.Error) {
        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val focusedView = currentFocus
            if (focusedView is EditText) {
                val outRect = Rect()
                focusedView.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    focusedView.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
