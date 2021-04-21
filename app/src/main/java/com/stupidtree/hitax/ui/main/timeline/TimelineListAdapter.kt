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
import com.alorma.timeline.TimelineView
import com.github.lzyzsd.circleprogress.ArcProgress
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.style.base.BaseListAdapterClassic
import com.stupidtree.hitax.utils.MaterialCircleAnimator
import com.stupidtree.hitax.utils.TextTools
import com.stupidtree.hitax.utils.TimeTools
import net.cachapa.expandablelayout.ExpandableLayout
import java.util.*

@SuppressLint("ParcelCreator")
class TimelineListAdapter(
        mContext: Context?,
        res: MutableList<EventItem>
) : BaseListAdapterClassic<EventItem, RecyclerView.ViewHolder>(
        mContext!!, res
) {

    private var nowEvent: EventItem? = null
    private var nextEvent: EventItem? = null
    private var nowProgress: Float = 0f


    private var onHintConfirmedListener: OnHintConfirmedListener? = null
    fun setOnHintConfirmedListener(onHintConfirmedListener: OnHintConfirmedListener?) {
        this.onHintConfirmedListener = onHintConfirmedListener
    }


    private fun refreshNowAndNextEvent(todayEvents: List<EventItem>) {
        var changedNow = false
        var changedNext = false
        for (i in todayEvents.indices.reversed()) {
            val ei: EventItem = todayEvents[i]
            if (ei.containsTimeStamp(System.currentTimeMillis())) {
                nowEvent = ei
                changedNow = true
            } else if (ei.happensAfterTimeStamp(System.currentTimeMillis())) {
                nextEvent = ei
                changedNext = true
            }
        }
        if (!changedNext) nextEvent = null
        if (!changedNow) nowEvent = null
        nowEvent?.let {
            nowProgress = it.getProgress(System.currentTimeMillis())
        }
    }

    private fun getTodayCourseNum(): Int {
        var result = 0
        for (ei in mBeans) {
            if (ei.type === EventItem.TYPE.CLASS) {
                result++
            }
        }
        return result
    }

    override fun notifyItemChangedSmooth(newL: List<EventItem>) {
        super.notifyItemChangedSmooth(newL)
        refreshNowAndNextEvent(mBeans)
    }

    override fun getLayoutId(viewType: Int): Int {
        val id = R.layout.dynamic_timeline_card_passed
        when (viewType) {
            HINT -> return R.layout.dynamic_timeline_hint
            HEADER -> return R.layout.dynamic_timeline_header
            EMPTY -> return R.layout.dynamic_timeline_empty
            FOOT -> return R.layout.dynamic_timeline_foot
            CLASS -> return R.layout.dynamic_timeline_card_important
            PASSED -> return R.layout.dynamic_timeline_card_passed
        }
        return id
    }

    override fun createViewHolder(v: View, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HINT -> HintHolder(v)
            HEADER -> timelineHeaderHolder(v)
            EMPTY, FOOT -> emptyHolder(v)
            else -> timelineHolder(v, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HintHolder -> {
                bindHintHolder(holder, position - 1)
            }
            is timelineHolder -> bindTimelineHolder(
                    holder,
                    position - 1
            )
            is timelineHeaderHolder -> bindHeaderHolder(
                    holder
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindTimelineHolder(timelineHolder: timelineHolder, position: Int) {
        try {
            if (timelineHolder.timeline != null) {
                var firstEventIndex = 0
                for (ei in mBeans) {
                    if (ei.type !== EventItem.TYPE.TAG) break
                    firstEventIndex++
                }
                timelineHolder.timeline!!.determineTimelineType(
                        position - firstEventIndex,
                        mBeans.size - firstEventIndex
                )
                if (mBeans.size - firstEventIndex == 1) {
                    timelineHolder.timeline!!.lineWidth = 0f
                    // timelineHolder.timeline.setVisibility(View.GONE);
                } else {
                    timelineHolder.timeline!!.lineWidth =
                            mContext.resources.getDimension(R.dimen.timeline_width)
                }
                // else timelineHolder.timeline.setVisibility(View.VISIBLE);
            }
            if (position >= mBeans.size || position < 0) return
            timelineHolder.tv_name.text = mBeans[position].name
            if (timelineHolder.tv_time != null) timelineHolder.tv_time.setText(
                    TextTools.getChatTimeText(mContext, mBeans[position].from)
                            + "-" + TextTools.getChatTimeText(mContext, mBeans[position].to)
            )
            if (timelineHolder.tv_duration != null) {
                val duration: Int =
                        (((mBeans[position].to.time - mBeans[position].from.time) / 1000).toInt())
                if (duration >= 60) timelineHolder.tv_duration!!.text =
                        (duration / 60).toString() + "h " + (if (duration % 60 == 0) "" else (duration % 60).toString() + "min") else timelineHolder.tv_duration!!.text =
                        duration.toString() + "min"
            }
            if (timelineHolder.progressBar != null) {
                if (mBeans[position] == nowEvent) {
                    timelineHolder.progressBar!!.visibility = View.VISIBLE
                    timelineHolder.progressBar!!.progress = (nowProgress * 100).toInt()
                    timelineHolder.timeline!!.setImageDrawable(
                            ContextCompat.getDrawable(
                                    mContext,
                                    R.drawable.ic_timelapse
                            )
                    )
                } else {
                    timelineHolder.progressBar!!.visibility = View.GONE
                }
            }
            if (timelineHolder.tv_place != null) {
                val result =
                        if (TextUtils.isEmpty(mBeans[position].place)) mContext.getString(R.string.unknown_location) else mBeans[position].place
                timelineHolder.tv_place!!.text = result
            }
            if (mOnItemClickListener != null) {
                timelineHolder.itemCard.setOnClickListener { v ->
                    mOnItemClickListener!!.onItemClick(mBeans[position], v, position)
                }
            }
            if (mOnItemLongClickListener != null) {
                timelineHolder.itemCard.setOnLongClickListener { v ->
                    mOnItemLongClickListener!!.onItemLongClick(mBeans[position], v, position)
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindHintHolder(holder: HintHolder, position: Int) {
        val hint = mBeans[position]
        holder.hint.setText(hint.name)
        holder.button.setOnClickListener { v ->
            if (onHintConfirmedListener != null) onHintConfirmedListener!!.onConfirmed(
                    v,
                    position + 1,
                    hint
            )
        }
    }

    private fun bindHeaderHolder(header: timelineHeaderHolder) {
        header.UpdateHeadView()
    }

    override fun getItemViewType(position: Int): Int {
        var firstEventIndex = 0
        for (ei in mBeans) {
            if (ei.type !== EventItem.TYPE.TAG) break
            firstEventIndex++
        }
        if (position == 0) return HEADER
        return if (mBeans.size - firstEventIndex == 0 && position - firstEventIndex == 1) {
            EMPTY
        } else {
            if (position == mBeans.size + 1) return FOOT
            var type = FOOT
            if (mBeans[position - 1].type === EventItem.TYPE.TAG) return HINT
            if (TimeTools.passed(mBeans[position - 1].to)) type = PASSED
            else if (mBeans[position - 1].type == EventItem.TYPE.EXAM || mBeans[position - 1].type == EventItem.TYPE.CLASS) type =
                    CLASS
            type
        }
    }


    interface OnHintConfirmedListener {
        fun onConfirmed(v: View?, position: Int, hint: EventItem?)
    }

    override fun getItemCount(): Int {
        return if (mBeans.size == 0) 2 else mBeans.size + 2
    }

    override val indexBias: Int
        get() = 1

    internal class timelineHolder(itemView: View, var type: Int) :
            RecyclerView.ViewHolder(itemView) {
        var tv_time = itemView.findViewById<TextView>(R.id.tl_tv_time)
        var tv_name: TextView
        var tv_duration: TextView?
        var tv_place: TextView?
        var progressBar: ProgressBar?
        var itemCard: CardView

        // TimelineView timelineView;
        var timeline: TimelineView?

        //LinearLayout naviButton;
        init {
            tv_name = itemView.findViewById(R.id.tl_tv_name)
            tv_duration = itemView.findViewById(R.id.tl_tv_duration)
            itemCard = itemView.findViewById(R.id.tl_card)
            progressBar = itemView.findViewById(R.id.event_progressbar)
            tv_place = itemView.findViewById(R.id.tl_tv_place)
            timeline = itemView.findViewById(R.id.timeline)
        }
    }

    internal class HintHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var hint: TextView
        var button: Button

        init {
            button = itemView.findViewById(R.id.button)
            hint = itemView.findViewById(R.id.title)
        }
    }

    internal class emptyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class timelineHeaderHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        private var head_expand: ExpandableLayout
        private var head_counting_layout: ViewGroup
        var head_image: ImageView
        var head_counting_image: ImageView
        var head_bg: ImageView
        var head_title: TextView
        var head_subtitle: TextView
        var head_card: CardView
        private var head_counting_time: TextView
        private var head_counting_name: TextView
        var head_goQuickly_classroom: TextView
        var head_goNow: LinearLayout
        var circleProgress: ArcProgress
        var bt_bar_timetable: ImageView
        private var bt_bar_addEvent: ImageView
        var heads: Array<View>
        private fun collapseCard() {
            val fromD: Float
            val toD: Float
            fromD = 180f
            toD = 0f
            val ra = RotateAnimation(
                    fromD,
                    toD,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
            )
            ra.interpolator = DecelerateInterpolator()
            ra.duration = 200 //设置动画持续周期
            ra.repeatCount = 0 //设置重复次数
            ra.fillAfter = true //动画执行完后是否停留在执行完的状态
            if (head_expand.isExpanded) {
                bt_bar_timetable.animation = ra
                bt_bar_addEvent.animation = ra
                bt_bar_timetable.startAnimation(ra)
                bt_bar_addEvent.startAnimation(ra)
            }
            head_expand.isExpanded = false
        }

        fun toggleHeadExpand() {
            if (!head_expand.isExpanded) {
                head_counting_layout.visibility = View.INVISIBLE
                MaterialCircleAnimator.animShow(head_counting_layout, 800)
            }

            val fromD: Float
            val toD: Float
            if (!head_expand.isExpanded) {
                fromD = 0f
                toD = 180f
            } else {
                fromD = 180f
                toD = 0f
            }
            val ra = RotateAnimation(
                    fromD,
                    toD,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
            )
            ra.interpolator = DecelerateInterpolator()
            ra.duration = 200 //设置动画持续周期
            ra.repeatCount = 0 //设置重复次数
            ra.fillAfter = true //动画执行完后是否停留在执行完的状态
            bt_bar_timetable.animation = ra
            bt_bar_addEvent.animation = ra
            bt_bar_timetable.startAnimation(ra)
            bt_bar_addEvent.startAnimation(ra)
            head_expand.toggle()
        }

        fun showHeadView() {
//            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,32f);
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    head_card.setCardElevation((Float) animation.getAnimatedValue());
//                }
//            });
//            valueAnimator.setDuration(600);
//            valueAnimator.start();
//            MaterialCircleAnimator.animShow(head_bg, 600);
        }

        @SuppressLint("SetTextI18n")
        fun UpdateHeadView() {
            val currentTime = Calendar.getInstance()
            val titleToSet: String
            val subtitltToSet: String
            if (mBeans.isEmpty()) {
                titleToSet = mContext.getString(R.string.timeline_head_free_title)
                subtitltToSet = mContext.getString(R.string.timeline_head_free_subtitle)
                switchHeadView(head_image, R.drawable.ic_timeline_head_free)
            } else if (nowEvent != null) {
                switchHeadView(circleProgress, -1)
                titleToSet = nowEvent!!.name
                subtitltToSet = mContext.getString(R.string.timeline_head_ongoing_subtitle)
                circleProgress.progress = (nowProgress * 100).toInt()
            } else {
                if (TimeInDay(currentTime) < TimeInDay(
                                5,
                                0
                        ) && TimeInDay(currentTime) > TimeInDay(0, 0)
                ) {
                    switchHeadView(head_image, R.drawable.ic_moon)
                    titleToSet = mContext.getString(R.string.timeline_head_goodnight_title)
                    subtitltToSet = mContext.getString(R.string.timeline_head_goodnight_subtitle)
                } else if (TimeInDay(currentTime) < TimeInDay(
                                8,
                                15
                        ) && TimeInDay(currentTime) > TimeInDay(5, 0)
                ) {
                    switchHeadView(head_image, R.drawable.ic_sunny)
                    titleToSet = mContext.getString(R.string.timeline_head_goodmorning_title)
                    subtitltToSet = String.format(
                            mContext.getString(R.string.timelinr_goodmorning_subtitle),
                            getTodayCourseNum()
                            )
                    //  headCardClickListener.setMode(com.stupidtree.hita.adapter.TimelineListAdapter.headCardClickListener.SHOW_NEXT)
                } else if (TimeInDay(currentTime) > TimeInDay(
                                12,
                                15
                        ) && TimeInDay(currentTime) < TimeInDay(13, 0)
                ) {
                    switchHeadView(head_image, R.drawable.ic_lunch)
                    titleToSet = mContext.getString(R.string.timeline_head_lunch_title)
                    subtitltToSet = mContext.getString(R.string.timeline_head_lunch_subtitle)
                } else if (TimeInDay(currentTime) > TimeInDay(
                                17,
                                10
                        ) && TimeInDay(currentTime) < TimeInDay(18, 10)
                ) {
                    switchHeadView(head_image, R.drawable.ic_lunch)
                    titleToSet = mContext.getString(R.string.timeline_head_dinner_title)
                    subtitltToSet = mContext.getString(R.string.timeline_head_dinner_subtitle)
                } else if (nextEvent != null) {
                    if (nextEvent!!.getFromTimeDistance() <= 15 && (nextEvent!!.type === EventItem.TYPE.CLASS || nextEvent!!.type == EventItem.TYPE.EXAM)) {
                        switchHeadView(head_goNow, -1)
                        titleToSet = nextEvent!!.name
                        subtitltToSet = java.lang.String.format(
                                mContext.getString(R.string.timeline_gonow_subtitle),
                                nextEvent!!.getFromTimeDistance()
                        )
                        head_goQuickly_classroom.text = nextEvent!!.place
                    } else {
                        titleToSet = mContext.getString(R.string.timeline_head_normal_title)
                        subtitltToSet = mContext.getString(R.string.timeline_head_normal_subtitle)
                        switchHeadView(head_image, R.drawable.ic_sunglasses)
                        //    headCardClickListener.setMode(com.stupidtree.hita.adapter.TimelineListAdapter.headCardClickListener.SHOW_NEXT)
                    }
                } else {
                    if (TimeInDay(currentTime) > TimeInDay(
                                    23,
                                    0
                            ) || TimeInDay(currentTime) < TimeInDay(5, 0)
                    ) {
                        switchHeadView(head_image, R.drawable.ic_moon)
                        titleToSet = mContext.getString(R.string.timeline_head_goodnight_title)
                        subtitltToSet =
                                mContext.getString(R.string.timeline_head_goodnight_subtitle)
                    } else {
                        switchHeadView(head_image, R.drawable.ic_finish)
                        titleToSet = mContext.getString(R.string.timeline_head_finish_title)
                        subtitltToSet = mContext.getString(R.string.timeline_head_finish_subtitle)
                    }
                    //  headCardClickListener.setMode(com.stupidtree.hita.adapter.TimelineListAdapter.headCardClickListener.SHOW_NEXT)
                }
            }
            if (nextEvent != null) {
                val text1 = java.lang.String.format(
                        mContext.getString(R.string.time_format_1),
                        nextEvent!!.getFromTimeDistance() / 60,
                        nextEvent!!.getFromTimeDistance() % 60
                )
                val text2 = java.lang.String.format(
                        mContext.getString(R.string.time_format_2),
                        nextEvent!!.getFromTimeDistance()
                )
                val timeText =
                        if (nextEvent!!.getFromTimeDistance() >= 60) text1 else text2
                head_counting_name.text = nextEvent!!.name
                head_counting_time.text =
                        timeText + mContext.getString(R.string.timeline_counting_middle)
                head_counting_image.setImageResource(R.drawable.ic_baseline_access_alarm_24)
                // head_counting_name.setVisibility(View.VISIBLE);
            } else {
                head_counting_name.text = "see you"
                head_counting_time.setText(R.string.timeline_counting_free)
                head_counting_image.setImageResource(R.drawable.ic_empty)
            }
            head_title.text = titleToSet
            head_subtitle.text = subtitltToSet
        }

        fun switchHeadView(view: View, imageId: Int) {
            for (head in heads) {
                if (head === view) head.visibility = View.VISIBLE else head.visibility = View.GONE
            }
            if (view is ImageView) view.setImageResource(imageId)
            collapseCard()
        }

        init {
            head_bg = v.findViewById(R.id.bg)
            head_counting_layout = v.findViewById(R.id.head_counting)
            head_expand = v.findViewById(R.id.head_expand)
            head_card = v.findViewById(R.id.timeline_head_card)
            circleProgress = v.findViewById(R.id.circle_progress)
            bt_bar_timetable = v.findViewById(R.id.bt_expand)
            bt_bar_addEvent = v.findViewById(R.id.bt_add)
            head_title = v.findViewById(R.id.timeline_titile)
            head_subtitle = v.findViewById(R.id.timeline_subtitle)
            head_image = v.findViewById(R.id.timeline_head_image)
            head_goNow = v.findViewById(R.id.timeline_head_gonow)
            head_card.setOnClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                toggleHeadExpand()
            }
            heads = arrayOf(head_image, head_goNow, circleProgress)
            head_counting_name = v.findViewById(R.id.tl_head_counting_name)
            head_counting_image = v.findViewById(R.id.tl_head_counting_image)
            head_counting_time = v.findViewById(R.id.tl_head_counting_time)
            head_goQuickly_classroom = v.findViewById(R.id.tl_head_gonow_classroom)
            bt_bar_timetable.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                toggleHeadExpand()
            }
            bt_bar_addEvent.setOnClickListener { v1: View? ->
            }
        }

    }


    companion object {
        private const val PASSED = 13
        private const val CLASS = 15
        private const val HINT = 959
        private const val HEADER = 690
        private const val EMPTY = 393
        private const val FOOT = 75
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, data: EventItem?, position: Int) {
        TODO("Not yet implemented")
    }

}