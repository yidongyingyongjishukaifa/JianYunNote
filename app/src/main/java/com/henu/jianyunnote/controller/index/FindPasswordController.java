package com.henu.jianyunnote.controller.index;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.henu.jianyunnote.Model.Bmob.Users_Bmob;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.util.AtyUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class FindPasswordController extends AppCompatActivity {

    public static boolean isEmail(String email){   //判断邮箱是否合法
        if (null==email || "".equals(email)) return false;
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public void MyAlertDialog(String message, String button) {  //AlertDialog 两个参数
        AlertDialog.Builder builder = new AlertDialog.Builder(FindPasswordController.this);
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
        AtyUtil.getInstance().addActivity(this);
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
                    BmobQuery<Users_Bmob> query = new BmobQuery<Users_Bmob>();
                    //查询Email为“***”的数据
                    query.addWhereEqualTo("Email", email.getText());
                    //返回1条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(1);
                    query.findObjects(new FindListener<Users_Bmob>() {
                        @Override
                        public void done(List<Users_Bmob> object, BmobException e) {
                            if (object.size() == 1) {
                                for (Users_Bmob u : object) {
                                    if ((u.getSafePassword()).equals(safepassword.getText().toString())) {
                                        if(newpassword.getText().length() != 0 && newpassword.getText().toString().equals(renewpassword.getText().toString()))
                                        {
                                            String Id = u.getObjectId();
                                            Users_Bmob user = new Users_Bmob();
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
                Intent intent = new Intent(FindPasswordController.this, LoginController.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
