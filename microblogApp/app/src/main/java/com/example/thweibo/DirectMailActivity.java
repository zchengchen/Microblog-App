package com.example.thweibo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DirectMailActivity extends AppCompatActivity {
    private EditText mUsernameEditText;
    private EditText mContentEditText;
    private EditText mTitleEditText;
    private String mToUsername;
    private String TAG = "DirectMailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newdm);

        mToUsername = getIntent().getStringExtra("toUsername");
        if(mToUsername == null) {
            mToUsername = "";
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.dm_toolbar);
        toolbar.inflateMenu(R.menu.menu_send);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.send) {
                    String to_username = mUsernameEditText.getText().toString();
                    String content = mContentEditText.getText().toString();
                    if(to_username.equals("")) {
                        Toast.makeText(DirectMailActivity.this, "To is empty.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if(content.equals("") || content.equals("Say what you want.")) {
                        Toast.makeText(DirectMailActivity.this, "Content is empty.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if(!ServerConfigure.isUsernameValid(to_username)) {
                        Toast.makeText(DirectMailActivity.this, "User is not existed.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if(mTitleEditText.getText().toString().equals("")) {
                        Toast.makeText(DirectMailActivity.this, "Title is empty.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if(ServerConfigure.getAccount().equals(to_username)) {
                        Toast.makeText(DirectMailActivity.this, "Do not send DM to yourself.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if(ServerConfigure.isInBlackList(to_username)) {
                        Toast.makeText(DirectMailActivity.this, "To is in your blacklist.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String to = mUsernameEditText.getText().toString();
                                String from = ServerConfigure.getAccount();
                                String cont = mContentEditText.getText().toString();
                                String title = mTitleEditText.getText().toString();
                                String urlStr = ServerConfigure.getSendDM();
                                String params = "from=" + from + "&to=" + to + "&content=" + cont + "&title=" + title;
                                new MyHttpRequest().sendHttpRequest(urlStr, params, "POST");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    Toast.makeText(DirectMailActivity.this, "Send DM successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return true;
            }
        });

        mUsernameEditText = (EditText) findViewById(R.id.new_dm_to_content);
        mUsernameEditText.setText(mToUsername);
        mContentEditText = (EditText) findViewById(R.id.dm_content);
        mTitleEditText = (EditText) findViewById(R.id.dm_title);
    }
}
