package com.example.thweibo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ReadDMActivity extends AppCompatActivity {
    private MyDM mDM;
    private ImageView mAvatarImageView;
    private TextView mFromUserTextView;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readdm);

        mDM = getIntent().getParcelableExtra("DM");

        mToolbar = findViewById(R.id.readdm_toolbar);
        mToolbar.inflateMenu(R.menu.menu_dm);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.dm_back) {
                    onBackPressed();
                } else if (item.getItemId() == R.id.dm_delete) {
                    deleteDM();
                    Toast.makeText(ReadDMActivity.this, "Delete DM successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return true;
            }
        });
        mAvatarImageView = (ImageView) findViewById(R.id.readdm_avatar);
        mAvatarImageView.setImageBitmap(ServerConfigure.getOtherAvatar(mDM.getFromUsername()));
        mAvatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadDMActivity.this, OtherMeActivity.class);
                intent.putExtra("username", mDM.getFromUsername());
                startActivity(intent);
            }
        });

        mFromUserTextView = (TextView) findViewById(R.id.readdm_from);
        mFromUserTextView.setText(mDM.getFromUsername());

        mTitleTextView = (TextView) findViewById(R.id.readdm_title);
        mTitleTextView.setText(mDM.getTitle());

        mContentTextView = (TextView) findViewById(R.id.readdm_content);
        mContentTextView.setText(mDM.getContent());
    }

    private void deleteDM() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = ServerConfigure.getDeleteDM();
                    String params = "dm_id=" + String.valueOf(mDM.getId());
                    new MyHttpRequest().sendHttpRequest(urlStr, params, "POST");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
