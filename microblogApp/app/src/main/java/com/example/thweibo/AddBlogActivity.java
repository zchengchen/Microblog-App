package com.example.thweibo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AddBlogActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE_1 = 1;
    private static final int REQUEST_CODE_PICK_IMAGE_2 = 2;
    private static final int REQUEST_CODE_PICK_IMAGE_3 = 3;
    private static final String TAG = "AddBlogActivity";
    private EditText mAddBlogTextView;
    private ImageButton mUploadPic1;
    private ImageButton mUploadPic2;
    private ImageButton mUploadPic3;
    private Bitmap mPic1Bitmap;
    private Bitmap mPic2Bitmap;
    private Bitmap mPic3Bitmap;
    private String mPic1Id;
    private String mPic2Id;
    private String mPic3Id;
    private Uri mPic1Uri;
    private Uri mPic2Uri;
    private Uri mPic3Uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);

        mUploadPic1 = (ImageButton) findViewById(R.id.upload_pic1);
        mUploadPic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE_1);
                } else {
                    Toast.makeText(AddBlogActivity.this, "Album not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mUploadPic2 = (ImageButton) findViewById(R.id.upload_pic2);
        mUploadPic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE_2);
                } else {
                    Toast.makeText(AddBlogActivity.this, "Album not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mUploadPic3 = (ImageButton) findViewById(R.id.upload_pic3);
        mUploadPic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE_3);
                } else {
                    Toast.makeText(AddBlogActivity.this, "Album not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_blog_toolbar);
        mAddBlogTextView = (EditText) findViewById(R.id.add_blog);
        toolbar.inflateMenu(R.menu.menu_send);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.send) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String imagePath = "";
                                if(mPic1Bitmap != null) imagePath += mPic1Id;
                                if(mPic2Bitmap != null) imagePath += ("#" + mPic2Id);
                                if(mPic3Bitmap != null) imagePath += ("#" + mPic3Id);
                                int result = send(mAddBlogTextView.getText().toString(), imagePath);
                                Looper.prepare();
                                if (result == 1) {
                                    Toast.makeText(AddBlogActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(AddBlogActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                                Looper.loop();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            } catch (JSONException jse) {
                                jse.printStackTrace();
                            }
                        }
                    }).start();
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri;

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(AddBlogActivity.this, "Aborted", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PICK_IMAGE_1: {
                    imageUri = data.getData();
                    mPic1Uri = imageUri;
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
                    mUploadPic1.setBackground(new BitmapDrawable(bm));
                    if(mPic1Bitmap == null) {
                        mPic1Id = String.valueOf(System.currentTimeMillis());
                        Log.e(TAG, mPic1Id);
                    }
                    mPic1Bitmap = bm;
                    break;
                }
                case REQUEST_CODE_PICK_IMAGE_2: {
                    imageUri = data.getData();
                    mPic2Uri = imageUri;
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
                    mUploadPic2.setBackground(new BitmapDrawable(bm));
                    if(mPic2Bitmap == null) {
                        mPic2Id = String.valueOf(System.currentTimeMillis());
                    }
                    mPic2Bitmap = bm;
                    break;
                }
                case REQUEST_CODE_PICK_IMAGE_3: {
                    imageUri = data.getData();
                    mPic3Uri = imageUri;
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
                    mUploadPic3.setBackground(new BitmapDrawable(bm));
                    if(mPic3Bitmap == null) {
                        mPic3Id = String.valueOf(System.currentTimeMillis());
                    }
                    mPic3Bitmap = bm;
                    break;
                }
            }
        }
    }

    private int send(String content, String imagePath) throws IOException, JSONException {
        //send content
        String urlStr = ServerConfigure.getSend();
        String params = "username=" + getUsername() + "&content=" + content + "&imagePath=" + imagePath;
        String requestMethod = "POST";
        String responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, requestMethod);
        Log.i(TAG, "send: " + responseText);
        JSONObject jsonObject = new JSONObject(responseText);
        int status = jsonObject.getInt("status");

        //send picture
        if(mPic1Uri != null) {
            UploadFile uploadFile = new UploadFile(AddBlogActivity.this, mPic1Uri);
            params = "pic_id=" + mPic1Id;
            uploadFile.uploadFile(ServerConfigure.getUploadPic(), params);
        }
        if(mPic2Uri != null) {
            UploadFile uploadFile = new UploadFile(AddBlogActivity.this, mPic2Uri);
            params = "pic_id=" + mPic2Id;
            uploadFile.uploadFile(ServerConfigure.getUploadPic(), params);
        }
        if(mPic3Uri != null) {
            UploadFile uploadFile = new UploadFile(AddBlogActivity.this, mPic3Uri);
            params = "pic_id=" + mPic3Id;
            uploadFile.uploadFile(ServerConfigure.getUploadPic(), params);
        }
        return status;
    }

    private String getUsername() {
        SharedPreferences pre = getSharedPreferences("userInfo", MODE_PRIVATE);
        return pre.getString("username", "");
    }
}
