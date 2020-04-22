package com.alexaat.stocktracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.alexaat.stocktracker.R
import com.alexaat.stocktracker.adapters.StockItemClickListener
import com.alexaat.stocktracker.adapters.StockOverviewAdapter
import com.alexaat.stocktracker.database.StockItemDatabase
import com.alexaat.stocktracker.databinding.FragmentStockOverviewBinding
import com.alexaat.stocktracker.viewmodels.StockOverviewFragmentViewModel
import com.alexaat.stocktracker.viewmodels.StockOverviewFragmentViewModelFactory


class StockOverviewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentStockOverviewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_overview, container, false)
        binding.lifecycleOwner = this
        val activity = this.requireActivity()
        val db = StockItemDatabase.getInstance(activity).stockItemDatabaseDao
        val viewModelFactory = StockOverviewFragmentViewModelFactory(db)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(StockOverviewFragmentViewModel::class.java)

        // set apapter
        val adapter = StockOverviewAdapter(StockItemClickListener {
            viewModel.itemClicked(it)
        })
        binding.stockOverviewRecyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(context,3)
        binding.stockOverviewRecyclerView.layoutManager = layoutManager

        viewModel.stockItems.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.navigateToNewItemFragment.observe(viewLifecycleOwner, Observer {
            if(it>0){
             val action = StockOverviewFragmentDirections.actionStockOverviewFragmentToNewItemFragment(it)
             findNavController().navigate(action)
            }
        })
        return binding.root
    }



}
