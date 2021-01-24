package com.stupidtree.hita.ui.timetable.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.stupidtree.hita.databinding.ActivityTimeTableBinding;
import com.stupidtree.hita.ui.base.BaseActivity;

import org.jetbrains.annotations.NotNull;

public class TimetableActivity extends BaseActivity<TimetableViewModel, ActivityTimeTableBinding> {

    public static final String TIMETABLE_REFRESH = "COM.STUPIDTREE.HITA.TIMETABLE_ACTIVITY_REFRESH";
    int pageWeekOfTerm;
    boolean hasInit = false;
    boolean refreshOnResume = false;
    boolean firstEnter = true;
    public static final int TIMETABLE_REQUEST_SETTING = 858;
    public static final int SETTING_RESULT_TIMETABLE = 865;

//    public AppBarLayout mAppBarLayout;
//    Toolbar mToolbar;
//    LinearLayout invalidLayout;
//    ViewPager viewPager;
//
//    FloatingActionButton fab;
//    TabLayout tabs;

    TimeTablePagerAdapter pagerAdapter;
    SharedPreferences timetableSP;

    /*个性化参数*/
//    private boolean drawNowLine;
//    private boolean drawBGLine;
//    private String titleGravity;
//    private String cardBackground;
//    private String titleColor;
//    private String subTitleColor;
//    private String iconColor;
//    private boolean enableIcon;
//    private boolean colorfulMode;
//    private boolean enableAnim;
//    private int bgOpacity;
//    private int titleAlpha;
//    private int subtitleAlpha;
//    private boolean boldText;
//    private int card_height;
//    private HTime fromTime;


    void initViewPager() {
        pagerAdapter = new TimeTablePagerAdapter(this, getSupportFragmentManager(),20);
        binding.timetableViewpager.setAdapter(pagerAdapter);
        binding.timetableViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                pageWeekOfTerm = i + 1;
//                if (pageWeekOfTerm == TimetableCore.getInstance(HContext).getThisWeekOfTerm()) {
//                    fab.hide();
//                    //toolbar_title.setTextColor(ContextCompat.getColor(TimetableActivity.this, R.color.theme1_colorPrimaryDark));
//                } else if (TimetableCore.getInstance(HContext).isThisTerm()) {
//                    if (timetableSP.getBoolean("timetable_back_enable", true)) fab.show();
//                    //toolbar_title.setTextColor(ContextCompat.getColor(TimetableActivity.this, R.color.material_primary_text));
//                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        binding.timetableTabs.setupWithViewPager(binding.timetableViewpager);
        binding.timetableViewpager.setCurrentItem(20);
        binding.timetableViewpager.setOffscreenPageLimit(3);
        //if(thisWeekOfTerm-1>=0) viewPager.setCurrentItem(thisWeekOfTerm-1);
    }

//    void initReceiver() {
//        BroadcastReceiver br = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                //Log.e("timetable的Activity接收到广播","进行刷新");
//                try {
//                    onCalledRefresh();
//                    refreshOnResume = false;
//                } catch (Exception e) {
//                    refreshOnResume = true;
//                }
//
//            }
//        };
//        IntentFilter iF = new IntentFilter(TIMETABLE_CHANGED);
//        iF.addAction(TIMETABLE_REFRESH);
//        LocalBroadcastManager.getInstance(this).registerReceiver(br, iF);
//    }


