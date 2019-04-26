package com.henu.jianyunnote.Index;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.henu.jianyunnote.Beans.Users;
import com.henu.jianyunnote.Parttion.NoteParttion;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.Util.AESUtil;

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
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this, "bc95d28fa2c059530870d4dbb550b38f");//初始化Bmob  后面是服务器端应用ID
        Button login = findViewById(R.id.login);
        final EditText Email_local =findViewById(R.id.email);
        final EditText Password_local =findViewById(R.id.password);
        final TextView forgotpassword=findViewById(R.id.forgot_password);
        Button  register = findViewById(R.id.register);
        remember_password=findViewById(R.id.remember_password);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_password",false);
        if(isRemember){
            // 将账号和密码都设置到文本框中
            String d_email = pref.getString("email","");
            String email= AESUtil.decrypt(d_email);
            //对读取到的密码进行解密
            String d_password=pref.getString("password", "");
            String password=AESUtil.decrypt(d_password);
            Email_local.setText(email);
            Password_local.setText(password);
            remember_password.setChecked(true);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmail(Email_local.getText().toString())==false)
                {
                    String info = "请输入正确的邮箱";
                    Toast.makeText(Login.this, info, LENGTH_LONG).show();
                }
                else {
                    BmobQuery<Users> query = new BmobQuery<Users>();
                    //查询playerName叫“比目”的数据
                    query.addWhereEqualTo("Email", Email_local.getText());
                    //返回50条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(1);
                    //执行查询方法
                    query.findObjects(new FindListener<Users>() {
                        @Override
                        public void done(List<Users> object, BmobException e) {
                            if(object.size()==1)
                            {
                                if (e == null) {
                                    for (Users u : object)
                                        if (u.getPassword().equals(Password_local.getText().toString())) {
                                            String info = "登录成功";
                                            Toast.makeText(Login.this, info, LENGTH_LONG).show();
                                            editor=pref.edit();
                                            if(remember_password.isChecked()){
                                                editor.putBoolean("remember_password",true);
                                                String d_email= AESUtil.encrypt(Email_local.getText().toString());
                                                editor.putString("email",d_email);
                                                String d_password=AESUtil.encrypt(Password_local.getText().toString());
                                                editor.putString("password",d_password);
                                            }else{
                                                editor.clear();
                                            }
                                            editor.apply();
                                            Intent intent = new Intent(Login.this,NoteParttion.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else if(Password_local.getText().length()==0)
                                        {
                                            String info = "请输入密码";
                                            Toast.makeText(Login.this, info, LENGTH_LONG).show();
                                        }
                                        else {

                                            MyAlertDialog("邮箱或密码错误","确定");
                                        }
                                }
                                else
                                {
                                    String Error="异常"+e.getErrorCode();
                                    MyAlertDialog(Error,"确定");
                                }
                            }
                            else
                            {
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
    }

    public static boolean isEmail(String email){   //判断邮箱是否合法
        if (null==email || "".equals(email)) return false;
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
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
}

