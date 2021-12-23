package com.stupidtree.hitax.ui.eas.score

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.stupidtree.hitax.data.model.eas.CourseScoreItem
import com.stupidtree.hitax.databinding.ActivityEasScoreListItemBinding
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder

@SuppressLint("ParcelCreator")
class ScoresListAdapter(mContext: Context, mBeans: MutableList<CourseScoreItem>):
        BaseListAdapter<CourseScoreItem,ScoresListAdapter.SHolder>(
            mContext, mBeans
        ) {

    class SHolder(viewBinding: ActivityEasScoreListItemBinding) :
        BaseViewHolder<ActivityEasScoreListItemBinding>(viewBinding)

    override fun bindHolder(
        holder: ScoresListAdapter.SHolder,
        data: CourseScoreItem?,
        position: Int
    ) {
        if (position == mBeans.size - 1) {
            holder.binding.divider2.visibility = View.GONE
        } else {
            holder.binding.divider2.visibility = View.VISIBLE
        }
        holder.binding.title.text = data?.courseName
        holder.binding.scores.text = data?.finalScores.toString()
    }

    override fun createViewHolder(
        viewBinding: ViewBinding,
        viewType: Int
    ): ScoresListAdapter.SHolder {
        return ScoresListAdapter.SHolder(viewBinding = viewBinding as ActivityEasScoreListItemBinding)
    }

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return ActivityEasScoreListItemBinding.inflate(mInflater,parent,false)
    }

}