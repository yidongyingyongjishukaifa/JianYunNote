package com.henu.jianyunnote.Index;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.henu.jianyunnote.R;

public class Register extends AppCompatActivity {
    private Button Return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        Return = findViewById(R.id.Return);
        Return.setOnClickListener(returnListener);
    }

    Button.OnClickListener returnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Register.this, Index.class);
            startActivity(intent);
        }
    };
}
