package com.example.thweibo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText mAccount;
    private EditText mPassword;
    private CheckBox mSavePassword;
    private TextView mGoRegister;
    private Button mLoginButton;
    private CustomVideoView mVideoView;
    private ArrayList<String> mAllID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAllID = new ArrayList<String>();
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initResource();
            }
        }).start();
        mAccount = (EditText) findViewById(R.id.account);
        mPassword = (EditText) findViewById(R.id.password);
        mSavePassword = (CheckBox) findViewById(R.id.remember_pass);
        mGoRegister = (TextView) findViewById(R.id.go_register);
        mLoginButton = (Button) findViewById(R.id.login);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAccount(mAccount.getText().toString());
                ServerConfigure.setAccount(mAccount.getText().toString());
                setSavePassword(mSavePassword.isChecked());
                if (mSavePassword.isChecked()) {
                    setPassword(mPassword.getText().toString());
                } else {
                    setPassword("");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int result = login(mAccount.getText().toString(), mPassword.getText().toString());
                            initBlackList();
                            Looper.prepare();
                            if (result == 1) {
                                ServerConfigure.setAccount(mAccount.getText().toString());
                                Toast.makeText(LoginActivity.this, "Sign in successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                new Thread() {
                                    public void run() {
                                        //下载图片的路径
                                        String iPath = ServerConfigure.getDownloadAvatar() + ServerConfigure.getAccount() + ".png";
                                        try {
                                            URL url = new URL(iPath);
                                            InputStream inputStream = url.openStream();
                                            ServerConfigure.setMyAvatar(BitmapFactory.decodeStream(inputStream));
                                            inputStream.close();
                                        } catch (Exception e) {
                                            Log.e(TAG, e.getMessage());
                                        }
                                    }
                                }.start();
                                finish();
                            } else if (result == 0) {
                                Toast.makeText(LoginActivity.this, "Account not found.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Password or account is wrong.", Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        } catch (JSONException jse) {
                            jse.printStackTrace();
                        } finally {

                        }
                    }
                }).start();
            }
        });
        mGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void initResource() {
        String urlStr = ServerConfigure.getAllAvatar();
        String params = "";
        String requestMethod = "POST";
        String responseText;
        try {
            Log.e(TAG, urlStr);
            responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, requestMethod);
            JSONObject object = new JSONObject(responseText);
            JSONArray array = object.getJSONArray("user_array");
            for (int i = 0; i < array.length(); i++) {
                JSONObject avatarObject = array.getJSONObject(i);
                String mbID = avatarObject.getString("username");
                mAllID.add(mbID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < mAllID.size(); ++i) {
            try {
                String iPath = ServerConfigure.getAvatarURL(mAllID.get(i));
                URL url = new URL(iPath);
                InputStream inputStream = url.openStream();
                ServerConfigure.addOtherAvatar(mAllID.get(i), BitmapFactory.decodeStream(inputStream));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void initView() {
        mVideoView = (CustomVideoView) findViewById(R.id.videoview);
        mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
        mVideoView.start();
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVideoView.start();
            }
        });
    }

    @Override
    protected void onRestart() {
        initView();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        mVideoView.stopPlayback();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAccount.setText(getAccount());
        mPassword.setText(getPassword());
        mSavePassword.setChecked(getSavePassword());
    }

    private int login(String username, String password) throws IOException, JSONException {
        String urlStr = ServerConfigure.getLogin();
        String params = "username=" + username + "&password=" + password;
        String requestMethod = "POST";
        String responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, requestMethod);
        JSONObject userInfo = new JSONObject(responseText);
        return userInfo.getInt("status");
    }

    private void setAccount(String account) {
        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
        editor.putString("username", account);
        editor.commit();
    }

    private void setPassword(String password) {
        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
        editor.putString("password", password);
        editor.commit();
    }

    private void setSavePassword(boolean savePassword) {
        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
        editor.putBoolean("savePassword", savePassword);
        editor.commit();
    }

    private String getAccount() {
        SharedPreferences pre = getSharedPreferences("userInfo", MODE_PRIVATE);
        return pre.getString("username", "");
    }

    private String getPassword() {
        SharedPreferences pre = getSharedPreferences("userInfo", MODE_PRIVATE);
        return pre.getString("password", "");
    }

    private boolean getSavePassword() {
        SharedPreferences pre = getSharedPreferences("userInfo", MODE_PRIVATE);
        return pre.getBoolean("savePassword", false);
    }

    private void initBlackList() {
        try {
            String urlStr = ServerConfigure.getBlackList();
            String params = "username=" + getAccount();
            String responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, "POST");
            JSONObject object = new JSONObject(responseText);
            JSONObject blacklist = object.getJSONObject("userbl");
            String bl = blacklist.getString("blacklist");
            String[] bl_list = bl.split("#");
            for(int i = 0; i < bl_list.length; ++i) {
                ServerConfigure.addToBlackListSet(bl_list[i]);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
