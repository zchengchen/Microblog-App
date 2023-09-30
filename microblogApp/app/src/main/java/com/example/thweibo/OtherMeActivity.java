package com.example.thweibo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OtherMeActivity extends AppCompatActivity {
    private static String TAG = "OtherMeActivity";
    private ImageView mAvatarImageView;
    private TextView mUsernameTextView;
    private Button mGobackButton;
    private Button mBanButton;
    private Button mDMButton;
    private String mAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherme);

        mAccount = getIntent().getStringExtra("username");
        mBanButton = (Button) findViewById(R.id.om_ban_button);
        mGobackButton = (Button) findViewById(R.id.goback_button);
        mUsernameTextView = (TextView) findViewById(R.id.other_microblogid);
        mUsernameTextView.setText("MicroBlog ID: " + mAccount);

        mGobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mDMButton = (Button) findViewById(R.id.om_DM_button);
        mDMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ServerConfigure.isInBlackList(mAccount)) {
                    Toast.makeText(OtherMeActivity.this, "User " + mAccount + " is in your blacklist.", Toast.LENGTH_SHORT);
                    return ;
                } else if(mAccount.equals(ServerConfigure.getAccount())) {
                    Intent intent = new Intent(OtherMeActivity.this, DMListActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(OtherMeActivity.this, DirectMailActivity.class);
                    intent.putExtra("toUsername", mAccount);
                    startActivity(intent);
                }
            }
        });

        if(mAccount.equals(ServerConfigure.getAccount())) {
            mBanButton.setText("Backlist");
            mBanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(OtherMeActivity.this, BlackListActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            if(ServerConfigure.isInBlackList(mAccount)) {
                mBanButton.setText("Unban");
                ServerConfigure.addToBlackListSet(mAccount);
            } else {
                mBanButton.setText("Ban");
            }
            mBanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mAccount.equals(ServerConfigure.getAccount())) {
                        Intent intent = new Intent(OtherMeActivity.this, BlackListActivity.class);
                        startActivity(intent);
                    } else{
                        if (ServerConfigure.isInBlackList(mAccount)) {
                            mBanButton.setText("Ban");
                            ServerConfigure.removeFromBlackListSet(mAccount);
                            ServerConfigure.updateBlacklist();
                            Toast.makeText(OtherMeActivity.this, "Unban " + mAccount + " successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            mBanButton.setText("Unban");
                            ServerConfigure.addToBlackListSet(mAccount);
                            ServerConfigure.updateBlacklist();
                            Toast.makeText(OtherMeActivity.this, "Ban " + mAccount + " successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        mAvatarImageView = (ImageView) findViewById(R.id.user_icon);
        mAvatarImageView.setImageBitmap(ServerConfigure.getOtherAvatar(mAccount));
    }
}
