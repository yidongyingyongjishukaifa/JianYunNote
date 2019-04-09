package com.henu.jianyunnote.Index;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.henu.jianyunnote.Parttion.NoteParttion;
import com.henu.jianyunnote.R;

public class Index extends AppCompatActivity {
    private Button login;
    private Button register;
    final String user = "111";
    final String pass = "111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        login = findViewById(R.id.login);
        login.setOnClickListener(loginListener);
        register = findViewById(R.id.register);
        register.setOnClickListener(registerListener);
    }

    Button.OnClickListener loginListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = "";
            EditText editText1 = findViewById(R.id.email);
            username = editText1.getText().toString();
            String password = "";
            EditText editText2 = findViewById(R.id.password);
            password = editText2.getText().toString();
            if (username.equals(user) && password.equals(pass)) {
                Intent intent = new Intent(Index.this, NoteParttion.class);
                startActivity(intent);
            } else {
                new AlertDialog.Builder(Index.this).setTitle("Error!").setMessage("Wrong username or password.")
                        .setNegativeButton("OK", null)
                        .show();
            }
        }
    };

    Button.OnClickListener registerListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Index.this, Register.class);
            startActivity(intent);
        }
    };
}

