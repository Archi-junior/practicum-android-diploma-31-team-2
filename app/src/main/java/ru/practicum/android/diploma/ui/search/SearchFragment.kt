package ru.practicum.android.diploma.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.SearchFragmentBinding

class SearchFragment : Fragment(R.layout.search_fragment) {

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchInput()
        setupObservers()
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

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Start -> {
                binding.progressBar.visibility = View.GONE
                binding.rvVacancies.visibility = View.GONE
                binding.tvTotalFound.visibility = View.GONE
                binding.placeholderStart.visibility = View.VISIBLE
                binding.placeholderError.visibility = View.GONE
                binding.placeholderNoConnection.visibility = View.GONE
                binding.placeholderEmpty.visibility = View.GONE
            }

            is SearchState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvVacancies.visibility = View.GONE
                binding.tvTotalFound.visibility = View.GONE
                binding.placeholderStart.visibility = View.GONE
                binding.placeholderError.visibility = View.GONE
                binding.placeholderNoConnection.visibility = View.GONE
                binding.placeholderEmpty.visibility = View.GONE
            }

            is SearchState.Content -> {
                binding.progressBar.visibility = View.GONE
                binding.rvVacancies.visibility = View.VISIBLE
                binding.tvTotalFound.visibility = View.VISIBLE
                binding.tvTotalFound.text = getString(R.string.total_found, state.totalFound)

                binding.placeholderStart.visibility = View.GONE
                binding.placeholderError.visibility = View.GONE
                binding.placeholderNoConnection.visibility = View.GONE
                binding.placeholderEmpty.visibility = View.GONE
            }

            is SearchState.Empty -> {
                binding.progressBar.visibility = View.GONE
                binding.rvVacancies.visibility = View.GONE
                binding.tvTotalFound.visibility = View.GONE
                binding.placeholderStart.visibility = View.GONE
                binding.placeholderError.visibility = View.GONE
                binding.placeholderNoConnection.visibility = View.GONE
                binding.placeholderEmpty.visibility = View.VISIBLE
            }

            is SearchState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.rvVacancies.visibility = View.GONE
                binding.tvTotalFound.visibility = View.GONE
                binding.placeholderStart.visibility = View.GONE
                binding.placeholderError.visibility = View.VISIBLE
                binding.placeholderNoConnection.visibility = View.GONE
                binding.placeholderEmpty.visibility = View.GONE
            }

            is SearchState.NoConnection -> {
                binding.progressBar.visibility = View.GONE
                binding.rvVacancies.visibility = View.GONE
                binding.tvTotalFound.visibility = View.GONE
                binding.placeholderStart.visibility = View.GONE
                binding.placeholderError.visibility = View.GONE
                binding.placeholderNoConnection.visibility = View.VISIBLE
                binding.placeholderEmpty.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
