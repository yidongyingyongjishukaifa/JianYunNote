package com.henu.jianyunnote.controller.noteParttion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.henu.jianyunnote.controller.index.LoginController;
import com.henu.jianyunnote.Model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.Model.LitePal.Note_LitePal;
import com.henu.jianyunnote.Model.LitePal.User_LitePal;
import com.henu.jianyunnote.Dao.INoteBookDao_LitePal;
import com.henu.jianyunnote.Dao.INoteDao_LitePal;
import com.henu.jianyunnote.Dao.IUserDao_LitePal;
import com.henu.jianyunnote.Dao.impl.INoteBookDaoImpl_LitePal;
import com.henu.jianyunnote.Dao.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.Dao.impl.IUserDaoImpl_LitePal;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.controller.noteContent.NoteContentController;
import com.henu.jianyunnote.controller.setting.SettingController;
import com.henu.jianyunnote.util.ArrayUtil;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.NoteBookAdapter;
import com.henu.jianyunnote.util.TimeUtil;
import com.henu.jianyunnote.controller.notePage.NotePageController;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteParttionController extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static int[] local_notebooks_id;
    public static int[] local_notes_id;
    private NoteBookAdapter myAdapter;
    private TextView login_Email;
    private ImageView imageView;
    public static int local_user_id;
    private int local_count;
    public static int notebooks_count = 0;
    private List<Map<String, Object>> listItems = new ArrayList<>();
    public static User_LitePal current_user;
    private long mExitTime;
    private IUserDao_LitePal userService = new IUserDaoImpl_LitePal();
    private INoteBookDao_LitePal noteBookService = new INoteBookDaoImpl_LitePal();
    private INoteDao_LitePal noteService = new INoteDaoImpl_LitePal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_parttion);
        // 添加Activity到堆栈

        AtyUtil.getInstance().addActivity(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        login_Email = view.findViewById(R.id.login_email);
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteParttionController.this, SettingController.class);
                startActivity(intent);
            }
        });
        final ListView mListView = findViewById(R.id.parttion_listview);
        init();
        final FloatingActionsMenu menu = findViewById(R.id.fab_menu);
        final com.getbase.floatingactionbutton.FloatingActionButton actionA = findViewById(R.id.fab_1);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(NoteParttionController.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NoteParttionController.this);
                final View myView = layoutInflater.inflate(R.layout.new_notebook, null);
                builder.setTitle("新建笔记本")
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
                                NoteBook_LitePal noteBook_litePal = noteBookService.insert2NoteBook(s, local_user_id);
                                if (noteBook_litePal != null) {
                                    userService.updateUserByUser(current_user);
                                    local_notebooks_id = ArrayUtil.insert2Array(local_notebooks_id, noteBook_litePal.getId());
                                    notebooks_count = local_notebooks_id.length;
                                    Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                                    listItem.put("NOTE_MESSAGE", noteBook_litePal.getNoteBookName());
                                    listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(noteBook_litePal.getUpdateTime()));
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
        final com.getbase.floatingactionbutton.FloatingActionButton actionB = findViewById(R.id.fab_2);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(NoteParttionController.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NoteParttionController.this);
                final View myView = layoutInflater.inflate(R.layout.new_note, null);
                builder.setTitle("新建笔记")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText Note_Name = myView.findViewById(R.id.note_Name);
                                String s = "";
                                if (Note_Name.getText() != null) {
                                    s = Note_Name.getText().toString();
                                }
                                Note_LitePal note_litePal = noteService.insert2Note(s, null, null, local_user_id);
                                if (note_litePal != null) {
                                    userService.updateUserByUser(current_user);
                                    local_notes_id = ArrayUtil.insert2Array(local_notes_id, note_litePal.getId());
                                    Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                                    listItem.put("NOTE_MESSAGE", note_litePal.getTitle());
                                    listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note_litePal.getUpdateTime()));
                                    listItems.add(notebooks_count, listItem);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // 为ListView设置Adapter
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                Intent intent;
                if (position < notebooks_count) {
                    intent = new Intent(NoteParttionController.this, NotePageController.class);
                    intent.putExtra("position", position + "");
                } else {
                    intent = new Intent(NoteParttionController.this, NoteContentController.class);
                    int note_position = position - notebooks_count;
                    intent.putExtra("position", note_position + "");
                    intent.putExtra("is_note", true);
                }
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteParttionController.this);
                builder.setMessage("确定删除?");
                builder.setTitle("提示");
                final int p = position;
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listItems.remove(p);
                        int id;
                        if (p < notebooks_count) {
                            id = local_notebooks_id[p];
                            noteBookService.updateNoteBookById(id);
                            local_notebooks_id = ArrayUtil.deleteIdInArray(local_notebooks_id, p);
                            notebooks_count = local_notebooks_id.length;
                        } else {
                            id = local_notes_id[p - notebooks_count];
                            noteService.updateNoteById(id);
                            local_notes_id = ArrayUtil.deleteIdInArray(local_notes_id, p - notebooks_count);
                        }
                        userService.updateUserByUser(current_user);
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
    }

    private void init() {
        List<NoteBook_LitePal> noteBooks;
        List<User_LitePal> user;
        user = LitePal.where("isRemember = ?", "1").order("loginTime desc").limit(1).find(User_LitePal.class);
        if (user == null || user.size() == 0) {
            user = LitePal.where("isLogin = ?", "1").find(User_LitePal.class);
        }
        if (user != null && user.size() != 0) {
            for (User_LitePal u : user) {
                login_Email.setText(u.getUsername());
                current_user = u;
                local_user_id = u.getId();
                String uid = String.valueOf(local_user_id);
                noteBooks = LitePal.where("userId= ? and isDelete = ?", uid, "0").order("updateTime desc").find(NoteBook_LitePal.class);
                if (noteBooks != null && noteBooks.size() != 0) {
                    local_count = 0;
                    local_notebooks_id = new int[noteBooks.size()];
                    listItems.clear();
                    for (NoteBook_LitePal noteBook : noteBooks) {
                        local_notebooks_id[local_count] = noteBook.getId();
                        local_count++;
                        Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                        listItem.put("NOTE_MESSAGE", noteBook.getNoteBookName());
                        listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(noteBook.getUpdateTime()));
                        listItems.add(listItem);
                    }
                    notebooks_count = local_count;
                }
                List<Note_LitePal> notes = LitePal.where("noteBookId=? and userId=? and isDelete = ?", "0", uid, "0").order("updateTime desc").find(Note_LitePal.class);
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
            }
        } else {
            login_Email.setText("未登录");
            noteBooks = LitePal.where("userId=? and isDelete = ?", "0", "0").order("updateTime desc").find(NoteBook_LitePal.class);
            List<Note_LitePal> notes = LitePal.where("noteBookId=? and userId=? and isDelete = ?", "0", "0", "0").order("updateTime desc").find(Note_LitePal.class);
            NoteBook_LitePal noteBook_litePal = new NoteBook_LitePal();
            Note_LitePal note_litePal = new Note_LitePal();
            boolean isAdd = false;//用于判断是否将存入数据库中的notebook添加了
            if (noteBooks != null && noteBooks.size() != 0) {
                listItems.clear();
                local_count = 0;
                local_notebooks_id = new int[noteBooks.size()];
                for (NoteBook_LitePal noteBook : noteBooks) {
                    isAdd = true;
                    local_notebooks_id[local_count] = noteBook.getId();
                    local_count++;
                    Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                    listItem.put("NOTE_MESSAGE", noteBook.getNoteBookName());
                    listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(noteBook.getUpdateTime()));
                    listItems.add(listItem);
                }
                notebooks_count = local_count;
            } else {
                noteBook_litePal = noteBookService.insert2NoteBook("未命名笔记本", 0);
                local_notebooks_id = new int[1];
                local_notebooks_id[0] = noteBook_litePal.getId();
                notebooks_count = 1;
            }
            if (notes != null && notes.size() != 0) {
                local_count = 0;
                local_notes_id = new int[notes.size()];
                for (Note_LitePal note : notes) {
                    isAdd = true;
                    local_notes_id[local_count] = note.getId();
                    local_count++;
                    Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                    listItem.put("NOTE_MESSAGE", note.getTitle());
                    listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note.getUpdateTime()));
                    listItems.add(listItem);
                }
            } else {
                noteService.insert2Note("未命名笔记", "测试内容", noteBook_litePal.getId(), 0);
                noteBook_litePal.setNoteNumber(1);
                noteBook_litePal.save();
                note_litePal = noteService.insert2Note("未命名笔记", "测试内容", null, 0);
                local_notes_id = new int[1];
                local_notes_id[0] = note_litePal.getId();
            }
            if (!isAdd) {
                listItems.clear();
                Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                listItem.put("NOTE_MESSAGE", noteBook_litePal.getNoteBookName());
                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(noteBook_litePal.getUpdateTime()));
                listItems.add(listItem);
                listItem = new HashMap<>();
                listItem.put("NOTE_MESSAGE", note_litePal.getTitle());
                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note_litePal.getUpdateTime()));
                listItems.add(listItem);
            }
        }
        myAdapter = new NoteBookAdapter(NoteParttionController.this, listItems);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //退出方法
    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(NoteParttionController.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            //用户退出处理
            if (current_user != null) {
                current_user.setIsLogin(0);
                current_user.save();
            }
            // 结束Activity&从栈中移除该Activity
            AtyUtil.getInstance().finishAllActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
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
        } );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                //
                break;
            case R.id.syns:
                //
                break;
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            LoginController.ActionStart(NoteParttionController.this);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(NoteParttionController.this, SettingController.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
