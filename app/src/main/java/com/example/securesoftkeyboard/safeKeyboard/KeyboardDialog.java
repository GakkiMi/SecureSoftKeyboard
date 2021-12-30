package com.example.securesoftkeyboard.safeKeyboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.example.securesoftkeyboard.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KeyboardDialog extends Dialog implements KeyboardView.OnKeyboardActionListener {
    private static final int ORDER_NUMBER = 0;
    private static final int ORDER_SYMBOL = 1;
    private static final int ORDER_LETTER = 2;
    private final Vibrator vibrator;
    protected CustomKeyboardView keyboardView;
    protected View transparentView;//透明view 为了预览布局的正确弹出
    private CallBack callBack;
    private Keyboard mLetterKeyboard;//字母键盘
    private Keyboard mSymbolKeyboard;//符号键盘
    private Keyboard mNumberKeyboard;//数字键盘
    private int mCurrentOrder;
    private SparseArray<Keyboard> mOrderToKeyboard;
    private ArrayList<String> mNumberPool;

    private boolean isNumberRandom = true;
    private boolean isWordRandom = true;
    public static boolean isUpper = false;

    private WeakReference<SecurityEditText> mTargetEditText;
    private KeyboardAttribute attribute;

    private String TAG = "KeyboardDialog";

    public KeyboardDialog(Context context, SecurityEditText editText) {
        super(context, R.style.NoFrameDialog);
        mOrderToKeyboard = new SparseArray<>();
        mNumberPool = new ArrayList<>();
        mNumberPool.add("48#0");
        mNumberPool.add("49#1");
        mNumberPool.add("50#2");
        mNumberPool.add("51#3");
        mNumberPool.add("52#4");
        mNumberPool.add("53#5");
        mNumberPool.add("54#6");
        mNumberPool.add("55#7");
        mNumberPool.add("56#8");
        mNumberPool.add("57#9");
        mTargetEditText = new WeakReference<>(editText);
        vibrator = (Vibrator) context.getSystemService(Activity.VIBRATOR_SERVICE);
    }

    public static KeyboardDialog show(Context context, SecurityEditText editText) {
        KeyboardDialog dialog = new KeyboardDialog(context, editText);
        dialog.show();
        return dialog;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_keyboard);
        initView();
        initAttribute();
        initKeyboards();
        initKeyboardChooser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        if (window != null) {
            window.setDimAmount(0f);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            window.setAttributes(layoutParams);
            window.setWindowAnimations(R.style.KeyboardDialogAnimation);
        }
    }

    private void initView() {
        keyboardView = (CustomKeyboardView) findViewById(R.id.keyboard_view);
        transparentView = (View) findViewById(R.id.keyboard_view_top);
        setCanceledOnTouchOutside(true);
    }

    private void initAttribute() {
        attribute = mTargetEditText.get().getKeyboardAttribute();
    }

    private void initKeyboards() {
//        if (attribute.keyboardBackground != null) {
//            keyboardView.setBackground(attribute.keyboardBackground);
//        }
//        if (attribute.isKeyPreview) {
//            keyboardView.setPreviewEnabled(true);
//        } else {
//            keyboardView.setPreviewEnabled(false);
//        }
        keyboardView.setEnabled(true);
        isUpper = false;
        keyboardView.setOnKeyboardActionListener(this);
        mLetterKeyboard = new Keyboard(getContext(), R.xml.keyboard_word2);
        mSymbolKeyboard = new Keyboard(getContext(), R.xml.keyboard_symbol2);
        mNumberKeyboard = new Keyboard(getContext(), R.xml.keyboard_numbers2);

        if (isNumberRandom) {
            randomNumbers();
        }
        if (isWordRandom) {
            randomWords();
        }
        mOrderToKeyboard.put(ORDER_NUMBER, mNumberKeyboard);
        mOrderToKeyboard.put(ORDER_SYMBOL, mSymbolKeyboard);
        mOrderToKeyboard.put(ORDER_LETTER, mLetterKeyboard);
        mCurrentOrder = ORDER_LETTER;
        onCurrentKeyboardChange();
    }

    private void initKeyboardChooser() {
//        tvNumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCurrentOrder = ORDER_NUMBER;
//                onCurrentKeyboardChange();
//            }
//        });
//        tvSymbol.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCurrentOrder = ORDER_SYMBOL;
//                onCurrentKeyboardChange();
//            }
//        });
//
//        tvLetter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCurrentOrder = ORDER_LETTER;
//                onCurrentKeyboardChange();
//            }
//        });
        transparentView.setOnClickListener(v -> dismiss());
    }

    private void hideKeyboard() {
        this.dismiss();
    }

    /**
     * 键盘数字随机切换
     */
    private void randomNumbers() {
        if (mNumberKeyboard != null) {
            ArrayList<String> source = new ArrayList<>(mNumberPool);
            List<Keyboard.Key> keys = mNumberKeyboard.getKeys();
            for (Keyboard.Key key : keys) {
                if (key.label != null && isNumber(key.label.toString())) {
                    int number = new Random().nextInt(source.size());
                    String[] text = source.get(number).split("#");
                    key.label = text[1];
                    key.codes[0] = Integer.valueOf(text[0], 10);
                    source.remove(number);
                }
            }
        }
    }

    private void randomWords() {
        if (mLetterKeyboard != null) {
            List<Keyboard.Key> keys = mLetterKeyboard.getKeys();
            ArrayList<KeyBean> numbers = new ArrayList<>();
            ArrayList<KeyBean> words = new ArrayList<>();
            for (int i = 0; i < keys.size(); i++) {
                if (keys.get(i).label != null && isword(keys.get(i).label.toString())) {
                    words.add(new KeyBean((String) keys.get(i).label, keys.get(i).codes[0]));
                }
                if (keys.get(i).label != null && keys.get(i).label.toString().length() == 1 && isNumber(keys.get(i).label.toString())) {
                    numbers.add(new KeyBean((String) keys.get(i).label, keys.get(i).codes[0]));
                }
            }

            for (Keyboard.Key key : keys) {
                if (key.label != null && isword(key.label.toString())) {
                    int number = new Random().nextInt(words.size());
                    KeyBean key1 = words.get(number);
                    key.label = key1.getLabel();
                    key.codes[0] = key1.getCode();
                    words.remove(number);
                }
                if (key.label != null && key.label.toString().length() == 1 && isNumber(key.label.toString())) {
                    int number = new Random().nextInt(numbers.size());
                    KeyBean key1 = numbers.get(number);
                    key.label = key1.getLabel();
                    key.codes[0] = key1.getCode();
                    numbers.remove(number);
                }
            }
        }
    }

    private void onCurrentKeyboardChange() {
//        if (mCurrentOrder == ORDER_NUMBER && isNumberRandom) {
//            randomNumbers();
//        }
        keyboardView.setKeyboard(mOrderToKeyboard.get(mCurrentOrder));
        switch (mCurrentOrder) {
            case ORDER_NUMBER:
                break;
            case ORDER_SYMBOL:
                break;
            case ORDER_LETTER:
                break;
            default:
                throw new IllegalStateException("无效的键盘类型");
        }
    }

    private boolean isNumber(String str) {
        String numStr = "1234567890";
        return numStr.contains(str.toLowerCase());
    }

    // 判断是否为字母
    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        return !TextUtils.isEmpty(str) && wordstr.contains(str.toLowerCase());
    }

    private boolean isPortrait() {
        return getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }


    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Log.i(TAG, "--------onPress-onKey:" + primaryCode);
        Editable editable = mTargetEditText.get().getText();
        int start = mTargetEditText.get().getSelectionStart();
        if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            hideKeyboard();
        } else if (primaryCode == Keyboard.KEYCODE_DELETE/*-5*/) {//符号键盘和字母键盘删除按钮code
            if (editable != null && editable.length() > 0) {
                if (start > 0) {
                    editable.delete(start - 1, start);
                }
            }
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            changeKey();
            mCurrentOrder = ORDER_LETTER;
            onCurrentKeyboardChange();
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE/*-2*/) {//数字切换code
            if (mCurrentOrder == ORDER_NUMBER) {
                mCurrentOrder = ORDER_LETTER;
            } else {
                mCurrentOrder = ORDER_NUMBER;
            }
            onCurrentKeyboardChange();
        } else if (primaryCode == 90001) {//符号或者ABC
            if (mCurrentOrder == ORDER_LETTER) {
                mCurrentOrder = ORDER_SYMBOL;
            } else if (mCurrentOrder == ORDER_NUMBER) {
                mCurrentOrder = ORDER_SYMBOL;
            } else {
                mCurrentOrder = ORDER_LETTER;
            }
            onCurrentKeyboardChange();
        } else if (primaryCode == 90002||primaryCode == 00000) {//安全键盘文字区域
            return;
        } else if (primaryCode == 90003) {//登录按钮
            if (callBack != null) {
                callBack.onComplete();
            } else {
                dismiss();
            }
        } else if (primaryCode == -35) {//数字键盘回退键code
            if (editable != null && editable.length() > 0) {
                if (start > 0) {
                    editable.delete(start - 1, start);
                }
            }
        } else {
            editable.insert(start, Character.toString((char) primaryCode));
        }
        try {
//            vibrator.vibrate(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        if (mLetterKeyboard != null) {
            List<Keyboard.Key> keys = mLetterKeyboard.getKeys();
            if (isUpper) {
                isUpper = false;
                for (Keyboard.Key key : keys) {
                    if (key.label != null && isLetter(key.label.toString())) {
                        key.label = key.label.toString().toLowerCase();
                        key.codes[0] = key.codes[0] + 32;
                    }
                    if (key.codes[0] == -1) {
                        key.icon = getContext().getResources().getDrawable(
                                R.drawable.keyboard_shift);
                    }
                }
            } else {// 小写切换大写
                isUpper = true;
                for (Keyboard.Key key : keys) {
                    if (key.label != null && isLetter(key.label.toString())) {
                        key.label = key.label.toString().toUpperCase();
                        key.codes[0] = key.codes[0] - 32;
                    }
                    if (key.codes[0] == -1) {
                        key.icon = getContext().getResources().getDrawable(
                                R.drawable.keyboard_shift_c);
                    }
                }
            }
        }
    }

    private boolean isLetter(String str) {
        String letterStr = "abcdefghijklmnopqrstuvwxyz";
        return letterStr.contains(str.toLowerCase());
    }


    @Override
    public void onPress(int primaryCode) {
        int edgeFlags = getPressEdgeFlags(primaryCode);
        keyboardView.changePreviewLayoutBg(keyboardView, edgeFlags);
        Log.i(TAG, "--------onPress-primaryCode:" + primaryCode + "==" + mCurrentOrder);
        switch (primaryCode) {
            case Keyboard.KEYCODE_CANCEL:
            case Keyboard.KEYCODE_DELETE://-5  符号键盘和字母键盘删除按钮code
            case Keyboard.KEYCODE_SHIFT:
            case Keyboard.KEYCODE_MODE_CHANGE://-2 数字切换code
            case 90001://符号或者ABC
            case 90002://安全键盘文字区域
            case 90003://登录按钮
            case -35://数字键盘回退键code
                keyboardView.setPreviewEnabled(false);
                break;
            default:
                keyboardView.setPreviewEnabled(mCurrentOrder == ORDER_NUMBER ? false : true);
                break;
        }
    }

    /**
     * 根据primaryCode获取按压的位置 靠左还是靠右
     *
     * @param primaryCode
     */
    private int getPressEdgeFlags(int primaryCode) {
        int edgeFlags = 0;
        switch (mCurrentOrder) {
            case ORDER_NUMBER:
                break;
            case ORDER_SYMBOL:
                if (mSymbolKeyboard != null) {
                    List<Keyboard.Key> keys = mSymbolKeyboard.getKeys();
                    for (Keyboard.Key key : keys) {
                        if (primaryCode == key.codes[0]) {
                            edgeFlags = key.edgeFlags;
                            Log.i(TAG, "-------符号键盘：" + key.label + "|" + key.codes[0] + "|" + key.edgeFlags + "|" + key.x + "|" + key.y);
                            return edgeFlags;
                        }
                    }
                }
                break;
            case ORDER_LETTER:
                if (mLetterKeyboard != null) {
                    List<Keyboard.Key> keys = mLetterKeyboard.getKeys();
                    for (Keyboard.Key key : keys) {
                        if (primaryCode == key.codes[0]) {
                            edgeFlags = key.edgeFlags;
                            Log.i(TAG, "-------字母键盘：" + key.label + "|" + key.codes[0] + "|" + key.edgeFlags + "|" + key.x + "|" + key.y);
                            return edgeFlags;
                        }
                    }
                }
                break;
            default:
                break;

        }
        return edgeFlags;
    }


    @Override
    public void onRelease(int primaryCode) {
        Log.i(TAG, "--------onRelease-primaryCode:" + primaryCode + "==" + mCurrentOrder);
    }

    @Override
    public void onText(CharSequence text) {
        Log.i(TAG, "--------onText-:" + text);
    }

    @Override
    public void swipeLeft() {
        Log.i(TAG, "--------swipeLeft-");
    }

    @Override
    public void swipeRight() {
        Log.i(TAG, "--------swipeRight-");
    }

    @Override
    public void swipeDown() {
        Log.i(TAG, "--------swipeDown-");
    }

    @Override
    public void swipeUp() {
        Log.i(TAG, "--------swipeUp-");
    }

    public abstract static class CallBack {
        protected abstract void onComplete();
    }
}
