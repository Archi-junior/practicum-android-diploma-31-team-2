package ru.practicum.android.diploma.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.SearchFragmentBinding
import ru.practicum.android.diploma.ui.vacancy.VacancyFragment

class SearchFragment : Fragment() {

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var adapter: VacancyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupSearchInput()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupAdapter() {
        adapter = VacancyAdapter { vacancyId ->
            findNavController().navigate(
                R.id.action_searchFragment_to_vacancyFragment,
                VacancyFragment.createArgs(vacancyId)
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchInput() {
        binding.searchEditText.addTextChangedListener { s: Editable? ->
            val hasText = !s.isNullOrBlank()
            val endDrawable = if (hasText) R.drawable.ic_clear else R.drawable.ic_search
            binding.searchEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, endDrawable, 0
            )
            viewModel.onSearchQueryChanged(s?.toString() ?: "")
        }

        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val d = binding.searchEditText.compoundDrawables[2] ?: return@setOnTouchListener false
                val endIconLeft = v.width - v.paddingEnd - d.intrinsicWidth
                if (event.x >= endIconLeft) {
                    viewModel.clearSearch()
                    binding.searchEditText.text?.clear()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun setupRecyclerView() {
        binding.vacancyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.vacancyRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SearchState) {
        binding.progressBar.isVisible = false
        binding.vacancyRecyclerView.isVisible = false
        binding.quantityVacancy.isVisible = false
        binding.imageSearch.isVisible = false
        binding.noInternetRoot.root.isVisible = false
        binding.serverErrorRoot.root.isVisible = false
        binding.emptyRoot.root.isVisible = false

        when (state) {
            is SearchState.Start -> {
                binding.imageSearch.isVisible = true
            }

            is SearchState.Loading -> {
                binding.progressBar.isVisible = true
            }

            is SearchState.Content -> {
                binding.vacancyRecyclerView.isVisible = true
                binding.quantityVacancy.isVisible = true
                binding.quantityVacancy.text = getString(R.string.vacancies_found, state.totalFound)
                adapter.submitList(state.vacancies)
            }

            is SearchState.Empty -> {
                binding.emptyRoot.root.isVisible = true
            }

            is SearchState.NoConnection -> {
                binding.noInternetRoot.root.isVisible = true
            }

            is SearchState.Error -> {
                binding.serverErrorRoot.root.isVisible = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
