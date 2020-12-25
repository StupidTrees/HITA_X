package com.stupidtree.hita.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import com.stupidtree.hita.ui.eas.EASActivity
import com.stupidtree.hita.ui.eas.login.LoginEASActivity



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