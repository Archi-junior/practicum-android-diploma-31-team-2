package ru.practicum.android.diploma.ui.region

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.RegionChooseFragmentBinding
import ru.practicum.android.diploma.presentation.mapper.AreaUi
import ru.practicum.android.diploma.presentation.mapper.toDomainArea
import ru.practicum.android.diploma.ui.work.WorkAction
import ru.practicum.android.diploma.ui.filters.SharedViewModel

class RegionChooseFragment : Fragment(R.layout.region_choose_fragment) {

    private var _binding: RegionChooseFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModel()
    private val viewModel: RegionChooseViewModel by viewModel()

    private val countryId: Int by lazy {
        arguments?.getInt("countryId") ?: throw IllegalArgumentException("countryId required")
    }

    private lateinit var adapter: RegionChooseAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = RegionChooseFragmentBinding.bind(view)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupObservers()

        viewModel.loadRegions(countryId)
    }

    private fun setupToolbar() {
        binding.choosingRegionToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = RegionChooseAdapter(emptyList()) { region ->
            sharedViewModel.workOnAction(WorkAction.WorkRegionSelect(region.toDomainArea()))
            findNavController().popBackStack()
        }
        binding.listRegionRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@RegionChooseFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.searchRegionEditText.addTextChangedListener { text ->
            viewModel.updateSearchQuery(text.toString())
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.regions,
                viewModel.searchQuery.debounce(DEBOUNCE_TIME).distinctUntilChanged()
            ) { regions, query ->
                if (query.isBlank()) {
                    regions
                } else {
                    regions.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filteredRegions ->
                adapter.updateRegions(filteredRegions)
                updatePlaceholders(filteredRegions)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                if (error != null) {
                    binding.noListPlaceholder.isVisible = true
                    binding.noRegionPlaceholder.isVisible = false
                    binding.listRegionRecyclerView.isVisible = false
                } else {
                    binding.noListPlaceholder.isVisible = false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.listRegionRecyclerView.isVisible = !isLoading && viewModel.regions.value.isNotEmpty()
            }
        }
    }

    private fun updatePlaceholders(regions: List<AreaUi>) {
        when {
            viewModel.error.value != null -> {
                binding.noListPlaceholder.isVisible = true
                binding.noRegionPlaceholder.isVisible = false
                binding.listRegionRecyclerView.isVisible = false
            }
            regions.isEmpty() && viewModel.searchQuery.value.isBlank() -> {
                binding.noRegionPlaceholder.isVisible = true
                binding.noListPlaceholder.isVisible = false
                binding.listRegionRecyclerView.isVisible = false
            }
            else -> {
                binding.noRegionPlaceholder.isVisible = false
                binding.noListPlaceholder.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val DEBOUNCE_TIME = 300L
    }
}
