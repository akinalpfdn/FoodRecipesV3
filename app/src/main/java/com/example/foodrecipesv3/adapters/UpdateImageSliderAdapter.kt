package com.example.foodrecipesv3.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipesv3.R

class UpdateImageSliderAdapter(
    private val context: Context,
    private val imageUris: MutableList<Uri>,
    private val imageUrls: MutableList<String>, // List to hold existing URLs
    private val onDeleteClick: (Int, Boolean) -> Unit // Pass a flag to indicate if the item is a URI or a URL
) : RecyclerView.Adapter<UpdateImageSliderAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image_slider_with_delete, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        if (position < imageUris.size) {
            holder.imageView.setImageURI(imageUris[position])
        } else {
            val urlPosition = position - imageUris.size
            Glide.with(context).load(imageUrls[urlPosition]).into(holder.imageView)
        }

        holder.deleteButton.setOnClickListener {
            if (position < imageUris.size) {
                onDeleteClick(position, true) // true indicates it's a URI
            } else {
                onDeleteClick(position - imageUris.size, false) // false indicates it's a URL
            }
        }

        holder.linearLayout.removeAllViews()
        for (i in 0 until itemCount) {
            val dot = ImageView(context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params
            dot.setImageResource(R.drawable.indicator_inactive_dot)
            holder.linearLayout.addView(dot)
        }

        for (i in 0 until holder.linearLayout.childCount) {
            val dot = holder.linearLayout.getChildAt(i) as ImageView
            if (i == position) {
                dot.setImageResource(R.drawable.indicator_active_dot)
            } else {
                dot.setImageResource(R.drawable.indicator_inactive_dot)
            }
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size + imageUrls.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.indicatorLayout)
    }
}
