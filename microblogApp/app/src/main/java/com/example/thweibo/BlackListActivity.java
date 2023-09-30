package com.example.thweibo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BlackListActivity extends AppCompatActivity {
    private static String TAG = "BlackListActivity";
    private RecyclerView mRecyclerView;
    private List<String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        users = ServerConfigure.getBlackListArray();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_bl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(BlackListActivity.this));
        mRecyclerView.setAdapter(new BlackListActivity.BlackUserAdapter(users));
    }

    public class BlackUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int mIndex;
        private TextView mUsernameTextView;
        private ImageView mAvatarImageView;

        public BlackUserHolder(View view) {
            super(view);
            mUsernameTextView = (TextView) view.findViewById(R.id.bl_item_name);
            mAvatarImageView = (ImageView) view.findViewById(R.id.small_avatar_bl);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(BlackListActivity.this, OtherMeActivity.class);
            intent.putExtra("username", users.get(mIndex));
            Log.e(TAG, users.get(mIndex));
            startActivity(intent);
        }
    }


    public class BlackUserAdapter extends RecyclerView.Adapter<BlackListActivity.BlackUserHolder> {
        private List<String> mUsers;

        public BlackUserAdapter(List<String> users) {
            mUsers = users;
        }

        @Override
        public BlackListActivity.BlackUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blacklist_item, parent, false);
            BlackListActivity.BlackUserHolder holder = new BlackListActivity.BlackUserHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(BlackUserHolder holder, @SuppressLint("RecyclerView") final int position) {
            String username = mUsers.get(position);
            holder.mIndex = position;
            holder.mUsernameTextView.setText(mUsers.get(position));
            holder.mAvatarImageView.setImageBitmap(ServerConfigure.getOtherAvatar(username));
            holder.mAvatarImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BlackListActivity.this, OtherMeActivity.class);
                    intent.putExtra("username", users.get(position));
                    startActivity(intent);
                }
            });
            holder.mUsernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BlackListActivity.this, OtherMeActivity.class);
                    intent.putExtra("username", users.get(position));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }
    }
}
