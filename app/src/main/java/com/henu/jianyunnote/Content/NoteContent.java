package com.henu.jianyunnote.Content;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.henu.jianyunnote.DataBase.Note;
import com.henu.jianyunnote.Page.NotePage;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.Util.TimeUtil;

import org.litepal.LitePal;

import java.util.List;

public class NoteContent extends AppCompatActivity {
    private int local_note_id;
    private EditText noteTitle;
    private EditText noteContent;
    private TextView updateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_content);
        noteTitle = findViewById(R.id.note_title);
        noteContent = findViewById(R.id.note_content);
        updateTime=findViewById(R.id.update_time);
        initNoteContent();
    }

    private void initNoteContent() {
        int p = Integer.parseInt(NoteContent.this.getIntent().getStringExtra("position"));
//        System.out.println("p------------------\n"+p+"\n-----------------------\n");
        local_note_id = NotePage.local_notes_id[p];
//        System.out.println("note_id------------------\n"+note_id+"\n-----------------------\n");
        String noteid = String.valueOf((local_note_id));
        List<Note> noteList = LitePal.where("id=?", noteid).find(Note.class);
        if (noteList != null && noteList.size() != 0) {
            for (Note note : noteList) {
                noteTitle.setText(note.getTitle());
                noteContent.setText(note.getContent());
                updateTime.setText(TimeUtil.Date2String(note.getUpdateTime()));
            }
        }
    }
}
