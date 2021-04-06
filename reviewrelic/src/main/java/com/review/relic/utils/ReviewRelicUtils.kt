package com.review.relic.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Build
import android.util.Base64.DEFAULT
import android.util.Base64.decode
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import com.review.relic.R
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


fun getBitmapFromBase64(base64String: String?): Bitmap? {
    val base64Image = base64String?.split(",")?.get(1)
    return base64Image?.let {
        val decodedString: ByteArray = decode(it, DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}

@BindingAdapter("sheetImage")
fun loadImage(view: ImageView?, reviewRelicImage: ReviewRelicImage?) {
    reviewRelicImage?.let {
        when (it.type) {
            ImageType.ResourceId -> {
                view?.setImageResource(reviewRelicImage.image as Int)
            }
            ImageType.Base64 -> {
                view?.setImageBitmap(getBitmapFromBase64(reviewRelicImage.image as String?))
            }
        }
    }

}

fun Drawable.overrideColor(@ColorInt colorInt: Int) {
    when (this) {
        is GradientDrawable -> setColor(colorInt)
        is ShapeDrawable -> paint.color = colorInt
        is ColorDrawable -> color = colorInt
    }
}


fun getRoundedDrawableWithStroke(
    context: Context,
    selectedColor: Int? = Color.WHITE
): GradientDrawable {
    return GradientDrawable().apply {
        cornerRadius = context.resources.getDimension(R.dimen.review_relic_item_corner_radius)
        setStroke(
            context.resources.getDimension(R.dimen.review_relic_item_stroke_width).toInt(),
            ContextCompat.getColor(context, R.color.grey_border_color)
        )
        selectedColor?.let { setColor(it) }
    }
}

fun getButtonSelectorDrawable(
    context: Context,
    selectedColor: Int? = Color.WHITE
): StateListDrawable {
    return StateListDrawable().apply {
        addState(
            intArrayOf(android.R.attr.state_enabled),
            getRoundedDrawableWithStroke(context, selectedColor)
        )
        addState(
            intArrayOf(-android.R.attr.state_enabled),
            getRoundedDrawableWithStroke(context)
        )
    }
}

fun getButtonSelectorColor(context: Context, selectedColor: Int?): ColorStateList {
    val color = selectedColor ?: Color.WHITE
    return ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf()
        ),
        intArrayOf(
            ContextCompat.getColor(context, android.R.color.white),
            color, color
        )
    )
}

fun ProgressBar.setIndeterminateTintCompat(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        val wrapDrawable = DrawableCompat.wrap(indeterminateDrawable)
        DrawableCompat.setTint(wrapDrawable, color)
        indeterminateDrawable = DrawableCompat.unwrap(wrapDrawable)
    } else {
        indeterminateTintList = ColorStateList.valueOf(color)
    }
}


fun getUnixTimePlus5min(): Long {
    return System.currentTimeMillis() / 1000
}

@Throws(Exception::class)
fun hmacEncryption(str: String, secret: String): String {
    val sha256Hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
    sha256Hmac.init(secretKey)
    return Hex.encodeHexString(sha256Hmac.doFinal(str.toByteArray()))
}

fun getTextInputLayoutBorderColor(selectedColor: Int?): ColorStateList {
    val color = selectedColor ?: Color.WHITE
    val states = arrayOf(
        intArrayOf(android.R.attr.state_focused),
        intArrayOf(android.R.attr.state_hovered),
        intArrayOf(android.R.attr.state_enabled),
        intArrayOf()
    )

    val colors = intArrayOf(
        color,
        color,
        color,
        Color.GRAY
    )
    return ColorStateList(states, colors)
}