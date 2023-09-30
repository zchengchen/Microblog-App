package com.example.thweibo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private ImageView mUploadIcon;
    private EditText mSetAccount;
    private EditText mSetPassword;
    private EditText mSetPassword2;
    private Button mRegisterBtn;
    private ImageView mImg;
    private Uri mImgUri;
    private Bitmap mAvatarBitmap;
    private static String TAG = "RegisterActivity";
    private static final int REQUEST_CODE_PICK_IMAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUploadIcon = (ImageView) findViewById(R.id.upload_icon);
        mSetAccount = (EditText) findViewById(R.id.set_account);
        mSetPassword = (EditText) findViewById(R.id.set_password);
        mSetPassword2 = (EditText) findViewById(R.id.set_password2);
        mRegisterBtn = (Button) findViewById(R.id.register);
        mImg = (ImageView) findViewById(R.id.upload_icon);

        mUploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Album not found.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String account = mSetAccount.getText().toString();
                final String password = mSetPassword.getText().toString();
                String password2 = mSetPassword2.getText().toString();
                if (password.equals("") || password2.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(password2)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int result = register(account, password, "");
                                    Looper.prepare();
                                    if (result == 1) {
                                        Toast.makeText(RegisterActivity.this, "Sign in successfully.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else if (result == 2) {
                                        Toast.makeText(RegisterActivity.this, "This account has been signed in.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Sign in failed.", Toast.LENGTH_SHORT).show();
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
                    } else {
                        Toast.makeText(RegisterActivity.this, "Inconsistent passwords.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri;

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(RegisterActivity.this, "onActivityResult() Failed: Register Activity", Toast.LENGTH_SHORT).show();
            mImg.setImageDrawable(getResources().getDrawable(R.drawable.set_icon));
        } else if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PICK_IMAGE: {
                    imageUri = data.getData();
                    Log.e(TAG,imageUri.toString());
                    mImgUri = imageUri;
                    Bitmap bm = null;
                    if (imageUri != null) {
                        try {
                            bm = ImageProcess.getBitmapFormUri(getApplicationContext(), imageUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Bundle bundleExtras = data.getExtras();
                        if (bundleExtras != null) {
                            bm = bundleExtras.getParcelable("data");
                        }
                    }
                    mImg.setImageBitmap(bm);
                    mAvatarBitmap = bm;
                    break;
                }
            }
        }
    }

    private int register(String username, String password, String iconPath) throws IOException, JSONException {
        String urlStr = ServerConfigure.getRegister();
        String params = "username=" + username + "&password=" + password + "&iconPath=" + iconPath;
        String requestMethod = "POST";
        String responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, requestMethod);
        JSONObject userInfo = new JSONObject(responseText);
        String nickName = userInfo.getString("username");
        String passwd = userInfo.getString("password");
        String icon = userInfo.getString("iconPath");
        int status = userInfo.getInt("status");
        if (status == 1) {
            setAccount(nickName);
            setPassword(passwd);
            setIconPath(icon);
        }

        UploadFile uploadFile = new UploadFile(this, mImgUri);
        params = "username=" + username;
        uploadFile.uploadFile(ServerConfigure.getUploadAvatar(), params);

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

    private void setIconPath(String iconPath) {
        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
        editor.putString("iconPath", iconPath);
        editor.commit();
    }
}
