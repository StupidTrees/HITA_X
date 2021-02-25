package com.stupidtree.hitax.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation

object MaterialCircleAnimator {
    fun animShow(myView: View, duration: Int) {
        try {
            //duration*=1.7;
            // 从 View 的中心开始
            val cx = (myView.left + myView.right) / 2
            val cy = (myView.top + myView.bottom) / 2
            val finalRadius = Math.max(myView.width, myView.height)

            //为此视图创建动画设计(起始半径为零)
            val anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius.toFloat())
            // 使视图可见并启动动画
            anim.interpolator = DecelerateInterpolator(3f)
            myView.visibility = View.VISIBLE
            anim.duration = duration.toLong()
            anim.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun animShow(myView: View, duration: Int, startDelay: Int, factor: Float) {
        try {
            //duration*=1.7;
            // 从 View 的中心开始
            val cx = (myView.left + myView.right) / 2
            val cy = (myView.top + myView.bottom) / 2
            val finalRadius = Math.max(myView.width, myView.height)

            //为此视图创建动画设计(起始半径为零)
            val anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius.toFloat())
            // 使视图可见并启动动画
            anim.interpolator = DecelerateInterpolator(factor)
            anim.startDelay = startDelay.toLong()
            anim.duration = duration.toLong()
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    myView.visibility = View.VISIBLE
                }
            })
            anim.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun animShow(myView: View, duration: Int, x: Float, y: Float) {
        try {
            val finalRadius = Math.max(myView.width, myView.height)

            //为此视图创建动画设计(起始半径为零)
            val anim = ViewAnimationUtils.createCircularReveal(
                myView,
                x.toInt(),
                y.toInt(),
                0f,
                finalRadius.toFloat()
            )
            // 使视图可见并启动动画
            myView.visibility = View.VISIBLE
            anim.duration = duration.toLong()
            anim.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun animHide(myView: View) {
        try {
            val cx = (myView.left + myView.right) / 2
            val cy = (myView.top + myView.bottom) / 2
            val initialRadius = myView.width

            // 半径 从 viewWidth -> 0
            val anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius.toFloat(), 0f)
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    myView.visibility = View.INVISIBLE
                }
            })
            anim.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun animHide(myView: View, duration: Int, x: Float, y: Float) {
        try {
            val initialRadius = myView.width

            // 半径 从 viewWidth -> 0
            val anim = ViewAnimationUtils.createCircularReveal(
                myView,
                x.toInt(),
                y.toInt(),
                initialRadius.toFloat(),
                0f
            )
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    myView.visibility = View.INVISIBLE
                }
            })
            anim.duration = duration.toLong()
            anim.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun rotateTo(down: Boolean, view: View) {
        val fromD: Float
        val toD: Float
        if (down) {
            fromD = 0f
            toD = 180f
        } else {
            fromD = 180f
            toD = 0f
        }
        val ra = RotateAnimation(
            fromD,
            toD,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        ra.interpolator = DecelerateInterpolator()
        ra.duration = 400 //设置动画持续周期
        ra.repeatCount = 0 //设置重复次数
        ra.fillAfter = true //动画执行完后是否停留在执行完的状态
        view.animation = ra
        view.startAnimation(ra)
    }
}