package com.example.foodrecipesv3.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipesv3.R
import com.example.foodrecipesv3.fragments.OtherRecipeDialogFragment
import com.example.foodrecipesv3.fragments.UpdateRecipeDialogFragment

class ImageSliderAdapter(
    private val context: Context,
    private val imageList: List<String>,
    private val recipeId: String,
    private val isClickable: Boolean,
    private val isOtherRecipe: Boolean
) :
    RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    private var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        Glide.with(context)
            .load(imageList[position])
            .into(holder.imageView)
        if(isClickable)
        {
            if(isOtherRecipe)
            {
                holder.imageView.setOnClickListener {
                    val fragment = OtherRecipeDialogFragment.newInstance(recipeId)
                    fragment.show((context as FragmentActivity).supportFragmentManager, "OtherRecipeDialogFragment")
                }
            }
            else {
                holder.imageView.setOnClickListener {
                    val fragment = UpdateRecipeDialogFragment.newInstance(recipeId)
                    fragment.show(
                        (holder.itemView.context as FragmentActivity).supportFragmentManager,
                        "UpdateRecipeDialogFragment"
                    )
                }
            }
        }
        else
        {
            holder.imageView.setOnClickListener {
                val fragment = FullImageDialogFragment.newInstance(imageList[position])
                fragment.show((context as FragmentActivity).supportFragmentManager, "FullImageDialog")
            }
        }
        holder.expandButton.setOnClickListener {
            val fragment = FullImageDialogFragment.newInstance(imageList[position])
            fragment.show((context as FragmentActivity).supportFragmentManager, "FullImageDialog")
        }
        // Indicator noktalarını ekleme
        if (holder.linearLayout.childCount == 0) {
            for (i in imageList.indices) {
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
        return imageList.size
    }

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val expandButton: ImageButton = itemView.findViewById(R.id.expandButton)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.indicatorLayout)
    }
}
