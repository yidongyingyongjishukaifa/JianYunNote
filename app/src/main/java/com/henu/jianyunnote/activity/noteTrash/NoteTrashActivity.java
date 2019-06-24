package com.henu.jianyunnote.activity.noteTrash;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.henu.jianyunnote.R;
import com.henu.jianyunnote.activity.notePage.NotePageActivity;
import com.henu.jianyunnote.activity.noteParttion.NoteParttionActivity;
import com.henu.jianyunnote.dao.LitePal.INoteBookDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.IUserDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteBookDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.IUserDaoImpl_LitePal;
import com.henu.jianyunnote.model.Bmob.Note_Bmob;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.Const;
import com.henu.jianyunnote.util.NetWorkUtil;
import com.henu.jianyunnote.util.NoteTrashAdapter;
import com.henu.jianyunnote.util.TimeUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class NoteTrashActivity extends AppCompatActivity {
    private ListView mListView;
    private List<Map<String, Object>> listItems = new ArrayList<>();
    public static int click_position;
    private NoteTrashAdapter myAdapter;
    public static String note_id;
    public static int[] local_notes_id;
    public static boolean flag = false;
    private IUserDao_LitePal userService = new IUserDaoImpl_LitePal();
    private INoteDao_LitePal noteService = new INoteDaoImpl_LitePal();
    private INoteBookDao_LitePal noteBookService = new INoteBookDaoImpl_LitePal();
    private Thread mThread;
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    myAdapter = new NoteTrashAdapter(NoteTrashActivity.this, listItems);
                    // 为ListView设置Adapter
                    mListView.setAdapter(myAdapter);
                    break;
                case 1:
                    List<Note_LitePal> notes = LitePal.where("isDelete = ?", Const.ISDELETE).order("updateTime desc").find(Note_LitePal.class);
                    listItems.clear();
                    if (notes != null && notes.size() != 0) {
                        int local_count = 0;
                        local_notes_id = new int[notes.size()];
                        for (Note_LitePal note : notes) {
                            local_notes_id[local_count] = note.getId();
                            local_count++;
                            boolean isSync = false;
                            if (note.getIsSync() == 1) {
                                isSync = true;
                            }
                            addListItem(note.getTitle(), TimeUtil.Date2String(note.getUpdateTime()), isSync);
                        }
                    }
                    myAdapter = new NoteTrashAdapter(NoteTrashActivity.this, listItems);
                    mListView.setAdapter(myAdapter);
                    break;
                default:
                    //do something
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_trash);
        AtyUtil.getInstance().addActivity(this);
        Bmob.initialize(this, "bc95d28fa2c059530870d4dbb550b38f");//初始化Bmob  后面是服务器端应用ID
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = findViewById(R.id.note_trash_listview);
        initNoteTrash();
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int p = position;
                click_position = position;
                note_id = String.valueOf(local_notes_id[position]);
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteTrashActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否恢复笔记？");
                builder.setIcon(R.drawable.huifu);
                //点击对话框以外的区域是否让对话框消失
                builder.setCancelable(true);
                //设置正面按钮
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mThread = new Thread() {
                            @Override
                            public void run() {
                                flag = true;
                                noteService.setNoteUnDeleteById(local_notes_id[p]);
                                List<Note_LitePal> noteList = LitePal.where("id = ?", String.valueOf(local_notes_id[p])).find(Note_LitePal.class);
                                for (Note_LitePal note_litePal : noteList) {
                                    note_litePal.setIsDelete(Integer.parseInt(Const.NOTDELETE));
                                    note_litePal.save();
                                    if (note_litePal.getBmob_note_id() != null) {
                                        Note_Bmob note_bmob = new Note_Bmob();
                                        note_bmob.setIsDelete(Integer.valueOf(Const.NOTDELETE));
                                        note_bmob.update(note_litePal.getBmob_note_id(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                handler.sendEmptyMessage(1);
                                            }
                                        });
                                    } else {
                                        handler.sendEmptyMessage(1);
                                    }
                                }
                            }
                        };
                        mThread.start();
                    }
                });
                //设置反面按钮
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                //显示对话框
                dialog.show();
                return true;
            }
        });
    }

    private void initNoteTrash() {
        mThread = new Thread() {
            @Override
            public void run() {
                listItems.clear();
                int count = 0;
                if (!"未登录".equals(NoteParttionActivity.login_email)) {
                    List<Note_LitePal> notes = LitePal.where("userId = ? and isDelete = ?", String.valueOf(NoteParttionActivity.local_user_id), Const.ISDELETE).find(Note_LitePal.class);
                    if (notes != null && notes.size() != 0) {
                        local_notes_id = new int[notes.size()];
                        for (Note_LitePal note_litePal : notes) {
                            local_notes_id[count] = note_litePal.getId();
                            count++;
                            boolean isSync = false;
                            if (note_litePal.getIsSync() == 1) {
                                isSync = true;
                            }
                            addListItem(note_litePal.getTitle(), TimeUtil.Date2String(note_litePal.getUpdateTime()), isSync);
                        }
                    }
                } else {
                    List<Note_LitePal> notes = LitePal.where("userId = ? and isDelete = ?", "0", Const.ISDELETE).find(Note_LitePal.class);
                    if (notes != null && notes.size() != 0) {
                        local_notes_id = new int[notes.size()];
                        for (Note_LitePal note_litePal : notes) {
                            local_notes_id[count] = note_litePal.getId();
                            count++;
                            boolean isSync = false;
                            if (note_litePal.getIsSync() == 1) {
                                isSync = true;
                            }
                            addListItem(note_litePal.getTitle(), TimeUtil.Date2String(note_litePal.getUpdateTime()), isSync);
                        }
                    }
                }
                handler.sendEmptyMessage(0);
            }
        };
        mThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //查询逻辑，确定
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //查询逻辑，查询中
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(NoteTrashActivity.this, "work", Toast.LENGTH_SHORT).show();
                break;
            case R.id.syns:
                //
                break;
            case android.R.id.home:
                setResult();

                break;
        }
        return true;
    }

    private void setResult() {
        Intent result = new Intent(NoteTrashActivity.this, NoteParttionActivity.class);
        if (flag) {
            setResult(RESULT_OK, result);
        } else {
            setResult(RESULT_CANCELED, result);
        }
        startActivity(result);
        finish();
    }

    private void addListItem(String NOTE_MESSAGE, Object NOTE_UPDATE_TIME, boolean isSync) {
        Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
        listItem.put("NOTE_MESSAGE", NOTE_MESSAGE);
        listItem.put("NOTE_UPDATE_TIME", NOTE_UPDATE_TIME);
        if (isSync) {
            listItem.put("IS_NOTE_SYNC", "未同步");
        }
        listItems.add(0, listItem);
    }

}
