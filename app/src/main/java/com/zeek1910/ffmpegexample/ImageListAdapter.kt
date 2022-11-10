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

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: Set<Uri>) {
        this.items.clear()
        this.items.addAll(items)
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