////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package com.stupidtree.hita.ui.widgets;
//
//import android.animation.Animator;
//import android.animation.Animator.AnimatorListener;
//import android.animation.ValueAnimator;
//import android.animation.ValueAnimator.AnimatorUpdateListener;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.Path.Direction;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.Region.Op;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.view.animation.Interpolator;
//import android.widget.ImageButton;
//
//import androidx.core.view.animation.PathInterpolatorCompat;
//
//import io.rmiri.buttonloading.ButtonLoadingAttribute;
//import io.rmiri.buttonloading.R.styleable;
//import io.rmiri.buttonloading.utils.DeviceScreenUtils;
//import io.rmiri.buttonloading.utils.FontUtils;
//
//public class ButtonLoading extends View {
//    private ButtonLoadingAttribute attribute = new ButtonLoadingAttribute();
//    private ButtonLoading.OnButtonLoadingListener onButtonLoadingListener;
//    private int height;
//    private int width;
//    private Paint paint;
//    private RectF rect;
//    Rect RectBoundCanvas;
//    Point point;
//    private ValueAnimator valueAnimatorCircleMain;
//    private ValueAnimator valueAnimatorCircleSecond;
//    private int valueAnimation1 = 0;
//    private int valueAnimation2 = 0;
//    private float fractionAnimation1 = 0.0F;
//    private float fractionAnimation2 = 0.0F;
//    private float fractionAnimation3 = 0.0F;
//    private boolean isNeedFinishAnimation;
//    private boolean isNeedAnimationBackground;
//    ViewGroup parentView;
//    ViewGroup rootView;
//    LayoutParams layoutParams;
//    ImageButton buttonGetTouch;
//
//    public ButtonLoading(Context context) {
//        super(context);
//        this.initView(context, null);
//    }
//
//    public ButtonLoading(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        this.initView(context, attrs);
//    }
//
//    public ButtonLoading(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.initView(context, attrs);
//    }
//
//    public void initView(Context context, AttributeSet attrs) {
//        if (!this.isInEditMode()) {
//            TypedArray typedArray = context.obtainStyledAttributes(attrs, styleable.buttonLoading);
//            this.attribute.setEnable(typedArray.getBoolean(styleable.buttonLoading_BL_enable, true));
//            this.attribute.setIdParent(typedArray.getResourceId(styleable.buttonLoading_BL_idParent, 0));
//            this.attribute.setFont(typedArray.getString(styleable.buttonLoading_BL_font));
//            this.attribute.setText(typedArray.getString(styleable.buttonLoading_BL_text));
//            this.attribute.setTextColor(typedArray.getColor(styleable.buttonLoading_BL_textColor, -1));
//            this.attribute.setBackgroundColor(typedArray.getColor(styleable.buttonLoading_BL_backgroundColor, Color.parseColor("#80ffffff")));
//            this.attribute.setCircleColor(typedArray.getColor(styleable.buttonLoading_BL_circleColor, Color.parseColor("#00AFEF")));
//            this.attribute.setCircleColorSecond(typedArray.getColor(styleable.buttonLoading_BL_circleColorSecond, Color.parseColor("#8000AFEF")));
//            this.attribute.setBackgroundDisableColor(typedArray.getColor(styleable.buttonLoading_BL_backgroundDisableColor, Color.parseColor("#f2f2f2")));
//            this.attribute.setTextSize(typedArray.getDimensionPixelSize(styleable.buttonLoading_BL_textSize, 14));
//            this.attribute.setStateShow(typedArray.getInt(styleable.buttonLoading_BL_stateShow, 1));
//            typedArray.recycle();
//            this.paint = new Paint();
//            this.paint.setAntiAlias(true);
//            this.rect = new RectF();
//            this.point = new Point();
//            this.RectBoundCanvas = new Rect();
//            this.buttonGetTouch = new ImageButton(this.getContext());
//            this.buttonGetTouch.setBackgroundColor(0);
//            this.layoutParams = new LayoutParams(-1, -1);
//            this.buttonGetTouch.setOnClickListener(new OnClickListener() {
//                public void onClick(View view) {
//                    Log.i("setOnClickListener", "buttonGetTouch get touch");
//                }
//            });
//            this.setOnClickListener(new OnClickListener() {
//                public void onClick(View view) {
//                    if (ButtonLoading.this.onButtonLoadingListener != null && ButtonLoading.this.attribute.isEnable() && ButtonLoading.this.attribute.getStateShow() == 1) {
//                        ButtonLoading.this.setProgress(true);
//                        ButtonLoading.this.onButtonLoadingListener.onClick();
//                    }
//
//                }
//            });
//        }
//    }
//
//    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
//        super.onSizeChanged(xNew, yNew, xOld, yOld);
//        if (this.width == 0) {
//            this.width = xNew;
//            this.height = yNew;
//        }
//
//    }
//
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        if (this.parentView == null) {
//            if (this.attribute.getIdParent() == 0) {
//                this.parentView = (ViewGroup)this.getParent();
//            } else {
//                this.getRootView(this);
//            }
//        }
//
//    }
//
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        int rectWAlpha;
//        int rectHAlpha;
//        int leftAlpha;
//        int topAlpha;
//        int rightAlpha;
//        int bottomAlpha;
//        switch(this.attribute.getStateShow()) {
//            case 1:
//                if (this.attribute.isEnable()) {
//                    this.paint.setColor(this.attribute.getCircleColor());
//                } else {
//                    this.paint.setColor(this.attribute.getBackgroundDisableColor());
//                }
//
//                this.rect.set(0.0F, 0.0F, (float)this.width, (float)this.height);
//                canvas.drawRoundRect(this.rect, (float)(this.height / 2), (float)(this.height / 2), this.paint);
//                this.paintText();
//                canvas.drawText(this.attribute.getText(), (float)this.getXPositionByText(canvas), (float)this.getYPositionByText(canvas), this.paint);
//                break;
//            case 2:
//                this.paint.setColor(this.attribute.getCircleColor());
//                this.point.set(getWidth() / 2, canvas.getHeight() / 2);
//                rectWAlpha = this.valueAnimation1;
//                rectHAlpha = this.height;
//                leftAlpha = this.point.x - rectWAlpha / 2;
//                topAlpha = this.point.y - rectHAlpha / 2;
//                rightAlpha = this.point.x + rectWAlpha / 2;
//                bottomAlpha = this.point.y + rectHAlpha / 2;
//                this.rect.set((float)leftAlpha, (float)topAlpha, (float)rightAlpha, (float)bottomAlpha);
//                canvas.drawRoundRect(this.rect, (float)(this.height / 2), (float)(this.height / 2), this.paint);
//                this.paintText();
//                this.paint.setAlpha((int)((1.0F - this.fractionAnimation1) * 255.0F));
//                canvas.drawText(this.attribute.getText(), (float)this.getXPositionByText(canvas), (float)this.getYPositionByText(canvas), this.paint);
//                break;
//            case 3:
//                canvas.getClipBounds(this.RectBoundCanvas);
//                this.RectBoundCanvas.inset(-DeviceScreenUtils.width(this.getContext()), -DeviceScreenUtils.height(this.getContext()));
//                if(Build.VERSION.SDK_INT >= 26){
//                    canvas.clipRect(this.RectBoundCanvas);
//                }else {
//                    canvas.clipRect(this.RectBoundCanvas, Op.REPLACE);
//                }
//
//                this.paint.setColor(this.attribute.getBackgroundColor());
//                this.point.set(canvas.getWidth() / 2, canvas.getHeight() / 2);
//                if (!this.isNeedAnimationBackground) {
//                    this.rect.set(-this.getX(), -this.getY(), (float) DeviceScreenUtils.width(this.getContext()), (float) DeviceScreenUtils.height(this.getContext()));
//                    canvas.drawRoundRect(this.rect, 0.0F, 0.0F, this.paint);
//                } else {
//                    rectWAlpha = (int)((float)(2 * DeviceScreenUtils.height(this.getContext())) * this.fractionAnimation3);
//                    rectHAlpha = (int)((float)(2 * DeviceScreenUtils.height(this.getContext())) * this.fractionAnimation3);
//                    leftAlpha = this.point.x - rectWAlpha / 2;
//                    topAlpha = this.point.y - rectHAlpha / 2;
//                    rightAlpha = this.point.x + rectWAlpha / 2;
//                    bottomAlpha = this.point.y + rectHAlpha / 2;
//                    this.rect.set((float)leftAlpha, (float)topAlpha, (float)rightAlpha, (float)bottomAlpha);
//                    Path ovalPath = new Path();
//                    ovalPath.addOval(this.rect, Direction.CW);
//                    canvas.drawPath(ovalPath, this.paint);
//                }
//
//                this.paint.setColor(this.attribute.getCircleColorSecond());
//                rectWAlpha = (int)((float)this.height * this.fractionAnimation2);
//                rectHAlpha = (int)((float)this.height * this.fractionAnimation2);
//                leftAlpha = this.point.x - rectWAlpha / 2;
//                topAlpha = this.point.y - rectHAlpha / 2;
//                rightAlpha = this.point.x + rectWAlpha / 2;
//                bottomAlpha = this.point.y + rectHAlpha / 2;
//                this.rect.set((float)leftAlpha, (float)topAlpha, (float)rightAlpha, (float)bottomAlpha);
//                canvas.drawRoundRect(this.rect, 100.0F, 100.0F, this.paint);
//                this.paint.setColor(this.attribute.getCircleColor());
//                int rectW = this.valueAnimation1;
//                int rectH = this.height;
//                int left = this.point.x - rectW / 2;
//                int top = this.point.y - rectH / 2;
//                int right = this.point.x + rectW / 2;
//                int bottom = this.point.y + rectH / 2;
//                this.rect.set((float)left, (float)top, (float)right, (float)bottom);
//                canvas.scale(this.fractionAnimation1, this.fractionAnimation1, (float)(canvas.getWidth() / 2), (float)(canvas.getHeight() / 2));
//                canvas.drawRoundRect(this.rect, (float)(this.height / 2), (float)(this.height / 2), this.paint);
//                break;
//            case 4:
//                this.paint.setColor(this.attribute.getCircleColor());
//                this.point.set(canvas.getWidth() / 2, canvas.getHeight() / 2);
//                rectWAlpha = this.valueAnimation2;
//                rectHAlpha = this.height;
//                leftAlpha = this.point.x - rectWAlpha / 2;
//                topAlpha = this.point.y - rectHAlpha / 2;
//                rightAlpha = this.point.x + rectWAlpha / 2;
//                bottomAlpha = this.point.y + rectHAlpha / 2;
//                this.rect.set((float)leftAlpha, (float)topAlpha, (float)rightAlpha, (float)bottomAlpha);
//                canvas.drawRoundRect(this.rect, (float)(this.height / 2), (float)(this.height / 2), this.paint);
//                this.paintText();
//                this.paint.setAlpha((int)(this.fractionAnimation2 * 255.0F));
//                canvas.drawText(this.attribute.getText(), (float)this.getXPositionByText(canvas), (float)this.getYPositionByText(canvas), this.paint);
//                canvas.scale(this.fractionAnimation1, this.fractionAnimation1, (float)(canvas.getWidth() / 2), (float)(canvas.getHeight() / 2));
//        }
//
//    }
//
//    private void paintText() {
//        this.paint.setColor(this.attribute.getTextColor());
//        this.paint.setTextSize((float)this.attribute.getTextSize());
//        if (this.attribute.getFont() != null && !this.attribute.getFont().isEmpty()) {
//            this.paint.setTypeface(FontUtils.getTypeface(this.getContext(), this.attribute.getFont()));
//        }
//
//    }
//
//    int getXPositionByText(Canvas canvas) {
//        return canvas.getWidth() / 2 - (int)this.paint.measureText(this.attribute.getText()) / 2;
//    }
//
//    int getYPositionByText(Canvas canvas) {
//        return (int)((float)(canvas.getHeight() / 2) - (this.paint.descent() + this.paint.ascent()) / 2.0F);
//    }
//
//    public void setProgress(boolean isProgressing) {
//        if (isProgressing) {
//            if (this.attribute.getStateShow() != 1) {
//                return;
//            }
//
//            this.animationStart();
//            this.parentView.addView(this.buttonGetTouch, this.layoutParams);
//
//        } else {
//            if (this.attribute.getStateShow() == 1) {
//                return;
//            }
//
//            this.isNeedFinishAnimation = true;
//        }
//
//    }
//
//    void animationStart() {
//        this.attribute.setStateShow(2);
//        if (this.onButtonLoadingListener != null) {
//            this.onButtonLoadingListener.onStart();
//        }
//
//        ValueAnimator valueAnimatorLoading = ValueAnimator.ofInt(this.width, this.height);
//        valueAnimatorLoading.setInterpolator(PathInterpolatorCompat.create(0.645F, 0.045F, 0.355F, 1.0F));
//        valueAnimatorLoading.setDuration(225L);
//        valueAnimatorLoading.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                ButtonLoading.this.valueAnimation1 = (Integer)valueAnimator.getAnimatedValue();
//                ButtonLoading.this.fractionAnimation1 = valueAnimator.getAnimatedFraction();
//                ButtonLoading.this.invalidate();
//            }
//        });
//        valueAnimatorLoading.start();
//        valueAnimatorLoading.addListener(new AnimatorListener() {
//            public void onAnimationStart(Animator animation) {
//            }
//
//            public void onAnimationEnd(Animator animation) {
//                ButtonLoading.this.animationProgress();
//            }
//
//            public void onAnimationCancel(Animator animation) {
//            }
//
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//    }
//
//    @SuppressLint("WrongConstant")
//    void animationProgress() {
//        this.attribute.setStateShow(3);
//        this.fractionAnimation1 = 0.0F;
//        this.fractionAnimation2 = 0.0F;
//        this.fractionAnimation3 = 0.0F;
//        this.isNeedAnimationBackground = true;
//        this.bringToFront();
//        final int[] countRepeat = new int[]{0};
//        Interpolator pathInterpolatorCompat = PathInterpolatorCompat.create(0.455F, 0.03F, 0.515F, 0.955F);
//        this.valueAnimatorCircleMain = ValueAnimator.ofFloat(1.0F, 0.6F);
//        this.valueAnimatorCircleMain.setRepeatCount(-1);
//        this.valueAnimatorCircleMain.setRepeatMode(2);
//        this.valueAnimatorCircleMain.setInterpolator(pathInterpolatorCompat);
//        this.valueAnimatorCircleMain.setDuration(525L);
//        this.valueAnimatorCircleMain.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                ButtonLoading.this.fractionAnimation1 = (Float)animation.getAnimatedValue();
//                ButtonLoading.this.invalidate();
//            }
//        });
//        this.valueAnimatorCircleMain.start();
//        this.valueAnimatorCircleMain.addListener(new AnimatorListener() {
//            public void onAnimationStart(Animator animator) {
//            }
//
//            public void onAnimationEnd(Animator animator) {
//            }
//
//            public void onAnimationCancel(Animator animator) {
//            }
//
//            public void onAnimationRepeat(Animator animator) {
//                if (countRepeat[0] % 2 != 0 && ButtonLoading.this.isNeedAnimationBackground) {
//                    ButtonLoading.this.isNeedAnimationBackground = false;
//                }
//
//            }
//        });
//        this.valueAnimatorCircleSecond = ValueAnimator.ofFloat(0.6F, 1.4F);
//        this.valueAnimatorCircleSecond.setRepeatCount(-1);
//        this.valueAnimatorCircleSecond.setRepeatMode(2);
//        this.valueAnimatorCircleSecond.setStartDelay(525L);
//        this.valueAnimatorCircleSecond.setInterpolator(pathInterpolatorCompat);
//        this.valueAnimatorCircleSecond.setDuration(525L);
//        this.valueAnimatorCircleSecond.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                ButtonLoading.this.fractionAnimation2 = (Float)animation.getAnimatedValue();
//                ButtonLoading.this.fractionAnimation3 = animation.getAnimatedFraction();
//            }
//        });
//        this.valueAnimatorCircleSecond.start();
//        this.valueAnimatorCircleMain.addListener(new AnimatorListener() {
//            public void onAnimationStart(Animator animator) {
//            }
//
//            public void onAnimationEnd(Animator animator) {
//            }
//
//            public void onAnimationCancel(Animator animator) {
//            }
//
//            public void onAnimationRepeat(Animator animator) {
//                int var10002 = countRepeat[0]++;
//                if (countRepeat[0] % 2 != 0 & ButtonLoading.this.isNeedFinishAnimation) {
//                    ButtonLoading.this.animationFinish();
//                } else if (countRepeat[0] % 2 == 0 & ButtonLoading.this.isNeedFinishAnimation) {
//                    ButtonLoading.this.isNeedAnimationBackground = true;
//                }
//
//            }
//        });
//    }
//
//    void animationFinish() {
//        this.valueAnimatorCircleMain.removeAllListeners();
//        this.valueAnimatorCircleMain.end();
//        this.valueAnimatorCircleMain.cancel();
//        this.valueAnimatorCircleSecond.removeAllListeners();
//        this.valueAnimatorCircleSecond.end();
//        this.valueAnimatorCircleSecond.cancel();
//        this.attribute.setStateShow(4);
//        this.fractionAnimation1 = 0.0F;
//        ValueAnimator valueAnimatorFinish = ValueAnimator.ofInt(this.height, this.width);
//        valueAnimatorFinish.setInterpolator(PathInterpolatorCompat.create(0.645F, 0.045F, 0.355F, 1.0F));
//        valueAnimatorFinish.setDuration(225L);
//        valueAnimatorFinish.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                ButtonLoading.this.valueAnimation2 = (Integer)valueAnimator.getAnimatedValue();
//                ButtonLoading.this.fractionAnimation2 = valueAnimator.getAnimatedFraction();
//                ButtonLoading.this.invalidate();
//            }
//        });
//        valueAnimatorFinish.start();
//        valueAnimatorFinish.addListener(new AnimatorListener() {
//            public void onAnimationStart(Animator animation) {
//            }
//
//            public void onAnimationEnd(Animator animation) {
//                ButtonLoading.this.attribute.setStateShow(1);
//                ButtonLoading.this.parentView.removeView(ButtonLoading.this.buttonGetTouch);
//                ButtonLoading.this.isNeedFinishAnimation = false;
//                if (ButtonLoading.this.onButtonLoadingListener != null) {
//                    ButtonLoading.this.onButtonLoadingListener.onFinish();
//                }
//
//            }
//
//            public void onAnimationCancel(Animator animation) {
//            }
//
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//    }
//
//    public void getRootView(View v) {
//        while(v.getParent() != null && v.getParent() instanceof ViewGroup) {
//            ViewGroup viewGroup = (ViewGroup) v.getParent();
//            v = viewGroup;
//            this.rootView = viewGroup;
//        }
//
//        this.setParentView(this.rootView);
//    }
//
//    private void setParentView(View v) {
//        ViewGroup viewgroup = (ViewGroup)v;
//        if (viewgroup.getId() == this.attribute.getIdParent()) {
//            this.parentView = viewgroup;
//        }
//
//        for(int i = 0; i < viewgroup.getChildCount(); ++i) {
//            View v1 = viewgroup.getChildAt(i);
//            if (v1 instanceof ViewGroup) {
//                this.setParentView(v1);
//            }
//        }
//
//    }
//
//    public void setText(String text) {
//        this.attribute.setText(text);
//        this.requestLayout();
//    }
//
//    public String getText() {
//        return this.attribute.getText();
//    }
//
//    public void setOnButtonLoadingListener(ButtonLoading.OnButtonLoadingListener onButtonLoadingListener) {
//        this.onButtonLoadingListener = onButtonLoadingListener;
//    }
//
//    public int getState() {
//        return this.attribute.getStateShow();
//    }
//
//    public boolean isLoadingState() {
//        return this.attribute.getStateShow() != 1;
//    }
//
//    public interface OnButtonLoadingListener {
//        void onClick();
//
//        void onStart();
//
//        void onFinish();
//    }
//}
