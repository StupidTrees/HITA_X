//package com.stupidtree.hita.ui.eas;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Pair;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.WorkerThread;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.widget.Toolbar;
//import androidx.coordinatorlayout.widget.CoordinatorLayout;
//import androidx.fragment.app.Fragment;
//import androidx.viewpager.widget.ViewPager;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.tabs.TabLayout;
//import com.stupidtree.hita.R;
//import com.stupidtree.hita.data.model.eas.TermItem;
//import com.stupidtree.hita.ui.base.BaseActivity;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//
//public class ActivityJWTS extends BaseActivity implements EASFragment.JWRoot {
//    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
//    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
//    ViewPager pager;
//    TabLayout tabs;
//    FloatingActionButton fab;
//    CoordinatorLayout rootLayout;
//    List<TermItem> termItems;
//    Map<String, String> keyToTitle;
//    View loading;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setWindowParams(true, false, false);
//        setContentView(R.layout.activity_jwts);
//        initViews();
//        initToolbar();
//        initPager();
//        new loadBasicInfoTask(this).executeOnExecutor(TPE);
//    }
//
//    void initViews() {
//        loading = findViewById(R.id.loading);
//        rootLayout = findViewById(R.id.jwts_root);
//        fab = findViewById(R.id.fab);
//        termItems = new ArrayList<>();
//    }
//
//    @WorkerThread
//    public static boolean tryToReLogin() throws EASException {
//        if (CurrentUser != null) {
//            String stun = CurrentUser.getStudentnumber();
//            String password = null;
//            if (!TextUtils.isEmpty(stun)) password = defaultSP.getString(stun + ".password", null);
//            if (password != null) {
//                return EASCore.login(stun, password);
//            }
//        }
//        return false;
//    }
//
//    void initPager() {
//        tabs = findViewById(R.id.jwts_tab);
//        pager = findViewById(R.id.jwts_pager);
//        keyToTitle = new HashMap<>();
//        final int[] titles = new int[]{R.string.jw_tabs_frkb, R.string.jw_tabs_kjs, R.string.jw_tabs_xk, R.string.jw_tabs_cj, R.string.jw_tabs_xxjd};
//        pager.setAdapter(new BaseTabAdapter(getSupportFragmentManager(), titles.length) {
//            @Override
//            protected Fragment initItem(int position) {
//                switch (position) {
//                    case 0:
//                        return FragmentJWTS_grkb.newInstance();
//                    case 1:
//                        return FragmentJWTS_kjs.newInstance();
//                    case 2:
//                        return FragmentJWTS_xsxk.newInstance();
//                    case 3:
//                        return FragmentJWTS_cjgl.newInstance();
//                    case 4:
//                        return FragmentJWTS_xxjd.newInstance();
//
//                }
//                return null;
//            }
//
//            @NonNull
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return getString(titles[position]);
//            }
//        }.setDestroyFragment(false));
//        tabs.setupWithViewPager(pager);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_jwts, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public List<Map<String, String>> getXNXQItems() {
//        return termItems;
//    }
//
//    @Override
//    public Map<String, String> getKeyToTitleMap() {
//        return keyToTitle;
//    }
//
//    @Override
//    public void onOperationStart(String id, Boolean[] params) {
//        loading.setVisibility(View.VISIBLE);
//        pager.setVisibility(View.GONE);
//    }
//
//    void initToolbar() {
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
//        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        toolbar.inflateMenu(R.menu.toolbar_jwts);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                if (menuItem.getItemId() == R.id.action_logout) {
//                    AlertDialog ad = new AlertDialog.Builder(com.stupidtree.hita.activities.ActivityJWTS.this).create();
//                    ad.setMessage("下次进入需要重新登录，是否退出？");
//                    ad.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            EASCore.logOut();
//                            Intent i = new Intent(com.stupidtree.hita.activities.ActivityJWTS.this, LoginEASActivity.class);
//                            com.stupidtree.hita.activities.ActivityJWTS.this.startActivity(i);
//                            finish();
//                        }
//                    });
//                    ad.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//                    ad.show();
//
//                }
//                return true;
//            }
//        });
//
//    }
//
//    @Override
//    public void onOperationDone(String id, BaseOperationTask<Pair<List<Map<String, String>>, HashMap<String, String>>> task, Boolean[] params, Pair<List<Map<String, String>>, HashMap<String, String>> result) {
//        MaterialCircleAnimator.animHide(loading);
//        pager.setVisibility(View.VISIBLE);
//        if (result == null) {
//            EASCore.logOut();
//            Toast.makeText(HContext, "页面过期，请返回重新登录！", Toast.LENGTH_SHORT).show();
//            Intent i = new Intent(com.stupidtree.hita.activities.ActivityJWTS.this, LoginEASActivity.class);
//            startActivity(i);
//            finish();
//        } else {
//            keyToTitle.clear();
//            keyToTitle.putAll(result.second);
//            termItems.clear();
//            termItems.addAll(result.first);
//            if (!TextUtils.isEmpty(getIntent().getStringExtra("terminal"))) {
//                pager.setCurrentItem(Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("terminal"))));
//            }
//            // Log.e("refresh2", String.valueOf(getSupportFragmentManager().getFragments()));
//            for (Fragment f : getSupportFragmentManager().getFragments()) {
//                if (f instanceof EASFragment) {
//                    if (f.isResumed()) {
//                        ((EASFragment) f).Refresh();
//                    } else {
//                        ((EASFragment) f).setWillRefreshOnResume(true);
//                    }
//                }
//            }
//
//
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
//    static class loadBasicInfoTask extends BaseOperationTask<Pair<List<Map<String, String>>, HashMap<String, String>>> {
//
//        loadBasicInfoTask(OperationListener listRefreshedListener) {
//            super(listRefreshedListener);
//        }
//
//        @Override
//        protected Pair<List<Map<String, String>>, HashMap<String, String>> doInBackground(OperationListener<Pair<List<Map<String, String>>, HashMap<String, String>>> listRefreshedListener, Boolean... booleans) {
//            List<Map<String, String>> xnxqItems = new ArrayList<>();
//            HashMap<String, String> keyToTitle = new HashMap<>();
//            try {
//                xnxqItems.addAll(EASCore.getXNXQ());
//                keyToTitle.putAll(EASCore.getXKColumnTitles());
//                return new Pair<>(xnxqItems, keyToTitle);
//            } catch (EASException e) {
//                try {
//                    xnxqItems.clear();
//                    if (tryToReLogin()) {
//                        xnxqItems.addAll(EASCore.getXNXQ());
//                        keyToTitle.clear();
//                        keyToTitle.putAll(EASCore.getXKColumnTitles());
//                        return new Pair<>(xnxqItems, keyToTitle);
//                    } else return null;
//                } catch (EASException e2) {
//                    return null;
//                }
//            }
//        }
//
//    }
//
//
//}
