package com.example.gblastcourse.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.gblastcourse.R
import com.example.gblastcourse.databinding.FragmentMarkersBinding
import com.example.gblastcourse.utils.showSnackBar
import com.example.gblastcourse.model.AppState
import com.example.gblastcourse.viewmodel.MainViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MarkersFragment : Fragment() {
    private var _binding: FragmentMarkersBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModel()
    private val adapter: MarkersAdapter by lazy { MarkersAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMarkersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.markersFragmentRecyclerview.adapter = adapter
        mainViewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })
        mainViewModel.getAllMarkers()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.markersFragmentRecyclerview.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                adapter.setData(appState.markers)
            }
            is AppState.Loading -> {
                binding.markersFragmentRecyclerview.visibility = View.GONE
                binding.includedLoadingLayout.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.markersFragmentRecyclerview.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                binding.markersFragmentRecyclerview.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    {
                        mainViewModel.getAllMarkers()
                    })
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            MarkersFragment()
    }
}