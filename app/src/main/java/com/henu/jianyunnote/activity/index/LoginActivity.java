package com.henu.jianyunnote.activity.index;

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

import com.henu.jianyunnote.dao.LitePal.INoteBookDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteBookDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.model.LitePal.User_LitePal;
import com.henu.jianyunnote.model.Bmob.Users_Bmob;
import com.henu.jianyunnote.activity.noteParttion.NoteParttionActivity;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.util.AESUtil;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.Const;
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

public class LoginActivity extends AppCompatActivity {
    public static User_LitePal login_user;
    private CheckBox remember_password;
    private CheckBox auto_login;
    private ListPopupWindow listPopupWindow;
    private EditText Email_local;
    private EditText Password_local;
    private String email;
    private String password;
    private boolean is_Remember;
    private boolean autoLogin;
    public static boolean is_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AtyUtil.getInstance().addActivity(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bmob.initialize(this, "bc95d28fa2c059530870d4dbb550b38f");//初始化Bmob  后面是服务器端应用ID
        Button login = findViewById(R.id.login);
        Email_local = findViewById(R.id.email);
        Password_local = findViewById(R.id.password);
        final TextView forgotpassword = findViewById(R.id.forgot_password);
        Button register = findViewById(R.id.register);
        remember_password = findViewById(R.id.remember_password);
        auto_login = findViewById(R.id.auto_login);
        List<User_LitePal> userList = LitePal.where("isRemember = ?", Const.ISREMEMBER).order("loginTime desc").limit(1).find(User_LitePal.class);
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
                    Toast.makeText(LoginActivity.this, info, LENGTH_LONG).show();
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
                            if (e == null) {
                                if (object != null) {
                                    if (object.size() == 1) {
                                        for (Users_Bmob u : object)
                                            if (u.getPassword().equals(MD5Util.Encode(password))) {
//                                            String info = "登录成功";
//                                            Toast.makeText(LoginActivity.this, info, LENGTH_LONG).show();
                                                is_login = true;
                                                is_Remember = remember_password.isChecked();
                                                autoLogin = auto_login.isChecked();
                                                initUser(u);
                                                gotoNote();
                                            } else if (Password_local.getText().length() == 0) {
                                                String info = "请输入密码";
                                                Toast.makeText(LoginActivity.this, info, LENGTH_LONG).show();
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
                        }
                    });
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return true;
        }
    }

    private void initUser(final Users_Bmob users_bmob) {
        List<User_LitePal> user = LitePal.where("username= ?", email).find(User_LitePal.class);
        String d_password = AESUtil.encrypt(password);
        if (user == null || user.size() == 0) {
            final User_LitePal user_litePal = new User_LitePal();
            user_litePal.setUsername(email);
            user_litePal.setPassword(d_password);
            user_litePal.setBmob_user_id(users_bmob.getObjectId());
            user_litePal.setIsLogin(Integer.parseInt(Const.ISLOGIN));
            user_litePal.setLoginTime(new Date());
            if (is_Remember) {
                user_litePal.setIsRemember(Integer.parseInt(Const.ISREMEMBER));
            } else {
                user_litePal.setIsRemember(Integer.parseInt(Const.NOTREMEMBER));
            }
            user_litePal.setAutoLogin(autoLogin);
            user_litePal.save();
            login_user = user_litePal;
        } else {
            for (User_LitePal user_litePal : user) {
                user_litePal.setPassword(d_password);
                user_litePal.setIsLogin(Integer.parseInt(Const.ISLOGIN));
                if (is_Remember) {
                    user_litePal.setIsRemember(Integer.parseInt(Const.ISREMEMBER));
                } else {
                    user_litePal.setIsRemember(Integer.parseInt(Const.NOTREMEMBER));
                }
                user_litePal.setAutoLogin(autoLogin);
                user_litePal.setLoginTime(new Date());
                user_litePal.save();
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
        listPopupWindow = new ListPopupWindow(LoginActivity.this);
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
        Intent intent = new Intent(LoginActivity.this, NoteParttionActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public static void ActionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}

