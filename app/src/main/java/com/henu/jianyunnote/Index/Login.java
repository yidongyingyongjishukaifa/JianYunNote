package com.henu.jianyunnote.Index;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
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

import com.henu.jianyunnote.Beans.Users;
import com.henu.jianyunnote.Parttion.NoteParttion;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.Util.AESUtil;
import com.henu.jianyunnote.Util.MD5Util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.widget.Toast.LENGTH_LONG;

public class Login extends AppCompatActivity {

    private CheckBox remember_password;
    private CheckBox auto_login;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ListPopupWindow listPopupWindow;
    private EditText Email_local;
    private EditText Password_local;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this, "bc95d28fa2c059530870d4dbb550b38f");//初始化Bmob  后面是服务器端应用ID
        Button login = findViewById(R.id.login);
        Email_local = findViewById(R.id.email);
        Password_local = findViewById(R.id.password);
        final TextView forgotpassword = findViewById(R.id.forgot_password);
        Button register = findViewById(R.id.register);
        remember_password = findViewById(R.id.remember_password);
        auto_login = findViewById(R.id.auto_login);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_password", false);
        boolean autoLogin = pref.getBoolean("auto_login", false);
        if (isRemember) {
            // 将账号和密码都设置到文本框中
            int usernum = pref.getInt("user_num", 0) - 1;
            int nowNum = pref.getInt("now_num", 0);
            String email = "";
            String d_password = "";
            if (nowNum < usernum) {
                email = pref.getString("email" + nowNum, "");
                //对读取到的密码进行解密
                d_password = pref.getString("password" + nowNum, "");
            } else {
                email = pref.getString("email" + usernum, "");
                //对读取到的密码进行解密
                d_password = pref.getString("password" + usernum, "");
            }
            String password = AESUtil.decrypt(d_password);
            Email_local.setText(email);
            Password_local.setText(password);
            remember_password.setChecked(true);
            if (autoLogin) {
                auto_login.setChecked(true);
                gotoNote();
            }
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = Email_local.getText().toString();
                password = Password_local.getText().toString();
                if (isEmail(email) == false) {
                    String info = "请输入正确的邮箱";
                    Toast.makeText(Login.this, info, LENGTH_LONG).show();
                } else {
                    BmobQuery<Users> query = new BmobQuery<Users>();
                    //查询Email的数据
                    query.addWhereEqualTo("Email", Email_local.getText());
                    //返回50条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(1);
                    //执行查询方法
                    query.findObjects(new FindListener<Users>() {
                        @Override
                        public void done(List<Users> object, BmobException e) {
                            if (object.size() == 1) {
                                if (e == null) {
                                    for (Users u : object)
                                        if (u.getPassword().equals(MD5Util.Encode(password))) {
                                            String info = "登录成功";
                                            Toast.makeText(Login.this, info, LENGTH_LONG).show();
                                            editor = pref.edit();
                                            if (remember_password.isChecked()) {
                                                String d_password = AESUtil.encrypt(password);
                                                boolean isFind = isFind(email);
                                                int usernum = 0;
                                                int nowNum = 0;
                                                if (isFind) {
                                                    usernum = getPosition(email);
                                                    nowNum = usernum;
                                                    editor.putString("email" + usernum, email);
                                                    editor.putString("password" + usernum, d_password);
                                                    usernum = pref.getInt("user_num", 0) - 1;
                                                } else {
                                                    usernum = pref.getInt("user_num", 0);
                                                    editor.putString("email" + usernum, email);
                                                    editor.putString("password" + usernum, d_password);
                                                }
                                                editor.putBoolean("remember_password", true);
                                                if (auto_login.isChecked()) {
                                                    editor.putBoolean("auto_login", true);
                                                }
                                                editor.putString("login_email", email);
                                                editor.putInt("user_num", usernum + 1);
                                                editor.putInt("now_num", nowNum);
                                            } else {
                                                editor.clear();
                                            }
                                            editor.apply();
                                            Intent intent=new Intent(Login.this, NoteParttion.class);
                                            intent.putExtra("email",email);//把当前登陆成功的账号的数据传递给下一个活动，用来显示是谁登陆成功了
                                            startActivity(intent);
                                        } else if (Password_local.getText().length() == 0) {
                                            String info = "请输入密码";
                                            Toast.makeText(Login.this, info, LENGTH_LONG).show();
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
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, FindPassword.class);
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

    public boolean isFind(String email) {
        final String acc[] = new String[pref.getInt("user_num", 0)];//定义一个账号数组，长度为选择记住密码的登录成功的账号个数
        boolean isFind = false;
        int l = email.length();
        for (int a = 0; a < pref.getInt("user_num", 0); a++) {
            acc[a] = pref.getString("email" + a, "");//初始化账号数组，把已保存的账号放到数组里面去
            if (email.equals(acc[a].substring(0, l))) {
                isFind = true;
            }
        }
        return isFind;
    }

    public int getPosition(String email) {
        final String acc[] = new String[pref.getInt("user_num", 0)];//定义一个账号数组，长度为选择记住密码的登录成功的账号个数
        int position = 0;
        int l = email.length();
        for (int a = 0; a < pref.getInt("user_num", 0); a++) {
            acc[a] = pref.getString("email" + a, "");//初始化账号数组，把已保存的账号放到数组里面去
            if (email.equals(acc[a].substring(0, l))) {
                position = a;
            }
        }
        return position;
    }

    public void showListPopulWindow() {//用来显示下拉框               
        final String acc[] = new String[pref.getInt("user_num", 0)];//定义一个账号数组，长度为选择记住密码的登录成功的账号个数               
        final String pas[] = new String[pref.getInt("user_num", 0)];//定义一个密码数组，长度为选择记住密码的登录成功的账号个数               
        for (int a = pref.getInt("user_num", 0) - 1; a >= 0; a--) {
            acc[a] = pref.getString("email" + a, "");//初始化账号数组，把已保存的账号放到数组里面去                   
            pas[a] = AESUtil.decrypt(pref.getString("password" + a, ""));//初始化密码数组，把已保存的密码放到数组里面去               
        }
        listPopupWindow = new ListPopupWindow(Login.this);
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
        Intent intent = new Intent(Login.this, NoteParttion.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setMessage(message);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public static void ActionStart(Context context) {
        Intent intent = new Intent(context, Login.class);
        context.startActivity(intent);
    }
}

