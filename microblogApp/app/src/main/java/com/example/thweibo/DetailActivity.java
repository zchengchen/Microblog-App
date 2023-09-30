package com.example.thweibo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private static Vector<Bitmap> sBitmapVec;
    private static String[] sPicPathArray;
    private AlertDialog.Builder mBuilder;
    private MyBlog mBlog;
    private ImageView[] mPicArray;
    private List<Comment> mComments = new ArrayList<>();
    private ImageView mSenderAvatarImageView;
    private RecyclerView mRecyclerView;
    private TextView mDetailMblogID;
    private TextView mDetailTime;
    private TextView mDetailText;
    private Button mDetailDelete;
    private EditText mDetailCommentEditText;
    private Button mDetailCommentButton;
    private String mCommentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPicArray = new ImageView[3];
        mPicArray[0] = findViewById(R.id.content_pic1);
        mPicArray[1] = findViewById(R.id.content_pic2);
        mPicArray[2] = findViewById(R.id.content_pic3);

        mBlog = getIntent().getParcelableExtra("blog");
        sBitmapVec = new Vector<Bitmap>();
        getBlogPic(mBlog.getImgPath());

        mSenderAvatarImageView = (ImageView) findViewById(R.id.blog_sender_avatar);
        mSenderAvatarImageView.setImageBitmap(ServerConfigure.getOtherAvatar(mBlog.getUsername()));

        mSenderAvatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, OtherMeActivity.class);
                intent.putExtra("username", mBlog.getUsername());
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDetailMblogID = (TextView) findViewById(R.id.detail_nickname);
        mDetailTime = (TextView) findViewById(R.id.detail_time);
        mDetailText = (TextView) findViewById(R.id.detail_text);
        mDetailDelete = (Button) findViewById(R.id.detail_delete);
        mDetailCommentEditText = (EditText) findViewById(R.id.detail_comment);
        mDetailCommentButton = (Button) findViewById(R.id.detail_comment_btn);

        mDetailCommentButton.setVisibility(View.GONE);
        mDetailCommentEditText.setHint(getUsername() + " Share things around you!...");
        mDetailMblogID.setText(mBlog.getUsername());
        mDetailTime.setText(mBlog.getDatetime());
        mDetailText.setText(mBlog.getContent());
        mRecyclerView = (RecyclerView) findViewById(R.id.detail_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new CommentAdapter(mComments));

        if (getUsername().equals(mBlog.getUsername())) {
            mDetailDelete.setVisibility(View.VISIBLE);
        } else {
            mDetailDelete.setVisibility(View.GONE);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mComments.addAll(fetchComments());
            }
        }).start();
        mDetailDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBlog();
            }
        });
        mDetailCommentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mDetailCommentButton.setVisibility(View.VISIBLE);
                } else {
                    mDetailCommentButton.setVisibility(View.GONE);
                }
            }
        });
        mDetailCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDetailCommentEditText.getText().toString().equals("")) {
                    Toast.makeText(DetailActivity.this, "Content is null.", Toast.LENGTH_SHORT).show();
                } else {
                    mCommentText = mDetailCommentEditText.getText().toString();
                    new FetchCommentsTask().execute();
                }
            }
        });
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        private TextView mCommentMBlogID;
        private TextView mCommentContent;
        private TextView mCommentTime;
        private int mIndex;

        public CommentHolder(View itemView) {
            super(itemView);
            mCommentMBlogID = (TextView) itemView.findViewById(R.id.comment_username);
            mCommentContent = (TextView) itemView.findViewById(R.id.comment_text);
            mCommentTime = (TextView) itemView.findViewById(R.id.comment_time);
        }
    }

    public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {

        private List<Comment> commentList;
        private String mUsername;
        private ImageView mCommentAvatar;

        public CommentAdapter(List<Comment> comments) {
            commentList = comments;
        }

        @Override
        public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
            mCommentAvatar = view.findViewById(R.id.comment_item_avatar);

            CommentHolder holder = new CommentHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(CommentHolder holder, @SuppressLint("RecyclerView") int position) {
            Comment comment = commentList.get(position);
            holder.mIndex = position;
            holder.mCommentMBlogID.setText(comment.getUsername());
            holder.mCommentContent.setText(comment.getContent());
            holder.mCommentTime.setText(comment.getCommentTime());
            mCommentAvatar.setImageBitmap(ServerConfigure.getOtherAvatar(comment.getUsername()));
            mUsername = comment.getUsername();
            mCommentAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DetailActivity.this, OtherMeActivity.class);
                    intent.putExtra("username", mUsername);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }
    }

    private void setupAdapter() {
        mRecyclerView.setAdapter(new CommentAdapter(mComments));
    }

    private class FetchCommentsTask extends AsyncTask<Void, Boolean, List<Comment>> {

        @Override
        protected List<Comment> doInBackground(Void... voids) {
            publishProgress(sendComment(mCommentText));
            return fetchComments();
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            if (!values[0]) {
                Toast.makeText(DetailActivity.this, "Unknown fail.", Toast.LENGTH_SHORT).show();
            } else {
                mDetailCommentEditText.setText("");
            }
        }

        @Override
        protected void onPostExecute(List<Comment> commentList) {
            mComments.clear();
            mComments.addAll(commentList);
            setupAdapter();
        }
    }

    private List<Comment> fetchComments() {
        String urlStr = ServerConfigure.getFetchComment();
        String params = "blog_id=" + mBlog.getWeiboId();
        String requestMethod = "POST";
        List<Comment> commentList = new ArrayList<>();
        try {
            String responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, requestMethod);
            JSONObject object = new JSONObject(responseText);
            JSONArray array = object.getJSONArray("comments");
            for (int i = 0; i < array.length(); i++) {
                JSONObject commentObject = array.getJSONObject(i);
                Comment comment = new Comment();
                comment.setCommentId(commentObject.getInt("comment_id"));
                comment.setBlogId(commentObject.getInt("blog_id"));
                String username = commentObject.getString("username");
                if(ServerConfigure.isInBlackList(username)) {
                    continue ;
                }
                comment.setUsername(username);
                comment.setContent(commentObject.getString("comment_content"));
                comment.setCommentTime(commentObject.getString("comment_time"));
                commentList.add(comment);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException jse) {
            jse.printStackTrace();
        } finally {
            return commentList;
        }
    }

    public void deleteBlog() {
        mBuilder = new AlertDialog.Builder(this)
                .setTitle("Delete this blog?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                if (delete()) {
                                    Toast.makeText(DetailActivity.this, "Success.", Toast.LENGTH_LONG).show();
                                    try {
                                        Thread.sleep(1000);
                                        finish();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(DetailActivity.this, "Unknown fail.", Toast.LENGTH_LONG).show();
                                }
                                Looper.loop();
                            }
                        }).start();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        mBuilder.create().show();
    }

    private boolean delete() {
        String urlStr = ServerConfigure.getDeleteBlog();
        String params = "username=" + mBlog.getUsername() + "&id=" + mBlog.getWeiboId();
        String requestMethod = "POST";
        String responseText;
        try {
            responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, requestMethod);
            Log.i("Delete", "delete: " + responseText);
            JSONObject object = new JSONObject(responseText);
            if (object.getInt("status") == 1) {
                return true;
            } else {
                return false;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } catch (JSONException jse) {
            jse.printStackTrace();
            return false;
        }
    }

    private boolean sendComment(String comment) {
        String urlStr = ServerConfigure.getSendComment();
        String params = "blog_id=" + mBlog.getWeiboId() + "&username=" + getUsername() + "&comment=" + comment;
        String requestMethod = "POST";
        try {
            String responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, requestMethod);
            Log.i("sendComment", "sendComment: " + responseText);
            JSONObject object = new JSONObject(responseText);
            if (object.getInt("status") == 1) {
                return true;
            } else {
                return false;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } catch (JSONException jse) {
            jse.printStackTrace();
            return false;
        }
    }

    private String getUsername() {
        SharedPreferences pre = getSharedPreferences("userInfo", MODE_PRIVATE);
        return pre.getString("username", "");
    }

    private void getBlogPic(String picPath) {
        if(picPath == "" || picPath == null) {
            return ;
        }
        Log.e(TAG, picPath);
        sPicPathArray = null;
        sPicPathArray = picPath.split("#");
        Log.e(TAG, sPicPathArray.toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                sBitmapVec = new Vector<Bitmap>();
                for(int i = 0; i < sPicPathArray.length; ++i) {
                    try {
                        String iPath = ServerConfigure.getDownloadPic(sPicPathArray[i]);
                        Log.e(TAG,iPath);
                        URL url = new URL(iPath);
                        InputStream inputStream = url.openStream();
                        Bitmap bm = BitmapFactory.decodeStream(inputStream);
                        sBitmapVec.add(bm);
                        mPicArray[i].setImageBitmap(bm);
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
