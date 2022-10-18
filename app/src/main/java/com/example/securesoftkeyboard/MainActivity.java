package com.example.securesoftkeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.securesoftkeyboard.otherview.ElemeStyleCountModifyView;
import com.example.securesoftkeyboard.upush.helper.MyPreferences;
import com.example.securesoftkeyboard.upush.helper.PushHelper;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushRegisterCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initElemeView();
        initElemeView2();
        initElemeView3();
        if (hasAgreedAgreement()) {
            PushAgent.getInstance(this).onAppStart();
            setDeviceToken();
        } else {
            showAgreementDialog();
        }
    }

    private void initElemeView(){
        ElemeStyleCountModifyView modifyView = findViewById(R.id.item_goods_info_eleme_view);
        modifyView.setOnClickListener(v -> {
            //不须做任何操作  只是为了占用item的点击区域
        });
        modifyView.setMultiple(1);
        modifyView.setCurrentCount(0);
        modifyView.setMaxCount(3);
        modifyView.setText();
        modifyView.setGoodsCountModifyImp(modifyCount -> {
            modifyView.setCurrentCount(modifyCount);
            modifyView.setTextWithAnima(300);
        });
    }

    private void initElemeView2(){
        ElemeStyleCountModifyView modifyView = findViewById(R.id.item_goods_info_eleme_view2);
        modifyView.setOnClickListener(v -> {
            //不须做任何操作  只是为了占用item的点击区域
        });
        modifyView.setMultiple(90);
        modifyView.setCurrentCount(10);
        modifyView.setMaxCount(1100);
        modifyView.setText();
        modifyView.setGoodsCountModifyImp(modifyCount -> {
            modifyView.setCurrentCount(modifyCount);
            modifyView.setTextWithAnima(300);
        });
    }


    private void initElemeView3(){
        ElemeStyleCountModifyView modifyView = findViewById(R.id.item_goods_info_eleme_view3);
        modifyView.setOnClickListener(v -> {
            //不须做任何操作  只是为了占用item的点击区域
        });
        modifyView.setMultiple(1);
        modifyView.setCurrentCount(0);
        modifyView.setMaxCount(0);
        modifyView.setText();
        modifyView.setGoodsCountModifyImp(modifyCount -> {
            modifyView.setCurrentCount(modifyCount);
            modifyView.setTextWithAnima(300);
        });
    }

    private boolean hasAgreedAgreement() {
        return MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
    }

    private void showAgreementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.agreement_title);
        builder.setMessage(R.string.agreement_msg);
        builder.setPositiveButton(R.string.agreement_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //用户点击隐私协议同意按钮后，初始化PushSDK
                MyPreferences.getInstance(getApplicationContext()).setAgreePrivacyAgreement(true);
                PushHelper.init(getApplicationContext());
                PushAgent.getInstance(getApplicationContext()).register(new UPushRegisterCallback() {
                    @Override
                    public void onSuccess(final String deviceToken) {
                        Log.i(PushHelper.TAG, "deviceToken --> " + deviceToken);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDeviceToken();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String code, String msg) {
                        Log.e(PushHelper.TAG, "register failure：--> " + "code:" + code + ",desc:" + msg);
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.agreement_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    private void setDeviceToken() {
        String deviceToken = PushAgent.getInstance(this).getRegistrationId();
        TextView token = findViewById(R.id.tv_device_token);
        token.setText(deviceToken);
    }


}