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
import ru.practicum.android.diploma.domain.models.Contacts
import ru.practicum.android.diploma.domain.models.Vacancy
import java.text.DecimalFormat

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
        setupClickListeners()
        binding.ivBack.setOnClickListener { findNavController().navigateUp() }
        binding.ivFavorite.setOnClickListener { viewModel.onAddedToFavorites() }


    }

    private fun setupClickListeners() {
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
            if (vacancy.contacts != null) {
                tvContactInfo.setOnClickListener(null)
                tvContactInfo.isClickable = false

                setupContactClicks(vacancy.contacts)
            }
        }
        showSalary(vacancy)
        setupContacts(vacancy.contacts)
    }

    private fun setupContactClicks(contacts: Contacts) {
        binding.apply {
            if (contacts.email.isNotEmpty()) {
                tvEmail.isVisible = true
                tvEmail.text = contacts.email
                tvEmail.setOnClickListener {
                    viewModel.openEmail(contacts.email)
                }
            } else {
                tvEmail.isVisible = false
            }

            if (contacts.phones.isNotEmpty()) {
                contacts.phones.forEachIndexed { index, phone ->
                    val phoneView = when (index) {
                        0 -> binding.tvPhone1
                        1 -> binding.tvPhone2
                        else -> null
                    }
                    phoneView?.apply {
                        isVisible = true
                        text = if (phone.comment.isNullOrEmpty()) {
                            phone.formatted
                        } else {
                            "${phone.comment}: ${phone.formatted}"
                        }
                        setOnClickListener {
                            viewModel.callPhone(phone.formatted)
                        }
                    }
                }
            }
        }
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
        val numberFormat = DecimalFormat(NUMBER_FORMAT_PATTERN).apply {
            decimalFormatSymbols = decimalFormatSymbols.apply {
                groupingSeparator = NUMBER_FORMAT_GROUPING_SEPARATOR
            }
            isGroupingUsed = true
            groupingSize = NUMBER_FORMAT_GROUPING_SIZE
        }
        binding.tvSalary.text = when {
            vacancy.salary ==  null -> resources.getString(R.string.vacancy_salary_not_specified)
            vacancy.salary.from != null && vacancy.salary.to == null -> {
                resources.getString(R.string.vacancy_salary_from)
                    .format(
                        numberFormat.format(vacancy.salary.from),
                        getCurrencySymbol(vacancy.salary.currency)
                    )
            }
            vacancy.salary.from == null && vacancy.salary.to != null -> {
                resources.getString(R.string.vacancy_salary_to)
                    .format(
                        numberFormat.format(vacancy.salary.to),
                        getCurrencySymbol(vacancy.salary.currency)
                    )
            }
            vacancy.salary.from != null && vacancy.salary.to != null -> {
                resources.getString(R.string.vacancy_salary_from_to)
                    .format(
                        numberFormat.format(vacancy.salary.from),
                        numberFormat.format(vacancy.salary.to),
                        getCurrencySymbol(vacancy.salary.currency)
                    )
            }
            else -> resources.getString(R.string.vacancy_salary_not_specified)
        }
    }

    private fun setupContacts(contacts: Contacts?) {
        binding.apply {
            if (contacts == null) {
                tvContactInfo.isVisible = false
                return
            }

            tvContactInfo.isVisible = true
            tvContactInfo.setOnClickListener(null)
            tvContactInfo.isClickable = false

            // Email
            if (contacts.email.isNotEmpty()) {
                tvEmail.isVisible = true
                tvEmail.text = contacts.email
                tvEmail.setOnClickListener {
                    viewModel.openEmail(contacts.email)
                }
            } else {
                tvEmail.isVisible = false
            }

            val visiblePhones = contacts.phones.take(MAX_VISIBLE_PHONES)
            val phoneViews = listOf(tvPhone1, tvPhone2, tvPhone3)

            phoneViews.forEach { it.isVisible = false }

            visiblePhones.forEachIndexed { index, phone ->
                val phoneView = phoneViews.getOrNull(index)
                phoneView?.apply {
                    isVisible = true
                    text = if (phone.comment.isNullOrEmpty()) {
                        phone.formatted
                    } else {
                        "${phone.comment}: ${phone.formatted}"
                    }
                    setOnClickListener {
                        viewModel.callPhone(phone.formatted)
                    }
                }
            }
            if (contacts.email.isEmpty() && visiblePhones.isEmpty()) {
                tvContactInfo.isVisible = false
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

                binding.ivShare.setOnClickListener {
                    viewModel.shareVacancy(state.vacancy.url)
                }
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
        private const val NUMBER_FORMAT_PATTERN = "#,###"
        private const val NUMBER_FORMAT_GROUPING_SIZE = 3
        private const val NUMBER_FORMAT_GROUPING_SEPARATOR = ' '
        private const val MAX_VISIBLE_PHONES = 3
    }
}
