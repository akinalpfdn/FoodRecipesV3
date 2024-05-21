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
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<UpdateImageSliderAdapter.ImageViewHolder>() {

    private var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image_slider_with_delete, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        val uri = imageUris[position]
        Glide.with(context)
            .load(uri)
            .into(holder.imageView)

        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }

        // Indicator noktalarını ekleme
        holder.linearLayout.removeAllViews()
            for (i in imageUris.indices) {
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


        // Mevcut pozisyonu güncelleme
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
        return imageUris.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.indicatorLayout)
    }
}
