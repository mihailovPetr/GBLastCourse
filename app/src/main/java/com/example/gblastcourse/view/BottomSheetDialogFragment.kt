package com.example.gblastcourse.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.gblastcourse.R
import com.example.gblastcourse.databinding.FragmentBottomSheetDialogBinding
import com.example.gblastcourse.utils.showSnackBar
import com.example.gblastcourse.model.AppStateSingle
import com.example.gblastcourse.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.viewModel

private const val ARG_LATITUDE = "latitude"
private const val ARG_LONGITUDE = "longitude"


class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModel()

    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getDouble(ARG_LATITUDE)
            longitude = it.getDouble(ARG_LONGITUDE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        binding.ok.setOnClickListener {
            setMarkerToViewModel()
            mainViewModel.setMarkerById()
            dismiss()
        }
        binding.cancel.setOnClickListener { dismiss() }
    }

    private fun setMarkerToViewModel() {
        mainViewModel.setLocalMarkerFields(
            name = binding.markerNameET.text.toString(),
            desctiption = binding.markerDescriptionET.text.toString()
        )
    }

    private fun initViewModel() {
        mainViewModel.getLiveDataSingle().observe(viewLifecycleOwner, Observer { renderData(it) })
        mainViewModel.getMarkerByLatitudeAndLongitude(latitude ?: 0.0, longitude ?: 0.0)
    }

    private fun renderData(appState: AppStateSingle) {
        when (appState) {
            is AppStateSingle.Success -> {
                binding.bottomSheetConstraintLayout.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE

                mainViewModel.getLocalMarker()?.let { marker ->
                    binding.markerNameET.setText(marker.name)
                    binding.markerDescriptionET.setText(marker.description)
                    binding.markerLatitude.text = marker.latitude.toString()
                    binding.markerLongitude.text = marker.longitude.toString()
                }

                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE

            }
            is AppStateSingle.Loading -> {
                binding.bottomSheetConstraintLayout.visibility = View.GONE
                binding.includedLoadingLayout.loadingLayout.visibility = View.VISIBLE

            }
            is AppStateSingle.Error -> {
                binding.bottomSheetConstraintLayout.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                binding.bottomSheetConstraintLayout.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    {
                        mainViewModel.getAllMarkers()
                    })
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setMarkerToViewModel()
        _binding = null
    }

    fun setData(latitude: Double, longitude: Double) {
        arguments = Bundle().apply {
            putDouble(ARG_LATITUDE, latitude)
            putDouble(ARG_LONGITUDE, longitude)
        }
    }

    companion object {
        const val TAG = "BottomSheetDialogFragment"
    }
}