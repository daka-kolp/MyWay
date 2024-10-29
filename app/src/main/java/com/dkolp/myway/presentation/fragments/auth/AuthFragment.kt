package com.dkolp.myway.presentation.fragments.auth

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dkolp.myway.R
import com.dkolp.myway.presentation.helpers.OnAuthLaunch
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as OnAuthLaunch
        if (viewModel.getAccount() != null) activity.showContent()

        val signInButton: MaterialButton = view.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener { activity.login() }
    }
}
