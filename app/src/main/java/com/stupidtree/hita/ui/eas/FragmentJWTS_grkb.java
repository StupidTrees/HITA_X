//package com.stupidtree.hita.ui.eas;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.RotateAnimation;
//import android.widget.CalendarView;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.WorkerThread;
//import androidx.appcompat.app.AlertDialog;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import com.google.android.material.snackbar.Snackbar;
//import com.stupidtree.hita.R;
//import com.stupidtree.hita.activities.ActivityMain;
//import com.stupidtree.hita.fragments.BaseOperationTask;
//import com.stupidtree.hita.online.Teacher;
//import com.stupidtree.hita.online.errorTableText;
//import com.stupidtree.hita.timetable.CurriculumCreator;
//import com.stupidtree.hita.timetable.TimetableCore;
//import com.stupidtree.hita.views.ButtonLoading;
//import com.stupidtree.hita.views.MaterialCircleAnimator;
//import com.stupidtree.hita.views.RoundedBarPicker;
//
//import net.cachapa.expandablelayout.ExpandableLayout;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//import cn.bmob.v3.BmobQuery;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.SaveListener;
//
//import static com.stupidtree.hita.HITAApplication.EASCore;
//import static com.stupidtree.hita.HITAApplication.HContext;
//import static com.stupidtree.hita.HITAApplication.TPE;
//import static com.stupidtree.hita.timetable.TimeWatcherService.TIMETABLE_CHANGED;
//
//
//public class FragmentJWTS_grkb extends EASFragment implements BaseOperationTask.OperationListener<Object> {
//    private ButtonLoading bt_import_grkb;
//    private CalendarView calendarView;
//    private CheckBox uploadTeacher, syncSubject;
//    private ImageView refreshStartButton;
//    private RoundedBarPicker picknxq;
//
//    //数据区
//    private List<String> XnxqNames, XnxqKeys;
//    private Calendar startDate;
//
//    public FragmentJWTS_grkb() {
//
//    }
//
//    public static com.stupidtree.hita.eas.FragmentJWTS_grkb newInstance() {
//        return new com.stupidtree.hita.eas.FragmentJWTS_grkb();
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_jwts_grkb;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initViews(view);
//    }
//
//    @WorkerThread
//    private static void getSubjectsInfo(CurriculumCreator ci, String xn, String xq) throws EASException {
//        ci.updateSubjectInfo(EASCore.getChosenSubjectsInfo(xn, xq));
//    }
//
//    @WorkerThread
//    private static void uploadTeacherInfo(List<String> teacherId) throws EASException {
//        List<Teacher> teacherList = new ArrayList<>();
//        for (String id : teacherId) {
//            Map<String, String> m = EASCore.getTeacherData(id);
//            String teacherCode = m.get("id");
//            String gender = m.get("gender");
//            String title = m.get("title");
//            String school = m.get("school");
//            String phone = m.get("phone");
//            String email = m.get("email");
//            String detail = m.get("detail");
//            String name = m.get("name");
//            final Teacher t = new Teacher(teacherCode, name, gender, title, school, phone, email, detail);
//            if (!teacherList.contains(t)) teacherList.add(t);
//        }
//        // Log.e("ts",teacherList.toString());
//        for (final Teacher t : teacherList) {
//            BmobQuery<Teacher> bq = new BmobQuery<>();
//            bq.addWhereEqualTo("name", t.getName());
//            List<Teacher> queryExisted = bq.findObjectsSync(Teacher.class);
//            // Log.e("外部查找教师：",t.getName());
//            if (queryExisted != null && queryExisted.size() > 0) {
//                Teacher existed = queryExisted.get(0);
//                t.setObjectId(existed.getObjectId());
//                String result = t.updateSync();
//                Log.e("TEACHER", "更新教师信息：" + t.getName() + "   result:" + result);
//            } else {
//                String result = t.saveSync();
//                Log.e("TEACHER", "上传教师信息：" + t.getName() + "   result:" + result);
//            }
//        }
//
//    }
//
//    private void initViews(View v) {
//        super.initRefresh(v);
//        calendarView = v.findViewById(R.id.calendarView);
//        picknxq = v.findViewById(R.id.pick_xnxq);
//        XnxqKeys = new ArrayList<>();
//        XnxqNames = new ArrayList<>();
//        bt_import_grkb = v.findViewById(R.id.button_import_grkb);
//        refreshStartButton = v.findViewById(R.id.refresh_start);
//        uploadTeacher = v.findViewById(R.id.switch_teacher);
//        syncSubject = v.findViewById(R.id.switch_subject);
//        picknxq.setUp(XnxqKeys, XnxqNames, new RoundedBarPicker.OnSelectedListener() {
//            @Override
//            public void OnSelected(String key, String name, int index) {
//                refreshStartDate();
//            }
//        });
//
//        bt_import_grkb.setOnButtonLoadingListener(new ButtonLoading.OnButtonLoadingListener() {
//            @Override
//            public void onClick() {
//                if (startDate == null) {
//                    Snackbar.make(bt_import_grkb, R.string.set_start_date_plz, Snackbar.LENGTH_SHORT).show();
//                    bt_import_grkb.setProgress(false);
//                    return;
//                }
//                if (jwRoot.getXNXQItems().size() > 0) {
//                    Map<String, String> xnxq = jwRoot.getXNXQItems().get(picknxq.getSelectedIndex());
//                    String xn = xnxq.get("xn");
//                    String xq = xnxq.get("xq");
//                    String name = xnxq.get("xnmc") + xnxq.get("xqmc") + "课表";
//                    new importGRKBTask(com.stupidtree.hita.eas.FragmentJWTS_grkb.this, xn, xq, startDate, name, uploadTeacher.isChecked(), syncSubject.isChecked()).execute();
//                }
//            }
//
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                startDate = Calendar.getInstance();
//                startDate.set(year, month, dayOfMonth);
//            }
//        });
//        ViewGroup optionsButton = v.findViewById(R.id.more);
//        final ImageView optionsArrow = v.findViewById(R.id.more_arrow);
//        final ExpandableLayout expandableLayout = v.findViewById(R.id.expand);
//        optionsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                expandableLayout.toggle();
//                MaterialCircleAnimator.rotateTo(expandableLayout.isExpanded(), optionsArrow);
//            }
//        });
//        refreshStartButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                refreshStartDate();
//            }
//        });
//    }
//
//
//    @Override
//    public int getTitle() {
//        return R.string.jw_tabs_frkb;
//    }
//
//    @Override
//    protected void stopTasks() {
////        if (pageTask != null && pageTask.getStatus() != AsyncTask.Status.FINISHED)
////            pageTask.cancel(true);
//    }
//
//    @Override
//    public void Refresh() {
//        refresh.setRefreshing(false);
//        stopTasks();
//        XnxqNames.clear();
//        XnxqKeys.clear();
//        int i = 0;
//        int now = 0;
//        for (Map<String, String> item : jwRoot.getXNXQItems()) {
//            if (Objects.equals(item.get("sfdqxq"), "1")) now = i;
//            XnxqNames.add(item.get("xnmc") + item.get("xqmc"));
//            XnxqKeys.add(item.get("xn") + "-" + item.get("xq"));
//            i++;
//        }
//        picknxq.setSelection(now, false);
//        refreshStartDate();
//    }
//
//    @Override
//    public void onOperationStart(String id, Boolean[] params) {
//        switch (id) {
//            case "start_date":
//                startDate = null;
//                calendarView.setEnabled(false);
//                calendarView.setAlpha(0.5f);
//                RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                rotateAnimation.setRepeatCount(-1);
//                rotateAnimation.setDuration(500);
//                refreshStartButton.setAnimation(rotateAnimation);
//                refreshStartButton.startAnimation(rotateAnimation);
//                break;
//            case "import":
//                bt_import_grkb.setProgress(true);
//        }
//    }
//
//    @Override
//    public void onOperationDone(String id, BaseOperationTask task, Boolean[] params, Object o) {
//        switch (id) {
//            case "start_date":
//                refreshStartButton.getAnimation().setRepeatCount(0);
//                calendarView.setEnabled(true);
//                calendarView.setAlpha(1f);
//                if (o instanceof Calendar) {
//                    startDate = (Calendar) o;
//                    calendarView.setDate(((Calendar) o).getTimeInMillis());
//                } else {
//                    Snackbar.make(calendarView, R.string.fetch_start_date_failed, Snackbar.LENGTH_SHORT).show();
//                }
//                break;
//            case "import":
//                bt_import_grkb.setProgress(false);
//                Intent i = new Intent(TIMETABLE_CHANGED);
//                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(i);
//                if (o instanceof EASException) {
//                    if (((EASException) o).getType() == EASException.DIALOG_MESSAGE) {
//                        AlertDialog ad = new AlertDialog.Builder(com.stupidtree.hita.eas.FragmentJWTS_grkb.this.requireActivity()).create();
//                        ad.setTitle(R.string.attention);
//                        ad.setMessage(((EASException) o).getDialogMessage());
//                        ad.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.button_confirm), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//                        ad.show();
//                    } else if (((EASException) o).getType() == EASException.CONNECT_ERROR) {
//                        Toast.makeText(HContext, "网络错误", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(HContext, "导入异常" + o.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                } else if (o instanceof Exception) {
//                    Toast.makeText(HContext, "导入错误" + o.toString(), Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(HContext, ((Boolean) o) ? "课表导入成功，请务必检查正确性" : "课表导入失败！", Toast.LENGTH_SHORT).show();
//                    //if(o.toString().contains("成功")) new ActivityJWTS.getPYJHTask().executeOnExecutor(HITAApplication.TPE);
//                }
//                ActivityMain.saveData();
//                break;
//        }
//    }
//
//
//    private void refreshStartDate() {
//        Map<String, String> xnxq = jwRoot.getXNXQItems().get(picknxq.getSelectedIndex());
//        String xn = xnxq.get("xn");
//        String xq = xnxq.get("xq");
//        new refreshStartDateTask(this, xn, xq).executeOnExecutor(TPE);
//    }
//
//    static class refreshStartDateTask extends BaseOperationTask<Calendar> {
//
//        String xn, xq;
//
//        refreshStartDateTask(OperationListener<?> listRefreshedListener, String xn, String xq) {
//            super(listRefreshedListener);
//            this.xn = xn;
//            this.xq = xq;
//            id = "start_date";
//        }
//
//        @Override
//        protected Calendar doInBackground(OperationListener<Calendar> listRefreshedListener, Boolean... booleans) {
//            try {
//                return EASCore.getFirstDateOfCurriculum(xn, xq);
//            } catch (EASException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//    }
//
//    static class importGRKBTask extends BaseOperationTask<Object> {
//
//        String xn;
//        String xq;
//        String kbName;
//        boolean syncSubject;
//        boolean uploadTeacher;
//        Calendar startDate;
//
//        importGRKBTask(OperationListener<?> listRefreshedListener, String xn, String xq, Calendar startDate, String kbName, boolean uploadTeacher, boolean syncSubject) {
//            super(listRefreshedListener);
//            this.xn = xn;
//            this.xq = xq;
//            this.kbName = kbName;
//            this.syncSubject = syncSubject;
//            this.uploadTeacher = uploadTeacher;
//            this.startDate = startDate;
//            id = "import";
//        }
//
//
//        @Override
//        protected Object doInBackground(OperationListener<Object> listRefreshedListener, Boolean... booleans) {
//            String code = xn + xq;
//            List<Map<String, String>> kbData;
//            try {
//                kbData = EASCore.getGRKBData(xn, xq);
//                // Calendar startDate = jwCore.getFirstDateOfCurriculum(xn, xq);
//                CurriculumCreator s = CurriculumCreator.create(code, kbName, startDate).loadCourse(kbData);
//                try {
//                    getSubjectsInfo(s, xn, xq);
//                } catch (EASException e) {
//                    e.printStackTrace();
//                }
//                if (uploadTeacher) {
//                    uploadTeacherInfo(EASCore.getTeacherOfChosenSubjects(xn, xq));
//                }
//                if (TimetableCore.getInstance(HContext).addCurriculum(s, syncSubject)) {
//                    ActivityMain.saveData();
//                    return true;
//                }
//                return false;
//            } catch (Exception e) {
//                errorTableText et = new errorTableText("外层错误", e);
//                et.save(new SaveListener<String>() {
//                    @Override
//                    public void done(String s, BmobException e) {
//                        if (e != null) {
//                            e.printStackTrace();
//                            Log.e("upload_error", e.toString());
//                        }
//                    }
//                });
//                return e;
//            }
//
//        }
//
//
//    }
//
//
//
//}
