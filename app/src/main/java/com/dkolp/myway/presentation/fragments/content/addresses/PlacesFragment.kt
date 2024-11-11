package com.dkolp.myway.presentation.fragments.content.addresses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dkolp.myway.R
import com.dkolp.myway.core.domain.entities.Place
import com.dkolp.myway.presentation.fragments.content.save_address.SavePlaceFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlacesFragment : Fragment() {
    private lateinit var placesVM: PlacesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var textNoAddresses: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesVM = ViewModelProvider(requireActivity())[PlacesViewModel::class.java]
        getPlaces()

        swipeContainer = view.findViewById(R.id.refreshAddress)
        swipeContainer.setOnRefreshListener { getPlaces() }

        recyclerView = view.findViewById(R.id.addresses_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        textNoAddresses = view.findViewById(R.id.no_addresses)

        val addNewAddressButton = view.findViewById<MaterialButton>(R.id.add_new_address_button)
        addNewAddressButton.setOnClickListener { addNewAddress() }

        placesVM.uiPlacesState.observe(viewLifecycleOwner) { onPlacesViewUpdate(it) }
    }

    private fun getPlaces() {
        placesVM.getPlaces()
    }

    private fun addNewAddress() {
        parentFragmentManager
            .beginTransaction()
            .add(R.id.container, SavePlaceFragment())
            .addToBackStack("AddressesFragment")
            .commit()
    }

    private fun onPlacesViewUpdate(uiState: PlacesViewModel.UIPlacesState) {
        swipeContainer.isRefreshing = false
        textNoAddresses.isVisible = uiState is PlacesViewModel.UIPlacesState.Empty

        when (uiState) {
            is PlacesViewModel.UIPlacesState.Result -> onPlacesFetched(uiState.places)
            is PlacesViewModel.UIPlacesState.Error -> onPlacesFetchedError(uiState.error)
            else -> Unit
        }
    }

    private fun onPlacesFetched(places: List<Place>) {
        val adapter = PlacesRecycleViewAdapter(places)
        recyclerView.adapter = adapter
    }

    private fun onPlacesFetchedError(error: String) {
        Toast.makeText(context, "Error, the app can not fetch addresses: $error", Toast.LENGTH_LONG).show()
    }
}
