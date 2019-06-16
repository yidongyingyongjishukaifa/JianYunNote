package com.henu.jianyunnote.controller.notePage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.henu.jianyunnote.controller.index.LoginController;
import com.henu.jianyunnote.dao.LitePal.INoteBookDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteBookDaoImpl_LitePal;
import com.henu.jianyunnote.model.Bmob.Note_Bmob;
import com.henu.jianyunnote.model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.IUserDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.IUserDaoImpl_LitePal;
import com.henu.jianyunnote.util.ArrayUtil;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.NetWorkUtil;
import com.henu.jianyunnote.util.NoteAdapter;
import com.henu.jianyunnote.util.TimeUtil;
import com.henu.jianyunnote.controller.noteContent.NoteContentController;
import com.henu.jianyunnote.controller.noteParttion.NoteParttionController;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class NotePageController extends AppCompatActivity {
    private List<Map<String, Object>> listItems = new ArrayList<>();
    private static final int NOTECONTENT_ACTIVITY = 1;
    public static int[] local_notes_id;
    private int local_notebook_id;
    private ListView mListView;
    public static int click_position;
    private NoteAdapter myAdapter;
    private String notebookid;
    public static String note_id;
    public static boolean flag = false;
    private IUserDao_LitePal userService = new IUserDaoImpl_LitePal();
    private INoteDao_LitePal noteService = new INoteDaoImpl_LitePal();
    private INoteBookDao_LitePal noteBookService = new INoteBookDaoImpl_LitePal();
    private Thread mThread;
    private Handler hander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    myAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    myAdapter = new NoteAdapter(NotePageController.this, listItems);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_page);
        AtyUtil.getInstance().addActivity(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        flag = false;
        mListView = findViewById(R.id.note_listview);
        initNotePage();
        final FloatingActionsMenu menu = findViewById(R.id.fab_menu);
        final com.getbase.floatingactionbutton.FloatingActionButton actionA = findViewById(R.id.fab_1);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                click_position = position;
                Intent intent = new Intent(NotePageController.this, NoteContentController.class);
                intent.putExtra("note_id", local_notes_id[position] + "");
                note_id = String.valueOf(local_notes_id[position]);
                startActivityForResult(intent, NOTECONTENT_ACTIVITY);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                final int p = position;
                final AlertDialog.Builder builder = new AlertDialog.Builder(NotePageController.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NotePageController.this);
                final View myView = layoutInflater.inflate(R.layout.new_notebook, null);
                builder.setTitle("修改笔记标题")
                        .setIcon(R.drawable.note)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                flag = true;
                                final EditText Notebook_Name = myView.findViewById(R.id.notebook_Name);
                                String s = "";
                                if (Notebook_Name.getText() != null) {
                                    s = Notebook_Name.getText().toString();
                                }
                                boolean isSync = canAccessNetWork();
                                Note_LitePal note_litePal = noteService.updateNoteTitleById(s, local_notes_id[p], isSync);
                                if (note_litePal != null) {
                                    listItems.remove(p);
                                    local_notes_id = ArrayUtil.deleteIdInArray(local_notes_id, p);
                                    local_notes_id = ArrayUtil.insert2Array(local_notes_id, note_litePal.getId());
                                    addListItem(note_litePal.getTitle(), TimeUtil.Date2String(note_litePal.getUpdateTime()), isSync);
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
                return true;
            }
        });

        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(NotePageController.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NotePageController.this);
                final View myView = layoutInflater.inflate(R.layout.new_note, null);
                builder.setTitle("新建笔记")
                        .setIcon(R.drawable.note)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                flag = true;
                                mThread = new Thread() {
                                    @Override
                                    public void run() {
                                        final EditText Note_Title = myView.findViewById(R.id.note_Title);
                                        String s = "";
                                        if (Note_Title.getText() != null) {
                                            s = Note_Title.getText().toString();
                                        }
                                        boolean isSync = canAccessNetWork();
                                        Note_LitePal note_litePal = noteService.insert2Note(s, null, local_notebook_id, NoteParttionController.local_user_id, isSync);
                                        if (note_litePal != null) {
                                            userService.updateUserByUser(NoteParttionController.current_user);
                                            local_notes_id = ArrayUtil.insert2Array(local_notes_id, note_litePal.getId());
                                            addListItem(note_litePal.getTitle(), TimeUtil.Date2String(note_litePal.getUpdateTime()), isSync);
                                            hander.sendEmptyMessage(0);
                                        }
                                    }
                                };
                                mThread.start();
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
        mThread = new Thread() {
            @Override
            public void run() {
                String Bmob_notebook_id = "";
                if (NotePageController.this.getIntent().getStringExtra("notebook_id") != null) {
                    local_notebook_id = Integer.parseInt(NotePageController.this.getIntent().getStringExtra("notebook_id"));
                } else {
                    List<Note_LitePal> notes = LitePal.where("id = ?", note_id).find(Note_LitePal.class);
                    for (Note_LitePal note_litePal : notes) {
                        local_notebook_id = note_litePal.getNoteBookId();
                    }
                }
                notebookid = String.valueOf(local_notebook_id);
                List<NoteBook_LitePal> noteBooks = LitePal.where("id= ?",notebookid).order("updateTime asc").find(NoteBook_LitePal.class);
                for(NoteBook_LitePal noteBook_litePal : noteBooks){
                    Bmob_notebook_id = noteBook_litePal.getBmob_notebook_id();
                }

                BmobQuery<Note_Bmob> query2 = new BmobQuery<>();
                //查询Email的数据
                query2.addWhereEqualTo("userId", NoteParttionController.current_user.getBmob_user_id());
                query2.addWhereEqualTo("noteBookId", Bmob_notebook_id);
                //返回50条数据，如果不加上这条语句，默认返回10条数据
                query2.setLimit(999);
                //执行查询方法
                query2.findObjects(new FindListener<Note_Bmob>() {
                    @Override
                    public void done(List<Note_Bmob> object, BmobException e) {
                        if (e == null) {
                            for (Note_Bmob note_bmob : object) {
                                Note_LitePal note_litePal = new Note_LitePal();
                                note_litePal.setBmob_note_id(note_bmob.getObjectId());
                                note_litePal.setBmob_user_id(NoteParttionController.current_user.getBmob_user_id());
                                note_litePal.setUserId(NoteParttionController.current_user.getId());
                                note_litePal.setIsDelete(note_bmob.getIsDelete());
                                note_litePal.setTitle(note_bmob.getTitle());
                                note_litePal.setContent(note_bmob.getContent());
                                boolean flag = canAccessNetWork();
                                if (flag) {
                                    note_litePal.setIsSync(1);
                                }
                                note_litePal.setCreateTime(new Date());
                                note_litePal.setUpdateTime(new Date());
                                note_litePal.save();
                            }
                        }
                    }
                });

                List<Note_LitePal> notes = LitePal.where("noteBookId = ? and isDelete = ?", notebookid, "0").order("updateTime asc").find(Note_LitePal.class);
                if (notes != null && notes.size() != 0) {
                    int local_count = notes.size() - 1;
                    local_notes_id = new int[notes.size()];
                    for (Note_LitePal note : notes) {
                        local_notes_id[local_count] = note.getId();
                        local_count--;
                        boolean isSync = canAccessNetWork();
                        addListItem(note.getTitle(), TimeUtil.Date2String(note.getUpdateTime()), isSync);
                    }
                }
                hander.sendEmptyMessage(1);
            }
        };
        mThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (flag) {
            boolean isSync = canAccessNetWork();
            noteBookService.updateNoteBookById(notebookid, isSync);
            userService.updateUserByUser(NoteParttionController.current_user);
            setResult();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTECONTENT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                flag = true;
                updateItem();
            }
        }
    }

    private void updateItem() {
        mThread = new Thread() {
            @Override
            public void run() {
                userService.updateUserByUser(NoteParttionController.current_user);
                listItems.remove(click_position);
                local_notes_id = ArrayUtil.deleteIdInArray(local_notes_id, click_position);
                boolean isSync = canAccessNetWork();
                List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", note_id).find(NoteBook_LitePal.class);
                for (NoteBook_LitePal noteBook_litePal : noteBookList) {
                    local_notes_id = ArrayUtil.insert2Array(local_notes_id, noteBook_litePal.getId());
                    addListItem(noteBook_litePal.getNoteBookName(), TimeUtil.Date2String(noteBook_litePal.getUpdateTime()), isSync);
                }
                hander.sendEmptyMessage(0);
            }
        };
        mThread.start();
    }

    private boolean canAccessNetWork() {
        boolean isSync = false;
        if (!NetWorkUtil.isNetworkConnected(NotePageController.this)) {
            isSync = true;
            Toast.makeText(NotePageController.this, "未联网", Toast.LENGTH_LONG).show();
        }
        if (NoteParttionController.local_user_id == 0) {
            isSync = true;
        }
        return isSync;
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
                Toast.makeText(NotePageController.this, "work", Toast.LENGTH_SHORT).show();
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
        Intent result = new Intent(NotePageController.this, NoteParttionController.class);
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
