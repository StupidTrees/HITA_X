package com.stupidtree.hitax.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation


/**
 * 动画控制函数
 */
object AnimationUtils {
    /**
     * 旋转view
     *
     * @param view view
     * @param down 是否转向下
     */
    fun rotateTo(view: View, down: Boolean) {
        val fromD: Float
        val toD: Float
        if (down) {
            fromD = 0f
            toD = 180f
        } else {
            fromD = 180f
            toD = 0f
        }
        val ra = RotateAnimation(fromD, toD, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        ra.interpolator = DecelerateInterpolator()
        ra.duration = 400 //设置动画持续周期
        ra.repeatCount = 0 //设置重复次数
        ra.fillAfter = true //动画执行完后是否停留在执行完的状态
        view.animation = ra
        view.startAnimation(ra)
    }

    fun rotate(view: View) {
        val fromD = 0f
        val toD = 360f
        val ra = RotateAnimation(fromD, toD, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        ra.interpolator = DecelerateInterpolator()
        ra.duration = 400 //设置动画持续周期
        ra.repeatCount = 0 //设置重复次数
        ra.fillAfter = true //动画执行完后是否停留在执行完的状态
        view.animation = ra
        view.startAnimation(ra)
    }

    /**
     * 顺时针旋转view(90°)
     *
     * @param view view
     * @param down 是否向下
     */
    fun rotateRightQuarterTo(view: View, down: Boolean) {
        val fromD: Float
        val toD: Float
        if (down) {
            fromD = 0f
            toD = 90f
        } else {
            fromD = 90f
            toD = 0f
        }
        val va = ValueAnimator.ofFloat(fromD, toD)
        va.duration = 360
        va.interpolator = DecelerateInterpolator()
        va.addUpdateListener { valueAnimator: ValueAnimator -> view.rotation = (valueAnimator.animatedValue as Float) }
        va.start()
    }

    @SuppressLint("WrongConstant")
     fun floatAnim(view: View, delay: Int) {
        val animators: MutableList<Animator> = ArrayList()
        val translationXAnim = ObjectAnimator.ofFloat(view, "translationX", -6.0f, 6.0f, -6.0f)
        translationXAnim.duration = 2200
        translationXAnim.repeatCount = ValueAnimator.INFINITE //无限循环
        translationXAnim.repeatMode = -1
        //translationXAnim.interpolator = DecelerateInterpolator()
        translationXAnim.start()

        animators.add(translationXAnim)
        val translationYAnim = ObjectAnimator.ofFloat(view, "translationY", -24.0f, 24.0f, -24.0f)
        translationYAnim.duration = 4000
        translationYAnim.repeatCount = ValueAnimator.INFINITE
        translationYAnim.repeatMode = -1
       // translationYAnim.interpolator = DecelerateInterpolator()
        translationYAnim.start()
        animators.add(translationYAnim)
        val btnSexAnimatorSet = AnimatorSet()
        btnSexAnimatorSet.playTogether(animators)
        btnSexAnimatorSet.startDelay = delay.toLong()
        btnSexAnimatorSet.start()
    }
}