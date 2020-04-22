package com.alexaat.stocktracker.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexaat.stocktracker.R
import com.alexaat.stocktracker.database.StockItem
import com.alexaat.stocktracker.databinding.StockItemLayoutBinding



class MyAdapter(var stockItemClickListener:StockItemClickListener):ListAdapter<StockItem, MyAdapter.MyViewHolder>(StockItemsCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.inflateFrom(parent)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(stockItemClickListener, getItem(position))
    }


    class MyViewHolder private constructor(private val binding:StockItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        companion object{
            fun inflateFrom(parent: ViewGroup):MyViewHolder{
                val binding:StockItemLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.stock_item_layout, parent, false)
                return  MyViewHolder(binding)
            }
        }

        fun bind(stockItemClickListener:StockItemClickListener, item: StockItem){
            binding.stockItem = item
            binding.stockItemClickListener = stockItemClickListener

            binding.executePendingBindings()
        }
    }
}


class StockItemsCallBack:DiffUtil.ItemCallback<StockItem>() {
    override fun areItemsTheSame(oldItem: StockItem, newItem: StockItem): Boolean {
        return oldItem.itemId == newItem.itemId
    }

    override fun areContentsTheSame(oldItem: StockItem, newItem: StockItem): Boolean {
        return oldItem == newItem
    }


}


