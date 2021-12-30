package com.example.securesoftkeyboard.safeKeyboard;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.securesoftkeyboard.R;


import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


/**
 * SecurityEditText
 *
 * @author yidong (onlyloveyd@gmaill.com)
 * @date 2018/6/15 08:29
 */
public class SecurityEditText extends AppCompatEditText {
    private KeyboardDialog dialog;
    private KeyboardAttribute keyboardAttribute;
    private boolean canShow = false;
    private KeyboardDialog.CallBack callBack;

    private String TAG = "SecurityEditText";

    private InputMethodManager manager = (InputMethodManager) this.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);

    private boolean isSystemKeyBoardShow = false;//默认没显示 为false

    public SecurityEditText(Context context) {
        this(context, null);
    }

    public SecurityEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public SecurityEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SecurityEditText);
        ColorStateList chooserSelectedColor = a.getColorStateList(R .styleable.SecurityEditText_chooserSelectedColor);
        ColorStateList chooserUnselectedColor = a.getColorStateList(R.styleable.SecurityEditText_chooserUnselectedColor);
        Drawable chooserBackground = a.getDrawable(R.styleable.SecurityEditText_chooserBackground);
        Drawable keyboardBackground = a.getDrawable(R.styleable.SecurityEditText_keyboardBackground);
        boolean isKeyPreview = a.getBoolean(R.styleable.SecurityEditText_keyPreview, true);
        a.recycle();
        keyboardAttribute = new KeyboardAttribute(chooserSelectedColor, chooserUnselectedColor, chooserBackground, keyboardBackground, isKeyPreview);
        initialize();
    }

    public void setCallBack(KeyboardDialog.CallBack callBack) {
        this.callBack = callBack;
    }


    private void initialize() {
        setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(false);
        } else {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean performClick() {
        if (this.isFocused()) {
            canShow = true;
            hideSystemKeyboard();
            showSoftInput();
        }
        return false;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!canShow) {
            canShow = true;
        } else {
            if (this.isFocused()) {
                hideSoftKeyboard();
            }
        }
    }

    public void hideSoftKeyboard() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void hideSystemKeyboard() {
        if (manager != null) {
            manager.hideSoftInputFromWindow(this.getWindowToken(), 0);
        }
    }

    private void showSoftInput() {
        if (dialog == null) {
            dialog = KeyboardDialog.show(getContext(), this);
            if (callBack != null) {
                dialog.setCallBack(callBack);
            }
        } else {
            if (isSystemKeyBoardShow) {
//                Observable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<Long>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//                            }
//
//                            @Override
//                            public void onNext(Long value) {
//                                dialog.show();
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                            }
//
//                            @Override
//                            public void onComplete() {
//                            }
//
//                        });
            } else {
                dialog.show();
            }
        }
    }

    public boolean isShow() {
        return dialog != null && dialog.isShowing();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!canShow) {
            canShow = true;
        } else {
            if (this.isFocused()) {
                hideSystemKeyboard();
                showSoftInput();
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                return true;
            } else {
                return false;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && canShow) {
            hideSystemKeyboard();
            showSoftInput();
        } else {
            hideSoftKeyboard();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
//        if (hasWindowFocus && hasFocus()) {
//            this.post(new Runnable() {
//                @Override
//                public void run() {
//                    hideSystemKeyboard();
//                    showSoftInput();
//                }
//            });
//        }
    }

    public KeyboardAttribute getKeyboardAttribute() {
        return keyboardAttribute;
    }


//    public void initKeyBoardListener(Activity activity) {
//        SoftKeyBoardListener softKeyBoardListener = new SoftKeyBoardListener(activity);
//        //软键盘状态监听
//        softKeyBoardListener.setListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
//            @Override
//            public void keyBoardShow(int height) {
//                isSystemKeyBoardShow = true;
//                Log.d(TAG, "-------系统软键盘弹出");
//            }
//
//            @Override
//            public void keyBoardHide(int height) {
//                isSystemKeyBoardShow = false;
//                Log.d(TAG, "-------系统软键盘关闭");
//            }
//        });
//    }


}
