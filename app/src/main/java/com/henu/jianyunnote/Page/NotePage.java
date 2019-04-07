package com.henu.jianyunnote.Page;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.henu.jianyunnote.Content.NoteContent;
import com.henu.jianyunnote.R;

import java.util.ArrayList;
import java.util.List;

public class NotePage extends AppCompatActivity {

    private List<Page> pageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_note_page );

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

    }

    private void initpage() {
        Page page1 = new Page( "笔记一" );
        pageList.add( page1 );
        Page page2 = new Page( "笔记二" );
        pageList.add( page2 );
        Page page3 = new Page( "笔记三" );
        pageList.add( page3 );
    }
    
    public static void ActionStart(Context context){
        Intent intent = new Intent(context,NotePage.class);
        context.startActivity(intent);
    }
}
