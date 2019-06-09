package com.henu.jianyunnote.controller.noteParttion;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.henu.jianyunnote.model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.model.LitePal.User_LitePal;
import com.henu.jianyunnote.dao.LitePal.INoteBookDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.IUserDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteBookDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.IUserDaoImpl_LitePal;
import com.henu.jianyunnote.R;
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

import static com.henu.jianyunnote.controller.notePage.NotePageController.local_notes_id;

public class NoteParttionController extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static int[] local_notebooks_id;
    private static final int NOTEPAGE_ACTIVITY = 1;
    private int p;
    private NoteBookAdapter myAdapter;
    private TextView login_Email;
    private ImageView imageView;
    public static int local_user_id;
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
                if (login_Email.getText() == " 未登录") {
                    LoginController.ActionStart(NoteParttionController.this);
                    Log.d("NoteParttionController", login_Email.getText().toString() + "///////////////////////");
                } else {
                    Log.d("NoteParttionController", login_Email.getText().toString() + "///////////////////////");
                    Intent intent = new Intent(NoteParttionController.this, SettingController.class);
                    startActivity(intent);
                }
            }
        });
        final ListView mListView = findViewById(R.id.notebook_listview);
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
                        .setIcon(R.drawable.notebook)
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
                                    addListItem(noteBook_litePal.getNoteBookName(), TimeUtil.Date2String(noteBook_litePal.getUpdateTime()));
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
                        .setIcon(R.drawable.note)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText Note_Title = myView.findViewById(R.id.note_Title);
                                String s = "";
                                if (Note_Title.getText() != null) {
                                    s = Note_Title.getText().toString();
                                }
                                NoteBook_LitePal noteBook_litePal = noteBookService.insert2NoteBook("无标题笔记本", local_user_id);
                                noteService.insert2Note(s, null, noteBook_litePal.getId(), local_user_id);
                                userService.updateUserByUser(NoteParttionController.current_user);
                                local_notebooks_id = ArrayUtil.insert2Array(local_notebooks_id, noteBook_litePal.getId());
                                addListItem(noteBook_litePal.getNoteBookName(), TimeUtil.Date2String(noteBook_litePal.getUpdateTime()));
                                myAdapter.notifyDataSetChanged();
                                Intent intent = new Intent(NoteParttionController.this, NotePageController.class);
                                intent.putExtra("position", 0 + "");
                                startActivityForResult(intent, NOTEPAGE_ACTIVITY);
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
                Intent intent = new Intent(NoteParttionController.this, NotePageController.class);
                intent.putExtra("position", position + "");
                startActivityForResult(intent, NOTEPAGE_ACTIVITY);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                p = position;
                final AlertDialog.Builder builder = new AlertDialog.Builder(NoteParttionController.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NoteParttionController.this);
                final View myView = layoutInflater.inflate(R.layout.new_notebook, null);
                builder.setTitle("修改笔记本名")
                        .setIcon(R.drawable.notebook)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText Notebook_Name = myView.findViewById(R.id.notebook_Name);
                                String s = "";
                                if (Notebook_Name.getText() != null) {
                                    s = Notebook_Name.getText().toString();
                                }
                                noteBookService.updateNoteBookNameById(s, local_notebooks_id[p]);
                                updateItem();
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
                noteBooks = LitePal.where("userId= ? and isDelete = ?", uid, "0").order("updateTime asc").find(NoteBook_LitePal.class);
                if (noteBooks != null && noteBooks.size() != 0) {
                    int local_count = noteBooks.size() - 1;
                    local_notebooks_id = new int[noteBooks.size()];
                    listItems.clear();
                    for (NoteBook_LitePal noteBook : noteBooks) {
                        local_notebooks_id[local_count] = noteBook.getId();
                        local_count--;
                        addListItem(noteBook.getNoteBookName(), TimeUtil.Date2String(noteBook.getUpdateTime()));
                    }
                }
            }
        } else {
            login_Email.setText("未登录");
            noteBooks = LitePal.where("userId=? and isDelete = ?", "0", "0").order("updateTime asc").find(NoteBook_LitePal.class);
            NoteBook_LitePal noteBook_litePal = new NoteBook_LitePal();
            boolean isAdd = false;//用于判断是否将存入数据库中的notebook添加了
            if (noteBooks != null && noteBooks.size() != 0) {
                listItems.clear();
                isAdd = true;
                int local_count = noteBooks.size() - 1;
                local_notebooks_id = new int[noteBooks.size()];
                for (NoteBook_LitePal noteBook : noteBooks) {
                    local_notebooks_id[local_count] = noteBook.getId();
                    local_count--;
                    addListItem(noteBook.getNoteBookName(), TimeUtil.Date2String(noteBook.getUpdateTime()));
                }
            } else {
                noteBook_litePal = noteBookService.insert2NoteBook("无标题笔记本", 0);
                local_notebooks_id = new int[1];
                local_notebooks_id[0] = noteBook_litePal.getId();
                noteService.insert2Note("无标题笔记", "测试内容", noteBook_litePal.getId(), 0);
                noteBook_litePal.setNoteNumber(1);
                noteBook_litePal.save();
            }
            if (!isAdd) {
                listItems.clear();
                addListItem(noteBook_litePal.getNoteBookName(), TimeUtil.Date2String(noteBook_litePal.getUpdateTime()));
            }
        }
        myAdapter = new NoteBookAdapter(NoteParttionController.this, listItems);
    }

    private void addListItem(String NOTEBOOK_MESSAGE, Object NOTEBOOK_UPDATE_TIME) {
        Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
        listItem.put("NOTEBOOK_MESSAGE", NOTEBOOK_MESSAGE);
        listItem.put("NOTEBOOK_UPDATE_TIME", NOTEBOOK_UPDATE_TIME);
        listItems.add(0, listItem);
    }

    private void updateItem() {
        int notebook_id = local_notebooks_id[p];
        userService.updateUserByUser(current_user);
        listItems.remove(p);
        local_notebooks_id = ArrayUtil.deleteIdInArray(local_notebooks_id, p);
        List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", String.valueOf(notebook_id)).find(NoteBook_LitePal.class);
        for (NoteBook_LitePal noteBook_litePal : noteBookList) {
            local_notebooks_id = ArrayUtil.insert2Array(local_notebooks_id, noteBook_litePal.getId());
            addListItem(noteBook_litePal.getNoteBookName(), TimeUtil.Date2String(noteBook_litePal.getUpdateTime()));
        }
        myAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTEPAGE_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                updateItem();
            }
        }
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
            if (login_Email.getText() == " 未登录") {
                LoginController.ActionStart(NoteParttionController.this);
                Log.d("NoteParttionController", login_Email.getText().toString() + "///////////////////////");
            } else {
                Log.d("NoteParttionController", login_Email.getText().toString() + "///////////////////////");
                Intent intent = new Intent(NoteParttionController.this, SettingController.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_manage) {
            if (login_Email.getText() == " 未登录") {
                View view = findViewById(R.id.sack);
                Snackbar.make(view, "当前没有登陆", Snackbar.LENGTH_LONG).setAction("登陆", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginController.ActionStart(NoteParttionController.this);
                    }
                }).show();
            } else {
                login_Email.setText(" 未登录");
                if (current_user != null) {
                    current_user.setIsLogin(0);
                    current_user.save();
                }
                Toast.makeText(NoteParttionController.this, "成功注销！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
