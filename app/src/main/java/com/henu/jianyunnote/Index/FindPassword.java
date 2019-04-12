package com.henu.jianyunnote.Index;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.henu.jianyunnote.Beans.Users;
import com.henu.jianyunnote.Parttion.NoteParttion;
import com.henu.jianyunnote.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.widget.Toast.LENGTH_LONG;

public class FindPassword extends AppCompatActivity {

    public static boolean isEmail(String email){   //判断邮箱是否合法
        if (null==email || "".equals(email)) return false;
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public void MyAlertDialog(String message, String button) {  //AlertDialog 两个参数
        AlertDialog.Builder builder = new AlertDialog.Builder(FindPassword.this);
        builder.setMessage(message);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);
        Bmob.initialize(this, "bc95d28fa2c059530870d4dbb550b38f");
        final EditText email = findViewById(R.id.email);
        final EditText safepassword = findViewById(R.id.safepassword);
        final EditText newpassword = findViewById(R.id.newpassword);
        final EditText renewpassword = findViewById(R.id.renewpassword);
        Button findpassword = findViewById(R.id.findpassword);
        Button Return = findViewById(R.id.Return);

        findpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().length()==0)
                {
                    MyAlertDialog("请输入邮箱","确定");
                }
                else if(isEmail(email.getText().toString())==false)
                {
                    MyAlertDialog("请输入正确的邮箱","确定");
                }
                else
                {
                    BmobQuery<Users> query = new BmobQuery<Users>();
                    //查询Email为“***”的数据
                    query.addWhereEqualTo("Email", email.getText());
                    //返回1条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(1);
                    query.findObjects(new FindListener<Users>() {
                        @Override
                        public void done(List<Users> object, BmobException e) {
                            if (object.size() == 1) {
                                for (Users u : object) {
                                    if ((u.getSafePassword()).equals(safepassword.getText().toString())) {
                                        if(newpassword.getText().length() != 0 && newpassword.getText().toString().equals(renewpassword.getText().toString()))
                                        {
                                            String Id = u.getObjectId();
                                            Users user = new Users();
                                            user.setPassword(newpassword.getText().toString());
                                            user.update(Id, new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if (e == null) {
                                                        MyAlertDialog("修改成功，请返回登录", "确定");
                                                    }
                                                }
                                            });
                                        }
                                        else if(newpassword.getText().length() == 0)
                                        {
                                            MyAlertDialog("请输入新密码", "确定");
                                        }
                                        else if(renewpassword.getText().length() == 0)
                                        {
                                            MyAlertDialog("请确认新密码", "确定");
                                        }
                                    } else if (safepassword.getText().length() == 0)
                                    {
                                        MyAlertDialog("请输入密保密码", "确定");
                                    }
                                    else
                                    {
                                        MyAlertDialog("密保密码错误，请重试", "确定");
                                    }
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

        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindPassword.this, Index.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
