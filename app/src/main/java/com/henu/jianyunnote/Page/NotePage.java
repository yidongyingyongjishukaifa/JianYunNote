package com.henu.jianyunnote.Page;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.henu.jianyunnote.Content.NoteContent;
import com.henu.jianyunnote.Parttion.NoteParttion;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.Setting.Settings;

import java.util.ArrayList;
import java.util.List;

public class NotePage extends AppCompatActivity {

    private List<Page> pageList = new ArrayList<>();
    private PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_note_page );
        Toolbar toolbar = findViewById( R.id.page_toolbar );
        setSupportActionBar( toolbar );
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );

        initpage();
        PageAdapter pageAdapter = new PageAdapter( NotePage.this,R.layout.page_item, pageList);
        ListView listView = findViewById( R.id.page_listview );
        listView.setAdapter( pageAdapter );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoteContent.ActionStart( NotePage.this );
            }
        } );

        FloatingActionButton floatingActionButton = findViewById( R.id.page_fab );
        floatingActionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加笔记
            }
        } );
    }


    private void initpage() {
        Page page1 = new Page( "笔记一" );
        pageList.add( page1 );
        Page page2 = new Page( "笔记二" );
        pageList.add( page2 );
        Page page3 = new Page( "笔记三" );
        pageList.add( page3 );
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
                Settings.ActionStart( NotePage.this );
                break;
        }
        return true;
    }

    
    public static void ActionStart(Context context){
        Intent intent = new Intent(context,NotePage.class);
        context.startActivity(intent);
    }
}
