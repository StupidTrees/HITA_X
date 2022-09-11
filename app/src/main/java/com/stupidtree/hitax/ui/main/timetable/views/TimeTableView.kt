package com.stupidtree.hitax.ui.main.timetable.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.ui.main.timetable.TimetableStyleSheet
import com.stupidtree.hitax.ui.main.timetable.views.TimeTableBlockView.*
import com.stupidtree.hitax.utils.TimeTools
import java.util.*

class TimeTableView : ViewGroup {
    var mWidth = 0
    var mHeight = 0
    var sectionWidth = 0
    var sectionHeight = 180
    var timelineColor = 0
    var endTime = TimeInDay(24, 0)
    var addButton: TimeTableBlockAddView? = null
    lateinit var styleSheet: TimetableStyleSheet
    private val startDate: Calendar = Calendar.getInstance()
    private var onCardClickListener: OnCardClickListener? = null
    private var onAddClickListener:OnAddClickListener?=null

    private var onCardLongClickListener: OnCardLongClickListener? = null
    private val mPathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
    private val mLinePaint = Paint()
    var timetableStructure = Timetable().scheduleStructure


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        typedTimeTableView(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        typedTimeTableView(attrs, defStyleAttr)
    }

    fun setOnCardClickListener(onCardClickListener: OnCardClickListener?) {
        this.onCardClickListener = onCardClickListener
    }

    fun setOnAddClickListener(onAssClickListener: OnAddClickListener) {
        this.onAddClickListener = onAssClickListener
    }

    fun setOnCardLongClickListener(onCardLongClickListener: OnCardLongClickListener?) {
        this.onCardLongClickListener = onCardLongClickListener
    }

    constructor(context: Context?) : super(context) {}

    override fun dispatchDraw(canvas: Canvas) {
        if (TimeTools.isSameWeekWithStartDate(startDate, System.currentTimeMillis())) {
            drawTodayRect(canvas)
        }
        drawLabels(canvas)
        super.dispatchDraw(canvas)
    }

