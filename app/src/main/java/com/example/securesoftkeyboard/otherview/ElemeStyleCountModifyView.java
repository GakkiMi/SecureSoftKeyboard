package com.example.securesoftkeyboard.otherview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securesoftkeyboard.R;

/**
 * 文 件 名：ElemeCountModifyView
 * 描   述：
 */
public class ElemeStyleCountModifyView extends LinearLayout {

    private static final String TAG = "ElemeStyleCountModify";


    private RelativeLayout rlRootView;
    private ImageView ivNumJian;
    private ImageView ivNumJia;
    private ImageView ivNumJiaCannot;
    private TextView tvNumShow;

    private boolean alignParentRight;
    private boolean defaultAlignParentRight = false;


    private double maxCount = 0.0;//最大限制数（会出现小数）
    private int currentCount = 0;//当前数量
    private int multiple = 1;//倍数   默认为1

    //打开动画
    private AnimatorSet openAnimatorSet;
    //关闭动画
    private AnimatorSet closeAnimatorSet;

    private boolean isToAnima = false;//是否需要执行动画

    private Context mContext;

    //根布局宽度
    private int rootViewWidthDp = 65;
    //显示textview宽度
    private int tvNumShowWidthDp = 25;
    //减号宽度
    private int ivJianWidthDp = 20;

    public ElemeStyleCountModifyView(Context context) {
        this(context, null);
    }

