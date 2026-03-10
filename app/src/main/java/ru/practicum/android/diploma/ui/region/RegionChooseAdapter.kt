package ru.practicum.android.diploma.ui.region

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.ItemRegionBinding
import ru.practicum.android.diploma.presentation.mapper.AreaUi

class RegionChooseAdapter(
    private var regions: List<AreaUi>,
    private val onRegionClick: (region: AreaUi) -> Unit,
) :
    RecyclerView.Adapter<RegionChooseAdapter.RegionViewHolder>() {

    fun updateRegions(newRegion: List<AreaUi>) {
        regions = newRegion
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        val region = regions[position]
        holder.bind(region)
        holder.itemView.setOnClickListener { onRegionClick(region) }
    }

    override fun getItemCount(): Int = regions.size

    class RegionViewHolder(private val binding: ItemRegionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reg: AreaUi) = binding.apply {
            region.text = reg.name
        }
    }
}
