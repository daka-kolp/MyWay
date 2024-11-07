package com.dkolp.myway.presentation.fragments.content.addresses

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dkolp.myway.R
import com.dkolp.myway.core.domain.map.entities.Place
import com.dkolp.myway.presentation.fragments.content.save_address.SaveAddressFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressesFragment : Fragment() {
    private val addressesVM by viewModels<AddressesViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var textNoAddresses: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_addresses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAddresses()

        swipeContainer = view.findViewById(R.id.refreshAddress)
        swipeContainer.setOnRefreshListener { getAddresses() }

        recyclerView = view.findViewById(R.id.addresses_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        textNoAddresses = view.findViewById(R.id.no_addresses)

        val addNewAddressButton = view.findViewById<MaterialButton>(R.id.add_new_address_button)
        addNewAddressButton.setOnClickListener { addNewAddress() }

        addressesVM.uiAddressesState.observe(viewLifecycleOwner) { onAddressesViewUpdate(it) }
    }

    private fun getAddresses() {
        addressesVM.getAddresses()
    }

    private fun addNewAddress() {
        parentFragmentManager
            .beginTransaction()
            .add(R.id.container, SaveAddressFragment())
            .addToBackStack("AddressesFragment")
            .commit()
    }

    private fun onAddressesViewUpdate(uiState: AddressesViewModel.UIAddressesState) {
        swipeContainer.isRefreshing = false
        textNoAddresses.isVisible = uiState is AddressesViewModel.UIAddressesState.Empty

        when (uiState) {
            is AddressesViewModel.UIAddressesState.Result -> onAddressFetched(uiState.places)
            is AddressesViewModel.UIAddressesState.Error -> onAddressFetchedError(uiState.error)
            else -> Unit
        }
    }

    private fun onAddressFetched(places: List<Place>) {
        val adapter = AddressesRecycleViewAdapter(places)
        recyclerView.adapter = adapter
    }

    private fun onAddressFetchedError(error: String) {
        Toast.makeText(context, "Error, the app can not fetch addresses: $error", Toast.LENGTH_LONG).show()
    }
}
