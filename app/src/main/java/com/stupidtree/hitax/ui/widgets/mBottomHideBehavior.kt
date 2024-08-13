package com.stupidtree.hitax.ui.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.animation.AnimationUtils

class mBottomHideBehavior<V : View> : CoordinatorLayout.Behavior<V> {
    private var height = 0
    private var currentState = 2
    private var currentAnimator: ViewPropertyAnimator? = null
    private var fabSlideEnable = true

    constructor() {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun setFabSlideEnable(fabSlideEnable: Boolean) {
        this.fabSlideEnable = fabSlideEnable
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: V,
        layoutDirection: Int
    ): Boolean {
        height = child.measuredHeight
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    //    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int nestedScrollAxes) {
    //        return nestedScrollAxes == 2;
    //    }
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == 2
        //  return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    //    public void onNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    //        if (this.currentState != 1 && dyConsumed > 0) {
    //            this.slideDown(child);
    //        } else if (this.currentState != 2 && dyConsumed < 0) {
    //            this.slideUp(child);
    //        }
    //    }
    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (!fabSlideEnable) return
        if (currentState != 1 && dyConsumed > 0) {
            slideDown(child)
        } else if (currentState != 2 && dyConsumed < 0) {
            slideUp(child)
        }
        //super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
    }

    @SuppressLint("RestrictedApi")
    fun slideUp(child: V) {
        if (currentAnimator != null) {
            currentAnimator!!.cancel()
            child.clearAnimation()
        }
        currentState = 2
        animateChildTo(child, 0, 225L, AnimationUtils.DECELERATE_INTERPOLATOR)
    }

    @SuppressLint("RestrictedApi")
    fun slideDown(child: V) {
        if (currentAnimator != null) {
            currentAnimator!!.cancel()
            child.clearAnimation()
        }
        currentState = 1
        animateChildTo(child, height + 100, 175L, AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR)
    }

    private fun animateChildTo(
        child: V,
        targetY: Int,
        duration: Long,
        interpolator: TimeInterpolator
    ) {
        currentAnimator =
            child.animate().translationY(targetY.toFloat()).setInterpolator(interpolator)
                .setDuration(duration).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        currentAnimator = null
                    }
                })
    }
}