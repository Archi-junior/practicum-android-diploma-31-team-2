package ru.practicum.android.diploma.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.SearchFragmentBinding
import ru.practicum.android.diploma.presentation.SearchViewModel
import ru.practicum.android.diploma.util.SearchState
import ru.practicum.android.diploma.util.UiState

class SearchFragment : Fragment(R.layout.search_fragment) {

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()
                val endDrawable = if (hasText) R.drawable.ic_clear else R.drawable.ic_search
                binding.searchEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, endDrawable, 0
                )
            }
        })

        // 2️⃣ Наблюдение за состоянием ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    renderState(state)
                }
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun renderState(state: UiState<List<Vacancy>>) {
        when (state) {

            is UiState.Success -> {
                binding.progressBar.isVisible = false
                binding.vacancyRecyclerView.isVisible = true
                binding.emptyRoot.root.isVisible = false
                binding.serverErrorRoot.root.isVisible = false
                binding.quantityVacancy.isVisible = true
                binding.quantityVacancy.text = requireContext().getString(
                    R.string.vacancies_found, state.data.size
                )
            }

            is UiState.Error -> {
                binding.progressBar.isVisible = false
                binding.vacancyRecyclerView.isVisible = false
                binding.emptyRoot.root.isVisible = false
                binding.serverErrorRoot.root.isVisible = true
            }

            is UiState.NoConnection -> {
                binding.progressBar.isVisible = false
                binding.vacancyRecyclerView.isVisible = false
                binding.emptyRoot.root.isVisible = true
                binding.noInternetRoot.root.isVisible = false
                binding.quantityVacancy.isVisible = true
                binding.quantityVacancy.text = requireContext().getString(R.string.no_vacancies, state.data.size)
            }

            is UiState.Empty -> {
                binding.progressBar.isVisible = false
                binding.vacancyRecyclerView.isVisible = false
                binding.noInternetRoot.root.isVisible = true
                binding.serverErrorRoot.root.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
