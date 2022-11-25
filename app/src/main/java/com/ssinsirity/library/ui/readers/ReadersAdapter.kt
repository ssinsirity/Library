package com.ssinsirity.library.ui.readers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssinsirity.core.data.model.ReaderCard
import com.ssinsirity.library.databinding.ItemReaderBinding

class ReadersAdapter : RecyclerView.Adapter<ReadersAdapter.ViewHolder>() {
    private val items = mutableListOf<ReaderCard>()

    fun updateItems(readers: List<ReaderCard>) {
        val diffUtilCallback = ReadersDiffUtilCallback(items, readers)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items.clear()
        items.addAll(readers)

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReaderBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size


    inner class ViewHolder(private val binding: ItemReaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reader: ReaderCard) = with(binding) {
            val fullName = "${reader.firstName} ${reader.lastName}"
            readerFullName.text = fullName
            readerCardNumber.text = reader.cardNumber
            readerPhoneNumber.text = reader.phoneNumber
        }
    }


    private class ReadersDiffUtilCallback(
        private val oldList: List<ReaderCard>,
        private val newList: List<ReaderCard>
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