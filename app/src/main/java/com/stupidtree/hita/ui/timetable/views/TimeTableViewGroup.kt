package com.stupidtree.hita.ui.timetable.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.model.timetable.TimeInDay
import com.stupidtree.hita.ui.timetable.views.TimeTableBlockView.*
import com.stupidtree.hita.utils.TimeUtils

class TimeTableViewGroup : ViewGroup {
    var mWidth = 0
    var mHeight = 0
    var sectionWidth = 0
    var sectionHeight = 180
    var labelWidth = 0
    var labelSize = 0
    var labelColor = 0
    var timelineColor = 0
    var firstLoad = true
    var startTime: TimeInDay = TimeInDay(0, 0)
    var endTime = TimeInDay(24, 0)
    var addButton: TimeTableBlockAddView? = null
    var root: TimeTablePreferenceRoot? = null
    private var onCardClickListener: OnCardClickListener? = null
    private var onCardLongClickListener: OnCardLongClickListener? = null
    private val mLabelPaint = Paint()
    private val mPathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
    private val mLinePaint = Paint()
    private val mLinePath = Path()

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        typedTimeTableView(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        typedTimeTableView(attrs, defStyleAttr)
    }

    fun setOnCardClickListener(onCardClickListener: OnCardClickListener?) {
        this.onCardClickListener = onCardClickListener
    }

    fun setOnCardLongClickListener(onCardLongClickListener: OnCardLongClickListener?) {
        this.onCardLongClickListener = onCardLongClickListener
    }

    constructor(context: Context?) : super(context) {}

