package dev.amr.travelmantics.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import dev.amr.travelmantics.R
import dev.amr.travelmantics.data.Deal
import dev.amr.travelmantics.databinding.DealItemBinding
import dev.amr.travelmantics.ui.common.DataBoundListAdapter

class DealsAdapter(
    private val onClick: ((Deal) -> Unit)? = null
) : DataBoundListAdapter<Deal, DealItemBinding>(
    diffCallback = object : DiffUtil.ItemCallback<Deal>() {
        override fun areItemsTheSame(oldItem: Deal, newItem: Deal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Deal, newItem: Deal): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun createBinding(parent: ViewGroup): DealItemBinding {
        return DataBindingUtil.inflate<DealItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.deal_item,
            parent, false
        ).apply {
            this.root.setOnClickListener {
                this.deal?.let { onClick?.invoke(it) }
            }
        }


    }

    override fun bind(binding: DealItemBinding, item: Deal) {
        binding.deal = item
    }
}