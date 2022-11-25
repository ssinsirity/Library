package com.ssinsirity.library.ui.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssinsirity.core.data.model.Catalog
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.ItemCatalogBinding

class CatalogAdapter : RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {
    private val items = mutableListOf<Catalog>()
    private var onClickListener: (Catalog) -> Unit = {}

    fun updateItems(catalogs: List<Catalog>) {
        val diffUtilCallback = CatalogsDiffUtilCallback(items, catalogs)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items.clear()
        items.addAll(catalogs)

        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnClickListener(onClick: (Catalog) -> Unit) {
        onClickListener = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCatalogBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemCatalogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(catalog: Catalog) = with(binding) {
            val left = root.context.getString(R.string.left)
            val amountLeft = "${catalog.amount} $left"
            val authorFullName = "${catalog.book.author.firstName} ${catalog.book.author.lastName}"


            catalogNameTv.text = catalog.book.title
            catalogAuthorNameTv.text = authorFullName
            catalogGenreTv.text = catalog.book.genre.name.lowercase()
            catalogAmountLeftTv.text = amountLeft
            root.setOnClickListener { onClickListener(catalog) }
        }
    }

    private class CatalogsDiffUtilCallback(
        private val oldList: List<Catalog>,
        private val newList: List<Catalog>
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