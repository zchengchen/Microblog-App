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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<MyBlog> blogs = new ArrayList<>();
    private final static String TAG = "HomeFragment";
    private View mRoot;
    private RecyclerView mRecyclerView;

    @Override
    public void onStart() {
        super.onStart();
        new FetchBlogsTask().execute();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(mRoot == null) {
            mRoot = inflater.inflate(R.layout.fragment_home, container, false);
            Toolbar toolbar = (Toolbar) mRoot.findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.add) {
                        startActivity(new Intent(getActivity(), AddBlogActivity.class));
                    }
                    return true;
                }
            });

            mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(new BlogAdapter(blogs));
        }
        ViewGroup parent = (ViewGroup) mRoot.getParent();
        if (parent != null) {
            parent.removeView(mRoot);
        }
        return mRoot;
    }

    public class BlogHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int id;
        private TextView user_nickname;
        private TextView pub_time;
        private TextView blog_text;
        private ImageView mAvatarImageView;

        public BlogHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            user_nickname = (TextView) view.findViewById(R.id.user_nickname);
            pub_time = (TextView) view.findViewById(R.id.pub_time);
            blog_text = (TextView) view.findViewById(R.id.blog_text);
            mAvatarImageView = (ImageView) view.findViewById(R.id.small_avatar);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("blog", blogs.get(id));
            startActivity(intent);
        }
    }

    public class BlogAdapter extends RecyclerView.Adapter<BlogHolder> {
        private List<MyBlog> myBlogs;

        public BlogAdapter (List<MyBlog> blogs) {
            myBlogs = blogs;
        }

        @Override
        public BlogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myblog_item, parent, false);
            BlogHolder holder = new BlogHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(BlogHolder holder, @SuppressLint("RecyclerView") int position) {
            MyBlog blog = myBlogs.get(position);
            holder.id = position;
            holder.user_nickname.setText(blog.getUsername());
            holder.pub_time.setText(blog.getDatetime());
            holder.blog_text.setText(blog.getContent());
            if(blog.getUsername().equals(ServerConfigure.getAccount())) {
                holder.mAvatarImageView.setImageBitmap(ServerConfigure.getMyAvatar());
            } else {
                holder.mAvatarImageView.setImageBitmap(ServerConfigure.getOtherAvatar(blog.getUsername()));
            }
        }

        @Override
        public int getItemCount() {
            return myBlogs.size();
        }
    }

    private void setupAdapter() {
        if (isAdded()) {
            mRecyclerView.setAdapter(new BlogAdapter(blogs));
        }
    }

    private class FetchBlogsTask extends AsyncTask<Void, Void, List<MyBlog>> {

        @Override
        protected List<MyBlog> doInBackground(Void... voids) {
            return fetchblogs();
        }

        @Override
        protected void onPostExecute(List<MyBlog> myblogs) {
            blogs = myblogs;
            setupAdapter();
        }
    }

    private List<MyBlog> fetchblogs() {
        List<MyBlog> blogs = new ArrayList<>();
        String urlStr = ServerConfigure.getFetchBlog();
        String params = "";
        String requestMethod = "POST";
        String responseText;
        try {
            responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, requestMethod);
            JSONObject object = new JSONObject(responseText);
            JSONArray array = object.getJSONArray("blogs");
            for (int i = 0; i < array.length(); i++) {
                JSONObject blogObject = array.getJSONObject(i);
                MyBlog blog = new MyBlog();
                blog.setWeiboId(blogObject.getInt("id"));
                String username = blogObject.getString("username");
                if(ServerConfigure.isInBlackList(username)) {
                    continue ;
                }
                blog.setUsername(username);
                blog.setContent(blogObject.getString("content"));
                blog.setDatetime(blogObject.getString("pubtime"));
                Log.e(TAG, blogObject.getString("imgpath"));
                blog.setImgPath(blogObject.getString("imgpath"));
                blogs.add(blog);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException jse) {
            jse.printStackTrace();
        } finally {
            return blogs;
        }
    }
}
