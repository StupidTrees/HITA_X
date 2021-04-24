package com.stupidtree.hita.theta.ui.create

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stupidtree.hita.theta.R
import com.stupidtree.stupiduser.util.ImageUtils.dp2px
import com.stupidtree.style.base.BaseListAdapterClassic
import com.stupidtree.style.picker.GalleryPicker


@SuppressLint("ParcelCreator")
class CreateImageListAdapter(
    mContext: Context,
    val viewModel: CreateArticleViewModel,
    mBeans: MutableList<String>
) :
    BaseListAdapterClassic<String, CreateImageListAdapter.IHolder>(
        mContext, mBeans
    ) {

    var transformation: CornerTransform = CornerTransform(mContext, dp2px(mContext, 8f).toFloat())


    inner class IHolder(itemView: View, var type: Int) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView? = itemView.findViewById(R.id.image)
        var delete: ImageView? = itemView.findViewById(R.id.delete)
        var add: View? = itemView.findViewById(R.id.add)
    }

    companion object {
        private const val ADD = 67
        private const val ITEM = 498
    }

    init {
        transformation.setExceptCorner(
            leftTop = false,
            rightTop = false,
            leftBottom = false,
            rightBottom = false
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mBeans.size) ADD else ITEM
    }

    override fun getItemCount(): Int {
        return if (mBeans.size == 9) 9 else mBeans.size + 1
    }


    override fun getLayoutId(viewType: Int): Int {
        return if (viewType == ADD) R.layout.create_post_img_add else R.layout.create_post_img_item
    }

    override fun createViewHolder(view: View, viewType: Int): IHolder {
        return IHolder(view, viewType)
    }

    override fun bindHolder(holder: IHolder, data: String?, position: Int) {
        if (holder.type == ITEM) {
            holder.image?.let {
                Glide.with(mContext).load(data)
                    .apply(RequestOptions.bitmapTransform(transformation)) //.centerCrop().
                    .into(it)
            }
            holder.delete?.setOnClickListener {
                viewModel.removeImage(position)
            }
        } else {
            holder.add?.setOnClickListener {
                GalleryPicker.choosePhoto(mContext as CreateArticleActivity, true)
            }
        }
    }
}

