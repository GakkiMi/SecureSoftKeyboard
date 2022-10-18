package com.example.securesoftkeyboard;

import android.app.Application;

import com.example.securesoftkeyboard.upush.helper.MyPreferences;
import com.example.securesoftkeyboard.upush.helper.PushHelper;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;

public class MyApplication  extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initUmengSDK();
        instance = this;
    }

    /**
     * 初始化友盟SDK
     */
    private void initUmengSDK() {
        //日志开关
        UMConfigure.setLogEnabled(true);
        boolean isMainProcess = UMUtils.isMainProgress(this);
        //预初始化
        PushHelper.preInit(this);
        //是否同意隐私政策
        boolean agreed = MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
        if (!agreed) {
            return;
        }
        if (isMainProcess) {
            //启动优化：建议在子线程中执行初始化
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushHelper.init(getApplicationContext());
                }
            }).start();
        }
    }

}
