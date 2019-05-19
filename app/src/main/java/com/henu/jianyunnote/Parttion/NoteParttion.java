package com.henu.jianyunnote.Parttion;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.henu.jianyunnote.Content.NoteContent;
import com.henu.jianyunnote.DataBase.Note;
import com.henu.jianyunnote.DataBase.NoteBook;
import com.henu.jianyunnote.DataBase.User;
import com.henu.jianyunnote.Index.Login;
import com.henu.jianyunnote.Page.NotePage;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.Util.MyAdapter;
import com.henu.jianyunnote.Util.TimeUtil;

import org.litepal.LitePal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteParttion extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static int[] local_notebooks_id;
    public static int[] local_notes_id;
    private MyAdapter myAdapter;
    private SharedPreferences pref;
    private TextView login_Email;
    public static int local_user_id;
    private String login_email;
    private int local_count;
    public static int notebooks_count = 0;
    private List<Map<String, Object>> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_parttion);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionsMenu menu = findViewById(R.id.fab_menu);
        final com.getbase.floatingactionbutton.FloatingActionButton actionA = findViewById(R.id.fab_1);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(NoteParttion.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NoteParttion.this);
                final View myView = layoutInflater.inflate(R.layout.new_notebook, null);
                builder.setTitle("新建笔记本")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText Notebook_Name = myView.findViewById(R.id.notebook_Name);
                                String s = Notebook_Name.getText().toString();
                                NoteBook notebook = new NoteBook();
                                if (!login_email.equals("未登录") || local_user_id != 0) {
                                    notebook.setUserId(local_user_id);
                                }
                                if ("".equals(s)) {
                                    notebook.setNoteBookName("未命名笔记本");
                                } else {
                                    notebook.setNoteBookName(s);
                                }
                                notebook.setCreateTime(new Date());
                                notebook.setUpdateTime(new Date());
                                notebook.save();
                                notebooks_count += 1;
                                local_notebooks_id = (int[]) arrayAddLength(local_notebooks_id, 1);
                                local_notebooks_id[0] = notebook.getId();
                                Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                                listItem.put("NOTE_MESSAGE", notebook.getNoteBookName());
                                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(notebook.getUpdateTime()));
                                listItems.add(0, listItem);
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
        final com.getbase.floatingactionbutton.FloatingActionButton actionB = findViewById(R.id.fab_2);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(NoteParttion.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NoteParttion.this);
                final View myView = layoutInflater.inflate(R.layout.new_notebook, null);
                builder.setTitle("新建笔记")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText Notebook_Name = myView.findViewById(R.id.notebook_Name);
                                String s = Notebook_Name.getText().toString();
                                Note note = new Note();
                                if ("".equals(s)) {
                                    note.setTitle("未命名笔记");
                                } else {
                                    note.setTitle(s);
                                }
                                note.setUserId(local_user_id);
                                note.setCreateTime(new Date());
                                note.setUpdateTime(new Date());
                                note.save();

                                local_notes_id = (int[]) arrayAddLength(local_notes_id, 1);
                                local_notes_id[0] = note.getId();
                                Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                                listItem.put("NOTE_MESSAGE", note.getTitle());
                                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note.getUpdateTime()));
                                listItems.add(notebooks_count, listItem);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        login_Email = navigationView.inflateHeaderView(R.layout.nav_header_main).findViewById(R.id.login_email);
        login_email = pref.getString("login_email", "未登录");
        login_Email.setText(login_email);
        final ListView mListView = findViewById(R.id.parttion_listview);
        init();
