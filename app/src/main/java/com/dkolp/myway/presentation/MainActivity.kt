package com.dkolp.myway.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dkolp.myway.R
import com.dkolp.myway.presentation.fragments.auth.AuthFragment
import com.dkolp.myway.presentation.fragments.auth.AuthViewModel
import com.dkolp.myway.presentation.fragments.content.home.HomeFragment
import com.dkolp.myway.presentation.helpers.OnAuthLaunch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnAuthLaunch {
    private val viewModel by viewModels<AuthViewModel>()
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        onResultCallback(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }

    override fun login() {
        startForResult.launch(intent)
    }

    override fun logout() {
        viewModel.logout()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AuthFragment())
            .commit()
    }

    override fun showContent() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, HomeFragment())
            .commit()
    }

    private fun onResultCallback(result: ActivityResult) {
        viewModel.login(
            this,
            onSuccess = { showContent() },
            onFailure = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            },
        )
    }
}
