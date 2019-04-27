package com.henu.jianyunnote.Setting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.henu.jianyunnote.Page.NotePage;
import com.henu.jianyunnote.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );
        Toolbar toolbar = findViewById( R.id.set_toolbar );
        setSupportActionBar( toolbar );
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
    }

    public static void ActionStart(Context context){
        Intent intent = new Intent(context, Settings.class);
        context.startActivity(intent);
    }
}
