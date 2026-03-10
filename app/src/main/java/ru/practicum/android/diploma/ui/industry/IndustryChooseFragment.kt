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
        setupRecyclerView()
        setupSearch()
        setupObservers()
        setupButtons()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = IndustryAdapter { industry ->
            viewModel.onIndustrySelected(industry)
        }
        binding.rvIndustries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIndustries.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchQueryChanged(binding.etSearch.text.toString())
                true
            } else false
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchQueryChanged(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etSearch.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etSearch.compoundDrawables[2]
                if (drawableEnd != null &&
                    event.rawX >= binding.etSearch.right - drawableEnd.bounds.width()) {
                    binding.etSearch.text?.clear()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun setupButtons() {
        binding.btnChoose.setOnClickListener {
            viewModel.selectedIndustry.value?.let { selectedIndustry ->
                sharedViewModel.industryOnAction(IndustryAction.IndustryChoose(selectedIndustry))
            }
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                render(state)
            }
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

    private fun render(state: IndustryChooseState) {
        when (state) {
            is IndustryChooseState.Loading -> showLoading()
            is IndustryChooseState.Content -> showContent(state)
            is IndustryChooseState.Error -> showError()
            is IndustryChooseState.NoConnection -> showNoConnection()
            is IndustryChooseState.Initial -> showLoading()
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.isVisible = true
            rvIndustries.isVisible = false
            etSearch.isVisible = false
            inclNoInternet.root.isVisible = false
            inclServerError.root.isVisible = false
            inclEmpty.root.isVisible = false
            btnChoose.isVisible = false
        }
    }

    private fun showContent(state: IndustryChooseState.Content) {
        binding.apply {
            progressBar.isVisible = false
            rvIndustries.isVisible = true
            etSearch.isVisible = true
            inclNoInternet.root.isVisible = false
            inclServerError.root.isVisible = false
            inclEmpty.root.isVisible = state.industries.isEmpty()
        }
    }

    private fun showError() {
        binding.apply {
            progressBar.isVisible = false
            rvIndustries.isVisible = false
            etSearch.isVisible = false
            inclNoInternet.root.isVisible = false
            inclServerError.root.isVisible = true
            inclEmpty.root.isVisible = false
            btnChoose.isVisible = false
        }
    }

    private fun showNoConnection() {
        binding.apply {
            progressBar.isVisible = false
            rvIndustries.isVisible = false
            etSearch.isVisible = false
            inclNoInternet.root.isVisible = true
            inclServerError.root.isVisible = false
            inclEmpty.root.isVisible = false
            btnChoose.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
