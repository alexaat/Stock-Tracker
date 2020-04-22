package com.alexaat.stocktracker.adapters

import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import com.alexaat.stocktracker.database.StockItem
import com.alexaat.stocktracker.fileExists
import com.alexaat.stocktracker.makeBarCode
import com.alexaat.stocktracker.viewmodels.NewItemFragmentViewModel
import java.text.DecimalFormat


@BindingAdapter("icon_uri_binding")
fun ImageView.setImageIcon(stockItem:StockItem?){
    stockItem?.let{it->
        if(fileExists(context, it.itemIconUri)){
            setImageURI(Uri.parse(it.itemIconUri))
            tag = stockItem.itemIconUri
        }
    }
}

@BindingAdapter("stock_code_binding")
fun TextView.setStockCode(stockItem:StockItem){
    text = stockItem.itemStockCode
}

@BindingAdapter("item_name_binding")
fun TextView.setItemName(stockItem:StockItem){
    text = stockItem.itemName
}

@BindingAdapter("item_description_binding")
fun TextView.setItemDescription(stockItem:StockItem){
    text = stockItem.itemDescription
}

@BindingAdapter("price_per_item_binding")
fun TextView.setPricePerItem(stockItem:StockItem){
    val formattedPricePerItem = DecimalFormat("#,###,##0.00").format(stockItem.pricePerItem)
    text = "£ ${formattedPricePerItem}"
}

@BindingAdapter("number_of_items_binding")
fun TextView.setNumberOfItems(stockItem:StockItem){
    text = stockItem.numberOfItems.toString()
}

@BindingAdapter("stock_value_binding")
fun TextView.setStockValue(stockItem:StockItem){
    val stockValue = stockItem.pricePerItem*stockItem.numberOfItems
    val formattedstockValue = DecimalFormat("#,###,##0.00").format(stockValue)
    text = "£ $formattedstockValue"
}

/////////////////////new item///////////////////////






@BindingAdapter("new_item_stock_code_binding")
fun EditText.setStockCode(stockItem:StockItem?){
    stockItem?.let{
        setText(it.itemStockCode)
    }

}

@BindingAdapter("new_item_stock_code_binding_text_change_listener")
fun EditText.stockCodeSetOnTextChangedListener(viewModel: NewItemFragmentViewModel){
    this.addTextChangedListener{
        viewModel.stockItem?.let{item->
            item.itemStockCode = it.toString()
        }
    }
}

@BindingAdapter("new_item_stock_item_name")
fun EditText.setStockItemName(stockItem:StockItem?){
    stockItem?.let{
        setText(it.itemName)
    }

}

@BindingAdapter("new_item_name_binding_text_change_listener")
fun EditText.itemNameOnTextChangedListener(viewModel: NewItemFragmentViewModel){
    this.addTextChangedListener{
        viewModel.stockItem?.let{item->
            item.itemName = it.toString()
        }
    }
}


@BindingAdapter("new_item_stock_item_description")
fun EditText.setStockItemDescription(stockItem:StockItem?){
    stockItem?.let{
        setText(it.itemDescription)
    }

}

@BindingAdapter("new_item_description_binding_text_change_listener")
fun EditText.itemDescriptionOnTextChangedListener(viewModel: NewItemFragmentViewModel){
    this.addTextChangedListener{
        viewModel.stockItem?.let{item->
            item.itemDescription = it.toString()
        }
    }
}



@BindingAdapter("new_item_number_of_items")
fun EditText.setNumberOfItems(stockItem:StockItem?){
    stockItem?.let{
        setText(it.numberOfItems.toString())
    }

}

@BindingAdapter("new_item_number_of_items_binding_text_change_listener")
fun EditText.itemNumberOfItemsOnTextChangedListener(viewModel: NewItemFragmentViewModel){
    this.addTextChangedListener{
        viewModel.stockItem?.let{item->
            val numberString = it.toString()
            if(numberString=="" || numberString=="."){
                item.numberOfItems = 0.0
            }else {
                item.numberOfItems = it.toString().toDouble()
            }
        }
    }
}

@BindingAdapter("new_item_price_per_item")
fun EditText.setPricePerItem(stockItem:StockItem?){
    stockItem?.let{
        setText(stockItem.pricePerItem.toString())
    }

}

@BindingAdapter("new_item_price_per_items_binding_text_change_listener")
fun EditText.itemPricePerItemOnTextChangedListener(viewModel: NewItemFragmentViewModel){
    this.addTextChangedListener {
        viewModel.stockItem?.let { item ->
            val priceString = it.toString()
            if (priceString == "" || priceString == ".") {
                item.pricePerItem = 0.0
            } else {
                item.pricePerItem = it.toString().toDouble()
            }
        }
    }
}




@BindingAdapter("new_item_bar_code_icon_binding")
fun ImageView.setBarCodeIcon(stockItem: StockItem?){
    stockItem?.let{
        val bitmap = makeBarCode(it.itemBarCode)
        bitmap?.let{
            setImageBitmap(bitmap)
        }
    }
}

@BindingAdapter("new_item_bar_code_text_binding")
fun TextView.setBarCodeText(stockItem: StockItem?){
    stockItem?.let {
        if(it.itemBarCode!=""){
            visibility= View.VISIBLE
            text = it.itemBarCode
        }
    }
}



////////////stock overview///////////////
@BindingAdapter("stock_overview_item_name_and_amount")
fun TextView.setStockItemNameAndAmount(stockItem:StockItem?){
    stockItem?.let{
        text = "${stockItem.itemName} x${stockItem.numberOfItems}"
    }
}
