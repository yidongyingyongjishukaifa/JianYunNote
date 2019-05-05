package com.henu.jianyunnote.Page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.henu.jianyunnote.Content.NoteContent;
import com.henu.jianyunnote.DataBase.Note;
import com.henu.jianyunnote.Parttion.NoteParttion;
import com.henu.jianyunnote.R;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class NotePage extends AppCompatActivity {

    private List<Page> pageList = new ArrayList<>();
    public static int[] local_notes_id;
    private int local_notebook_id;
    private int local_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_page);
        initNotePage();
        PageAdapter pageAdapter = new PageAdapter(NotePage.this, R.layout.page_item, pageList);
        ListView listView = findViewById(R.id.page_listview);
        listView.setAdapter(pageAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NotePage.this, NoteContent.class);
                intent.putExtra("position",position+"");
                startActivity(intent);
            }
        });

    }

    private void initNotePage() {
        int p = Integer.parseInt(NotePage.this.getIntent().getStringExtra("position"));
//        System.out.println("p------------------\n"+p+"\n-----------------------\n");
        local_notebook_id = NoteParttion.local_notebooks_id[p];
//        System.out.println("notebook_id------------------\n"+notebook_id+"\n-----------------------\n");
        String uid = String.valueOf(NoteParttion.local_user_id);
        String notebookid = String.valueOf(local_notebook_id);
        List<Note> notes = LitePal.where("noteBookId=? and userId=?", notebookid, uid).order("updateTime desc").find(Note.class);
        if (notes != null && notes.size() != 0) {
            local_count = 0;
            local_notes_id=new int[notes.size()];
            for (Note note : notes) {
                local_notes_id[local_count] = note.getId();
                local_count++;
                Page page = new Page(note.getTitle());
                pageList.add(page);
            }
        }
    }
}
