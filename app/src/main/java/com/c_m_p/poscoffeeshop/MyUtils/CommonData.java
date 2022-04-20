package com.c_m_p.poscoffeeshop.MyUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.c_m_p.poscoffeeshop.Model.Category;
import com.c_m_p.poscoffeeshop.Model.IconObj;
import com.c_m_p.poscoffeeshop.Model.Profile;
import com.c_m_p.poscoffeeshop.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CommonData {
    public static String STORE_PROFILE  = "STORE_PROFILE",
                         USER_PROFILE   = "USER_PROFILE",
                         LOCALE_APP     = "LOCALE_APP",
                        LOCALE          = "LOCALE",
                        COUNT_START     = "COUNT_START",
                        STORE_NAME      = "STORE_NAME",
                        PHONE_NUMBER    = "PHONE_NUMBER",
                        ADDRESS         = "ADDRESS";

    public static String urlDatabase = "https://pos-coffee-shop-default-rtdb.asia-southeast1.firebasedatabase.app/";
    public static FirebaseAuth fbAuth;
    public static FirebaseUser fbUser;
    public static FirebaseDatabase fbDatabase;
    public static DatabaseReference fbRef, fbRef_Category, fbRef_Drink, fbRef_Table,
                                    fbRef_Receipt, fbRef_Profile;
    public static GoogleSignInClient mGoogleSignInClient = null;

    public static Category category;


    public static String getRealPathFromURI(Context context, Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if(cursor != null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }
    public static String logoExist(Context context, String name){
        String[] arrFile = context.getFilesDir().list();
        for(int i = 0 ; i < arrFile.length ; i++){
            if (arrFile[i].contains(name+".")){
                return context.getFilesDir().getAbsolutePath() + "/" + arrFile[i];
            }
        }
        return null;
    }

    public static void copyImageToPrivateStorage(File src, File dst){
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getNameDevice(Context context){
        String manufacturer = Build.MANUFACTURER;
        String name = Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
        String result = (manufacturer + " " + name).toUpperCase();
        result = result.replace(' ', '_');
        result = result.replace('-', '_');
        return result;
    }


    public static List<IconObj> getListIconInDrawable(String prefix){
        Field[] fields = R.drawable.class.getFields();
        List<IconObj> result = new ArrayList<>();
        for (Field field : fields) {
            // Take only those with name starting with "ic_cat_"
            if (field.getName().startsWith(prefix)) {
                try {
                    result.add(new IconObj(
                        field.getInt(null),
                        field.getName()
                    ));


                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String capitalize(String string){
        String[] phrases = string.split("_");
        String result = "";
        for(String phrase : phrases){
            result += phrase.substring(0, 1).toUpperCase() + phrase.substring(1) + " ";
        }
        return result.trim();
    }

    public static Bitmap getImageLogo(Context context) {
        Bitmap bitmap;
        boolean hasLogo = false;
        File file = context.getFilesDir().getAbsoluteFile();
        String imagePath = file.getPath();
        String imageFullName = "";
        String[] arrFiles = file.list();
        if (arrFiles != null) {
            for (int i = 0; i < arrFiles.length; i++) {
                // aaa.jpg , bbb.png , ccc.jpeg , logo.jpg , ddd.gif
                if (arrFiles[i].contains("logo.")) {
                    hasLogo = true;
                    imageFullName = arrFiles[i];
                    break;
                }
            }
        }
        if(hasLogo) {
            bitmap = BitmapFactory.decodeFile(imagePath + "/" + imageFullName);
        }else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_default);
        }
        return bitmap;
    }

    public static void loadProfileStoreFromFirebase(Context context){
//        if(CommonData.fbRef_Profile != null) {
            CommonData.fbRef_Profile.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        LogdlnStatic("Error getting data", 125);
                    } else {
                        Profile profile = task.getResult().getValue(Profile.class);
                        if (profile != null) {
                            saveProfileToSharedPreferences(context, profile);
                        } else {
                            LogdlnStatic("Profile is null", 131);
                        }
                    }
                }
            });
//        }else{
//            Profile profile = new Profile("", "", "");
//            CommonData.fbRef_Profile.setValue(profile);
//            saveProfileToSharedPreferences(context, profile);
//        }
    }
    private static void saveProfileToSharedPreferences(Context context, Profile profile){
        SharedPreferences sp = context.getSharedPreferences(CommonData.STORE_PROFILE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(CommonData.STORE_NAME, profile.getName());
        editor.putString(CommonData.PHONE_NUMBER, profile.getPhone_number());
        editor.putString(CommonData.ADDRESS, profile.getAddress());
        editor.apply();
    }

    public static Dialog getMyDialog(Context context){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_login_spin);
        Window window = dialog.getWindow();
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static List<Category> getListCategoryFromFirebase(){
        List<Category> result = new ArrayList<>();
        CommonData.fbRef_Category.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()){
                        Category category = dataSnapshot.getValue(Category.class);
                        result.add(category);
                    }
                }else{
                    LogdlnStatic("firebase:\n" +String.valueOf(task.getResult().getValue()), 167);
                }
            }
        });
        return result;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static DecimalFormat setCurrencyByLanguageApp(Context context){
        String localApp = context.getResources().getConfiguration().getLocales().get(0).getLanguage();
        DecimalFormat df;
            switch (localApp){
            case "vi":
                df = new DecimalFormat(",##0 đ");
                break;
            default:
                df = new DecimalFormat("$ ,##0.##");
                break;
        }
        return df;
    }

    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== CommonData.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== CommonData.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== CommonData.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== CommonData.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
