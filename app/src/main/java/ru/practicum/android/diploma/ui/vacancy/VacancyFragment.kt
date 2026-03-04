package ru.practicum.android.diploma.ui.vacancy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.VacancyFragmentBinding
import ru.practicum.android.diploma.domain.models.Vacancy

class VacancyFragment : Fragment(){

    private val viewModel by viewModel<VacancyViewModel> {
        parametersOf(requireArguments().getString(ARGS_VACANCY))
    }
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

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.ivBack.setOnClickListener { findNavController().navigateUp() }
        binding.ivFavorite.setOnClickListener { viewModel.onAddedToFavorites() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showContent(vacancy: Vacancy) {
        binding.apply {

            groupMain.isVisible = true
            groupVacancyNotFound.isVisible = false
            groupServerError.isVisible = false
            progressBar.isVisible = false

            Glide.with(ivEmployerPlaceholder)
                .load(vacancy.employer.logo)
                .placeholder(R.drawable.ic_placeholder_employer)
                .transform(
                    FitCenter(),
                    RoundedCorners(
                        resources.getDimensionPixelSize(R.dimen.layout_12dp)
                    )
                )
                .into(ivEmployerPlaceholder)

            tvName.text = vacancy.name
            tvEmployerName.text = vacancy.employer.name
            tvAddress.text = vacancy.address?.fullAddress ?: vacancy.area.name
            tvExperienceName.text = vacancy.experience?.name
            tvScheduleName.text = vacancy.schedule?.name
            tvDescription.text = HtmlCompat.fromHtml(
                vacancy.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            if (vacancy.skills.isEmpty()) {
                tvSkillsTitle.isVisible = false
                tvSkills.isVisible = false
            } else {
                tvSkills.text = vacancy.skills.joinToString("\n")
            }
        }
        showSalary(vacancy)
        showContacts(vacancy)
    }

    private fun getCurrencySymbol(currencyCode: String?): String {
        return when (currencyCode) {
            "USD" -> "$"
            "RUB" -> "₽"
            "EUR" -> "€"
            "GBP" -> "£"
            "AUD" -> "A$"
            "JPY" -> "¥"
            "NZD" -> "NZ$"
            "SGD" -> "S$"
            "KZT" -> "₸"
            "BYN", "BYR" -> "Br"
            else -> currencyCode.toString()
        }
    }

    private fun showSalary(vacancy: Vacancy) {
        binding.apply {
            tvSalary.text = when {
                vacancy.salary ==  null -> resources.getString(R.string.vacancy_salary_not_specified)
                vacancy.salary.from != null && vacancy.salary.to == null -> {
                    resources.getString(R.string.vacancy_salary_from)
                        .format(vacancy.salary.from, getCurrencySymbol(vacancy.salary.currency))
                }
                vacancy.salary.from == null && vacancy.salary.to != null -> {
                    resources.getString(R.string.vacancy_salary_to)
                        .format(vacancy.salary.to, getCurrencySymbol(vacancy.salary.currency))
                }
                vacancy.salary.from != null && vacancy.salary.to != null -> {
                    resources.getString(R.string.vacancy_salary_from_to)
                        .format(vacancy.salary.from, vacancy.salary.to, getCurrencySymbol(vacancy.salary.currency))
                }
                else -> resources.getString(R.string.vacancy_salary_not_specified)
            }
        }
    }
    private fun showContacts(vacancy: Vacancy) {
        binding.apply {
            if (vacancy.contacts == null) tvContacts.isVisible = false
            else {
                tvContacts.text = resources.getString(R.string.vacancy_contacts)
                    .format(
                        vacancy.contacts.name.takeIf { it.isNotEmpty() }?.let { "$it, " } ?: "",
                        vacancy.contacts.email.takeIf { it.isNotEmpty() }?.let { "$it, " } ?: "",
                        vacancy.contacts.phones.joinToString(", ") {
                            (it.comment?.let { child -> "$child " } ?: "") + it.formatted
                        }
                    )
            }
        }
    }

    private fun render(state: VacancyState) {
        when (state) {
            is VacancyState.Content -> {
                binding.ivFavorite.setImageResource(
                    if (state.isFavorite) R.drawable.ic_vacancy_is_favourite
                    else R.drawable.ic_vacancy_add_favourites
                )
                if (!state.onlyFavoriteChanged) showContent(state.vacancy)
            }
            is VacancyState.Loading -> {
                binding.apply {
                    groupMain.isVisible = false
                    groupVacancyNotFound.isVisible = false
                    groupServerError.isVisible = false
                    progressBar.isVisible = true
                }
            }
            is VacancyState.NotFound -> {
                binding.apply {
                    groupMain.isVisible = false
                    groupVacancyNotFound.isVisible = true
                    groupServerError.isVisible = false
                    progressBar.isVisible = false
                }
            }
            is VacancyState.Error -> {
                binding.apply {
                    groupMain.isVisible = false
                    groupVacancyNotFound.isVisible = false
                    groupServerError.isVisible = true
                    progressBar.isVisible = false
                }
            }
        }
    }

    companion object {
        const val ARGS_VACANCY = "vacancyId"
        fun createArgs(vacancyId: String) = Bundle().apply { putString(ARGS_VACANCY, vacancyId) }
    }
}
