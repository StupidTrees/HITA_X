//package com.stupidtree.hita.ui.timetable.views;
//
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//
//import androidx.annotation.NonNull;
//
//import com.stupidtree.hita.R;
//import com.stupidtree.hita.activities.BaseActivity;
//import com.stupidtree.hita.fragments.popup.FragmentAddEvent;
//import com.stupidtree.hita.timetable.packable.TimePeriod;
//
//public class TimeTableBlockAddView extends FrameLayout {
//    View card;
//    View add;
//    int dow;
//    int week;
//    TimePeriod timePeriod;
//
//    public TimeTableBlockAddView(@NonNull final BaseActivity context, final int week, final int dow, final TimePeriod timePeriod) {
//        super(context);
//        this.dow = dow;
//        this.week = week;
//        this.timePeriod = timePeriod;
//        inflate(context, R.layout.dynamic_timetable_block_add,this);
//        add = findViewById(R.id.add);
//        card = findViewById(R.id.card);
//        add.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new FragmentAddEvent().setInitialData(week,dow,timePeriod).show(context.getSupportFragmentManager(),"fae");
//                ViewGroup parent = (ViewGroup) getParent();
//                if(parent!=null) parent.removeView(com.stupidtree.hita.views.TimeTableBlockAddView.this);
//            }
//        });
//        card.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                add.callOnClick();
//            }
//        });
//    }
//
//    public int getDow() {
//        return dow;
//    }
//
//    public TimePeriod getTimePeriod() {
//        return timePeriod;
//    }
//
//    public int getDuration(){
//        return timePeriod==null?0:timePeriod.getLength();
//    }
//}
