//package com.stupidtree.hita.ui.eas;
//
//import android.app.Activity;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.app.ActivityCompat;
//
//import com.google.android.material.snackbar.Snackbar;
//import com.stupidtree.hita.R;
//import com.stupidtree.hita.eas.EASException;
//import com.stupidtree.hita.fragments.BaseOperationTask;
//import com.stupidtree.hita.ui.base.BaseActivity;
//import com.stupidtree.hita.util.ActivityUtils;
//import com.stupidtree.hita.views.ButtonLoading;
//
//import org.jetbrains.annotations.Nullable;
//
//import java.util.List;
//import java.util.Objects;
//
//import cn.bmob.v3.BmobArticle;
//import cn.bmob.v3.BmobQuery;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.FindListener;
//
//import static com.stupidtree.hita.HITAApplication.CurrentUser;
//import static com.stupidtree.hita.HITAApplication.EASCore;
//import static com.stupidtree.hita.HITAApplication.TPE;
//import static com.stupidtree.hita.HITAApplication.defaultSP;
//
//public class LoginEASActivity extends BaseActivity implements BaseOperationTask.OperationListener<Object>{
//    EditText username, password;
//    ButtonLoading login;
//    LinearLayout loginCard;
//    ImageView vpnHint;
//    loginTask pageTask_login;
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (pageTask_login != null && pageTask_login.getStatus()!=AsyncTask.Status.FINISHED) pageTask_login.cancel(true);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setWindowParams(true, false, false);
//        setContentView(R.layout.activity_login_eas);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
//        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        username = findViewById(R.id.username);
//        password = findViewById(R.id.password);
//        login = findViewById(R.id.login);
//        vpnHint = findViewById(R.id.vpn);
//        loginCard = findViewById(R.id.logincard);
//        vpnHint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BmobQuery<BmobArticle> bq = new BmobQuery<>();
//                bq.addWhereEqualTo("objectId","\tVkLlSSSW");
//                bq.findObjects(new FindListener<BmobArticle>() {
//                    @Override
//                    public void done(List<BmobArticle> list, BmobException e) {
//                        String url;
//                        if(e==null&&list!=null&&list.size()>0) url = list.get(0).getUrl();
//                        else url = "http://files.hita.store/2020/02/14/ae00758f40817fab808e32dbf3aea7eb.html";
//                        ActivityUtils.openInBrowser(com.stupidtree.hita.activities.ActivityLoginJWTS.this,url);
//                    }
//                });
//            }
//        });
//        login.setOnButtonLoadingListener(new ButtonLoading.OnButtonLoadingListener() {
//            @Override
//            public void onClick() {
//                if (TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(password.getText())) {
//                    Snackbar.make(username, R.string.input_username_and_pswd_plz, Snackbar.LENGTH_SHORT).show();
//                    return;
//                }
//                if (pageTask_login != null && pageTask_login.getStatus() != AsyncTask.Status.FINISHED)
//                    pageTask_login.cancel(true);
//                pageTask_login = new loginTask(com.stupidtree.hita.activities.ActivityLoginJWTS.this, username.getText().toString(), password.getText().toString(), true);
//                pageTask_login.executeOnExecutor(TPE);
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
//        if(CurrentUser!=null&& !TextUtils.isEmpty(CurrentUser.getStudentnumber())){
//            username.setText(CurrentUser.getStudentnumber());
//            password.setText(defaultSP.getString(CurrentUser.getStudentnumber()+".password",""));
//        }
//        //new directlyLoginTask().executeOnExecutor(HITAApplication.TPE);
//    }
//
//
//    public void presentActivity(Activity activity, View view) {
////        int revealX,revealY;
////        if(view==null){
////             revealX= 0;
////            revealY = 0;
////        }else{
////            revealX = (int) (view.getX() + view.getWidth() / 2);
////            revealY = (int) (view.getY() + view.getHeight() / 2);
////        }
//        Intent intent = new Intent(this, ActivityJWTS.class);
////        intent.putExtra(ActivityJWTS.EXTRA_CIRCULAR_REVEAL_X, revealX);
////        intent.putExtra(ActivityJWTS.EXTRA_CIRCULAR_REVEAL_Y, revealY);
//
//        if (view != null) view.setVisibility(View.VISIBLE);
//        ActivityCompat.startActivity(activity, intent, null);
////        overridePendingTransition(0, 0);
//        finish();
//    }
//
//    @Override
//    public void onOperationStart(String id, Boolean[] params) {
//        login.setProgress(true);
//    }
//
//    @Override
//    public void onOperationDone(String id, BaseOperationTask task, Boolean[] params, Object o) {
//        login.setProgress(false);
//        loginTask lt = (loginTask) task;
//        boolean toast = lt.toast;
//
//        String password = lt.password;
//        if (o instanceof EASException) {
//            ((EASException) o).printStackTrace();
//            EASException jwe = (EASException) o;
//            AlertDialog ad = new AlertDialog.Builder(com.stupidtree.hita.activities.ActivityLoginJWTS.this).create();
//            ad.setTitle("提示");
//            String message = "登录失败！";
//            if (jwe.getType() == EASException.CONNECT_ERROR) message = "网络连接错误";
//            else if (jwe.getType() == EASException.LOGIN_FAILED || jwe.getType() == EASException.FORMAT_ERROR)
//                message = "登录失败！";
//            else if (jwe.getType() == EASException.DIALOG_MESSAGE) message = jwe.getDialogMessage();
//            ad.setMessage(message);
//            ad.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
//            ad.show();
//        }
//        else if(o instanceof Boolean){
//            if ((boolean)o) {
//                presentActivity(com.stupidtree.hita.activities.ActivityLoginJWTS.this,login);
//                if (CurrentUser != null)
//                    defaultSP.edit().putString(CurrentUser.getStudentnumber() + ".password", password).apply();
//            } else {
//                if (toast) {
//                    AlertDialog ad = new AlertDialog.Builder(com.stupidtree.hita.activities.ActivityLoginJWTS.this)
//                            .setTitle("提示")
//                            .setMessage("登录失败,请检查账号密码")
//                            .setPositiveButton(R.string.button_confirm, null).create();
//                    ad.show();
//                }
//
//            }
//        }
//    }
//
//    @Override
//    protected int getLayoutIdAndInitViewBinding() {
//        return 0;
//    }
//
//    @Nullable
//    @Override
//    protected Class getViewModelClass() {
//        return null;
//    }
//
//    @Override
//    protected void initViews() {
//
//    }
//
//
//    static class loginTask extends BaseOperationTask<Object> {
//
//        String username, password;
//        boolean toast;
//
//
//        loginTask(OperationListener listRefreshedListener, String username, String password, boolean toast) {
//            super(listRefreshedListener);
//            this.username = username;
//            this.password = password;
//            this.toast = toast;
//        }
//
//        @Override
//        protected Object doInBackground(OperationListener<Object> listRefreshedListener, Boolean... booleans) {
//            try {
//                return EASCore.login(username, password);
//            } catch (EASException e) {
//                return e;
//            }
//        }
//
//    }
//
//
//
//
//
//
//
//}
