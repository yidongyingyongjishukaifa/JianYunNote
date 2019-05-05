package com.henu.jianyunnote.Parttion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.henu.jianyunnote.DataBase.Note;
import com.henu.jianyunnote.DataBase.NoteBook;
import com.henu.jianyunnote.DataBase.User;
import com.henu.jianyunnote.Index.Login;
import com.henu.jianyunnote.Page.NotePage;
import com.henu.jianyunnote.R;

import org.litepal.LitePal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteParttion extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static int[] local_notebooks_id;
    private List<Parttion> parttionList = new ArrayList<>();
    private ParttionAdapter parttionAdapter;
    private SharedPreferences pref;
    private TextView login_Email;
    public static int local_user_id;
    private String login_email;
    private int local_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_parttion);
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteBook notebook = new NoteBook();
                if (!login_email.equals("未登录") || local_user_id != 0) {
                    notebook.setUserId(local_user_id);
                }
                notebook.setNoteBookName("未命名笔记本");
                notebook.setCreateTime(new Date());
                notebook.setUpdateTime(new Date());
                notebook.save();
                local_notebooks_id= (int[]) arrayAddLength(local_notebooks_id,1);
                local_notebooks_id[local_notebooks_id.length-1]=notebook.getId();
                Parttion parttion = new Parttion(notebook.getNoteBookName());
                parttionList.add(parttion);
                parttionAdapter.notifyDataSetChanged();
                //Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG ).setAction( "Action", null ).show();
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
        if (!login_email.equals("未登录")) {
            List<User> user = LitePal.where("username=?", login_email).find(User.class);
            if (user != null && user.size() != 0) {
                for (User u : user) {
                    local_user_id = u.getId();
                }
                String uid = String.valueOf(local_user_id);
                List<NoteBook> noteBooks = LitePal.where("userId=?", uid).order("updateTime desc").find(NoteBook.class);
                if (noteBooks != null && noteBooks.size() != 0) {
                    local_count = 0;
                    local_notebooks_id = new int[noteBooks.size()];
                    for (NoteBook noteBook : noteBooks) {
                        local_notebooks_id[local_count] = noteBook.getId();
                        local_count++;
                        Parttion parttion = new Parttion(noteBook.getNoteBookName());
                        parttionList.add(parttion);
                    }
                }
//                List<Note> notes = LitePal.where("noteBookId=? and userId=?", "0", uid).order("updateTime desc").find(Note.class);
            } else {
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
                local_notebooks_id = new int[1];
                local_notebooks_id[0] = notebook.getId();
                Parttion parttion = new Parttion(notebook.getNoteBookName());
                parttionList.add(parttion);
            }
        } else {
            List<NoteBook> noteBooks = LitePal.where("userId=?", "0").order("updateTime desc").find(NoteBook.class);
            List<Note> notes = LitePal.where("noteBookId=? and userId=?", "0", "0").order("updateTime desc").find(Note.class);
            NoteBook notebook = new NoteBook();
            boolean isAdd=false;//用于判断是否将存入数据库中的notebook添加到parttion
            if (noteBooks != null && noteBooks.size() != 0) {
                local_count = 0;
                local_notebooks_id = new int[noteBooks.size()];
                for (NoteBook noteBook : noteBooks) {
                    isAdd=true;
                    local_notebooks_id[local_count] = noteBook.getId();
                    local_count++;
                    Parttion parttion = new Parttion(noteBook.getNoteBookName());
                    parttionList.add(parttion);
                }
            } else {
                notebook.setCreateTime(new Date());
                notebook.setUpdateTime(new Date());
                notebook.setNoteBookName("未命名笔记本");
                notebook.save();
                local_notebooks_id = new int[1];
                local_notebooks_id[0] = notebook.getId();
            }
            if (notes == null || notes.size() == 0) {
                Note note1 = new Note();
                Note note2 = new Note();
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
            }
            if(!isAdd){
                Parttion parttion = new Parttion(notebook.getNoteBookName());
                parttionList.add(parttion);
            }
        }
//        View view = navigationView.inflateHeaderView( R.layout.nav_header_main );
//        ImageView imageView = view.findViewById( R.id.imageView111 );
//        imageView.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //此处可跳转用户设置
//            }
//        } );

        parttionAdapter = new ParttionAdapter(NoteParttion.this, R.layout.parttion_item, parttionList);
        ListView listView = findViewById(R.id.parttion_listview);
        listView.setAdapter(parttionAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoteParttion.this, NotePage.class);
//                System.out.println("position------------------\n"+position+"\n-----------------------\n");
                intent.putExtra("position", position + "");
                startActivity(intent);
            }
        });
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

    public static Object arrayAddLength(Object oldArray,int addLength) {
        Class c = oldArray.getClass();
        if(!c.isArray())return null;
        Class componentType = c.getComponentType();
        int length = Array.getLength(oldArray);
        int newLength = length + addLength;
        Object newArray = Array.newInstance(componentType,newLength);
        System.arraycopy(oldArray,0,newArray,0,length);
        return newArray;
    }
}
