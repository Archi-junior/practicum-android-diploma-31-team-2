package ru.practicum.android.diploma.ui.vacancy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.practicum.android.diploma.databinding.VacancyFragmentBinding
import ru.practicum.android.diploma.domain.models.Vacancy

class VacancyFragment : Fragment(){

    private var _binding: VacancyFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = VacancyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        const val ARGS_VACANCY = "vacancy"
        fun createArgs(vacancy: Vacancy) = Bundle().apply { putParcelable(ARGS_VACANCY, vacancy) }
    }
}
