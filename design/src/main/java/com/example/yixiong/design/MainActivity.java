package com.example.yixiong.design;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private EditText account,password;
    private Button login;
    private String acc,psd;

    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        account = (EditText) findViewById(R.id.text1);
        password = (EditText) findViewById(R.id.text2);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acc = account.getText().toString();
                psd = password.getText().toString();
                if (acc.equals("") || psd.equals("")) {
                    Toast.makeText(MainActivity.this, "请输入账号或密码", Toast.LENGTH_LONG).show();
                } else if (acc.equals("123") && psd.equals("123")) {
                    Intent intent = new Intent(MainActivity.this, second.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "账号或密码错误", Toast.LENGTH_LONG).show();
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("登陆");
        setSupportActionBar(toolbar);


    }





}
