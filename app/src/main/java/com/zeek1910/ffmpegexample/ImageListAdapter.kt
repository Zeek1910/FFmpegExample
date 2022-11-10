package com.zeek1910.ffmpegexample

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zeek1910.ffmpegexample.databinding.ListItemBinding

class ImageListAdapter : RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder>() {

    private val items: MutableSet<Uri> = mutableSetOf()

    fun addItems(items: List<Uri>) {
        val startIndex = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(startIndex, items.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear(){
        this.items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ImageListViewHolder(
        ListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        holder.bind(items.elementAt(position))
    }

    override fun getItemCount() = items.size

    class ImageListViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {
            Glide.with(binding.root).load(uri).into(binding.imagePreview)
        }

    }
}