package com.example.thweibo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MeFragment extends Fragment {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static String TAG = "MeFragment";
    private ImageView mAvatarImageView;
    private TextView mUsernameTextView;
    private Button mLogoutButton;
    private Button mChangeIconButton;
    private Button mBlacklistButton;
    private Button mDMbutton;
    private Uri mImgUri;
    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if(mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_me, container, false);
            mAvatarImageView = (ImageView) mRootView.findViewById(R.id.user_icon);
            mUsernameTextView = (TextView) mRootView.findViewById(R.id.me_nickname);
            mUsernameTextView.setText("Microblog ID: " + ServerConfigure.getAccount());
            mChangeIconButton = (Button) mRootView.findViewById(R.id.change_icon);
            mAvatarImageView.setImageBitmap(ServerConfigure.getMyAvatar());
            mChangeIconButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                    } else {
                        Toast.makeText(getActivity(), "Album not found.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        mLogoutButton = (Button) mRootView.findViewById(R.id.logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        mBlacklistButton = (Button) mRootView.findViewById(R.id.bl_button);
        mBlacklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BlackListActivity.class);
                startActivity(intent);
            }
        });

        mDMbutton = (Button) mRootView.findViewById(R.id.DM_button);
        mDMbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DMListActivity.class);
                startActivity(intent);
            }
        });

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri;
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "onActivityResult() Failed: Register Activity", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PICK_IMAGE: {
                    imageUri = data.getData();
                    mImgUri = imageUri;
                    Log.e(TAG,imageUri.toString());
                    Bitmap bm = null;
                    if (imageUri != null) {
                        try {
                            bm = ImageProcess.getBitmapFormUri(getActivity().getApplicationContext(), imageUri);
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
                    ServerConfigure.setMyAvatar(bm);
                    mAvatarImageView.setImageBitmap(bm);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UploadFile uploadFile = new UploadFile(getActivity(), mImgUri);
                            String params = "username=" + getUsername();
                            uploadFile.uploadFile(ServerConfigure.getUploadAvatar(), params);
                        }
                    }).start();
                    break;
                }
            }
        }
    }

    private String getUsername() {
        SharedPreferences pre = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        return pre.getString("username", "");
    }
}