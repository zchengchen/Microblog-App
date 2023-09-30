package com.example.thweibo;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ServerConfigure {
    private static String mIp = "49.232.146.60:80";
    private static String mAccount;
    private static Bitmap sAvatarBitmap;
    private static HashMap<String, Bitmap> sAvatarMap;
    private static HashSet<String> sBlackListSet;
    private static String updateBlackList = "";
    private static String TAG = "ServerConfigure";

    public static String getIP() {
        return mIp;
    }

    public static String getLogin() {
        return "http://" + getIP() + "/microblog/login.php";
    }

    public static String getRegister() {
        return "http://" + getIP() + "/microblog/register.php";
    }

    public static String getSend() {
        return "http://" + getIP() + "/microblog/send.php";
    }

    public static String getBlackList() { return "http://" + getIP() + "/microblog/fetchblacklist.php"; }

    public static String getUploadAvatar() {
        return "http://" + getIP() + "/microblog/uploadavatar.php";
    }

    public static String getDownloadAvatar() {
        return "http://" + getIP() + "/microblog/pic/avatar/";
    }

    public static String getAllAvatar() {
        return "http://" + getIP() + "/microblog/downloadmicroblogid.php";
    }

    public static String getDeleteBlog() {
        return "http://" + getIP() + "/microblog/deleteblog.php";
    }

    public static String getFetchBlog() {
        return  "http://" + getIP() + "/microblog/fetchblogs.php";
    }

    public static String getFetchComment() {
        return  "http://" + getIP() + "/microblog/fetchcomments.php";
    }

    public static String getSendComment() {
        return "http://" + getIP() + "/microblog/sendcomment.php";
    }

    public static String getUploadPic() {
        return "http://" + getIP() + "/microblog/uploadpic.php";
    }

    public static String getDownloadPic(String account) {
        return "http://" + getIP() + "/microblog/pic/blogpic/" + account + ".png";
    }

    public static String getUpdateBlackListUrl() {
        return "http://" + getIP() + "/microblog/uploadblacklist.php";
    }

    public static String getSendDM() {
        return "http://" + getIP() + "/microblog/uploaddm.php";
    }

    public static String getFetchDM() {
        return "http://" + getIP() + "/microblog/fetchdm.php";
    }

    public static String getDeleteDM() {
        return "http://" + getIP() + "/microblog/deletedm.php";
    }

    public static Bitmap getMyAvatar() {
        return sAvatarBitmap;
    }

    public static void setMyAvatar(Bitmap myAtavatBitmap) {
        sAvatarBitmap = myAtavatBitmap;
    }

    public static String getAvatarURL(String account) {
        return getDownloadAvatar() + account + ".png";
    }

    public static Bitmap getOtherAvatar(String account) {
        if(sAvatarMap == null) {
            sAvatarMap = new HashMap<String,Bitmap>();
        }
        return sAvatarMap.get(account);
    }

    public static void addOtherAvatar(String account, Bitmap avatar) {
        if(sAvatarMap == null) {
            sAvatarMap = new HashMap<String,Bitmap>();
        }
        sAvatarMap.put(account, avatar);
    }

    public static void setAccount(String account) {
        mAccount = account;
    }

    public static String getAccount() {
        return mAccount;
    }

    public static boolean isInBlackList(String account) {
        return sBlackListSet.contains(account);
    }

    public static void addToBlackListSet(String account) {
        if(sBlackListSet == null) {
            sBlackListSet = new HashSet<String>();
        }
        if(mAccount.equals(account)) {
            return ;
        }
        sBlackListSet.add(account);
    }

    public static void removeFromBlackListSet(String account) {
        if(sBlackListSet == null) {
            return ;
        }
        sBlackListSet.remove(account);
    }

    public static ArrayList<String> getBlackListArray() {
        ArrayList<String> list = new ArrayList<>();
        Iterator<String> it = sBlackListSet.iterator();
        while(it.hasNext()) {
            String username = it.next();
            if(username.equals("@")) {
                continue ;
            }
            list.add(username);
        }
        return list;
    }

    public static void updateBlacklist() {
        Iterator<String> it = sBlackListSet.iterator();
        String newBlacklist = "@";
        while(it.hasNext()) {
            String username = it.next();
            if(username.equals("@")) {
                continue;
            } else {
                newBlacklist += ("#" + username);
            }
        }
        updateBlackList = newBlacklist;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = getUpdateBlackListUrl();
                    String params = "username=" + mAccount + "&blacklist=" + updateBlackList;
                    String responseText = new MyHttpRequest().sendHttpRequest(urlStr, params, "POST");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static boolean isUsernameValid(String username) {
        return sAvatarMap.containsKey(username);
    }
}