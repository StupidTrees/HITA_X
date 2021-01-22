//package com.stupidtree.hita.ui.timetable.views;
//
//import android.content.Context;
//import android.content.Intent;
//
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import androidx.preference.Preference;
//
//import static com.stupidtree.hita.activities.ActivityTimeTable.TIMETABLE_REFRESH;
//
//public class TimeTablePreferenceChangeListener implements Preference.OnPreferenceChangeListener{
//    Context context;
//    ChangeAction change;
//
//    public TimeTablePreferenceChangeListener(Context context, ChangeAction change) {
//        this.context = context;
//        this.change = change;
//    }
//
//    public TimeTablePreferenceChangeListener(Context context) {
//        this.context = context;
//    }
//
//    public interface ChangeAction{
//        boolean OnChanged(Preference preference,Object newValue);
//    }
//
//    @Override
//    public boolean onPreferenceChange(Preference preference, Object newValue) {
//        callTimeTableToRefresh();
//        if(change!=null) return change.OnChanged(preference,newValue);
//        return true;
//    }
//    private void callTimeTableToRefresh(){
//        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(TIMETABLE_REFRESH));
//    }
//}