    private fun typedTimeTableView(attrs: AttributeSet, defStyleAttr: Int) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TimeTableViewGroup,
            defStyleAttr,
            0
        )
        val n = a.indexCount
        for (i in 0..n) {
            when (val attr = a.getIndex(i)) {
                R.styleable.TimeTableViewGroup_timeLineColor ->                     // 默认颜色设置为黑色
                    timelineColor = a.getColor(attr, Color.BLACK)
            }
        }
    }

    private fun drawTodayRect(canvas: Canvas) {
        val left: Int = sectionWidth * (TimeTools.currentDOW() - 1)
        val right = left + sectionWidth
        val paint = Paint()
        paint.color = styleSheet.todayBGColor
        canvas.drawRect(left.toFloat(), 0f, right.toFloat(), mHeight.toFloat(), paint)
    }


    private fun drawLabels(canvas: Canvas) {
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.strokeWidth = 1f
        mLinePaint.color = timelineColor
        mLinePaint.alpha = 50
        mLinePaint.pathEffect = mPathEffect
        val temp = TimeInDay(0, 0)
        for (i in styleSheet.getStartTimeObject().hour..23) {
            temp.hour = i
            val top = ((i - styleSheet.getStartTimeObject().hour) * sectionHeight)
            if (styleSheet.drawBGLine) {
                val mLinePath = Path()
                mLinePath.moveTo(0f, top.toFloat())
                mLinePath.lineTo(mWidth.toFloat(), (top + 1).toFloat())
                canvas.drawPath(mLinePath, mLinePaint)
            }
        }
    }


    /**
     * 更新视图
     */
    fun notifyRefresh(startDate: Long, events: List<EventItem>, styleSheet: TimetableStyleSheet) {
        this.styleSheet = styleSheet
        this.startDate.timeInMillis = startDate
        removeAllViewsInLayout()
        requestLayout()
        for (o in events) {
            addBlock(o)
        }
        invalidate()
        sectionHeight = this.styleSheet.cardHeight
    }

    /**
     * 仅更新startDate
     */
    fun setStartDate(ts: Long) {
        startDate.timeInMillis = ts
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            removeView(addButton)
            val dow = event.x.toInt() / sectionWidth + 1
            val st = styleSheet.getStartTimeObject()
            val time: TimeInDay = st.getAdded((event.y / sectionHeight * 60f).toInt())
            var period: TimePeriodInDay? = null
            for(i in timetableStructure.indices){
                if(i==0 && timetableStructure[i].after(time)){
                    period = TimePeriodInDay(styleSheet.getStartTimeObject(),timetableStructure[i].from)
                    break
                }else if (timetableStructure[i].contains(time)) {
                    period = timetableStructure[i].clone()
                    break
                }else if(i+1<timetableStructure.size && timetableStructure[i+1].after(time)){
                    period = TimePeriodInDay(timetableStructure[i].to,timetableStructure[i+1].from)
                    break
                }else if(i==timetableStructure.size-1 && timetableStructure[i].before(time)){
                    period = TimePeriodInDay(timetableStructure[i].to,TimeInDay(23,59))
                    break
                }
            }
            if (period != null) {
                if (period.from.before(st)) period.from = st.getAdded(0)
                addButton = TimeTableBlockAddView(context, period, dow)

                addView(addButton)
                addButton?.onAddClickListener = object: TimeTableBlockAddView.OnAddClickListener {

                    override fun onClick(view: View) {
                        onAddClickListener?.onAddClick(dow,period)
                    }
                }
            }
        } else {
            removeView(addButton)
        }
        return super.onTouchEvent(event)
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val totalMinutes: Int =
            (endTime.hour - styleSheet.startTime / 100) * 60 + endTime.minute - styleSheet!!.startTime % 100
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.makeMeasureSpec(
                (totalMinutes.toFloat() / 60f * sectionHeight).toInt(),
                MeasureSpec.EXACTLY
            )
        )
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.makeMeasureSpec(
            (totalMinutes.toFloat() / 60f * sectionHeight).toInt(),
            MeasureSpec.EXACTLY
        )
        sectionWidth = (mWidth) / 7
        for (i in 0 until childCount) {
            when (val child = getChildAt(i)) {
                is TimeTableBlockView -> {
                    val cw = MeasureSpec.makeMeasureSpec(sectionWidth, MeasureSpec.EXACTLY)
                    val cH: Int =
                        MeasureSpec.makeMeasureSpec(getCardHeight(child), MeasureSpec.EXACTLY)
                    child.measure(cw, cH)
                }
                is TimeTableNowLine -> {
                    val cw = MeasureSpec.makeMeasureSpec(sectionWidth, MeasureSpec.EXACTLY)
                    val cH = MeasureSpec.makeMeasureSpec(4, MeasureSpec.EXACTLY)
                    child.measure(cw, cH)
                }
                is TimeTableBlockAddView -> {
                    val cw = MeasureSpec.makeMeasureSpec(sectionWidth, MeasureSpec.EXACTLY)
                    val cH = MeasureSpec.makeMeasureSpec(
                        (child.duration / 60f * sectionHeight).toInt(),
                        MeasureSpec.EXACTLY
                    )
                    child.measure(cw, cH)
                }
                else -> {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec)
                }
            }

            //
        }
    }

    fun init() {
        this.styleSheet = TimetableStyleSheet()
        sectionHeight = styleSheet.cardHeight
        isClickable = true //设置为可点击，否则onTouchEvent只返回DOWN
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount //获得子控件个数

        for (i in 0 until count) {
            when (val child = getChildAt(i)) {
                is TimeTableBlockView -> {
                    val lastTime = child.getDuration().toFloat()
                    val startTimeFromBeginning: Int =
                        styleSheet.getStartTimeObject()
                            .getDistanceInMinutes(child.getEvent().from.time)
                    if(startTimeFromBeginning==0){
                        println("ZERO!")
                    }
                    //计算左边的坐标
                    val left = sectionWidth * (child.getDow() - 1)
                    //计算右边坐标
                    val right = left + sectionWidth
                    //计算顶部坐标
                    val top = (startTimeFromBeginning / 60f * sectionHeight).toInt()
                    val bottom = top + (lastTime / 60f * sectionHeight).toInt()
                    // block.measure(sectionWidth,getCardHeight(block));
                    child.layout(left, top, right, bottom)
                }
                is TimeTableNowLine -> {
                    val startTimeFromBeginning =
                        styleSheet.getStartTimeObject()
                            .getDistanceInMinutes(System.currentTimeMillis())
                    val top = (startTimeFromBeginning / 60f * sectionHeight).toInt()
                    child.layout(0, top, mWidth, top + 4)
                }
                is TimeTableBlockAddView -> {
                    val left: Int = sectionWidth * (child.dow - 1)
                    val right = left + sectionWidth
                    val startTimeFromBeginning: Int = styleSheet.getStartTimeObject()
                        .getDistanceInMinutes(child.timePeriod.from)
                    val top = (startTimeFromBeginning / 60f * sectionHeight).toInt()
                    val bottom = top + (child.duration / 60f * sectionHeight).toInt()
                    //Log.e("pos",top+","+bottom+","+left+","+right);
                    child.layout(left, top, right, bottom)
                }
            }


            // Log.e("mes:",top+","+bottom+","+left+","+right);
        }
    }

    private fun addBlock(o: Any) {
        if (o is EventItem) {
            val timeTableBlockView = TimeTableBlockView(context, o, styleSheet)
            timeTableBlockView.onCardClickListener =
                object : TimeTableBlockView.OnCardClickListener {
                    override fun onClick(v: View, ei: EventItem) {
                        onCardClickListener?.onEventClick(v, ei)
                    }
                }

            timeTableBlockView.onCardLongClickListener =
                object : TimeTableBlockView.OnCardLongClickListener {
                    override fun onLongClick(v: View, ei: EventItem): Boolean {
                        return onCardLongClickListener?.onEventLongClick(v, ei) == true
                    }
                }
            addView(timeTableBlockView)
        } else if (o is List<*>) {
            val timeTableBlockView = TimeTableBlockView(context, o, styleSheet)
            timeTableBlockView.onDuplicateCardClickListener =
                object : OnDuplicateCardClickListener {
                    override fun onDuplicateClick(v: View, list: List<EventItem>) {
                        onCardClickListener?.onDuplicateEventClick(v, list)
                    }
                }
            timeTableBlockView.onDuplicateCardLongClickListener =
                object : OnDuplicateCardLongClickListener {
                    override fun onDuplicateLongClick(v: View, list: List<EventItem>): Boolean {
                        return onCardLongClickListener?.onDuplicateEventClick(v, list) == true
                    }
                }
            addView(timeTableBlockView)
        }
    }

    interface OnCardClickListener {
        fun onEventClick(v: View, eventItem: EventItem)
        fun onDuplicateEventClick(v: View, eventItems: List<EventItem>)
    }

    private fun getCardHeight(timeTableBlockView: TimeTableBlockView): Int {
        return (timeTableBlockView.getDuration() / 60f * sectionHeight).toInt()
    }

    interface OnCardLongClickListener {
        fun onEventLongClick(v: View, eventItem: EventItem): Boolean
        fun onDuplicateEventClick(v: View, eventItems: List<EventItem>): Boolean
    }

    interface OnAddClickListener{
        fun onAddClick(dow:Int,period:TimePeriodInDay)
    }
}