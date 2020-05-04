package zion830.com.common.base

import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import zion830.com.common.ext.disableDoubleClick
import zion830.com.common.ext.filterNotNull
import zion830.com.common.ext.toItemListener
import zion830.com.common.ext.toViewListener

@Suppress("UNCHECKED_CAST")
@BindingAdapter("bindItem")
fun RecyclerView.bindItems(items: List<Any>?) {
    val defaultList = items?.filterNotNull() ?: listOf()
    (adapter as BaseRecyclerView<*, Any>).setItems(defaultList)
}

@BindingAdapter("loadImage")
fun ImageView.loadDrawableImg(drawableResId: Int) {
    Glide.with(context)
        .load(context.getDrawable(drawableResId))
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

@BindingAdapter("loadImage")
fun ImageView.loadUrlImg(url: String?) {
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

@BindingAdapter("loadImage")
fun ImageView.loadUriImg(uri: Uri?) {
    Glide.with(context)
        .load(uri)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

@BindingAdapter("onSingleClick")
fun Button.onSingleClick(listener: View.OnClickListener) {
    val onItemClickListener = listener.toItemListener()
        .disableDoubleClick()
        .toViewListener()

    setOnClickListener {
        onItemClickListener.onClick(it)
    }
}