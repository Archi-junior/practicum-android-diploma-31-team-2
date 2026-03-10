package ru.practicum.android.diploma.ui.industry

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.IndustryChooseFragmentBinding
import ru.practicum.android.diploma.ui.filters.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class IndustryChooseFragment : Fragment(R.layout.industry_choose_fragment) {

    private var _binding: IndustryChooseFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IndustryChooseViewModel by viewModel()
    private val sharedViewModel: SharedViewModel by activityViewModel()

    private lateinit var adapter: IndustryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = IndustryChooseFragmentBinding.bind(view)

        setupToolbar()
        initRecyclerView()
        initSearch()
        initObservers()
        initButtons()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initRecyclerView() {
        adapter = IndustryAdapter { industry ->
            viewModel.onIndustrySelected(industry)
        }
        binding.rvIndustries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@IndustryChooseFragment.adapter
        }
    }

    private fun initSearch() {
        binding.etSearch.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.onSearchQueryChanged(text.toString())
                    true
                } else false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.onSearchQueryChanged(s?.toString() ?: "")
                }
                override fun afterTextChanged(s: Editable?) = Unit
            })

            setOnTouchListener { _, event ->
                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    val drawableEnd = compoundDrawables[2]
                    if (drawableEnd != null &&
                        event.rawX >= right - drawableEnd.bounds.width()) {
                        text?.clear()
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }
    }

    private fun initButtons() {
        binding.btnChoose.setOnClickListener {
            viewModel.selectedIndustry.value?.let { selectedIndustry ->
                sharedViewModel.industryOnAction(IndustryAction.IndustryChoose(selectedIndustry))
            }
            findNavController().navigateUp()
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state -> updateUI(state) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredIndustries.collect { industries ->
                adapter.submitList(industries)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedIndustry.collect { selected ->
                adapter.setSelectedIndustryId(selected?.id)
                binding.btnChoose.isVisible = selected != null
            }
        }
    }

    private fun updateUI(state: IndustryChooseState) {
        binding.apply {
            progressBar.isVisible = false
            rvIndustries.isVisible = false
            etSearch.isVisible = false
            inclNoInternet.root.isVisible = false
            inclServerError.root.isVisible = false
            inclEmpty.root.isVisible = false
            btnChoose.isVisible = false

            when (state) {
                is IndustryChooseState.Loading, is IndustryChooseState.Initial -> {
                    progressBar.isVisible = true
                }
                is IndustryChooseState.Content -> {
                    rvIndustries.isVisible = true
                    etSearch.isVisible = true
                    inclEmpty.root.isVisible = state.industries.isEmpty()
                }
                is IndustryChooseState.Error -> {
                    inclServerError.root.isVisible = true
                }
                is IndustryChooseState.NoConnection -> {
                    inclNoInternet.root.isVisible = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
