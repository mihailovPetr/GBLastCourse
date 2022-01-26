package com.example.gblastcourse.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.gblastcourse.R
import com.example.gblastcourse.databinding.FragmentMapsBinding
import com.example.gblastcourse.model.AppState
import com.example.gblastcourse.viewmodel.MainViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import org.koin.android.viewmodel.ext.android.viewModel


class MapsFragmentNew : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private var map: GoogleMap? = null

    private val mainViewModel: MainViewModel by viewModel()

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        googleMap.setOnMapClickListener { addMarker(it) }
        googleMap.setOnMarkerClickListener {
            onMarkerClick(it) }
        initViewModel()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun initViewModel() {
        mainViewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
    }

    private fun renderData(appState: AppState) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MapsFragment()
    }
}