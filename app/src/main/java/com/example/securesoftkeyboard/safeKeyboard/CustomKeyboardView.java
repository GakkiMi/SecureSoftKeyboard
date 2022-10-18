package com.example.securesoftkeyboard.safeKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import com.example.securesoftkeyboard.R;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by liuyu1 on 2017/8/2.
 */

public class CustomKeyboardView extends KeyboardView {
    private static final String TAG = "CustomKeyboardView";
    private Context context;
    private AttributeSet attrs;

    private Paint mStrokePaint;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        mStrokePaint = new Paint();
        mStrokePaint.setColor(Color.parseColor("#a7a7a7"));
        mStrokePaint.setStrokeWidth(6f);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);

    }


    @Override
    public void onDraw(Canvas canvas) {
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            if (key.codes[0] == 90002 || key.codes[0] == 00000) {
            } else {
                drawStroke(canvas, key);
            }
        }
        super.onDraw(canvas);
        try {
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == -5) {
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_del_layerlist_number);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                } else if (key.codes[0] == -35) {
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_del_layerlist_word);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                } else if (key.codes[0] == -36) {
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_del_layerlist_symbol);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                } else if (key.codes[0] == -1) {
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_shift_layerlist);
                    Drawable dr_da = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_shift_layerlist_da);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr_da.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    if (KeyboardDialog.isUpper) {
                        dr_da.draw(canvas);
                    } else {
                        dr.draw(canvas);
                    }
                } else if (key.codes[0] == -2 || key.codes[0] == 90001) {
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_selector_blue_bg);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    drawText(canvas, key);
                } else if (key.codes[0] == 90002 || key.codes[0] == 00000) {
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_transparent_bg);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    drawText(canvas, key);
                } else if (key.codes[0] == 90003) {
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_blue_bg);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    drawText(canvas, key, "#FFFFFF");
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawText(Canvas canvas, Keyboard.Key key) {
        drawText(canvas, key, "#39404C");
    }

    private void drawText(Canvas canvas, Keyboard.Key key, String color) {
        try {
            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor(color));
            if (key.label != null) {
                String label = key.label.toString();
                Field field;
                if (label.length() > 1 && key.codes.length < 2) {
                    int labelTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                        field.setAccessible(true);
                        labelTextSize = (int) field.get(this);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(labelTextSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    int keyTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                        field.setAccessible(true);
                        keyTextSize = (int) field.get(this);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setColor(Color.parseColor("#39404C"));
                    paint.setTextSize(keyTextSize);
                    paint.setTypeface(Typeface.DEFAULT);
                }

                paint.getTextBounds(key.label.toString(), 0, key.label.toString()
                        .length(), bounds);
                canvas.drawText(key.label.toString(), key.x + (key.width / 2),
                        (key.y + key.height / 2) + bounds.height() / 2, paint);
            } else if (key.icon != null) {
                key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2, key.y + (key.height - key.icon.getIntrinsicHeight()) / 2,
                        key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(), key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + key.icon.getIntrinsicHeight());
                key.icon.draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawStroke(Canvas canvas, Keyboard.Key key) {
        RectF oval3 = new RectF((key.x + 3f), key.y + 30, (key.x + key.width - 3f), key.y + key.height + 2);// 设置个新的长方形
        float corner = dp2px(getContext(), 5);
//        Log.i("drawStroke", "--------corner:" + corner);
        canvas.drawRoundRect(oval3, corner, corner, mStrokePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_MOVE) {
            return false;
        } else {
            return super.onTouchEvent(me);
        }
    }


    /**
     * 根据按压的位置改变
     *
     * @param object
     * @param edgeFlag
     */
    public void changePreviewLayoutBg(CustomKeyboardView customKeyboardView,Object object, int edgeFlag) {
        try {
            Class clazz = Class.forName("android.inputmethodservice.KeyboardView");
//            Class clazz = customKeyboardView.getClass().getSuperclass();
            Field fields[] = clazz.getFields();
           @SuppressLint("SoonBlockedPrivateApi") Field field = clazz.getField("mPreviewPopup");
            field.setAccessible(true);
            @SuppressLint("SoonBlockedPrivateApi") Field field1 = clazz.getDeclaredField("mPreviewOffset");
            field1.setAccessible(true);


            PopupWindow mPreviewPopup = (PopupWindow) field.get(object);
            int offset = (int) field1.get(object);

//            field1.set(object, 74);

            TextView mPreviewText = (TextView) mPreviewPopup.getContentView();

            if (1 == edgeFlag) {//按压最左边
                mPreviewText.setBackground(ContextCompat.getDrawable(context, R.drawable.keyboard_preview_enlarge_left));
            } else if (2 == edgeFlag) {//按压最右边
                mPreviewText.setBackground(ContextCompat.getDrawable(context, R.drawable.keyboard_preview_enlarge_right));
            } else {//按压中间
                mPreviewText.setBackground(ContextCompat.getDrawable(context, R.drawable.keyboard_enlarge));
            }

//            TextView mPreviewText = (TextView) LayoutInflater.from(context).inflate(R.layout.keyboardd_preview, null);不管用 无效方法
            mPreviewPopup.setContentView(mPreviewText);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static int dp2px(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5);
    }


}
