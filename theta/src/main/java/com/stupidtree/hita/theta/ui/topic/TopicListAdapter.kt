package com.stupidtree.hita.theta.ui.topic

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.theta.data.model.Topic
import com.stupidtree.hita.theta.databinding.TopicItemBinding
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder

class TopicListAdapter(
    val list: RecyclerView?,
    mContext: Context,
    mBeans: MutableList<Topic>
) :
    BaseListAdapter<Topic, TopicListAdapter.AHolder>(
        mContext, mBeans
    ) {
    inner class AHolder(viewBinding: TopicItemBinding) :
        BaseViewHolder<TopicItemBinding>(viewBinding)


    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return TopicItemBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): AHolder {
        return AHolder(viewBinding as TopicItemBinding)
    }

    override fun bindHolder(holder: AHolder, data: Topic?, position: Int) {
        data?.let {
            holder.binding.name.text = data.name
            holder.binding.description.text = data.description
            holder.binding.item.setOnClickListener { v ->
                mOnItemClickListener?.onItemClick(data, v, position)
            }
           // ImageUtils.loadAvatarInto(mContext, it.avatar, holder.binding.avatar)
        }
    }

}