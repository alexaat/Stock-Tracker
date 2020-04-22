package com.alexaat.stocktracker.adapters



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexaat.stocktracker.R
import com.alexaat.stocktracker.database.StockItem
import com.alexaat.stocktracker.databinding.StockOverviewItemBinding




class StockOverviewAdapter(val clickListener: StockItemClickListener):ListAdapter<StockItem, StockOverviewAdapter.StockOverviewViewHolder> (StockItemsCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockOverviewViewHolder = StockOverviewViewHolder.inflateFrom(parent)
    override fun onBindViewHolder(holder: StockOverviewViewHolder, position: Int) = holder.bind(clickListener, getItem(position))

    class StockOverviewViewHolder(val binding: StockOverviewItemBinding): RecyclerView.ViewHolder(binding.root){

        companion object{
            fun inflateFrom(parent: ViewGroup):StockOverviewViewHolder{
               val binding: StockOverviewItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.stock_overview_item,parent,false)
               return StockOverviewViewHolder(binding)
            }
        }
        fun bind(clickListener: StockItemClickListener, item:StockItem){
            binding.stockItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
}

class StockItemClickListener(val clickListener: (itemId:Long) -> Unit){
    fun onClick(stockItem:StockItem) = clickListener(stockItem.itemId)

}




