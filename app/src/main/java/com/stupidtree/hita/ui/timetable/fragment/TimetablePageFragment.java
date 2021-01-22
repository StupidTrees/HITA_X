//package com.stupidtree.hita.ui.timetable.fragment;
//
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.PopupMenu;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import com.stupidtree.hita.HITAApplication;
//import com.stupidtree.hita.R;
//import com.stupidtree.hita.timetable.TimetableCore;
//import com.stupidtree.hita.timetable.packable.EventItem;
//import com.stupidtree.hita.timetable.packable.HTime;
//import com.stupidtree.hita.util.EventsUtils;
//import com.stupidtree.hita.views.TimeTableBlockView;
//import com.stupidtree.hita.views.TimeTableNowLine;
//import com.stupidtree.hita.views.TimeTableViewGroup;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.List;
//
//import tyrantgit.explosionfield.ExplosionField;
//
//import static com.stupidtree.hita.HITAApplication.HContext;
//import static com.stupidtree.hita.timetable.TimeWatcherService.TIMETABLE_CHANGED;
//import static com.stupidtree.hita.timetable.TimetableCore.COURSE;
//
//
//public class TimetablePageFragment extends BaseFragment implements BaseOperationTask.OperationListener<List> {
//
//    private boolean willRefreshOnResume = false;
//    private int pageWeek;
//    private TimeTableBlockView.TimeTablePreferenceRoot root;
//    private TimeTableViewGroup timeTableView;
//    private TextView[] topDateTexts = new TextView[8]; //顶部日期文本
//    private TextView[] tWholeDays = new TextView[7];
//    private LinearLayout allDayLayout;
//    private refreshPageTask pageTask;
//
//    public TimetablePageFragment() {
//        // Required empty public constructor
//    }
//
//    public static com.stupidtree.hita.fragments.FragmentTimeTablePage newInstance(int week) {
//        Bundle b = new Bundle();
//        b.putInt("week", week);
//        com.stupidtree.hita.fragments.FragmentTimeTablePage f = new com.stupidtree.hita.fragments.FragmentTimeTablePage();
//        f.setArguments(b);
//        return f;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_timetable_page;
//
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        if (getArguments() != null) pageWeek = getArguments().getInt("week");
//        super.onViewCreated(view, savedInstanceState);
//        initDateTextViews(view);
//        initWholeDayTextViews(view);
//        RefreshPageView(pageWeek);
//        timeTableView = view.findViewById(R.id.timetableview);
//        timeTableView.init(getBaseActivity(), pageWeek, root);
//        timeTableView.setOnCardClickListener(new TimeTableViewGroup.OnCardClickListener() {
//            @Override
//            public void onEventClick(View v, EventItem eventItem) {
//                EventsUtils.showEventItem(requireContext(), eventItem);
//            }
//
//            @Override
//            public void onDuplicateEventClick(View v, List<EventItem> eventItems) {
//                EventsUtils.showEventItem(requireContext(), eventItems);
//            }
//        });
//        timeTableView.setOnCardLongClickListener(new TimeTableViewGroup.OnCardLongClickListener() {
//            @Override
//            public boolean onEventLongClick(final View v, final EventItem ei) {
//                PopupMenu pm = new PopupMenu(requireContext(), v);
//                pm.inflate(R.menu.menu_opr_timetable);
//                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        if (item.getItemId() == R.id.opr_delete) {
//                            AlertDialog ad = new AlertDialog.Builder(requireContext()).
//                                    setNegativeButton(R.string.button_cancel, null)
//                                    .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            ExplosionField ef = ExplosionField.attach2Window(requireActivity());
//                                            ef.explode(v);
//                                            new deleteEventsTask(com.stupidtree.hita.fragments.FragmentTimeTablePage.this, ei).execute();
//                                        }
//                                    }).create();
//                            ad.setTitle(R.string.dialog_title_sure_delete);
//                            if (ei.eventType == COURSE) {
//                                ad.setMessage("删除课程后,可以通过导入课表或同步云端数据恢复初始课表");
//                            }
//
//                            ad.show();
//                        }
//                        return true;
//                    }
//                });
//                pm.show();
//                return true;
//            }
//
//            @Override
//            public boolean onDuplicateEventClick(final View v, final List<EventItem> eventItems) {
//                PopupMenu pm = new PopupMenu(requireContext(), v);
//                pm.inflate(R.menu.menu_opr_timetable);
//                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        if (item.getItemId() == R.id.opr_delete) {
//                            AlertDialog ad = new AlertDialog.Builder(requireContext()).
//                                    setNegativeButton(R.string.button_cancel, null)
//                                    .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            ExplosionField ef = ExplosionField.attach2Window(requireActivity());
//                                            ef.explode(v);
//                                            new deleteEventsTask(com.stupidtree.hita.fragments.FragmentTimeTablePage.this, eventItems).execute();
//                                        }
//                                    }).create();
//                            ad.setTitle(R.string.dialog_title_sure_delete);
//                            if (eventItems.size() > 0 && eventItems.get(0).getEventType() == COURSE) {
//                                ad.setMessage("删除课程后,可以通过导入课表或同步云端数据恢复初始课表");
//                            }
//
//                            ad.show();
//                        }
//                        return true;
//                    }
//                });
//                pm.show();
//                return true;
//            }
//        });
//    }
//
//
//    private void initDateTextViews(View v) {
//        topDateTexts[0] = v.findViewById(R.id.tt_tv_month);
//        topDateTexts[1] = v.findViewById(R.id.tt_tv_day1);
//        topDateTexts[2] = v.findViewById(R.id.tt_tv_day2);
//        topDateTexts[3] = v.findViewById(R.id.tt_tv_day3);
//        topDateTexts[4] = v.findViewById(R.id.tt_tv_day4);
//        topDateTexts[5] = v.findViewById(R.id.tt_tv_day5);
//        topDateTexts[6] = v.findViewById(R.id.tt_tv_day6);
//        topDateTexts[7] = v.findViewById(R.id.tt_tv_day7);
//    }
//
//    private void initWholeDayTextViews(View v) {
//        tWholeDays[0] = v.findViewById(R.id.tt_wholeday_text_1);
//        tWholeDays[1] = v.findViewById(R.id.tt_wholeday_text_2);
//        tWholeDays[2] = v.findViewById(R.id.tt_wholeday_text_3);
//        tWholeDays[3] = v.findViewById(R.id.tt_wholeday_text_4);
//        tWholeDays[4] = v.findViewById(R.id.tt_wholeday_text_5);
//        tWholeDays[5] = v.findViewById(R.id.tt_wholeday_text_6);
//        tWholeDays[6] = v.findViewById(R.id.tt_wholeday_text_7);
//        allDayLayout = v.findViewById(R.id.wholeday_layout);
//    }
//
//
//    @SuppressLint("SetTextI18n")
//    private void refreshDateViews() {
//
//        try {
//            /*显示上方日期*/
//            topDateTexts[0].setText(HContext.getResources().getStringArray(R.array.months)[TimetableCore.getInstance(HContext).getCurrentCurriculum().getFirstDateAtWOT(pageWeek).get(Calendar.MONTH)]);
//            Calendar firstDateTemp = TimetableCore.getInstance(HContext).getCurrentCurriculum().getFirstDateAtWOT(pageWeek);
//            Calendar temp = Calendar.getInstance();
//            for (int k = 1; k <= 7; k++) {
//                temp.setTime(firstDateTemp.getTime());
//                temp.add(Calendar.DATE, k - 1);
//                topDateTexts[k].setText(temp.get(Calendar.DAY_OF_MONTH) + "");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void RefreshPageView(int week) {
//        if (pageTask != null && pageTask.getStatus() != AsyncTask.Status.FINISHED)
//            pageTask.cancel(true);
//        pageTask = new refreshPageTask(this, week);
//        pageTask.executeOnExecutor(HITAApplication.TPE);
//    }
//
//    public void NotifyRefresh() {
//        if (isResumed()) {
//            willRefreshOnResume = false;
//            RefreshPageView(pageWeek);
//        } else willRefreshOnResume = true;
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (willRefreshOnResume) {
//            RefreshPageView(pageWeek);
//            willRefreshOnResume = false;
//        }
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (context instanceof TimeTableBlockView.TimeTablePreferenceRoot) {
//            root = (TimeTableBlockView.TimeTablePreferenceRoot) context;
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        root = null;
//    }
//
//    @Override
//    protected void stopTasks() {
//        if (pageTask != null && pageTask.getStatus() != AsyncTask.Status.FINISHED)
//            pageTask.cancel(true);
//    }
//
//    @Override
//    public void Refresh() {
//
//    }
//
//    @Override
//    public void onOperationStart(String id, Boolean[] params) {
//
//    }
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onOperationDone(String id, BaseOperationTask task, Boolean[] params, List result) {
//        switch (id) {
//            case "refresh":
//                if (!TimetableCore.getInstance(HContext).isDataAvailable()) return;
//                refreshDateViews();
//                timeTableView.removeAllViews();
//                timeTableView.notifyRefresh();
//                for (TextView cv : tWholeDays) {
//                    cv.setVisibility(View.GONE);
//                }
//                boolean hasWholedayWholeWeek = false;
//                ArrayList<List<EventItem>> wholeDayMap = new ArrayList<>();
//                for (int i = 0; i < 7; i++) {
//                    wholeDayMap.add(new ArrayList<EventItem>());
//                }
//                for (Object o : result) {
//                    if (o instanceof EventItem) {
//                        if (!((EventItem) o).isWholeDay()) timeTableView.addBlock(o);
//                        else {
//                            wholeDayMap.get(((EventItem) o).DOW - 1).add((EventItem) o);
//                            //Log.e("add", o.toString());
//                        }
//                    } else if (o instanceof List) {
//                        timeTableView.addBlock(o);
//                    }
//                }
//                for (int p = 0; p < 7; p++) {
//                    if (wholeDayMap.get(p).size() > 0) {
//                        hasWholedayWholeWeek = true;
//                        tWholeDays[p].setVisibility(View.VISIBLE);
//                        tWholeDays[p].setText(wholeDayMap.get(p).size() + "");
//                        tWholeDays[p].setOnClickListener(new OnWholeDayCardClickListener(wholeDayMap.get(p)));
//                    } else tWholeDays[p].setVisibility(View.GONE);
//                }
//                if (hasWholedayWholeWeek) allDayLayout.setVisibility(View.VISIBLE);
//                else allDayLayout.setVisibility(View.GONE);
//
//                if (pageWeek == TimetableCore.getInstance(HContext).getThisWeekOfTerm() && root.drawNowLine() && new HTime(TimetableCore.getNow()).after(root.getStartTime())) {
//                    timeTableView.addView(new TimeTableNowLine(requireContext(), getIconColorSecond()));
//                }
//                break;
//            case "delete":
//                Intent i = new Intent(TIMETABLE_CHANGED);
//                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(i);
//                Toast.makeText(requireContext(), R.string.delete_success, Toast.LENGTH_SHORT).show();
//
//        }
//    }
//
//
//    static class refreshPageTask extends BaseOperationTask<List> {
//
//        int week;
//
//        refreshPageTask(OperationListener<List> listRefreshedListener, int week) {
//            super(listRefreshedListener);
//            this.week = week;
//            id = "refresh";
//        }
//
//        @Override
//        protected List doInBackground(OperationListener listRefreshedListener, Boolean... booleans) {
//            List<Object> res = new ArrayList<>();
//            if (TimetableCore.getInstance(HContext).getCurrentCurriculum() == null) return res;
//            List<EventItem> oneWeekEvent = TimetableCore.getInstance(HContext).getEventsWithinWeeks(week, week);
//            for (int p = 1; p <= 7; p++) {
//                List<EventItem> oneDayEvent = new ArrayList<>();
//                for (EventItem x : oneWeekEvent) {
//                    if (x.getDOW() == p) oneDayEvent.add(x);
//                }
//                List<Object> oneDay = new ArrayList<>();
//                List<Integer> usedIndex = new ArrayList<>();
//                for (int i = 0; i < oneDayEvent.size(); i++) {
//                    if (usedIndex.contains(i)) continue;
//                    EventItem ei = oneDayEvent.get(i);
//                    if (ei.isWholeDay()) {
//                        oneDay.add(ei);
//                        continue;
//                    }
//                    List<EventItem> result = new ArrayList<>();
//                    result.add(ei);
//                    for (int j = i + 1; j < oneDayEvent.size(); j++) {
//                        EventItem x = oneDayEvent.get(j);
//                        if (x.isWholeDay()) continue;
//                        if (ei.startTime.equals(x.startTime) && ei.endTime.equals(x.endTime)) {
//                            result.add(x);
//                            usedIndex.add(j);
//                        }
//                    }
//                    if (result.size() > 1 && !ei.isWholeDay()) oneDay.add(result);
//                    else oneDay.add(ei);
//                }
//                res.addAll(oneDay);
//            }
//            return res;
//        }
//
//    }
//
//    static class deleteEventsTask extends BaseOperationTask<Boolean> {
//        List<EventItem> eventItems;
//
//        deleteEventsTask(OperationListener listRefreshedListener, EventItem eventItem) {
//            super(listRefreshedListener);
//            id = "delete";
//            this.eventItems = Collections.singletonList(eventItem);
//        }
//
//        deleteEventsTask(OperationListener listRefreshedListener, List<EventItem> eventItem) {
//            super(listRefreshedListener);
//            id = "delete";
//            this.eventItems = eventItem;
//        }
//
//
//        @Override
//        protected Boolean doInBackground(OperationListener<Boolean> listRefreshedListener, Boolean... booleans) {
//            for (EventItem ei : eventItems) {
//                boolean res = TimetableCore.getInstance(HContext).deleteEvent(ei, false);
//                if (!res) return false;
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(OperationListener<Boolean> listRefreshedListener, Boolean ts) {
//            super.onPostExecute(listRefreshedListener, ts);
//        }
//    }
//
//
//
//    class OnWholeDayCardClickListener implements View.OnClickListener {
//
//        List<EventItem> res;
//
//        OnWholeDayCardClickListener(final List<EventItem> res) {
//            this.res = res;
//        }
//
//        @Override
//        public void onClick(View v) {
//            EventsUtils.showEventItem(getBaseActivity(), res);
//        }
//    }
//
//}
