package com.example.thweibo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DMListActivity extends AppCompatActivity {
    private List<MyDM> mDMs = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private final static String TAG = "DMListActivity";

    @Override
    public void onStart() {
        super.onStart();
        new DMListActivity.FetchDMTask().execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dmlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_dmlist);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.add) {
                    Intent intent = new Intent(DMListActivity.this, DirectMailActivity.class);
                    intent.putExtra("toUsername", "");
                    startActivity(intent);
                }
                return true;
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_dmlist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DMListActivity.this));
        mRecyclerView.setAdapter(new DMListActivity.DMAdapter(mDMs));
    }

    public class DMHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int mIndex;
        private ImageView mAvatarImageView;
        private TextView mUsernameTextView;
        private TextView mTitleTextView;

        public DMHolder(View view) {
            super(view);
            mUsernameTextView = (TextView) view.findViewById(R.id.dmlist_item_username);
            mTitleTextView = (TextView) view.findViewById(R.id.dmlist_item_title);
            mAvatarImageView = (ImageView) view.findViewById(R.id.dmlist_item_avatar);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(DMListActivity.this, ReadDMActivity.class);
            intent.putExtra("DM", mDMs.get(mIndex));
            startActivity(intent);
        }
    }

    public class DMAdapter extends RecyclerView.Adapter<DMListActivity.DMHolder> {
        private List<MyDM> mdms;

        public DMAdapter(List<MyDM> dm) {
            mdms = dm;
        }

        @Override
        public DMListActivity.DMHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dm_item, parent, false);
            DMListActivity.DMHolder holder = new DMListActivity.DMHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(DMListActivity.DMHolder holder, @SuppressLint("RecyclerView") final int position) {
            MyDM dm = mdms.get(position);
            holder.mIndex = position;
            holder.mUsernameTextView.setText(dm.getFromUsername());
            holder.mAvatarImageView.setImageBitmap(ServerConfigure.getOtherAvatar(dm.getFromUsername()));
            holder.mTitleTextView.setText(dm.getTitle());

            holder.mAvatarImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DMListActivity.this, ReadDMActivity.class);
                    intent.putExtra("DM", mDMs.get(position));
                    startActivity(intent);
                }
            });

            holder.mUsernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DMListActivity.this, ReadDMActivity.class);
                    intent.putExtra("DM", mDMs.get(position));
                    startActivity(intent);
                }
            });

            holder.mTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DMListActivity.this, ReadDMActivity.class);
                    intent.putExtra("DM", mDMs.get(position));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mdms.size();
        }
    }

    private void setupAdapter() {
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(new DMListActivity.DMAdapter(mDMs));
        }
    }

    private class FetchDMTask extends AsyncTask<Void, Void, List<MyDM>> {

        @Override
        protected List<MyDM> doInBackground(Void... voids) {
            return fetchDMs();
        }

        @Override
        protected void onPostExecute(List<MyDM> mydms) {
            mDMs = mydms;
            setupAdapter();
        }
    }

    private List<MyDM> fetchDMs() {
        List<MyDM> dms = new ArrayList<>();
        String urlStr = ServerConfigure.getFetchDM();
        String params = "username=" + ServerConfigure.getAccount();
        String requestMethod = "POST";
        String responseText;
        try {
            responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, requestMethod);
            JSONObject object = new JSONObject(responseText);
            JSONArray array = object.getJSONArray("dms");
            for (int i = 0; i < array.length(); i++) {
                JSONObject dmObject = array.getJSONObject(i);
                MyDM dm = new MyDM();
                dm.setFromUsername(dmObject.getString("userfrom"));
                dm.setTitle(dmObject.getString("title"));
                String username = dmObject.getString("userfrom");
                if(ServerConfigure.isInBlackList(username)) {
                    continue ;
                }
                dm.setContent(dmObject.getString("content"));
                dm.setId(dmObject.getInt("id"));
                dms.add(dm);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException jse) {
            jse.printStackTrace();
        } finally {
            return dms;
        }
    }
}
