package com.stupidtree.hita.theta.ui.navigation

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.theta.data.model.Topic
import com.stupidtree.hita.theta.databinding.TopicItemSmallBinding
import com.stupidtree.hita.theta.utils.ActivityTools
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder

class HotTopicListAdapter(mContext: Context, mBeans: MutableList<Topic>) :
    BaseListAdapter<Topic, HotTopicListAdapter.HHolder>(
        mContext, mBeans
    ) {


    class HHolder(viewBinding: TopicItemSmallBinding) :
        BaseViewHolder<TopicItemSmallBinding>(viewBinding)

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return TopicItemSmallBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): HHolder {
        return HHolder(viewBinding as TopicItemSmallBinding)
    }

    override fun bindHolder(holder: HHolder, data: Topic?, position: Int) {
        holder.binding.name.text = data?.name
        holder.binding.num.text = (data?.articleNum ?: 0 * 10).toString()
        holder.binding.item.setOnClickListener {
            data?.id?.let { it1 ->
                ActivityTools.startTopicDetailActivity(
                    mContext,
                    it1
                )
            }
        }
    }
}