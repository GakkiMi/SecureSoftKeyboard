package com.example.securesoftkeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.securesoftkeyboard.otherview.ElemeStyleCountModifyView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initElemeView();
        initElemeView2();
        initElemeView3();
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


}