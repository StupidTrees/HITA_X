package com.stupidtree.hita.utils

import android.content.Context
import android.content.Intent
import com.stupidtree.hita.ui.eas.EASActivity
import com.stupidtree.hita.ui.eas.login.LoginEASActivity
import com.stupidtree.hita.ui.myprofile.MyProfileActivity
import com.stupidtree.hita.ui.subject.SubjectActivity
import com.stupidtree.hita.ui.timetable.TimetableManagerActivity
import com.stupidtree.hita.ui.welcome.WelcomeActivity


object ActivityUtils {

    fun startMyProfileActivity(from:Context){
        val i = Intent(from, MyProfileActivity::class.java)
        from.startActivity(i)
    }

    fun startWelcomeActivity(from:Context){
        val i = Intent(from,WelcomeActivity::class.java)
        from.startActivity(i)
    }

    fun startLoginEASActivity(from: Context){
        val i = Intent(from,LoginEASActivity::class.java)
        from.startActivity(i)
    }

    fun startEASActivity(from: Context){
        val i = Intent(from, EASActivity::class.java)
        from.startActivity(i)
    }

    fun startTimetableManager(from:Context){
        val i = Intent(from,TimetableManagerActivity::class.java)
        from.startActivity(i)
    }

    fun startSubjectActivity(from:Context,id:String){
        val i = Intent(from,SubjectActivity::class.java)
        i.putExtra("subjectId",id)
        from.startActivity(i)
    }
}