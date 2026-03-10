package ru.practicum.android.diploma.ui.filters

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FiltersFragmentBinding
import ru.practicum.android.diploma.domain.models.Area

class FiltersFragment : Fragment(R.layout.filters_fragment) {

    private var _binding: FiltersFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FiltersFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupNavigation()
        setupObservers()
    }

    private fun setupViews() {
        setupSearchInput()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupNavigation() {
        binding.itemWorkplace.setOnClickListener {
            sharedViewModel.filtersOnAction(FiltersAction.FiltersWorkChange)
            findNavController().navigate(R.id.action_filtersFragment_to_workChooseFragment)
        }

        binding.itemIndustry.setOnClickListener {
            sharedViewModel.filtersOnAction(FiltersAction.FiltersIndustryChange)
            findNavController().navigate(R.id.action_filtersFragment_to_industryChooseFragment)
        }
    }

    private fun setupSearchInput() {
        binding.etExpectedSalary.setOnFocusChangeListener { _, hasFocus ->
            updateSalaryHintColor(hasFocus, binding.etExpectedSalary.text.toString())
        }

        binding.etExpectedSalary.addTextChangedListener { s: Editable? ->
            val text = s?.toString() ?: ""
            updateSalaryHintColor(binding.etExpectedSalary.hasFocus(), text)
            binding.ivClearSalary.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
            sharedViewModel.filtersOnAction(FiltersAction.FiltersSalaryChange(text.toIntOrNull() ?: 0))
        }

        binding.ivClearSalary.setOnClickListener {
            binding.etExpectedSalary.text?.clear()
            binding.etExpectedSalary.clearFocus()
            updateSalaryHintColor(false, "")
            sharedViewModel.filtersOnAction(FiltersAction.FiltersSalaryClear)
        }

        binding.cbHideWithoutSalary.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.filtersOnAction(FiltersAction.FiltersOnlyWithSalaryChange(isChecked))
        }

        binding.btnApply.setOnClickListener {
            sharedViewModel.filtersOnAction(FiltersAction.FiltersApply)
            findNavController().navigateUp()
        }

        binding.btnReset.setOnClickListener {
            sharedViewModel.filtersOnAction(FiltersAction.FiltersReset)
            resetUI()
        }
    }

    private fun setupObservers() {
        sharedViewModel.filtersStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FiltersState.Content -> updateUI(state)
            }
        }
    }

    private fun updateUI(state: FiltersState.Content) {
        updateWorkplaceDisplay(state.country, state.region)

        if (state.salary > 0) {
            binding.etExpectedSalary.setText(state.salary.toString())
            binding.ivClearSalary.visibility = View.VISIBLE
        } else {
            binding.etExpectedSalary.setText("")
            binding.ivClearSalary.visibility = View.GONE
        }

        binding.cbHideWithoutSalary.isChecked = state.onlyWithSalary

        binding.bottomButtons.visibility = if (hasChanges(state)) View.VISIBLE else View.GONE
    }

    private fun updateWorkplaceDisplay(country: Area?, region: Area?) {
        val hasSelection = country != null

        binding.tvWorkplaceTitle.apply {
            textSize = if (hasSelection) TITLE_SELECTED_TEXT_SIZE else TITLE_DEFAULT_TEXT_SIZE

            setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.color_on_primary_selector))
        }

        binding.tvWorkplaceValue.apply {
            if (hasSelection) {
                val workplaceText = when {
                    country != null && region != null -> "${country.name}, ${region.name}"
                    country != null -> country.name
                    else -> ""
                }
                text = workplaceText
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
                text = getString(R.string.not_selected)
            }
        }
        val containerLayout = binding.itemWorkplace.getChildAt(0) as LinearLayout
        val params = binding.tvWorkplaceTitle.layoutParams as LinearLayout.LayoutParams

        if (hasSelection) {
            params.topMargin = MARGIN_DEFAULT
            params.gravity = android.view.Gravity.TOP
            containerLayout.gravity = android.view.Gravity.TOP
        } else {
            params.topMargin = MARGIN_UPDATED
            params.gravity = android.view.Gravity.CENTER_VERTICAL
            containerLayout.gravity = android.view.Gravity.CENTER_VERTICAL
        }
        binding.tvWorkplaceTitle.layoutParams = params
    }

    private fun resetUI() {
        binding.etExpectedSalary.text?.clear()
        binding.cbHideWithoutSalary.isChecked = false
        binding.ivClearSalary.visibility = View.GONE
        updateWorkplaceDisplay(null, null)
    }

    private fun hasChanges(state: FiltersState.Content): Boolean {
        return state.country != null ||
            state.region != null ||
            state.industry != null ||
            state.salary > 0 ||
            state.onlyWithSalary
    }

    private fun updateSalaryHintColor(hasFocus: Boolean, text: String) {
        val colorRes = when {
            hasFocus -> R.color.blue
            text.isNotEmpty() -> R.color.black
            else -> R.color.white
        }
        binding.tvSalaryHint.setTextColor(
            ContextCompat.getColor(requireContext(), colorRes)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        private const val TITLE_SELECTED_TEXT_SIZE = 12f
        private const val TITLE_DEFAULT_TEXT_SIZE = 16f

        private const val MARGIN_DEFAULT = 0
        private const val MARGIN_UPDATED = 12
    }
}
