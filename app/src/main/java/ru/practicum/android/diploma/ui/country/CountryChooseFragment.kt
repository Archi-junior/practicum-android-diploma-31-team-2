package ru.practicum.android.diploma.ui.country

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import ru.practicum.android.diploma.databinding.CountryChooseFragmentBinding
import ru.practicum.android.diploma.ui.filters.SharedViewModel

class CountryChooseFragment : Fragment() {

    private val viewModel: SharedViewModel by activityViewModel()
    private var _binding: CountryChooseFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CountryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CountryChooseFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupToolbar()
        loadCountries()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAdapter() {
        adapter = CountryAdapter { country ->
            viewModel.countryOnAction(CountryAction.CountrySelectItem(country))
            findNavController().navigateUp()
        }
        binding.rvCountries.adapter = adapter
    }

    private fun loadCountries() {
        viewModel.loadCountries()
    }

    private fun observeViewModel() {
        viewModel.countryChooseStateLiveData.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: CountryChooseState) {
        when (state) {
            is CountryChooseState.Initial -> {
                showLoading()
            }
            is CountryChooseState.NoConnection -> {
                binding.apply {
                    rvCountries.isVisible = false
                    progressBar.isVisible = false
                    inclNoInternet.root.isVisible = true
                    inclServerError.root.isVisible = false
                }
            }
            is CountryChooseState.Error -> {
                binding.apply {
                    rvCountries.isVisible = false
                    progressBar.isVisible = false
                    inclNoInternet.root.isVisible = false
                    inclServerError.root.isVisible = true
                }
            }
            is CountryChooseState.Loading -> {
                showLoading()
            }
            is CountryChooseState.Content -> {
                adapter.submitList(state.areas)
                binding.apply {
                    rvCountries.isVisible = true
                    progressBar.isVisible = false
                    inclNoInternet.root.isVisible = false
                    inclServerError.root.isVisible = false
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            rvCountries.isVisible = false
            progressBar.isVisible = true
            inclNoInternet.root.isVisible = false
            inclServerError.root.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
