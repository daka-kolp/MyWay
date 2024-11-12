package com.dkolp.myway.presentation.fragments.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dkolp.myway.R
import com.dkolp.myway.presentation.fragments.content.addresses.PlacesFragment
import com.dkolp.myway.presentation.fragments.content.routes.RoutesFragment
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addressesButton = view.findViewById<MaterialButton>(R.id.addresses)
        addressesButton.setOnClickListener { openAddresses() }

        val routesButton = view.findViewById<MaterialButton>(R.id.routes)
        routesButton.setOnClickListener { openRoutes() }
    }

    private fun openAddresses() {
        parentFragmentManager
            .beginTransaction()
            .add(R.id.container, PlacesFragment())
            .addToBackStack("HomeFragment")
            .commit()
    }

    private fun openRoutes() {
        parentFragmentManager
            .beginTransaction()
            .add(R.id.container, RoutesFragment())
            .addToBackStack("HomeFragment")
            .commit()
    }
}
