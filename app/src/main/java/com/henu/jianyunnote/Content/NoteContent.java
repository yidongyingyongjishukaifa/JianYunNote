package com.henu.jianyunnote.Content;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.henu.jianyunnote.DataBase.Note;
import com.henu.jianyunnote.Page.NotePage;
import com.henu.jianyunnote.Parttion.NoteParttion;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.Util.TimeUtil;

import org.litepal.LitePal;

import java.util.List;

public class NoteContent extends AppCompatActivity {
    private int local_note_id;
    private EditText noteTitle;
    private EditText noteContent;
    private static final String TAG = "NOTE_CONTENT_DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_content);
        noteTitle = findViewById(R.id.note_title);
        noteContent = findViewById(R.id.note_content);
        initNoteContent();
    }

    private void initNoteContent() {
        boolean flag = NoteContent.this.getIntent().getBooleanExtra("is_note", false);
        int p = Integer.parseInt(NoteContent.this.getIntent().getStringExtra("position"));
        if (flag) {
            local_note_id = NoteParttion.local_notes_id[p];
        } else {
//        System.out.println("p------------------\n"+p+"\n-----------------------\n");
            local_note_id = NotePage.local_notes_id[p];
        }
        String noteid = String.valueOf((local_note_id));
//        System.out.println("note_id------------------\n"+noteid+"\n-----------------------\n");
        List<Note> noteList = LitePal.where("id=?", noteid).find(Note.class);
        if (noteList != null && noteList.size() != 0) {
            for (Note note : noteList) {
                noteTitle.setText(note.getTitle());
                noteContent.setText(note.getContent());
            }
        }
    }
}
