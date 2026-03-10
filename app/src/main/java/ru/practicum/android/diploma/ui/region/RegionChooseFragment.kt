package ru.practicum.android.diploma.ui.region

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.RegionChooseFragmentBinding
import ru.practicum.android.diploma.presentation.mapper.AreaUi

class RegionChooseFragment : Fragment(R.layout.region_choose_fragment){
    private var _binding: RegionChooseFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RegionChooseViewModel
    private lateinit var adapter: RegionChooseAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = RegionChooseFragmentBinding.bind(view)

        viewModel = ViewModelProvider(this)[RegionChooseViewModel::class.java]
        adapter = RegionChooseAdapter(emptyList()) { region ->
            selectRegion(region)
        }

        setupRecyclerView()
        setupSearch()
        setupObservers()
    }

    private fun setupRecyclerView() {
        binding.listRegionRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@RegionChooseFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.searchRegionEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                viewModel.updateSearchQuery(binding.searchRegionEditText.text.toString())
                true
            } else false
        }

        binding.searchRegionEditText.addTextChangedListener { text ->
            viewModel.updateSearchQuery(text.toString())
        }
    }

    private suspend fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.regions.collectLatest { regions ->
                adapter.updateRegions(regions)
                updatePlaceholders(regions)
            }
        }

        viewModel.error.collectLatest { error ->
            if (error != null) {
                binding.noListPlaceholder.visibility = View.VISIBLE
                binding.noRegionPlaceholder.visibility = View.GONE
                binding.listRegionRecyclerView.visibility = View.GONE
            } else {
                binding.noListPlaceholder.visibility = View.GONE
            }
        }

        viewModel.isLoading.collectLatest { isLoading ->
            binding.listRegionRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.selectedRegion.collectLatest { region ->
            region?.let {
                requireActivity().apply {
                    val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putInt("selected_region_id", region.id)
                        .putString("selected_region_name", region.name)
                        .apply()
                }
                findNavController().popBackStack()
            }
        }
    }

    private fun updatePlaceholders(regions: List<AreaUi>) {
        if (regions.isEmpty() && viewModel.searchQuery.value.isBlank()) {
            binding.noRegionPlaceholder.visibility = View.VISIBLE
            binding.noListPlaceholder.visibility = View.GONE
            binding.listRegionRecyclerView.visibility = View.GONE
        } else {
            binding.noRegionPlaceholder.visibility = View.GONE
        }
    }

    private fun selectRegion(region: AreaUi) {
        viewModel.selectRegion(region)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
