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
import ru.practicum.android.diploma.domain.models.Area
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
            sharedViewModel.workOnAction(WorkAction.WorkRegionClick)

            val bundle = Bundle().apply {
                val countryId = (
                    sharedViewModel.workChooseStateLiveData.value as? WorkChooseState.Content
                    )?.country?.id ?: 0
                putInt("countryId", countryId)
            }
            findNavController().navigate(
                R.id.action_workChooseFragment_to_regionChooseFragment,
                bundle
            )
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
        binding.apply {
            progressBar.isVisible = false
            inclNoInternet.root.isVisible = false
            inclServerError.root.isVisible = false

            when (state) {
                is WorkChooseState.Content -> {
                    llContent.isVisible = true
                    renderCountry(state.country)
                    renderRegion(state.region)
                    btnChoose.isVisible = state.country != null || state.region != null
                }
                is WorkChooseState.Loading -> {
                    llContent.isVisible = false
                    progressBar.isVisible = true
                    btnChoose.isVisible = false
                }
                is WorkChooseState.Error -> {
                    llContent.isVisible = false
                    inclServerError.root.isVisible = true
                    btnChoose.isVisible = false
                }
                is WorkChooseState.NoConnection -> {
                    llContent.isVisible = false
                    inclNoInternet.root.isVisible = true
                    btnChoose.isVisible = false
                }
                is WorkChooseState.Empty, is WorkChooseState.Initial -> {
                    llContent.isVisible = true
                    tvCountryValue.isVisible = false
                    ivCountryClear.isVisible = false
                    tvRegionValue.isVisible = false
                    ivRegionClear.isVisible = false
                    btnChoose.isVisible = false
                }
            }
        }
    }

    private fun renderCountry(country: Area?) {
        binding.apply {
            if (country != null) {
                tvCountryValue.text = country.name
                tvCountryValue.isVisible = true
                ivCountryClear.isVisible = true
                tvCountryTitle.textSize = TITLE_SELECTED_TEXT_SIZE
                binding.ivCountryArrow.isVisible = false
            } else {
               resetCountryView()
            }
        }
    }

    private fun renderRegion(region: Area?) {
        binding.apply {
            if (region != null) {
                tvRegionValue.text = region.name
                tvRegionValue.isVisible = true
                ivRegionClear.isVisible = true
                tvRegionTitle.textSize = TITLE_SELECTED_TEXT_SIZE
                binding.ivRegionArrow.isVisible = false
            } else {
                resetRegionView()
            }
            clRegion.isEnabled = true
            clRegion.alpha = VISUAL_ALPHA_VALUE
        }
    }

    private fun resetCountryView() {
        binding.apply {
            ivCountryArrow.isVisible = true
            tvCountryValue.isVisible = false
            ivCountryClear.isVisible = false
            tvCountryTitle.textSize = TITLE_DEFAULT_TEXT_SIZE
        }
    }

    private fun resetRegionView() {
        binding.apply {
            ivRegionArrow.isVisible = true
            tvRegionValue.isVisible = false
            ivRegionClear.isVisible = false
            tvRegionTitle.textSize = TITLE_DEFAULT_TEXT_SIZE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val VISUAL_ALPHA_VALUE = 1.0f
        private const val TITLE_SELECTED_TEXT_SIZE = 12f
        private const val TITLE_DEFAULT_TEXT_SIZE = 16f
    }
}
