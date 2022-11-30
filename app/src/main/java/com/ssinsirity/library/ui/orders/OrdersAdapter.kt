package com.ssinsirity.library.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssinsirity.core.data.model.BookedCatalog
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.ItemOrderBinding
import com.ssinsirity.library.util.toFormat

class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {
    private val items = mutableListOf<BookedCatalog>()

    fun updateItems(orders: List<BookedCatalog>) {
        val diffUtilCallback = OrdersDiffUtilCallback(items, orders)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items.clear()
        items.addAll(orders)

        diffResult.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size


    class ViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: BookedCatalog) = with(binding) {
            val bookedBy = root.context.getString(R.string.booked_by)
            val authorFullName =
                "${order.catalog.book.author.firstName} ${order.catalog.book.author.lastName}"
            val readerFullName = "${order.reader.firstName} ${order.reader.lastName}"
            val bookedByReader = "$bookedBy $readerFullName (${order.reader.cardNumber})"

            val bookedString = root.context.getString(R.string.booked_date)
            val returnString = root.context.getString(R.string.return_date)
            val actualReturnString = root.context.getString(R.string.actual_return_date)

            val bookedDate = "$bookedString: ${order.bookedDate.toFormat()}"
            val returnDate = "$returnString: ${order.returnDate.toFormat()}"
            val actualReturnDate = "$actualReturnString: ${
                if (order.actualReturnDate.time == 0L)
                    "-"
                else
                    order.actualReturnDate.toFormat()
            }"

            bookedBookAuthorTv.text = authorFullName
            bookedBookNameTv.text = order.catalog.book.title
            bookedByReaderNameTv.text = bookedByReader

            bookedDateTv.text = bookedDate
            returnDateTv.text = returnDate
            actualReturnDateTv.text = actualReturnDate
        }
    }


    private class OrdersDiffUtilCallback(
        private val oldList: List<BookedCatalog>,
        private val newList: List<BookedCatalog>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem == newItem
        }
    }
}