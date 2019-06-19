package com.henu.jianyunnote.activity.noteParttion;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.henu.jianyunnote.activity.index.LoginActivity;
import com.henu.jianyunnote.activity.notePage.NotePageActivity;
import com.henu.jianyunnote.model.Bmob.NoteBook_Bmob;
import com.henu.jianyunnote.model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.model.LitePal.User_LitePal;
import com.henu.jianyunnote.dao.LitePal.INoteBookDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.IUserDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteBookDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.IUserDaoImpl_LitePal;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.activity.setting.SettingActivity;
import com.henu.jianyunnote.util.ArrayUtil;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.Const;
import com.henu.jianyunnote.util.NetWorkUtil;
import com.henu.jianyunnote.util.NoteBookAdapter;
import com.henu.jianyunnote.util.TimeUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class NoteParttionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static int[] local_notebooks_id;
    private static final int NOTEPAGE_ACTIVITY = 1;
    private int p;
    private NoteBookAdapter myAdapter;
    private TextView login_Email;
    private ImageView imageView;
    private ListView mListView;
    public static int local_user_id;
    private List<Map<String, Object>> listItems = new ArrayList<>();
    public static User_LitePal current_user;
    private long mExitTime;
    private IUserDao_LitePal userService = new IUserDaoImpl_LitePal();
    private INoteBookDao_LitePal noteBookService = new INoteBookDaoImpl_LitePal();
    private INoteDao_LitePal noteService = new INoteDaoImpl_LitePal();
    private Thread mThread;
    private Handler hander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    myAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    myAdapter = new NoteBookAdapter(NoteParttionActivity.this, listItems);
                    // 为ListView设置Adapter
                    mListView.setAdapter(myAdapter);
                    break;
                case 2:
                    String uid = String.valueOf(local_user_id);
                    List<NoteBook_LitePal> noteBooks = LitePal.where("userId= ? and isDelete = ?", uid, "0").order("updateTime asc").find(NoteBook_LitePal.class);
                    if (noteBooks != null && noteBooks.size() != 0) {
                        int local_count = noteBooks.size() - 1;
                        local_notebooks_id = new int[noteBooks.size()];
                        listItems.clear();
                        for (NoteBook_LitePal noteBook : noteBooks) {
                            local_notebooks_id[local_count] = noteBook.getId();
                            local_count--;
                            boolean isSync = false;
                            if (noteBook.getIsSync() == 1) {
                                isSync = true;
                            }
                            addListItem(noteBook.getNoteBookName(), TimeUtil.Date2String(noteBook.getUpdateTime()), isSync);
                        }
                    }
                    myAdapter = new NoteBookAdapter(NoteParttionActivity.this, listItems);
                    // 为ListView设置Adapter
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
        setContentView(R.layout.activity_note_parttion);
        // 添加Activity到堆栈
        Bmob.initialize(this, "bc95d28fa2c059530870d4dbb550b38f");//初始化Bmob  后面是服务器端应用ID

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
                    LoginActivity.ActionStart(NoteParttionActivity.this);
                    Log.d("NoteParttionActivity", login_Email.getText().toString() + "///////////////////////");
                } else {
                    Log.d("NoteParttionActivity", login_Email.getText().toString() + "///////////////////////");
                    Intent intent = new Intent(NoteParttionActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
            }
        });
        mListView = findViewById(R.id.notebook_listview);
        final FloatingActionsMenu menu = findViewById(R.id.fab_menu);
        init();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                p = position;
                Intent intent = new Intent(NoteParttionActivity.this, NotePageActivity.class);
                intent.putExtra("notebook_id", local_notebooks_id[position] + "");
                startActivityForResult(intent, NOTEPAGE_ACTIVITY);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                p = position;
                final AlertDialog.Builder builder = new AlertDialog.Builder(NoteParttionActivity.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NoteParttionActivity.this);
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
                                boolean isSync = canAccessNetWork();
                                noteBookService.updateNoteBookNameById(s, local_notebooks_id[p], isSync);
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
        final com.getbase.floatingactionbutton.FloatingActionButton actionA = findViewById(R.id.fab_1);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(NoteParttionActivity.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NoteParttionActivity.this);
                final View myView = layoutInflater.inflate(R.layout.new_notebook, null);
                builder.setTitle("新建笔记本")
                        .setIcon(R.drawable.notebook)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mThread = new Thread() {
                                    @Override
                                    public void run() {
                                        final EditText Notebook_Name = myView.findViewById(R.id.notebook_Name);
                                        String s = "";
                                        if (Notebook_Name.getText() != null) {
                                            s = Notebook_Name.getText().toString();
                                        }
                                        boolean isSync = canAccessNetWork();
                                        NoteBook_LitePal noteBook_litePal = noteBookService.insert2NoteBook(s, local_user_id, isSync);
                                        userService.updateUserByUser(current_user);
                                        local_notebooks_id = ArrayUtil.insert2Array(local_notebooks_id, noteBook_litePal.getId());
                                        addListItem(noteBook_litePal.getNoteBookName(), TimeUtil.Date2String(noteBook_litePal.getUpdateTime()), isSync);
                                        hander.sendEmptyMessage(0);
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

        final com.getbase.floatingactionbutton.FloatingActionButton actionB = findViewById(R.id.fab_2);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(NoteParttionActivity.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NoteParttionActivity.this);
                final View myView = layoutInflater.inflate(R.layout.new_note, null);
                builder.setTitle("新建笔记")
                        .setIcon(R.drawable.note)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mThread = new Thread() {
                                    @Override
                                    public void run() {
                                        final EditText Note_Title = myView.findViewById(R.id.note_Title);
                                        String s = "";
                                        if (Note_Title.getText() != null) {
                                            s = Note_Title.getText().toString();
                                        }
                                        boolean isSync = canAccessNetWork();
                                        NoteBook_LitePal noteBook_litePal = noteBookService.insert2NoteBook("无标题笔记本", local_user_id, isSync);
                                        noteService.insert2Note(s, null, noteBook_litePal.getId(), local_user_id, isSync);
                                        userService.updateUserByUser(NoteParttionActivity.current_user);
                                        local_notebooks_id = ArrayUtil.insert2Array(local_notebooks_id, noteBook_litePal.getId());
                                        addListItem(noteBook_litePal.getNoteBookName(), TimeUtil.Date2String(noteBook_litePal.getUpdateTime()), isSync);
                                        hander.sendEmptyMessage(0);
                                        Intent intent = new Intent(NoteParttionActivity.this, NotePageActivity.class);
                                        intent.putExtra("notebook_id", local_notebooks_id[0] + "");
                                        startActivityForResult(intent, NOTEPAGE_ACTIVITY);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void init() {
        mThread = new Thread() {
            @Override
            public void run() {
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
                        BmobQuery<NoteBook_Bmob> query1 = new BmobQuery<>();
                        query1.addWhereEqualTo("userId", u.getBmob_user_id());
                        query1.setLimit(999);
                        //执行查询方法
                        query1.findObjects(new FindListener<NoteBook_Bmob>() {
                            @Override
                            public void done(List<NoteBook_Bmob> object, BmobException e) {
                                if (e == null) {
                                    for (NoteBook_Bmob noteBook_bmob : object) {
                                        List<NoteBook_LitePal> noteBookList = LitePal.where("bmob_notebook_id = ? and isDownload = ?", noteBook_bmob.getObjectId(), Const.isDownload).find(NoteBook_LitePal.class);
                                        if (noteBookList == null || noteBookList.size() == 0) {
                                            NoteBook_LitePal noteBook_litePal = new NoteBook_LitePal();
                                            noteBook_litePal.setBmob_notebook_id(noteBook_bmob.getObjectId());
                                            noteBook_litePal.setBmob_user_id(current_user.getBmob_user_id());
                                            noteBook_litePal.setUserId(current_user.getId());
                                            noteBook_litePal.setIsDelete(noteBook_bmob.getIsDelete());
                                            noteBook_litePal.setNoteBookName(noteBook_bmob.getNoteBookName());
                                            noteBook_litePal.setNoteNumber(noteBook_bmob.getNoteNumber());
                                            boolean flag = canAccessNetWork();
                                            if (flag) {
                                                noteBook_litePal.setIsSync(1);
                                            }
                                            noteBook_litePal.setIsDownload(Integer.parseInt(Const.isDownload));
                                            noteBook_litePal.setCreateTime(new Date());
                                            noteBook_litePal.setUpdateTime(new Date());
                                            noteBook_litePal.save();
                                        }
                                    }
                                    hander.sendEmptyMessage(2);
                                }
                            }
                        });
                    }
                } else {
                    login_Email.setText("未登录");
                    local_user_id = 0;
                    noteBooks = LitePal.where("userId=? and isDelete = ?", String.valueOf(local_user_id), "0").order("updateTime asc").find(NoteBook_LitePal.class);
                    NoteBook_LitePal noteBook_litePal = new NoteBook_LitePal();
                    boolean isAdd = false;//用于判断是否将存入数据库中的notebook添加了
                    canAccessNetWork();
                    if (noteBooks != null && noteBooks.size() != 0) {
                        listItems.clear();
                        isAdd = true;
                        int local_count = noteBooks.size() - 1;
                        local_notebooks_id = new int[noteBooks.size()];
                        for (NoteBook_LitePal noteBook : noteBooks) {
                            local_notebooks_id[local_count] = noteBook.getId();
                            local_count--;
                            boolean isSync = false;
                            if (noteBook.getIsSync() == 1) {
                                isSync = true;
                            }
                            addListItem(noteBook.getNoteBookName(), TimeUtil.Date2String(noteBook.getUpdateTime()), isSync);
                        }
                    } else {
                        noteBook_litePal = noteBookService.insert2NoteBook("无标题笔记本", local_user_id, true);
                        local_notebooks_id = new int[1];
                        local_notebooks_id[0] = noteBook_litePal.getId();
                        noteService.insert2Note("无标题笔记", "海内存知己，天涯若比邻！", noteBook_litePal.getId(), local_user_id, true);
                        noteBook_litePal.setNoteNumber(1);
                        noteBook_litePal.save();
                    }
                    if (!isAdd) {
                        listItems.clear();
                        addListItem(noteBook_litePal.getNoteBookName(), TimeUtil.Date2String(noteBook_litePal.getUpdateTime()), true);
                    }
                    hander.sendEmptyMessage(1);
                }
            }
        };
        mThread.start();
    }

    private boolean canAccessNetWork() {
        boolean isSync = false;
        if (!NetWorkUtil.isNetworkConnected(NoteParttionActivity.this)) {
            isSync = true;
            Toast.makeText(NoteParttionActivity.this, "未联网", Toast.LENGTH_LONG).show();
        }
        if (local_user_id == 0) {
            isSync = true;
        }
        return isSync;
    }

    private void addListItem(String NOTEBOOK_MESSAGE, Object NOTEBOOK_UPDATE_TIME, boolean isSync) {
        Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
        listItem.put("NOTEBOOK_MESSAGE", NOTEBOOK_MESSAGE);
        listItem.put("NOTEBOOK_UPDATE_TIME", NOTEBOOK_UPDATE_TIME);
        if (isSync) {
            listItem.put("IS_NOTEBOOK_SYNC", "未同步");
        }
        listItems.add(0, listItem);
    }

    private void updateItem() {
        mThread = new Thread() {
            @Override
            public void run() {
                int notebook_id = local_notebooks_id[p];
                userService.updateUserByUser(current_user);
                listItems.remove(p);
                local_notebooks_id = ArrayUtil.deleteIdInArray(local_notebooks_id, p);
                List<NoteBook_LitePal> noteBookList = LitePal.where("id = ?", String.valueOf(notebook_id)).find(NoteBook_LitePal.class);
                for (NoteBook_LitePal noteBook_litePal : noteBookList) {
                    boolean isSync = false;
                    if (noteBook_litePal.getIsSync() == 1) {
                        isSync = true;
                    }
                    local_notebooks_id = ArrayUtil.insert2Array(local_notebooks_id, noteBook_litePal.getId());
                    addListItem(noteBook_litePal.getNoteBookName(), TimeUtil.Date2String(noteBook_litePal.getUpdateTime()), isSync);
                }
                hander.sendEmptyMessage(0);
            }
        };
        mThread.start();
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
            Toast.makeText(NoteParttionActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
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
            LoginActivity.ActionStart(NoteParttionActivity.this);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            if (login_Email.getText() == " 未登录") {
                LoginActivity.ActionStart(NoteParttionActivity.this);
                Log.d("NoteParttionActivity", login_Email.getText().toString() + "///////////////////////");
            } else {
                Log.d("NoteParttionActivity", login_Email.getText().toString() + "///////////////////////");
                Intent intent = new Intent(NoteParttionActivity.this, SettingActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_manage) {
            if (login_Email.getText() == " 未登录") {
                View view = findViewById(R.id.sack);
                Snackbar.make(view, "当前没有登陆", Snackbar.LENGTH_LONG).setAction("登陆", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginActivity.ActionStart(NoteParttionActivity.this);
                    }
                }).show();
            } else {
                login_Email.setText(" 未登录");
                if (current_user != null) {
                    current_user.setIsLogin(0);
                    current_user.save();
                }
                Toast.makeText(NoteParttionActivity.this, "成功注销！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_share) {
            if (canAccessNetWork()) {
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();

                // title标题，微信、QQ和QQ空间等平台使用
                oks.setTitle("简云笔记");
                // titleUrl QQ和QQ空间跳转链接
                oks.setTitleUrl("http://www.baixdu.com");
                // text是分享文本，所有平台都需要这个字段
                oks.setText("我是分享文本");
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
                // url在微信、微博，Facebook等平台中使用
                oks.setUrl("http://sharesdk.cn");
                // comment是我对这条分享的评论，仅在人人网使用
                oks.setComment("我是测试评论文本");
                // 启动分享GUI
                oks.show(this);
            }
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
