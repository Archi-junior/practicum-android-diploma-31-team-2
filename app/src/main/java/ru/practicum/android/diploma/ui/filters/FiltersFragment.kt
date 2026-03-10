package ru.practicum.android.diploma.ui.filters
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FiltersFragmentBinding

class FiltersFragment : Fragment(R.layout.filters_fragment){
    private var _binding: FiltersFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FiltersFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchInput()
    }
    private fun setupSearchInput() {
        binding.etExpectedSalary.setOnFocusChangeListener { _, hasFocus ->
            updateSalaryHintColor(hasFocus, binding.etExpectedSalary.text.toString())
        }
        binding.etExpectedSalary.addTextChangedListener { s: Editable? ->
            val text = s?.toString() ?: ""
            updateSalaryHintColor(binding.etExpectedSalary.hasFocus(), text)
            binding.ivClearSalary.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
        binding.ivClearSalary.setOnClickListener {
            binding.etExpectedSalary.text?.clear()
            binding.etExpectedSalary.clearFocus()
            updateSalaryHintColor(false, "")
        }
    }
    private fun updateSalaryHintColor(hasFocus: Boolean, text: String) {
        val colorRes = when {
            hasFocus -> R.color.blue
            text.isNotEmpty() -> R.color.black
            else -> R.color.white
        }
        binding.tvSalaryHint.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), colorRes))
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
