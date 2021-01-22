//package com.stupidtree.hita.ui.timetable.views;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.DashPathEffect;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.util.AttributeSet;
//import android.util.TypedValue;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.AnimationUtils;
//import android.view.animation.LayoutAnimationController;
//import java.util.List;
//
//import static com.stupidtree.hita.HITAApplication.HContext;
//import static com.stupidtree.hita.timetable.TimetableCore.DDL;
//
//public class TimeTableViewGroup extends ViewGroup {
//    int week;
//    int width, height;
//    int sectionWidth, sectionHeight = 180;
//    int labelWidth;
//    int labelSize;
//    int labelColor;
//    int timelineColor;
//    boolean firstLoad = true;
//
//    HTime startTime = new HTime(0, 0);
//    BaseActivity activityContext;
//    TimeTableBlockAddView addButton = null;
//    TimeTableBlockView.TimeTablePreferenceRoot root;
//
//    private OnCardClickListener onCardClickListener;
//    private OnCardLongClickListener onCardLongClickListener;
//
//
//    public TimeTableViewGroup(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        typedTimeTableView(attrs, 0);
//    }
//
//    public TimeTableViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        typedTimeTableView(attrs, defStyleAttr);
//    }
//
//    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
//        this.onCardClickListener = onCardClickListener;
//    }
//
//    public void setOnCardLongClickListener(OnCardLongClickListener onCardLongClickListener) {
//        this.onCardLongClickListener = onCardLongClickListener;
//    }
//
//    public TimeTableViewGroup(Context context) {
//        super(context);
//    }
//
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        try {
//            if (week == TimetableCore.getInstance(HContext).getThisWeekOfTerm())
//                drawTodayRect(canvas);
//            drawLabels(canvas);
//            super.dispatchDraw(canvas);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void typedTimeTableView(AttributeSet attrs, int defStyleAttr) {
//        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TimeTableViewGroup, defStyleAttr, 0);
//        int n = a.getIndexCount();
//        for (int i = 0; i <= n; i++) {
//            int attr = a.getIndex(i);
//            switch (attr) {
//                //注意获取属性的方式为 styleable的名称_属性名称
//                case R.styleable.TimeTableViewGroup_timeLabelSize:
//                    labelSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
//                            TypedValue.COMPLEX_UNIT_SP, 8, getResources().getDisplayMetrics()));
//                    break;
//                case R.styleable.TimeTableViewGroup_timeLabelColor:
//                    labelColor = a.getColor(attr, Color.BLACK);
//                    break;
//                case R.styleable.TimeTableViewGroup_timeLineColor:
//                    // 默认颜色设置为黑色
//                    timelineColor = a.getColor(attr, Color.BLACK);
//                    break;
//                case R.styleable.TimeTableViewGroup_timeLabelWidth:
//                    labelWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
//                            TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
//                    break;
//
//
//            }
//        }
//
//    }
//
//    private void drawTodayRect(Canvas canvas) {
//        int left = labelWidth + sectionWidth * (TimetableCore.getDOW(TimetableCore.getNow()) - 1);
//        int right = (left + sectionWidth);
//        Paint paint = new Paint();
//        paint.setColor(root.getTodayBGColor());
//        canvas.drawRect(left, 0, right, height, paint);
//    }
//
//    private void drawLabels(Canvas canvas) {
//        Paint paint = new Paint();
//        paint.setColor(labelColor);
//        paint.setTextSize(labelSize);
//        paint.setTextAlign(Paint.Align.LEFT);
//
//
//        DashPathEffect pathEffect = new DashPathEffect(new float[]{20f, 20f}, 0f);
//        Paint paint2 = new Paint();
//        paint2.setStyle(Paint.Style.STROKE);
//        paint2.setStrokeWidth(1);
//        paint2.setColor(timelineColor);
//        paint2.setPathEffect(pathEffect);
//
//
//        for (int i = startTime.hour; i <= 23; i++) {
//            HTime temp = new HTime(i, 0);
//            int left = 0;
//            //int right = (left + labelWidth);
//            float x = startTime.before(temp) ? 1.0f : -1.0f;
//            float startTimeFromBeginning = x * startTime.getDuration(temp);
//            int top = (int) ((startTimeFromBeginning / 60f) * sectionHeight);
//            //nt bottom = top + sectionHeight;
//            canvas.drawText(temp.tellTime(), left, top + labelSize, paint);
//            if (root.drawBGLine()) {
//                Path path = new Path();
//                path.moveTo(0, top);
//                path.lineTo(width, top + 1);
//                canvas.drawPath(path, paint2);
//            }
//
//            // label.layout(left, top, right, bottom);
//        }
//
//    }
//
//    public void notifyRefresh() {
//        this.sectionHeight = root.getCardHeight();
//        this.startTime = root.getStartTime();
//        if (root.animEnabled() && firstLoad) {
//            firstLoad = false;
//            setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(
//                    getContext(), R.anim.recycler_animation_float_up), 0.08f));
//        } else {
//            setLayoutAnimation(null);
//        }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //
//        // Log.e("action", String.valueOf(event.getAction()));
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            removeView(addButton);
//            int dow = (int) event.getX() / sectionWidth + 1;
//            HTime time = startTime.getAdded((int) (event.getY() / sectionHeight * 60f));
//            TimePeriod period = TimetableCore.getClassSimplifiedTimeByTimeContainedIn(time);
//            if (period != null) {
//                if (period.start.before(startTime)) period.setStart(startTime);
//                addButton = new TimeTableBlockAddView(activityContext, week, dow,
//                        period
//                );
//                addView(addButton);
//
//            }
//        } else {
//            removeView(addButton);
//        }
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        //Log.e("width,height",widthMeasureSpec+","+heightMeasureSpec);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        @SuppressLint("DrawAllocation") int totalMinutes = root.getStartTime().getDuration(new HTime(23, 59));
//        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.makeMeasureSpec((int) ((float) totalMinutes / 60f * sectionHeight), MeasureSpec.EXACTLY));
//        width = MeasureSpec.getSize(widthMeasureSpec);
//        height = MeasureSpec.makeMeasureSpec((int) ((float) totalMinutes / 60f * sectionHeight), MeasureSpec.EXACTLY);
//        sectionWidth = (width - labelWidth) / 7;
//        //setMeasuredDimension(width, height);
//        final int count = getChildCount();
//        for (int i = 0; i < count; i++) {
//            View child = getChildAt(i);
//            if (child instanceof TimeTableBlockView) {
//                int cw = MeasureSpec.makeMeasureSpec(sectionWidth, MeasureSpec.EXACTLY);
//                int cH;
//                EventItem eventItem = ((TimeTableBlockView) child).getEvent();
//                if (eventItem.eventType != DDL) {
//                    cH = MeasureSpec.makeMeasureSpec(getCardHeight((TimeTableBlockView) child), MeasureSpec.EXACTLY);
//                } else {
//                    cH = MeasureSpec.makeMeasureSpec((int) ((18 / 60f) * sectionHeight), MeasureSpec.EXACTLY);
//                }
//                child.measure(cw, cH);
//            } else if (child instanceof TimeTableNowLine) {
//                int cw = MeasureSpec.makeMeasureSpec(sectionWidth, MeasureSpec.EXACTLY);
//                int cH = MeasureSpec.makeMeasureSpec(4, MeasureSpec.EXACTLY);
//                child.measure(cw, cH);
//            } else if (child instanceof TimeTableBlockAddView) {
//                int cw = MeasureSpec.makeMeasureSpec(sectionWidth, MeasureSpec.EXACTLY);
//                int cH = MeasureSpec.makeMeasureSpec((int) ((((TimeTableBlockAddView) child).getDuration() / 60f) * sectionHeight), MeasureSpec.EXACTLY);
//                child.measure(cw, cH);
//            } else {
//                this.measureChild(child, widthMeasureSpec, heightMeasureSpec);
//            }
//
//            //
//        }
//    }
//
//    public void init(BaseActivity activity, int week, TimeTableBlockView.TimeTablePreferenceRoot root) {
//        this.week = week;
//        this.activityContext = activity;
//        this.root = root;
//        this.sectionHeight = root.getCardHeight();
//        this.startTime = root.getStartTime();
//        setClickable(true); //设置为可点击，否则onTouchEvent只返回DOWN
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        int count = getChildCount();//获得子控件个数
//        for (int i = 0; i < count; i++) {
//            View child = getChildAt(i);
//            if (child instanceof TimeTableBlockView) {
//                TimeTableBlockView block = (TimeTableBlockView) child;
//                float lastTime = block.getDuration();
//                float x = startTime.before(block.getEvent().startTime) ? 1.0f : -1.0f;
//                float startTimeFromBeginning = x * startTime.getDuration(block.getEvent().startTime);
//                int courseInWeek = block.getDow() - 1;//获得周几
//                //计算左边的坐标
//                int left = labelWidth + (sectionWidth * courseInWeek);
//                //计算右边坐标
//                int right = (left + sectionWidth);
//                //计算顶部坐标
//                int top = (int) ((startTimeFromBeginning / 60f) * sectionHeight);
//                //计算底部坐标
//                if (((TimeTableBlockView) child).getEvent().eventType == DDL) {
//                    lastTime = 18;
//                }
//                int bottom = top + (int) ((lastTime / 60f) * sectionHeight);
//                // block.measure(sectionWidth,getCardHeight(block));
//                block.layout(left, top, right, bottom);
//            } else if (child instanceof TimeTableNowLine) {
//                @SuppressLint("DrawAllocation") float startTimeFromBeginning = startTime.getDuration(new HTime(TimetableCore.getNow()));
//                int top = (int) ((startTimeFromBeginning / 60f) * sectionHeight);
//                child.layout(0, top, width, top + 4);
//            } else if (child instanceof TimeTableBlockAddView) {
//                TimeTableBlockAddView add = (TimeTableBlockAddView) child;
//                int left = sectionWidth * (add.getDow() - 1) + labelWidth;
//                int right = (left + sectionWidth);
//                float startTimeFromBeginning = startTime.getDuration(add.getTimePeriod().start);
//                int top = (int) ((startTimeFromBeginning / 60f) * sectionHeight);
//                int bottom = top + (int) ((add.getDuration() / 60f) * sectionHeight);
//                //Log.e("pos",top+","+bottom+","+left+","+right);
//                add.layout(left, top, right, bottom);
//            }
//
//
//            // Log.e("mes:",top+","+bottom+","+left+","+right);
//        }
//    }
//
//    public void addBlock(Object o) {
//        if (o instanceof EventItem) {
//
//            TimeTableBlockView timeTableBlockView = new TimeTableBlockView(getContext(), o, root);
//            if (onCardClickListener != null)
//                timeTableBlockView.setOnCardClickListener(new TimeTableBlockView.OnCardClickListener() {
//                @Override
//                public void OnClick(View v, EventItem ei) {
//                    onCardClickListener.onEventClick(v, ei);
//                }
//            });
//            if (onCardLongClickListener != null)
//                timeTableBlockView.setOnCardLongClickListener(new TimeTableBlockView.OnCardLongClickListener() {
//                @Override
//                public boolean OnLongClick(final View v, final EventItem ei) {
//                    return onCardLongClickListener.onEventLongClick(v, ei);
//                }
//            });
//            addView(timeTableBlockView);
//        } else if (o instanceof List) {
//            TimeTableBlockView timeTableBlockView = new TimeTableBlockView(getContext(), o, root);
//            if (onCardClickListener != null)
//                timeTableBlockView.setOnDuplicateCardClickListener(new TimeTableBlockView.OnDuplicateCardClickListener() {
//                @Override
//                public void OnDuplicateClick(View v, final List<EventItem> list) {
//                    onCardClickListener.onDuplicateEventClick(v, list);
//                }
//                });
//            if (onCardLongClickListener != null)
//                timeTableBlockView.setOnDuplicateCardLongClickListener(new TimeTableBlockView.OnDuplicateCardLongClickListener() {
//                    @Override
//                    public boolean OnDuplicateLongClick(View v, List<EventItem> list) {
//                        return onCardLongClickListener.onDuplicateEventClick(v, list);
//                }
//            });
//            addView(timeTableBlockView);
//        }
//
//    }
//
//    public interface OnCardClickListener {
//        void onEventClick(View v, EventItem eventItem);
//
//        void onDuplicateEventClick(View v, List<EventItem> eventItems);
//    }
//
//
//    private int getCardHeight(TimeTableBlockView timeTableBlockView) {
//        return (int) ((timeTableBlockView.getDuration() / 60f) * sectionHeight);
//    }
//
//
//    public interface OnCardLongClickListener {
//        boolean onEventLongClick(View v, EventItem eventItem);
//
//        boolean onDuplicateEventClick(View v, List<EventItem> eventItems);
//    }
//
//
//}
