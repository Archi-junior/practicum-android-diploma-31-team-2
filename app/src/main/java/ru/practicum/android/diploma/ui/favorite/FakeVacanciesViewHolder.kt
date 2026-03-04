package ru.practicum.android.diploma.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.FakeVacanciesViewBinding
import ru.practicum.android.diploma.domain.models.Vacancy

class FakeVacanciesViewHolder(private val binding: FakeVacanciesViewBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(vacancy: Vacancy) {

        binding.tvId.text = vacancy.id
        binding.tvName.text = vacancy.name
    }

    companion object {
        fun from(parent: ViewGroup): FakeVacanciesViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = FakeVacanciesViewBinding.inflate(inflater, parent, false)
            return FakeVacanciesViewHolder(binding)
        }
    }
}
