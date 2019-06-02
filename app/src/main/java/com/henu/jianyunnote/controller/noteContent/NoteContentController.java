package com.henu.jianyunnote.controller.noteContent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.TimeUtil;
import com.henu.jianyunnote.controller.notePage.NotePageController;
import com.henu.jianyunnote.controller.noteParttion.NoteParttionController;


import org.litepal.LitePal;

import java.util.Date;
import java.util.List;

public class NoteContentController extends AppCompatActivity {
    private int local_note_id;
    private EditText noteTitle;
    private EditText noteContent;
    private TextView timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_content);
        AtyUtil.getInstance().addActivity(this);



        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        noteTitle = findViewById(R.id.note_title);
        noteContent = findViewById(R.id.note_content);//自动换行
        noteContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        noteContent.setGravity(Gravity.TOP);
        noteContent.setSingleLine(false);
        noteContent.setHorizontallyScrolling(false);

        timeView = findViewById(R.id.time_info);

        initNoteContent();
    }

    private void initNoteContent() {
        boolean flag = NoteContentController.this.getIntent().getBooleanExtra("is_note", false);
        int p = Integer.parseInt(NoteContentController.this.getIntent().getStringExtra("position"));
        if (flag) {
            local_note_id = NoteParttionController.local_notes_id[p];
        } else {
            local_note_id = NotePageController.local_notes_id[p];
        }
        String noteid = String.valueOf((local_note_id));
        List<Note_LitePal> noteList = LitePal.where("id=?", noteid).find(Note_LitePal.class);
        if (noteList != null && noteList.size() != 0) {
            for (Note_LitePal note : noteList) {
                noteTitle.setText(note.getTitle());
                noteContent.setText(note.getContent());
                timeView.setText(TimeUtil.Date2String(note.getUpdateTime()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String noteid = String.valueOf((local_note_id));
        List<Note_LitePal> noteList = LitePal.where("id = ?", noteid).find(Note_LitePal.class);
        if (noteList != null && noteList.size() != 0) {
            for (Note_LitePal note : noteList) {
                note.setUpdateTime(new Date());
                note.setContent(noteContent.getText().toString());
                note.setTitle(noteTitle.getText().toString());
                note.save();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_forcontent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.syns:
                //
                break;
            case R.id.photo:
                //
                break;
            case android.R.id.home:
                finish();
        }
        return true;
    }
}
