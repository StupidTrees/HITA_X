package com.stupidtree.hita.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.stupidtree.hita.data.repository.EASRepository
import com.stupidtree.hita.ui.base.BaseActivity
import com.stupidtree.hita.ui.eas.EASActivity
import com.stupidtree.hita.ui.eas.login.PopUpLoginEAS
import com.stupidtree.hita.ui.myprofile.MyProfileActivity
import com.stupidtree.hita.ui.subject.SubjectActivity
import com.stupidtree.hita.ui.timetable.detail.TimetableDetailActivity
import com.stupidtree.hita.ui.timetable.manager.TimetableManagerActivity
import com.stupidtree.hita.ui.welcome.WelcomeActivity


object ActivityUtils {

    fun startMyProfileActivity(from: Context) {
        val i = Intent(from, MyProfileActivity::class.java)
        from.startActivity(i)
    }

    fun startWelcomeActivity(from: Context) {
        val i = Intent(from, WelcomeActivity::class.java)
        from.startActivity(i)
    }

    /**
     * 进行教务认证，或直接跳转
     * @param directTo 若存在已登录token，则直接跳转到activity。传null表示忽略
     * @param cancelable 窗口是否可被取消
     * @param onResponseListener 认证监听
     */
    fun<T:Activity> showEasVerifyWindow(
        from: Context,
        directTo: Class<T>? = null,
        cancelable: Boolean = true,
        onResponseListener: PopUpLoginEAS.OnResponseListener
    ) {
        if (from is BaseActivity<*, *>) {
            if (EASRepository.getInstance(from.application).getEasToken().isLogin()) {
                directTo?.let {
                    val i = Intent(from, directTo)
                    from.startActivity(i)
                    return
                }
            }
            val window = PopUpLoginEAS()
            window.isCancelable = cancelable
            window.onResponseListener = onResponseListener
            window.show(from.supportFragmentManager, "verify")
        }
    }


    fun startEASActivity(from: Context) {
        val i = Intent(from, EASActivity::class.java)
        from.startActivity(i)
    }

    fun startTimetableManager(from: Context) {
        val i = Intent(from, TimetableManagerActivity::class.java)
        from.startActivity(i)
    }

    fun startSubjectActivity(from: Context, id: String) {
        val i = Intent(from, SubjectActivity::class.java)
        i.putExtra("subjectId", id)
        from.startActivity(i)
    }

    fun startTimetableDetailActivity(from: Context, id: String) {
        val i = Intent(from, TimetableDetailActivity::class.java)
        i.putExtra("id", id)
        from.startActivity(i)
    }
}