package com.review.relic.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.review.relic.R
import com.review.relic.databinding.ReviewItemLayoutBinding
import com.review.relic.dto.ReviewRelicSettingsResponse
import com.review.relic.utils.overrideColor


class ReviewRelicItemAdapter(
    val context: Context,
    val showImage: Boolean? = false,
    val emptyBitmap: Bitmap?,
    val filledBitmap: Bitmap?,
    val selectedColor: Int?,
    val callBack: (Int) -> Unit
) :
    DataBoundListAdapter<ReviewRelicSettingsResponse.ReviewSetting, ReviewItemLayoutBinding>(
        object : DiffUtil.ItemCallback<ReviewRelicSettingsResponse.ReviewSetting>() {
            override fun areItemsTheSame(
                oldItem: ReviewRelicSettingsResponse.ReviewSetting,
                newItem: ReviewRelicSettingsResponse.ReviewSetting
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ReviewRelicSettingsResponse.ReviewSetting,
                newItem: ReviewRelicSettingsResponse.ReviewSetting
            ): Boolean {
                return oldItem == newItem
            }

        }) {

    private var checkedPosition = -1

    override fun createBinding(parent: ViewGroup): ReviewItemLayoutBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.review_item_layout,
            parent,
            false
        )
    }

    override fun bind(
        binding: ReviewItemLayoutBinding,
        item: ReviewRelicSettingsResponse.ReviewSetting,
        position: Int
    ) {

        binding.reviewRelicItem = item
        if (showImage == true) {
            binding.isSelected = (position <= checkedPosition)
        } else {
            binding.isSelected = (position == checkedPosition)

            val drawable = ContextCompat.getDrawable(context, R.drawable.review_item_text_bg)
            if (binding.isSelected == true) {
                selectedColor?.let { drawable?.overrideColor(it) }
            } else {
                drawable?.overrideColor(
                    ContextCompat.getColor(context, android.R.color.white)
                )
            }
            binding.textViewItem.background = drawable

        }
        binding.showImage = showImage
        binding.imageBitmapEmpty = emptyBitmap
        binding.imageBitmapFilled = filledBitmap

        binding.textViewItem.setOnClickListener {
            updateItem(binding, position)
        }

        binding.imageViewReview.setOnClickListener {
            updateItem(binding, position)
        }

    }

    private fun updateItem(binding: ReviewItemLayoutBinding, position: Int) {
        checkedPosition = position
        notifyDataSetChanged()
        binding.executePendingBindings()
        callBack.invoke(checkedPosition)
    }
}
