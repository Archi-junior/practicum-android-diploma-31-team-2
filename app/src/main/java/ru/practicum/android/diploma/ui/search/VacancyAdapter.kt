package ru.practicum.android.diploma.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ItemVacancyBinding
import ru.practicum.android.diploma.domain.models.Vacancy
import java.text.DecimalFormat

class VacancyAdapter(
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<VacancyAdapter.VacancyViewHolder>() {

    private var vacancies: List<Vacancy> = emptyList()
    private val numberFormat = DecimalFormat("#,###").apply {
        isGroupingUsed = true
        groupingSize = NUMBER_GROUPING_SIZE
        val symbols = decimalFormatSymbols
        symbols.groupingSeparator = ' '
        decimalFormatSymbols = symbols
    }

    fun submitList(newList: List<Vacancy>) {
        vacancies = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
        val binding = ItemVacancyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VacancyViewHolder(binding, onItemClick, numberFormat)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        holder.bind(vacancies[position])
    }

    override fun getItemCount(): Int = vacancies.size

    class VacancyViewHolder(
        private val binding: ItemVacancyBinding,
        private val onItemClick: (String) -> Unit,
        private val numberFormat: DecimalFormat
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vacancy: Vacancy) {
            binding.apply {
                Glide.with(ivCompanyLogo)
                    .load(vacancy.employer.logo)
                    .placeholder(R.drawable.ic_placeholder_employer)
                    .override(
                        itemView.resources.getDimensionPixelSize(R.dimen.logo_size),
                        itemView.resources.getDimensionPixelSize(R.dimen.logo_size)
                    )
                    .transform(
                        CenterCrop(),
                        RoundedCorners(
                            itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)
                        )
                    )
                    .into(ivCompanyLogo)

                tvVacancyName.text = vacancy.name
                tvCompanyName.text = vacancy.employer.name
                tvSalary.text = formatSalary(vacancy)

                root.setOnClickListener {
                    onItemClick(vacancy.id)
                }
            }
        }

        private fun formatSalary(vacancy: Vacancy): String {
            val context = itemView.context
            val currencySymbol = getCurrencySymbol(vacancy.salary?.currency)

            return when {
                vacancy.salary == null -> {
                    context.getString(R.string.vacancy_salary_not_specified)
                }
                vacancy.salary.from != null && vacancy.salary.to == null -> {
                    context.getString(
                        R.string.vacancy_salary_from,
                        numberFormat.format(vacancy.salary.from),
                        currencySymbol
                    )
                }
                vacancy.salary.from == null && vacancy.salary.to != null -> {
                    context.getString(
                        R.string.vacancy_salary_to,
                        numberFormat.format(vacancy.salary.to),
                        currencySymbol
                    )
                }
                vacancy.salary.from != null && vacancy.salary.to != null -> {
                    context.getString(
                        R.string.vacancy_salary_from_to,
                        numberFormat.format(vacancy.salary.from),
                        numberFormat.format(vacancy.salary.to),
                        currencySymbol
                    )
                }
                else -> {
                    context.getString(R.string.vacancy_salary_not_specified)
                }
            }
        }

        private fun getCurrencySymbol(currencyCode: String?): String {
            if (currencyCode.isNullOrBlank()) return ""

            return try {
                val currency = java.util.Currency.getInstance(currencyCode.uppercase())
                currency.symbol
            } catch (e: IllegalArgumentException) {
                Log.e("VacancyAdapter", "Unknown currency code: $currencyCode", e)
                when (currencyCode.uppercase()) {
                    "USD" -> "$"
                    "RUB" -> "₽"
                    "EUR" -> "€"
                    "AUD" -> "A$"
                    "JPY" -> "¥"
                    "NZD" -> "NZ$"
                    "SGD" -> "S$"
                    "KZT" -> "₸"
                    "BYN", "BYR" -> "Br"
                    else -> currencyCode
                }
            }
        }
    }

    companion object{
        const val NUMBER_GROUPING_SIZE = 3
    }
}