    override fun dispatchDraw(canvas: Canvas) {
//            if (week == TimetableCore.getInstance(HContext).getThisWeekOfTerm())
//                drawTodayRect(canvas);
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
                R.styleable.TimeTableViewGroup_timeLabelSize -> labelSize = a.getDimensionPixelSize(
                    attr, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 8f, resources.displayMetrics
                    ).toInt()
                )
                R.styleable.TimeTableViewGroup_timeLabelColor -> labelColor =
                    a.getColor(attr, Color.BLACK)
                R.styleable.TimeTableViewGroup_timeLineColor ->                     // 默认颜色设置为黑色
                    timelineColor = a.getColor(attr, Color.BLACK)
                R.styleable.TimeTableViewGroup_timeLabelWidth -> labelWidth =
                    a.getDimensionPixelSize(
                        attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics
                        ).toInt()
                    )
            }
        }
    }

    private fun drawTodayRect(canvas: Canvas) {
        val left: Int = labelWidth + sectionWidth * (TimeUtils.currentDOW() - 1)
        val right = left + sectionWidth
        val paint = Paint()
        paint.color = root!!.todayBGColor
        canvas.drawRect(left.toFloat(), 0f, right.toFloat(), mHeight.toFloat(), paint)
    }


    private fun drawLabels(canvas: Canvas) {
        mLabelPaint.color = labelColor
        mLabelPaint.textSize = labelSize.toFloat()
        mLabelPaint.textAlign = Paint.Align.LEFT
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.strokeWidth = 1f
        mLinePaint.color = timelineColor
        mLinePaint.pathEffect = mPathEffect
        for (i in startTime.hour..23) {
            val temp = TimeInDay(i, 0)
            val left = 0
            val x = if (startTime.before(temp)) 1.0f else -1.0f
            val startTimeFromBeginning: Float = x * startTime.getDistanceInMinutes(temp)
            val top = (startTimeFromBeginning / 60f * sectionHeight).toInt()
            canvas.drawText(temp.toString(), left.toFloat(), (top + labelSize).toFloat(),mLabelPaint)
            if (root!!.drawBGLine()) {
                mLinePath.moveTo(0f, top.toFloat())
                mLinePath.lineTo(mWidth.toFloat(), (top + 1).toFloat())
                canvas.drawPath(mLinePath, mLinePaint)
            }
        }
    }

    fun notifyRefresh() {
        sectionHeight = root!!.cardHeight
        startTime = root!!.startTime
        if (root!!.animEnabled() && firstLoad) {
            firstLoad = false
            layoutAnimation = LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    context, R.anim.recycler_animation_float_up
                ), 0.08f
            )
        } else {
            layoutAnimation = null
        }
    }

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        if (event.action == MotionEvent.ACTION_UP) {
//            removeView(addButton)
//            val dow = event.x.toInt() / sectionWidth + 1
//            val time: TimeInDay = startTime.getAdded((event.y / sectionHeight * 60f).toInt())
//            val period: TimePeriod = TimetableCore.getClassSimplifiedTimeByTimeContainedIn(time)
//            if (period != null) {
//                if (period.start.before(startTime)) period.setStart(startTime)
//                addButton = TimeTableBlockAddView(activityContext!!, week, dow,
//                        period
//                )
//                addView(addButton)
//            }
//        } else {
//            removeView(addButton)
//        }
//        return super.onTouchEvent(event)
//    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val totalMinutes: Int = root!!.startTime.getDistanceInMinutes(endTime)
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
        sectionWidth = (mWidth - labelWidth) / 7
        //setMeasuredDimension(width, height);
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

    fun init(root: TimeTablePreferenceRoot) {
        this.root = root
        sectionHeight = root.cardHeight
        startTime = root.startTime
        isClickable = true //设置为可点击，否则onTouchEvent只返回DOWN
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount //获得子控件个数
        for (i in 0 until count) {
            when (val child = getChildAt(i)) {
                is TimeTableBlockView -> {
                    val lastTime = child.getDuration().toFloat()
                    val startTimeFromBeginning: Int =
                        startTime.getDistanceInMinutes(child.getEvent().from.time)
                    val courseInWeek = child.getDow() - 1 //获得周几
                    //计算左边的坐标
                    val left = labelWidth + sectionWidth * courseInWeek
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
                        startTime.getDistanceInMinutes(System.currentTimeMillis())
                    val top = (startTimeFromBeginning / 60f * sectionHeight).toInt()
                    child.layout(0, top, mWidth, top + 4)
                }
//                is TimeTableBlockAddView -> {
//                    val left: Int = sectionWidth * (child.getDow() - 1) + labelWidth
//                    val right = left + sectionWidth
//                    val startTimeFromBeginning: Float = startTime.getDuration(child.timePeriod.start)
//                    val top = (startTimeFromBeginning / 60f * sectionHeight).toInt()
//                    val bottom = top + (child.duration / 60f * sectionHeight).toInt()
//                    //Log.e("pos",top+","+bottom+","+left+","+right);
//                    child.layout(left, top, right, bottom)
//                }
            }


            // Log.e("mes:",top+","+bottom+","+left+","+right);
        }
    }

    fun addBlock(o: Any) {
        if (o is EventItem) {
            val timeTableBlockView = TimeTableBlockView(context, o, root!!)
            if (onCardClickListener != null) timeTableBlockView.onCardClickListener =
                object : TimeTableBlockView.OnCardClickListener {
                    override fun OnClick(v: View, ei: EventItem) {
                        onCardClickListener!!.onEventClick(v, ei)
                    }
                }
            if (onCardLongClickListener != null) timeTableBlockView.onCardLongClickListener =
                object : TimeTableBlockView.OnCardLongClickListener {
                    override fun OnLongClick(v: View, ei: EventItem): Boolean {
                        return onCardLongClickListener!!.onEventLongClick(v, ei)
                    }
                }
            addView(timeTableBlockView)
        } else if (o is List<*>) {
            val timeTableBlockView = TimeTableBlockView(context, o, root!!)
            if (onCardClickListener != null) timeTableBlockView.onDuplicateCardClickListener =
                object : OnDuplicateCardClickListener {
                    override fun OnDuplicateClick(v: View, list: List<EventItem>) {
                        onCardClickListener!!.onDuplicateEventClick(v, list)
                    }
                }
            if (onCardLongClickListener != null) timeTableBlockView.onDuplicateCardLongClickListener =
                object : OnDuplicateCardLongClickListener {
                    override fun onDuplicateLongClick(v: View, list: List<EventItem>): Boolean {
                        return onCardLongClickListener!!.onDuplicateEventClick(v, list)
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
}