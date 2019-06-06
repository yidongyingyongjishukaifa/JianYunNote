package com.henu.jianyunnote.controller.index;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.henu.jianyunnote.model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.model.LitePal.User_LitePal;
import com.henu.jianyunnote.model.Bmob.Users_Bmob;
import com.henu.jianyunnote.controller.noteParttion.NoteParttionController;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.util.AESUtil;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.MD5Util;

import org.litepal.LitePal;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.widget.Toast.LENGTH_LONG;

public class LoginController extends AppCompatActivity {

    private CheckBox remember_password;
    private CheckBox auto_login;
    private ListPopupWindow listPopupWindow;
    private EditText Email_local;
    private EditText Password_local;
    private String email;
    private String password;
    private boolean is_Remember;
    private boolean autoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AtyUtil.getInstance().addActivity(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        Bmob.initialize(this, "bc95d28fa2c059530870d4dbb550b38f");//初始化Bmob  后面是服务器端应用ID
        Button login = findViewById(R.id.login);
        Email_local = findViewById(R.id.email);
        Password_local = findViewById(R.id.password);
        final TextView forgotpassword = findViewById(R.id.forgot_password);
        Button register = findViewById(R.id.register);
        remember_password = findViewById(R.id.remember_password);
        auto_login = findViewById(R.id.auto_login);
        List<User_LitePal> userList = LitePal.where("isRemember = ?", "1").order("loginTime desc").limit(1).find(User_LitePal.class);
        if (userList != null && userList.size() != 0) {
            for (User_LitePal u : userList) {
                String email = u.getUsername();
                String d_password = u.getPassword();
                //对读取到的密码进行解密
                String password = AESUtil.decrypt(d_password);
                Email_local.setText(email);
                Password_local.setText(password);
                remember_password.setChecked(true);
                if (u.isAutoLogin()) {
                    auto_login.setChecked(true);
                    gotoNote();
                }
            }
        }




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = Email_local.getText().toString();
                password = Password_local.getText().toString();
                if (!isEmail(email)) {
                    String info = "请输入正确的邮箱";
                    Toast.makeText(LoginController.this, info, LENGTH_LONG).show();
                } else {
                    BmobQuery<Users_Bmob> query = new BmobQuery<>();
                    //查询Email的数据
                    query.addWhereEqualTo("Email", Email_local.getText());
                    //返回50条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(1);
                    //执行查询方法
                    query.findObjects(new FindListener<Users_Bmob>() {
                        @Override
                        public void done(List<Users_Bmob> object, BmobException e) {
                            if (object.size() == 1) {
                                if (e == null) {
                                    for (Users_Bmob u : object)
                                        if (u.getPassword().equals(MD5Util.Encode(password))) {
//                                            String info = "登录成功";
//                                            Toast.makeText(LoginController.this, info, LENGTH_LONG).show();
                                            is_Remember = remember_password.isChecked();
                                            autoLogin = auto_login.isChecked();
                                            init();
                                            gotoNote();
                                        } else if (Password_local.getText().length() == 0) {
                                            String info = "请输入密码";
                                            Toast.makeText(LoginController.this, info, LENGTH_LONG).show();
                                        } else {
                                            MyAlertDialog("邮箱或密码错误", "确定");
                                        }
                                } else {
                                    String Error = "异常" + e.getErrorCode();
                                    MyAlertDialog(Error, "确定");
                                }
                            } else {
                                MyAlertDialog("此邮箱未注册", "确定");
                            }
                        }
                    });
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginController.this, RegisterController.class);
                startActivity(intent);
//                finish();
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginController.this, FindPasswordController.class);
                startActivity(intent);
                finish();
            }
        });

        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!remember_password.isChecked()) {
                    remember_password.setChecked(true);
                }
            }
        });

        Email_local.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getX() >= (Email_local.getWidth() - Email_local.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {//当点击了箭头位置的时候回调用下面的函数，显示出下拉框 
                        Email_local.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.arrowdown), null);
                        showListPopulWindow();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            default:
                return true;
        }
    }

    private void init() {
        List<User_LitePal> user = LitePal.where("username= ?", email).find(User_LitePal.class);
        String d_password = AESUtil.encrypt(password);
        if (user == null || user.size() == 0) {
            User_LitePal u = new User_LitePal();
            u.setUsername(email);
            u.setPassword(d_password);
            u.setIsLogin(1);
            u.setLoginTime(new Date());
            if (is_Remember) {
                u.setIsRemember(1);
            } else {
                u.setIsRemember(0);
            }
            u.setAutoLogin(autoLogin);
            u.save();
            NoteBook_LitePal notebook = new NoteBook_LitePal();
            notebook.setUserId(u.getId());
            notebook.setCreateTime(new Date());
            notebook.setUpdateTime(new Date());
            notebook.setNoteBookName("未命名笔记本");
            notebook.setIsDelete(0);
            notebook.save();
            Note_LitePal note = new Note_LitePal();
            note.setUserId(u.getId());
            note.setNoteBookId(notebook.getId());
            note.setTitle("未命名笔记");
            note.setContent("测试内容");
            note.setCreateTime(new Date());
            note.setUpdateTime(new Date());
            note.setIsDelete(0);
            note.save();
            notebook.setNoteNumber(1);
            notebook.save();
            Note_LitePal note2 = new Note_LitePal();
            note2.setUserId(u.getId());
            note2.setTitle("未命名笔记");
            note2.setContent("测试内容");
            note2.setCreateTime(new Date());
            note2.setUpdateTime(new Date());
            note2.setIsDelete(0);
            note2.save();
        } else {
            for (User_LitePal u : user) {
                u.setPassword(d_password);
                u.setIsLogin(1);
                if (is_Remember) {
                    u.setIsRemember(1);
                } else {
                    u.setIsRemember(0);
                }
                u.setAutoLogin(autoLogin);
                u.setLoginTime(new Date());
                u.save();
            }
        }
    }

    public void showListPopulWindow() {//用来显示下拉框     
        int userCount = LitePal.count(User_LitePal.class);
        final String[] acc = new String[userCount];//定义一个账号数组，长度为选择记住密码的登录成功的账号个数               
        final String[] pas = new String[userCount];//定义一个密码数组，长度为选择记住密码的登录成功的账号个数     
        List<User_LitePal> userList = LitePal.order("loginTime desc").find(User_LitePal.class);
        for (int i = 0; i < userCount; i++) {
            if (userList.get(i).getIsRemember() == 1) {
                acc[i] = userList.get(i).getUsername();//初始化账号数组，把已保存的账号放到数组里面去     
                pas[i] = AESUtil.decrypt(userList.get(i).getPassword());//初始化密码数组，把已保存的密码放到数组里面去   
            }
        }
        listPopupWindow = new ListPopupWindow(LoginController.this);
        listPopupWindow.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, acc));//把账号的数据显示到下拉列表里面去       
        listPopupWindow.setAnchorView(Email_local);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置项点击监听           
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Email_local.setText(acc[i]);//当选中下拉框的某一个选项的时候，把选择的选项内容展示在EditText上             
                Password_local.setText(pas[i]);//将选中的账号的密码显示出来，显示在面板上，               
                listPopupWindow.dismiss();//如果已经选择了，隐藏起来           
            }
        });
        listPopupWindow.show();
    }

    private void gotoNote() {
        Intent intent = new Intent(LoginController.this, NoteParttionController.class);
        startActivity(intent);
        finish();
    }

    public static boolean isEmail(String email) {   //判断邮箱是否合法
        if (null == email || "".equals(email)) return false;
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public void MyAlertDialog(String message, String button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginController.this);
        builder.setMessage(message);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public static void ActionStart(Context context) {
        Intent intent = new Intent(context, LoginController.class);
        context.startActivity(intent);
    }
}