    public ElemeStyleCountModifyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElemeStyleCountModifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ElemeStyleCountModifyView, defStyleAttr, 0);
        alignParentRight = a.getBoolean(R.styleable.ElemeStyleCountModifyView_alignParentRight, defaultAlignParentRight);

        a.recycle();
        initView(context);
        mContext = context;
    }

    private void initView(Context context) {
        View viewLayout = LayoutInflater.from(context).inflate(R.layout.view_goods_count_modify_eleme_style, this, true);
        rlRootView = viewLayout.findViewById(R.id.root_view);
        ivNumJian = viewLayout.findViewById(R.id.iv_sub_car_eleme_style);
        ivNumJia = viewLayout.findViewById(R.id.iv_add_car_eleme_style);
        ivNumJiaCannot = viewLayout.findViewById(R.id.iv_add_cannnot_car_eleme_style);
        tvNumShow = viewLayout.findViewById(R.id.tv_goods_buy_count_eleme_style);

        if (alignParentRight) {
            setAlignParentRight(ivNumJian);
            setAlignParentRight(ivNumJia);
            setAlignParentRight(ivNumJiaCannot);
            setAlignParentRight(tvNumShow);
        }


        ivNumJian.setOnClickListener(view -> {
            int showNum = currentCount;
            if (showNum - (1 * multiple) >= 0) {
                int modifyGoodsNum = showNum - (1 * multiple);
                if (modifyGoodsNum == 0) {
                    isToAnima = true;
                } else {
                    isToAnima = false;
                }
                goodsCountModifyImp.modifyCount(modifyGoodsNum);
            } else {//如果减完倍数后小于0 则修改的数量变为0  eg：1-（1*2）<0 不够减 则直接变为0
                int modifyGoodsNum = 0;
                if (modifyGoodsNum == 0) {
                    isToAnima = true;
                } else {
                    isToAnima = false;
                }
                goodsCountModifyImp.modifyCount(modifyGoodsNum);
            }
        });
        ivNumJia.setOnClickListener(view -> {
            int showNum = currentCount;
            if (showNum + (1 * multiple) <= maxCount) {
                int modifyGoodsNum = showNum + (1 * multiple);
                if (modifyGoodsNum == (1 * multiple)) {
                    isToAnima = true;
                } else {
                    isToAnima = false;
                }
                goodsCountModifyImp.modifyCount(modifyGoodsNum);
            } else {
                showToastyCenter(getContext(), "不能再多了");
            }
        });
        ivNumJiaCannot.setOnClickListener(view -> {
            showToastyCenter(getContext(), "库存不足");
        });

    }

    private void setAlignParentRight(View view) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    }

    /**
     * 展开动画 从右到左
     *
     * @param duration 动画时长
     */
    private void openAnimatorRtoL(long duration) {
        Log.i("eleme", "-------openAnimator-" + rlRootView.getMeasuredWidth() + "---" + ivNumJian.getMeasuredWidth() + "-------" + tvNumShow.getMeasuredWidth());

        if (openAnimatorSet == null) {
            ObjectAnimator ivTranslation = ObjectAnimator.ofFloat(ivNumJian, "TranslationX", 0, (DisPlayUtils.dip2px(mContext, rootViewWidthDp) - DisPlayUtils.dip2px(mContext, ivJianWidthDp)) * -1f);
            ObjectAnimator ivRotation = ObjectAnimator.ofFloat(ivNumJian, "Rotation", 0, -360);
            ObjectAnimator ivAlpha = ObjectAnimator.ofFloat(ivNumJian, "Alpha", 0, 1f);
            ObjectAnimator tvTranslation = ObjectAnimator.ofFloat(tvNumShow, "TranslationX", 0, (DisPlayUtils.dip2px(mContext, rootViewWidthDp) - DisPlayUtils.dip2px(mContext, ivJianWidthDp) - DisPlayUtils.dip2px(mContext, tvNumShowWidthDp)) * -1f);
            ObjectAnimator tvRotation = ObjectAnimator.ofFloat(tvNumShow, "Rotation", 0, 0);
            ObjectAnimator tvAlpha = ObjectAnimator.ofFloat(tvNumShow, "Alpha", 0, 1f);
            openAnimatorSet = new AnimatorSet();
            openAnimatorSet.setInterpolator(new LinearInterpolator());
            openAnimatorSet.playTogether(ivTranslation, ivRotation, ivAlpha, tvTranslation, tvRotation, tvAlpha);

            openAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ivNumJian.setVisibility(VISIBLE);
                    tvNumShow.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ivNumJian.clearAnimation();
                    tvNumShow.clearAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        openAnimatorSet.setDuration(duration);
        openAnimatorSet.start();
    }

    /**
     * 收起动画 从左到右
     *
     * @param duration 动画时长
     */
    private void closeAnimatorLtoR(long duration) {
        Log.i("eleme", "-------closeAnimator-" + rlRootView.getMeasuredWidth() + "---" + ivNumJian.getMeasuredWidth() + "-------" + tvNumShow.getMeasuredWidth());
        if (closeAnimatorSet == null) {
            ObjectAnimator ivTranslation = ObjectAnimator.ofFloat(ivNumJian, "TranslationX", (DisPlayUtils.dip2px(mContext, rootViewWidthDp) - DisPlayUtils.dip2px(mContext, ivJianWidthDp)) * -1f, 0);
            ObjectAnimator ivRotation = ObjectAnimator.ofFloat(ivNumJian, "Rotation", -360, 0);
            ObjectAnimator ivAlpha = ObjectAnimator.ofFloat(ivNumJian, "Alpha", 1f, 0);
            ObjectAnimator tvTranslation = ObjectAnimator.ofFloat(tvNumShow, "TranslationX", (DisPlayUtils.dip2px(mContext, rootViewWidthDp) - DisPlayUtils.dip2px(mContext, ivJianWidthDp) - DisPlayUtils.dip2px(mContext, tvNumShowWidthDp)) * -1f, 0);
            ObjectAnimator tvRotation = ObjectAnimator.ofFloat(tvNumShow, "Rotation", 0, 0);
            ObjectAnimator tvAlpha = ObjectAnimator.ofFloat(tvNumShow, "Alpha", 1f, 0);

            closeAnimatorSet = new AnimatorSet();
            closeAnimatorSet.setInterpolator(new LinearInterpolator());
            closeAnimatorSet.playTogether(ivTranslation, ivRotation, ivAlpha, tvTranslation, tvRotation, tvAlpha);

            closeAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ivNumJian.setVisibility(GONE);
                    tvNumShow.setVisibility(GONE);
                    ivNumJian.clearAnimation();
                    tvNumShow.clearAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        closeAnimatorSet.setDuration(duration);
        closeAnimatorSet.start();
    }

    /**
     * 展开动画  从左到右
     *
     * @param duration 动画时长
     */
    private void openAnimatorLtoR(long duration) {
//        Log.i("eleme", "-------openAnimator-" + rlRootView.getMeasuredWidth() + "---" + ivNumJian.getMeasuredWidth() + "-------" + tvNumShow.getMeasuredWidth());
        if (openAnimatorSet == null) {
            ObjectAnimator ivTranslation = ObjectAnimator.ofFloat(ivNumJia, "TranslationX", 0, (DisPlayUtils.dip2px(mContext, rootViewWidthDp) - DisPlayUtils.dip2px(mContext, ivJianWidthDp)) * 1f);
            ObjectAnimator ivRotation = ObjectAnimator.ofFloat(ivNumJia, "Rotation", 0, 360);
            ObjectAnimator ivAlpha = ObjectAnimator.ofFloat(ivNumJian, "Alpha", 0, 1f);
            ObjectAnimator tvTranslation = ObjectAnimator.ofFloat(tvNumShow, "TranslationX", 0, (DisPlayUtils.dip2px(mContext, rootViewWidthDp) - DisPlayUtils.dip2px(mContext, ivJianWidthDp) - DisPlayUtils.dip2px(mContext, tvNumShowWidthDp)) * 1f);
            ObjectAnimator tvRotation = ObjectAnimator.ofFloat(tvNumShow, "Rotation", 0, 0);
            ObjectAnimator tvAlpha = ObjectAnimator.ofFloat(tvNumShow, "Alpha", 0, 1f);
            openAnimatorSet = new AnimatorSet();
            openAnimatorSet.setInterpolator(new LinearInterpolator());
            openAnimatorSet.playTogether(ivTranslation, ivRotation, ivAlpha, tvTranslation, tvRotation, tvAlpha);

            openAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ivNumJia.setVisibility(VISIBLE);
                    tvNumShow.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ivNumJia.clearAnimation();
                    tvNumShow.clearAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        openAnimatorSet.setDuration(duration);
        openAnimatorSet.start();
    }

    /**
     * 收起动画 从右到左
     *
     * @param duration 动画时长
     */
    private void closeAnimatorRtoL(long duration) {
//        Log.i("eleme", "-------closeAnimator-" + rlRootView.getMeasuredWidth() + "---" + ivNumJian.getMeasuredWidth() + "-------" + tvNumShow.getMeasuredWidth());
        if (closeAnimatorSet == null) {
            ObjectAnimator ivTranslation = ObjectAnimator.ofFloat(ivNumJia, "TranslationX", (DisPlayUtils.dip2px(mContext, rootViewWidthDp) - DisPlayUtils.dip2px(mContext, ivJianWidthDp)) * 1f, 0);
            ObjectAnimator ivRotation = ObjectAnimator.ofFloat(ivNumJia, "Rotation", 360, 0);
            ObjectAnimator ivAlpha = ObjectAnimator.ofFloat(ivNumJian, "Alpha", 1f,0 );
            ObjectAnimator tvTranslation = ObjectAnimator.ofFloat(tvNumShow, "TranslationX", (DisPlayUtils.dip2px(mContext, rootViewWidthDp) - DisPlayUtils.dip2px(mContext, ivJianWidthDp) - DisPlayUtils.dip2px(mContext, tvNumShowWidthDp)) * 1f, 0);
            ObjectAnimator tvRotation = ObjectAnimator.ofFloat(tvNumShow, "Rotation", 0, 0);
            ObjectAnimator tvAlpha = ObjectAnimator.ofFloat(tvNumShow, "Alpha", 1f, 0);

            closeAnimatorSet = new AnimatorSet();
            closeAnimatorSet.setInterpolator(new LinearInterpolator());
            closeAnimatorSet.playTogether(ivTranslation, ivRotation, ivAlpha, tvTranslation, tvRotation, tvAlpha);

            closeAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    tvNumShow.setVisibility(GONE);
                    ivNumJia.clearAnimation();
                    tvNumShow.clearAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        closeAnimatorSet.setDuration(duration);
        closeAnimatorSet.start();
    }




    /**
     * 设置数量（无动画效果）
     */
    public void setText() {
        if (maxCount >= 1) {
            ivNumJiaCannot.setVisibility(GONE);
            ivNumJia.setVisibility(VISIBLE);
            ivNumJian.setVisibility(VISIBLE);
            tvNumShow.setText(currentCount + "");
            int strLength = (currentCount + "").length();
//            Log.i(TAG, "---------strLength:" + strLength);
            if (strLength > 3) {
                tvNumShow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            } else if (strLength > 2) {
                tvNumShow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            } else {
                tvNumShow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            if (currentCount > 0) {
                if (alignParentRight) {
                    openAnimatorRtoL(0);
                } else {
                    openAnimatorLtoR(0);
                }
            } else {
                if (alignParentRight) {
                    closeAnimatorLtoR(0);
                } else {
                    closeAnimatorRtoL(0);
                }
            }
        } else {
            ivNumJiaCannot.setVisibility(VISIBLE);
            ivNumJia.setVisibility(GONE);
            ivNumJian.setVisibility(GONE);
            tvNumShow.setVisibility(GONE);
        }
    }

    /**
     * 设置数量（有动画效果）
     */
    public void setTextWithAnima(long duration) {
//        Log.i("eleme", "--------局部刷新" + isToAnima + "---" + currentCount);
        int strLength = (currentCount + "").length();
//        Log.i(TAG, "---------strLength:" + strLength);
        if (strLength > 3) {
            tvNumShow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        } else if (strLength > 2) {
            tvNumShow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        } else {
            tvNumShow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        tvNumShow.setText(currentCount + "");
        if (currentCount == 0 && isToAnima) {//减少到0才会执行
            if (alignParentRight) {
                closeAnimatorLtoR(duration);
            } else {
                closeAnimatorRtoL(duration);
            }

        } else if (currentCount == (1 * multiple) && isToAnima) {//增加到1才会执行
            if (alignParentRight) {
                openAnimatorRtoL(duration);
            } else {
                openAnimatorLtoR(duration);
            }
        }
    }


    public void setMaxCount(double maxCount) {
        this.maxCount = maxCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public ElemeGoodsCountModifyImp goodsCountModifyImp;

    public void setGoodsCountModifyImp(ElemeGoodsCountModifyImp goodsCountModifyImp) {
        this.goodsCountModifyImp = goodsCountModifyImp;
    }

    /**
     * 数量修改后暴露的接口
     */
    public interface ElemeGoodsCountModifyImp {
        void modifyCount(int modifyCount);
    }


    private static void showToastyCenter(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
