package com.henu.jianyunnote.Controller.Start;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.henu.jianyunnote.Controller.NoteParttion.NoteParttionController;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.Util.AtyUtil;

import org.litepal.tablemanager.Connector;

public class StartController extends AppCompatActivity {

    private  final int SPLASH_DISPLAY_LENGHT = 500;//两秒后进入系统，时间可自行调整

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
//        getSupportActionBar().hide();//隐藏标题栏

        setContentView(R.layout.activity_start);
// 添加Activity到堆栈
        AtyUtil.getInstance().addActivity(this);
        //在Start停留2秒然后进入MainActivity
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(StartController.this, NoteParttionController.class);
                StartController.this.startActivity(mainIntent);
                StartController.this.finish();
            }
        },SPLASH_DISPLAY_LENGHT);

        Connector.getDatabase();
    }
}