//        View view = navigationView.inflateHeaderView( R.layout.nav_header_main );
//        ImageView imageView = view.findViewById( R.id.imageView111 );
//        imageView.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //此处可跳转用户设置
//            }
//        } );


        // 为ListView设置Adapter
        mListView.setAdapter(myAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                Intent intent;
                if (position < notebooks_count) {
                    intent = new Intent(NoteParttion.this, NotePage.class);
//                System.out.println("position------------------\n"+position+"\n-----------------------\n");
                    intent.putExtra("position", position + "");
                } else {
                    intent = new Intent(NoteParttion.this, NoteContent.class);
                    int note_position = position - notebooks_count;
                    intent.putExtra("position", note_position + "");
                    intent.putExtra("is_note", true);
                }
                startActivity(intent);
            }
        });
    }

    private void init() {
        List<NoteBook> noteBooks;
        if (!login_email.equals("未登录")) {
            List<User> user = LitePal.where("username=?", login_email).find(User.class);
            if (user != null && user.size() != 0) {
                // 本地已登录的用户
                for (User u : user) {
                    local_user_id = u.getId();
                }
                String uid = String.valueOf(local_user_id);
                noteBooks = LitePal.where("userId=?", uid).order("updateTime desc").find(NoteBook.class);
                if (noteBooks != null && noteBooks.size() != 0) {
                    local_count = 0;
                    local_notebooks_id = new int[noteBooks.size()];
                    listItems.clear();
                    for (NoteBook noteBook : noteBooks) {
                        local_notebooks_id[local_count] = noteBook.getId();
                        local_count++;
                        Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                        listItem.put("NOTE_MESSAGE", noteBook.getNoteBookName());
                        listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(noteBook.getUpdateTime()));
                        listItems.add(listItem);
                    }
                    notebooks_count = local_count;
                }
                List<Note> notes = LitePal.where("noteBookId=? and userId=?", "0", uid).order("updateTime desc").find(Note.class);
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
            } else {
                // 本地登录的新用户
                User u = new User();
                u.setUsername(login_email);
                u.save();
                local_user_id = u.getId();
                NoteBook notebook = new NoteBook();
                notebook.setUserId(local_user_id);
                notebook.setCreateTime(new Date());
                notebook.setUpdateTime(new Date());
                notebook.setNoteBookName("未命名笔记本");
                notebook.save();
                Note note = new Note();
                note.setUserId(local_user_id);
                note.setNoteBookId(notebook.getId());
                note.setTitle("未命名笔记");
                note.setContent("测试内容");
                note.setCreateTime(new Date());
                note.setUpdateTime(new Date());
                note.save();
                notebook.setNoteNumber(1);
                notebook.save();
                Note note2 = new Note();
                note2.setUserId(local_user_id);
                note2.setTitle("未命名笔记");
                note2.setContent("测试内容");
                note2.setCreateTime(new Date());
                note2.setUpdateTime(new Date());
                note2.save();
                local_notebooks_id = new int[1];
                local_notebooks_id[0] = notebook.getId();
                notebooks_count = 1;
                local_notes_id = new int[1];
                local_notes_id[0] = note2.getId();
                listItems.clear();
                Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                listItem.put("NOTE_MESSAGE", notebook.getNoteBookName());
                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(notebook.getUpdateTime()));
                listItems.add(listItem);
                listItem = new HashMap<>();
                listItem.put("NOTE_MESSAGE", note.getTitle());
                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note.getUpdateTime()));
                listItems.add(listItem);
            }
        } else {
            noteBooks = LitePal.where("userId=?", "0").order("updateTime desc").find(NoteBook.class);
            List<Note> notes = LitePal.where("noteBookId=? and userId=?", "0", "0").order("updateTime desc").find(Note.class);
            NoteBook notebook = new NoteBook();
            Note note1 = new Note();
            Note note2 = new Note();
            boolean isAdd = false;//用于判断是否将存入数据库中的notebook添加了
            if (noteBooks != null && noteBooks.size() != 0) {
                listItems.clear();
                local_count = 0;
                local_notebooks_id = new int[noteBooks.size()];
                for (NoteBook noteBook : noteBooks) {
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
                notebook.setCreateTime(new Date());
                notebook.setUpdateTime(new Date());
                notebook.setNoteBookName("未命名笔记本");
                notebook.save();
                local_notebooks_id = new int[1];
                local_notebooks_id[0] = notebook.getId();
                notebooks_count = 1;
            }
            if (notes != null && notes.size() != 0) {
                local_count = 0;
                local_notes_id = new int[notes.size()];
                for (Note note : notes) {
                    isAdd = true;
                    local_notes_id[local_count] = note.getId();
                    local_count++;
                    Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                    listItem.put("NOTE_MESSAGE", note.getTitle());
                    listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note.getUpdateTime()));
                    listItems.add(listItem);
                }
            } else {
                note1.setNoteBookId(notebook.getId());
                note1.setTitle("未命名笔记");
                note1.setContent("测试内容");
                note1.setCreateTime(new Date());
                note1.setUpdateTime(new Date());
                note1.save();
                notebook.setNoteNumber(1);
                notebook.save();
                note2.setTitle("未命名笔记");
                note2.setContent("测试内容");
                note2.setCreateTime(new Date());
                note2.setUpdateTime(new Date());
                note2.save();
                local_notes_id = new int[1];
                local_notes_id[0] = note2.getId();
            }
            if (!isAdd) {
                listItems.clear();
                Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
                listItem.put("NOTE_MESSAGE", notebook.getNoteBookName());
                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(notebook.getUpdateTime()));
                listItems.add(listItem);
                listItem = new HashMap<>();
                listItem.put("NOTE_MESSAGE", note2.getTitle());
                listItem.put("NOTE_UPDATE_TIME", TimeUtil.Date2String(note2.getUpdateTime()));
                listItems.add(listItem);
            }
        }
        myAdapter = new MyAdapter(NoteParttion.this, listItems);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup:
                //
                break;
            case R.id.delete:
                //
                break;
            case R.id.setting:
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
            Login.ActionStart(NoteParttion.this);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Object arrayAddLength(Object oldArray, int addLength) {
        Class c = oldArray.getClass();
        if (!c.isArray()) return null;
        Class componentType = c.getComponentType();
        int length = Array.getLength(oldArray);
        int newLength = length + addLength;
        Object newArray = Array.newInstance(componentType, newLength);
        System.arraycopy(oldArray, 0, newArray, 1, length);
        return newArray;
    }
}