    /*刷新课表视图函数*/
    public void Refresh(boolean backToThisWeek) {
//        TimetableCore tc = TimetableCore.getInstance(HContext);
//        tc.syncTimeFlags();
//        syncAllPreferences();
//        if (!tc.isDataAvailable()) {
//            invalidLayout.setVisibility(View.VISIBLE);
//            viewPager.setVisibility(View.GONE);
//            fab.hide();
//            return;
//        } else {
//            invalidLayout.setVisibility(View.GONE);
//            viewPager.setVisibility(View.VISIBLE);
//        }
//        pageWeekOfTerm = tc.getThisWeekOfTerm();
//        if (pageWeekOfTerm < 0) {
//            Snackbar.make(fab.getRootView(), getString(R.string.snack_semester_notstarted), Snackbar.LENGTH_SHORT).show();
//            pageWeekOfTerm = 1;
//        }
//        if (backToThisWeek) viewPager.setCurrentItem(pageWeekOfTerm - 1);
//        boolean fabEnable = timetableSP.getBoolean("timetable_back_enable", true);
//        if (!tc.isThisTerm() || !fabEnable || pageWeekOfTerm == tc.getThisWeekOfTerm())
//            fab.hide();
//        else fab.show();
    }


//    public void syncAllPreferences() {
//        cardBackground = timetableSP.getString("timetable_card_background", "gradient");
//        card_height = timetableSP.getInt("timetable_card_height", 160);//课程表卡片高度
//        titleGravity = timetableSP.getString("timetable_card_title_gravity", "top");
//        bgOpacity = timetableSP.getInt("timetable_card_opacity", 100);
//        enableIcon = timetableSP.getBoolean("timetable_card_icon_enable", true);
//        boldText = timetableSP.getBoolean("timetable_card_text_bold", false);
//        titleColor = timetableSP.getString("timetable_card_title_color", "white");
//        subTitleColor = timetableSP.getString("timetable_card_subtitle_color", "white");
//        iconColor = timetableSP.getString("timetable_card_icon_color", "white");
//        colorfulMode = timetableSP.getBoolean("subjects_color_enable", false);
//        enableAnim = timetableSP.getBoolean("timetable_animation_enable", true);
//        drawNowLine = timetableSP.getBoolean("timetable_draw_now_line", true);
//        drawBGLine = timetableSP.getBoolean("timetable_draw_bg_line", false);
//        titleAlpha = timetableSP.getInt("timetable_card_title_alpha", 100);
//        subtitleAlpha = timetableSP.getInt("timetable_card_subtitle_alpha", 100);
//        fromTime = new HTime(timetableSP.getString("timetable_start_time", "08:00"));
//    }


//    public void onCalledRefresh() {
//        syncAllPreferences();
//        pagerAdapter.notifyAllFragments();
//    }

//    @Override
//    public void changeEnableColor(boolean changed) {
//        timetableSP.edit().putBoolean("subjects_color_enable", changed).apply();
//        colorfulMode = changed;
//        pagerAdapter.notifyAllFragments();
//    }
//
//    @Override
//    public void drawLineChanged(boolean changed) {
//        timetableSP.edit().putBoolean("timetable_draw_now_line", changed).apply();
//        drawNowLine = changed;
//        pagerAdapter.notifyAllFragments();
//    }
//
//    @Override
//    public void callResetColor() {
//        new resetColorTask(this, timetableSP).execute();
//    }
//
//    @Override
//    public void changeFromTime(HTime from) {
//        fromTime.setTime(from.hour, from.minute);
//        timetableSP.edit().putString("timetable_start_time", from.hour + ":" + from.minute).apply();
//        pagerAdapter.notifyAllFragments();
//    }
//
//    @Override
//    public void drawBGLineChanged(boolean isChecked) {
//        drawBGLine = isChecked;
//        timetableSP.edit().putBoolean("timetable_draw_bg_line", isChecked).apply();
//        pagerAdapter.notifyAllFragments();
//    }
//
//    @Override
//    public SharedPreferences getSP() {
//        return timetableSP;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowParams(true, true, false);
        setToolbarActionBack(binding.mainToolBar);
        timetableSP = getSharedPreferences("timetable_pref", Context.MODE_PRIVATE);
        //initReceiver();
        Refresh(false);
        hasInit = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (refreshOnResume) {
//            onCalledRefresh();
//            refreshOnResume = false;
//        }
//        if (!firstEnter) Refresh(false);
//        else {
//            viewPager.setCurrentItem(TimetableCore.getInstance(HContext).getThisWeekOfTerm() - 1);
//        }
//        firstEnter = false;
    }


    @NotNull
    @Override
    protected ActivityTimeTableBinding initViewBinding() {
        return ActivityTimeTableBinding.inflate(getLayoutInflater());
    }

    @NotNull
    @Override
    protected Class<TimetableViewModel> getViewModelClass() {
        return TimetableViewModel.class;
    }

    @Override
    protected void initViews() {
//        final TimetableCore tc = TimetableCore.getInstance(HContext);
//        binding.fabThisweek.setBackgroundTintList(ColorStateList.valueOf(getColorAccent()));
//        binding.fabThisweek.setImageTintList(ColorStateList.valueOf(Color.WHITE));
//        binding.fabThisweek.setOnClickListener(v -> {
//            if (pageWeekOfTerm != tc.getThisWeekOfTerm() && tc.isThisTerm()) {
//                pageWeekOfTerm = tc.getThisWeekOfTerm();
//                viewPager.setCurrentItem(tc.getThisWeekOfTerm() - 1);
//            }
//        });
//        binding.more.setOnClickListener(v -> {
//            FragmentTimetablePanel tp = new FragmentTimetablePanel();
//            tp.show(getSupportFragmentManager(), "panel");
//        });
        initViewPager();

    }

//
//    static class resetColorTask extends BaseOperationTask<Object> {
//
//        SharedPreferences sharedPreferences;
//
//        resetColorTask(OperationListener listRefreshedListener, SharedPreferences sharedPreferences) {
//            super(listRefreshedListener);
//            id = "reset_color";
//            this.sharedPreferences = sharedPreferences;
//        }
//
//        @SuppressLint("ApplySharedPref")
//        @Override
//        protected Object doInBackground(OperationListener<Object> listRefreshedListener, Boolean... booleans) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            for (Subject s : TimetableCore.getInstance(HContext).getSubjects(null)) {
//                editor.putInt("color:" + s.getName(), ColorBox.getRandomColor_Material());
//            }
//            editor.commit();
//            return super.doInBackground(listRefreshedListener, booleans);
//        }
//
//    }
}
