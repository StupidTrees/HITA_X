package com.stupidtree.hitax.ui.widgets.pullextend

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.NestedScrollingChild
import androidx.recyclerview.widget.RecyclerView

class mRecyclerView : RecyclerView, NestedScrollingChild {
    private val lastMotionY = -1f

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    } //    @Override
    //    public boolean onTouchEvent(MotionEvent e) {
    //        ViewParent p = getParent();
    //        PullExtendLayout parent;
    //        if (p instanceof PullExtendLayout) {
    //            parent = (PullExtendLayout) p;
    //        } else return super.onTouchEvent(e);
    //        switch (e.getAction()) {
    //            //触摸开始、结束时，更新父布局状态
    //            case MotionEvent.ACTION_DOWN:
    //                lastMotionY = e.getY();
    //                parent.onTouchEvent(e);
    //                break;
    ////            case MotionEvent.ACTION_CANCEL:
    //            case MotionEvent.ACTION_UP:
    //                lastMotionY = -1;
    //                parent.onTouchEvent(e);
    //                break;
    //            //触摸滑动时，若已达上下滑动边界，则只调用父布局的动作
    //            //若未达上下边界，还是要更新以下父布局的状态（上次触摸位置）防止效果错位
    //            case MotionEvent.ACTION_MOVE:
    //                float delta = e.getY() - lastMotionY;
    //                lastMotionY = e.getY();
    //                //    Log.e("mymymy!", String.valueOf(parent.getHeaderProgress()));
    //                if (delta > 0 && !canScrollVertically(-1)) {
    //                    parent.onTouchEvent(e);
    //                    return true;
    //                } else if (delta < 0 && (parent.getHeaderProgress() > 0f || !canScrollVertically(1))) {
    //                    if (parent.getHeaderProgress() > 0f) {
    //                        parent.callWhenChildScrolled(e);
    //                        parent.pullHeaderLayout(delta / parent.mOffsetRadio);
    //                        return true;
    //                    } else {
    //                        parent.pullFooterLayout(delta);
    //                    }
    //                }
    //                parent.callWhenChildScrolled(e);
    //                break;
    //        }
    //        return super.onTouchEvent(e);
    //
    //    }
}