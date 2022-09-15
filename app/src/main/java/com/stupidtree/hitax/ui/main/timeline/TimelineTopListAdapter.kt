package com.stupidtree.hitax.ui.main.timeline

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.alorma.timeline.TimelineView
import com.github.lzyzsd.circleprogress.ArcProgress
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.hitax.databinding.DynamicTimelineTopBinding
import com.stupidtree.style.base.BaseListAdapterClassic
import com.stupidtree.hitax.utils.MaterialCircleAnimator
import com.stupidtree.hitax.utils.TextTools
import com.stupidtree.hitax.utils.TimeTools
import com.stupidtree.hitax.utils.TimeTools.TTY_REPLACE
import com.stupidtree.hitax.utils.TimeTools.isSameDay
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder
import net.cachapa.expandablelayout.ExpandableLayout
import java.util.*

@SuppressLint("ParcelCreator")
class TimelineTopListAdapter(
    mContext: Context,
    res: MutableList<EventItem>
) : BaseListAdapter<EventItem, TimelineTopListAdapter.THolder>(
    mContext, res
) {


    class THolder(viewBinding: DynamicTimelineTopBinding) : BaseViewHolder<DynamicTimelineTopBinding>(
        viewBinding
    )

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return DynamicTimelineTopBinding.inflate(mInflater,parent,false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): THolder {
        return THolder(viewBinding as DynamicTimelineTopBinding)
    }

    override fun bindHolder(holder: THolder, data: EventItem?, position: Int) {
        holder.binding.name.text = data?.name
        if(isSameDay(System.currentTimeMillis(),data?.from?.time?:0)){ //在今天，显示倒数
            val minutes = data?.getFromTimeDistance()?:0
            holder.binding.time.text = if(minutes>60){
                mContext.getString(R.string.timeline_countdown_template_hour,(minutes.toFloat()/60.0))
            }else{
                mContext.getString(R.string.timeline_countdown_template_minute,minutes)
            }
        }else{
            holder.binding.time.text = TimeTools.getDateString(mContext,data?.from?.time?:0,true, TTYMode = TTY_REPLACE)
        }

        holder.binding.card.setOnClickListener {
            mOnItemClickListener?.onItemClick(data,holder.binding.card,position)
        }
    }

}