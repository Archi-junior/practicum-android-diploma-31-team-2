package ru.practicum.android.diploma.ui.work

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.WorkChooseFragmentBinding
import ru.practicum.android.diploma.ui.filters.SharedViewModel

class WorkChooseFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModel()
    private var _binding: WorkChooseFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WorkChooseFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.clCountry.setOnClickListener {
            sharedViewModel.workOnAction(WorkAction.WorkCountryClick)
            findNavController().navigate(R.id.action_workChooseFragment_to_countryChooseFragment)
        }

        binding.clRegion.setOnClickListener {
            val currentState = sharedViewModel.workChooseStateLiveData.value
            if (currentState is WorkChooseState.Content && currentState.country != null) {
                sharedViewModel.workOnAction(WorkAction.WorkRegionClick)

            }
        }

        binding.ivCountryClear.setOnClickListener {
            sharedViewModel.workOnAction(WorkAction.WorkCountryClear)
        }

        binding.ivRegionClear.setOnClickListener {
            sharedViewModel.workOnAction(WorkAction.WorkRegionClear)
        }

        binding.btnChoose.setOnClickListener {
            val currentState = sharedViewModel.workChooseStateLiveData.value as? WorkChooseState.Content
            sharedViewModel.workOnAction(
                WorkAction.WorkChoose(
                    country = currentState?.country,
                    region = currentState?.region
                )
            )
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        sharedViewModel.workChooseStateLiveData.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: WorkChooseState) {
        when (state) {
            is WorkChooseState.Content -> showContent(state)
            is WorkChooseState.Loading -> showLoading()
            is WorkChooseState.Error -> showError()
            is WorkChooseState.NoConnection -> showNoConnection()
            is WorkChooseState.Empty, is WorkChooseState.Initial -> showInitial()
        }
    }

    private fun showInitial() {
        binding.apply {
            progressBar.isVisible = false
            inclNoInternet.root.isVisible = false
            inclServerError.root.isVisible = false
            llContent.isVisible = true
            btnChoose.isVisible = false

            tvCountryValue.isVisible = false
            ivCountryClear.isVisible = false
            tvRegionValue.isVisible = false
            ivRegionClear.isVisible = false
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.isVisible = true
            inclNoInternet.root.isVisible = false
            inclServerError.root.isVisible = false
            llContent.isVisible = false
            btnChoose.isVisible = false
        }
    }

    private fun showContent(state: WorkChooseState.Content) {
        binding.apply {
            progressBar.isVisible = false
            inclNoInternet.root.isVisible = false
            inclServerError.root.isVisible = false
            llContent.isVisible = true

            if (state.country != null) {
                tvCountryValue.text = state.country.name
                tvCountryValue.isVisible = true
                ivCountryClear.isVisible = true
            } else {
                tvCountryValue.isVisible = false
                ivCountryClear.isVisible = false
            }

            if (state.region != null) {
                tvRegionValue.text = state.region.name
                tvRegionValue.isVisible = true
                ivRegionClear.isVisible = true
            } else {
                tvRegionValue.isVisible = false
                ivRegionClear.isVisible = false
            }

            clRegion.isEnabled = state.country != null
            clRegion.alpha = if (state.country != null) 1.0f else 0.5f

            btnChoose.isVisible = state.country != null || state.region != null
        }
    }

    private fun showError() {
        binding.apply {
            progressBar.isVisible = false
            inclNoInternet.root.isVisible = false
            inclServerError.root.isVisible = true
            llContent.isVisible = false
            btnChoose.isVisible = false
        }
    }

    private fun showNoConnection() {
        binding.apply {
            progressBar.isVisible = false
            inclNoInternet.root.isVisible = true
            inclServerError.root.isVisible = false
            llContent.isVisible = false
            btnChoose.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
