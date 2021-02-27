package com.stupidtree.hitax.ui.eas.classroom.detail

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.ActivityEasClassroomSecondItemBinding
import com.stupidtree.hitax.ui.base.BaseActivity
import com.stupidtree.hitax.ui.base.BaseListAdapter
import com.stupidtree.hitax.ui.base.BaseViewHolder
import java.util.*

@SuppressLint("ParcelCreator")
class EmptyClassroomDetailAdapter(
    mContext: Context,
    mBeans: MutableList<HashMap<String, String?>>
) :
    BaseListAdapter<HashMap<String, String?>, EmptyClassroomDetailAdapter.XHolder>(
        mContext,
        mBeans
    ) {

    class XHolder(itemView: ActivityEasClassroomSecondItemBinding) :
        BaseViewHolder<ActivityEasClassroomSecondItemBinding>(itemView)

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return ActivityEasClassroomSecondItemBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): XHolder {
        return XHolder(viewBinding as ActivityEasClassroomSecondItemBinding)
    }


    override fun bindHolder(holder: XHolder, data: HashMap<String, String?>?, position: Int) {
        holder.binding.number.text = data?.get("number")
        holder.binding.time.text = data?.get("time")
        holder.binding.state.text = data?.get("state")
        if (data?.get("state")?.contains("空") == true && mContext is BaseActivity<*, *>) {
            holder.binding.state.setTextColor((mContext as BaseActivity<*, *>).getColorPrimary())
            holder.binding.state.setBackgroundResource(R.drawable.element_round_primary)
        } else {
            holder.binding.state.setTextColor(Color.GRAY)
            holder.binding.state.setBackgroundResource(R.drawable.element_round_grey)
        }
    }


}