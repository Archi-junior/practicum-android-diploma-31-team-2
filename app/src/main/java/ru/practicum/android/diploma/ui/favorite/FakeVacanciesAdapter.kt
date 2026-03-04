package ru.practicum.android.diploma.ui.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.domain.models.Vacancy

class FakeVacanciesAdapter(
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FakeVacanciesViewHolder> () {

    var vacancies = mutableListOf<Vacancy>()
    fun interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FakeVacanciesViewHolder = FakeVacanciesViewHolder.from(parent)

    override fun onBindViewHolder(holder: FakeVacanciesViewHolder, position: Int) {
        holder.bind(vacancies[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return vacancies.size
    }

}
