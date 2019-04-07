package com.henu.jianyunnote.Content;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.henu.jianyunnote.R;

public class NoteContent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_note_content );
    }




    public static void ActionStart(Context context){
        Intent intent = new Intent(context,NoteContent.class);
        context.startActivity(intent);
    }
}
