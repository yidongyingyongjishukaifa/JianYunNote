package com.henu.jianyunnote.Page;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.henu.jianyunnote.Content.NoteContent;
import com.henu.jianyunnote.DataBase.Note;
import com.henu.jianyunnote.DataBase.NoteBook;
import com.henu.jianyunnote.Parttion.NoteParttion;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.Util.MyAdapter;
import com.henu.jianyunnote.Util.TimeUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.henu.jianyunnote.Parttion.NoteParttion.arrayAddLength;

public class NotePage extends AppCompatActivity {

    private List<Map<String, Object>> listItems = new ArrayList<>();
    public static int[] local_notes_id;
    private int local_notebook_id;
    private int local_count;
    private MyAdapter myAdapter;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_page);
        initNotePage();
        final ListView mListView = findViewById(R.id.parttion_listview);
        final FloatingActionsMenu menu = findViewById(R.id.fab_menu);
        final com.getbase.floatingactionbutton.FloatingActionButton actionA = findViewById(R.id.fab_1);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                Intent intent = new Intent(NotePage.this, NoteContent.class);
                intent.putExtra("position", position + "");
                startActivity(intent);
            }
        });
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(NotePage.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NotePage.this);
                final View myView = layoutInflater.inflate(R.layout.new_notebook, null);
                builder.setTitle("新建笔记")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText Notebook_Name = myView.findViewById(R.id.notebook_Name);
                                String s = Notebook_Name.getText().toString();
                                String notebookid = String.valueOf(local_notebook_id);
                                List<NoteBook> noteBookList = LitePal.where("id=? and userId=?", notebookid, uid).order("updateTime desc").find(NoteBook.class);
                                Note note = new Note();
                                int notebook_id = 0;
                                for (NoteBook noteBook : noteBookList) {
                                    notebook_id=noteBook.getId();
                                    noteBook.setNoteNumber(noteBook.getNoteNumber()+1);
                                    noteBook.save();
                                }
                                if ("".equals(s)) {
                                    note.setTitle("未命名笔记");
                                } else {
                                    note.setTitle(s);
                                }
                                note.setUserId(Integer.parseInt(uid));
                                note.setNoteBookId(notebook_id);
                                note.setCreateTime(new Date());
                                note.setUpdateTime(new Date());
                                note.save();

                                local_notes_id = (int[]) arrayAddLength(local_notes_id, 1);
                                int nowLength = local_notes_id.length - 1;
                                local_notes_id[nowLength] = note.getId();
                                Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                                listItem.put("NOTE_MESSAGE", note.getTitle());
                                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note.getUpdateTime()));
                                listItems.add(listItem);
                                myAdapter.notifyDataSetChanged();
//                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG ).setAction( "Action", null ).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });

    }

    private void initNotePage() {
        int p = Integer.parseInt(NotePage.this.getIntent().getStringExtra("position"));
//        Log.d(TAG, String.valueOf(p));
//        System.out.println("p------------------\n"+p+"\n-----------------------\n");
        local_notebook_id = NoteParttion.local_notebooks_id[p];
//        Log.d(TAG, String.valueOf(local_notebook_id));
//        System.out.println("notebook_id------------------\n"+notebook_id+"\n-----------------------\n");
        uid = String.valueOf(NoteParttion.local_user_id);
        String notebookid = String.valueOf(local_notebook_id);
        List<Note> notes = LitePal.where("noteBookId=? and userId=?", notebookid, uid).order("updateTime desc").find(Note.class);
        if (notes != null && notes.size() != 0) {
            local_count = 0;
            local_notes_id = new int[notes.size()];
            for (Note note : notes) {
                local_notes_id[local_count] = note.getId();
                local_count++;
                Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                listItem.put("NOTE_MESSAGE", note.getTitle());
                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note.getUpdateTime()));
                listItems.add(listItem);
            }
        }
        myAdapter = new MyAdapter(NotePage.this, listItems);
    }
}
