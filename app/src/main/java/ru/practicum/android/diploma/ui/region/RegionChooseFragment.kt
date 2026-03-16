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
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.RegionChooseFragmentBinding
import ru.practicum.android.diploma.presentation.mapper.AreaUi
import ru.practicum.android.diploma.presentation.mapper.toDomainArea
import ru.practicum.android.diploma.ui.filters.SharedViewModel

class RegionChooseFragment : Fragment(R.layout.region_choose_fragment) {

    private var _binding: RegionChooseFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModel()

    private val countryId: Int by lazy {
        arguments?.getInt("countryId") ?: 0
    }

    private lateinit var adapter: RegionChooseAdapter
    private var allRegions: List<AreaUi> = emptyList()
    private var isFirstLoad = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = RegionChooseFragmentBinding.bind(view)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupObservers()

        showLoading()

        if (countryId > 0) {
            sharedViewModel.loadRegions(countryId)
        } else {
            sharedViewModel.loadAllRegions()
        }
    }

    private fun setupToolbar() {
        binding.choosingRegionToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = RegionChooseAdapter(emptyList()) { regionUi ->
            val region = regionUi.toDomainArea()
            sharedViewModel.regionOnAction(RegionAction.RegionSelectItem(region))

            if (countryId == 0) {
                sharedViewModel.findAndSetCountryByRegion(region)
            }

            findNavController().popBackStack()
        }
        binding.listRegionRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@RegionChooseFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.searchRegionEditText.addTextChangedListener { text ->
            sharedViewModel.updateSearchQuery(text.toString())
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                sharedViewModel.regions,
                sharedViewModel.searchQuery.debounce(DEBOUNCE_TIME).distinctUntilChanged()
            ) { regions, query ->
                allRegions = regions
                if (query.isBlank()) {
                    regions
                } else {
                    regions.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filteredRegions ->
                adapter.updateRegions(filteredRegions)
                if (!sharedViewModel.isLoading.value) {
                    updateUI(filteredRegions)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    showLoading()
                } else {
                    isFirstLoad = false
                    updateUI(adapter.getCurrentList())
                }
            }
        }
    }

    private fun updateUI(filteredRegions: List<AreaUi>) {
        val hasError = sharedViewModel.error.value != null
        val hasSearchQuery = sharedViewModel.searchQuery.value.isNotBlank()
        val hasRegions = filteredRegions.isNotEmpty()

        binding.apply {
            progressBar.isVisible = false
            listRegionRecyclerView.isVisible = false
            noRegionPlaceholder.isVisible = false
            noListPlaceholder.isVisible = false
            searchRegionEditText.isVisible = true

            when {
                hasError -> {
                    noListPlaceholder.isVisible = true
                }
                !hasRegions && hasSearchQuery -> {
                    noRegionPlaceholder.isVisible = true
                }
                !hasRegions && !hasSearchQuery && !isFirstLoad -> {
                    noRegionPlaceholder.isVisible = true
                }
                else -> {
                    listRegionRecyclerView.isVisible = true
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.isVisible = true
            listRegionRecyclerView.isVisible = false
            noRegionPlaceholder.isVisible = false
            noListPlaceholder.isVisible = false
            searchRegionEditText.isVisible = false
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
