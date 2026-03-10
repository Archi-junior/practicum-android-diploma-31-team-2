package ru.practicum.android.diploma.ui.industry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.ItemIndustryBinding
import ru.practicum.android.diploma.domain.models.Industry

class IndustryAdapter(
    private val onItemClick: (Industry) -> Unit
) : RecyclerView.Adapter<IndustryAdapter.IndustryViewHolder>() {

    private var industries: List<Industry> = emptyList()
    private var selectedIndustryId: Int? = null

    fun submitList(newIndustries: List<Industry>) {
        industries = newIndustries
        notifyDataSetChanged()
    }

    fun setSelectedIndustryId(id: Int?) {
        selectedIndustryId = id
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val binding = ItemIndustryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IndustryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val industry = industries[position]
        val isSelected = industry.id == selectedIndustryId
        holder.bind(industry, isSelected)
    }

    override fun getItemCount(): Int = industries.size

    class IndustryViewHolder(
        private val binding: ItemIndustryBinding,
        private val onItemClick: (Industry) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(industry: Industry, isSelected: Boolean) {
            binding.apply {
                tvIndustryName.text = industry.name
                rbSelected.isChecked = isSelected
                rbSelected.visibility = View.VISIBLE

                root.setOnClickListener {
                    onItemClick(industry)
                }
            }
        }
    }
}
