package com.stupidtree.hita.utils

import android.content.Context
import android.content.Intent
import com.stupidtree.hita.ui.eas.EASActivity
import com.stupidtree.hita.ui.eas.login.LoginEASActivity
import com.stupidtree.hita.ui.main.timetable.activity.TimetableFragment


object ActivityUtils {

    fun startLoginEASActivity(from: Context){
        val i = Intent(from,LoginEASActivity::class.java)
        from.startActivity(i)
    }

    fun startEASActivity(from: Context){
        val i = Intent(from, EASActivity::class.java)
        from.startActivity(i)
    }
}