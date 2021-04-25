package com.stupidtree.hita.theta.ui.detail

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.theta.databinding.ImageItemBinding
import com.stupidtree.hita.theta.utils.ImageUtils
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder

class ImageListAdapter(mContext: Context, mBeans: MutableList<String>) :
    BaseListAdapter<String, ImageListAdapter.IHolder>(
        mContext, mBeans
    ) {


    class IHolder(viewBinding: ImageItemBinding) : BaseViewHolder<ImageItemBinding>(viewBinding)

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return ImageItemBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): IHolder {
        return IHolder(viewBinding as ImageItemBinding)
    }

    override fun bindHolder(holder: IHolder, data: String?, position: Int) {
        ImageUtils.loadArticleImageInto(mContext, data, holder.binding.image)
    }
}