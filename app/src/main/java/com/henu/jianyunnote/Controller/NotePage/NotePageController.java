package com.henu.jianyunnote.Controller.NotePage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.henu.jianyunnote.Model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.Model.LitePal.Note_LitePal;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.Dao.INoteDao_LitePal;
import com.henu.jianyunnote.Dao.IUserDao_LitePal;
import com.henu.jianyunnote.Dao.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.Dao.impl.IUserDaoImpl_LitePal;
import com.henu.jianyunnote.Util.ArrayUtil;
import com.henu.jianyunnote.Util.AtyUtil;
import com.henu.jianyunnote.Util.NoteAdapter;
import com.henu.jianyunnote.Util.TimeUtil;
import com.henu.jianyunnote.Controller.NoteContent.NoteContentController;
import com.henu.jianyunnote.Controller.NoteParttion.NoteParttionController;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotePageController extends AppCompatActivity {
    private List<Map<String, Object>> listItems = new ArrayList<>();
    public static int[] local_notes_id;
    private int local_notebook_id;
    private int local_count;
    private NoteAdapter myAdapter;
    private String uid;
    private String notebookid;
    public static boolean flag = false;
    private IUserDao_LitePal userService = new IUserDaoImpl_LitePal();
    private INoteDao_LitePal noteService = new INoteDaoImpl_LitePal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_page);
        AtyUtil.getInstance().addActivity(this);
        int p = Integer.parseInt(NotePageController.this.getIntent().getStringExtra("position"));
        local_notebook_id = NoteParttionController.local_notebooks_id[p];
        uid = String.valueOf(NoteParttionController.local_user_id);
        notebookid = String.valueOf(local_notebook_id);
        initNotePage();
        final ListView mListView = findViewById(R.id.parttion_listview);
        final FloatingActionsMenu menu = findViewById(R.id.fab_menu);
        final com.getbase.floatingactionbutton.FloatingActionButton actionA = findViewById(R.id.fab_1);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                Intent intent = new Intent(NotePageController.this, NoteContentController.class);
                intent.putExtra("position", position + "");
                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(NotePageController.this);
                builder.setMessage("确定删除?");
                builder.setTitle("提示");
                final int p = position;
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listItems.remove(p);
                        flag = true;
                        int id = local_notes_id[p];
                        noteService.updateNoteById(id);
                        userService.updateUserByUser(NoteParttionController.current_user);
                        local_notes_id = ArrayUtil.deleteIdInArray(local_notes_id, p);
                        myAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "删除列表项", Toast.LENGTH_SHORT).show();
                    }
                });
                //添加AlertDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                return true;
            }
        });

        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(NotePageController.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NotePageController.this);
                final View myView = layoutInflater.inflate(R.layout.new_notebook, null);
                builder.setTitle("新建笔记")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText Notebook_Name = myView.findViewById(R.id.notebook_Name);
                                String s = "";
                                if (Notebook_Name.getText() != null) {
                                    s = Notebook_Name.getText().toString();
                                }
                                Note_LitePal note_litePal = noteService.insert2Note(s,null, local_notebook_id, NoteParttionController.local_user_id);
                                if (note_litePal != null) {
                                    flag = true;
                                    userService.updateUserByUser(NoteParttionController.current_user);
                                    local_notes_id = ArrayUtil.insert2Array(local_notes_id, note_litePal.getId());
                                    Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                                    listItem.put("NOTE_MESSAGE", note_litePal.getTitle());
                                    listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note_litePal.getUpdateTime()));
                                    listItems.add(0, listItem);
                                    myAdapter.notifyDataSetChanged();
                                }
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
        List<Note_LitePal> notes = LitePal.where("noteBookId = ? and isDelete = ?", notebookid, "0").order("updateTime desc").find(Note_LitePal.class);
        if (notes != null && notes.size() != 0) {
            local_count = 0;
            local_notes_id = new int[notes.size()];
            for (Note_LitePal note : notes) {
                local_notes_id[local_count] = note.getId();
                local_count++;
                Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                listItem.put("NOTE_MESSAGE", note.getTitle());
                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note.getUpdateTime()));
                listItems.add(listItem);
            }
        }
        myAdapter = new NoteAdapter(NotePageController.this, listItems);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (flag) {
            List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", notebookid).find(NoteBook_LitePal.class);
            int num = 0;
            if (local_notes_id != null) {
                num = local_notes_id.length;
            }
            for (NoteBook_LitePal noteBook : noteBookList) {
                noteBook.setUpdateTime(new Date());
                noteBook.setNoteNumber(num);
                noteBook.save();
            }
        }
    }
}
