package com.henu.jianyunnote.Content;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.henu.jianyunnote.R;

public class NoteContent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_note_content );
        Toolbar toolbar = findViewById( R.id.page_toolbar );
        setSupportActionBar( toolbar );
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.toolbar,menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_:
                //
                break;
            case R.id.feedback:
                //
                break;
            case R.id.setting:
                //
                break;
        }
        return true;
    }

    public static void ActionStart(Context context){
        Intent intent = new Intent(context,NoteContent.class);
        context.startActivity(intent);
    }
}
