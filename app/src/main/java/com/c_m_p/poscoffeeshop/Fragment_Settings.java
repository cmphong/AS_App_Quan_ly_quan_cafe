package com.c_m_p.poscoffeeshop;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Model.Profile;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Settings extends Fragment {

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    ShapeableImageView siv_logo;
    EditText edt_store_name, edt_phone_number, edt_address;
    Button btn_save;
    LinearLayout ln_locale;
    TextView tv_locale;
    ImageView iv_arrow_dropdown;

    Uri uri;
    SharedPreferences sharedPreferences;
    Profile profile;
    boolean firstStart;
    String name_store, phone_number, address,
            localeName = "en_US";



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        ln_locale           = v.findViewById(R.id.ln_locale);
        siv_logo            = v.findViewById(R.id.siv_logo_store);
        edt_store_name      = v.findViewById(R.id.edt_store_name);
        edt_phone_number    = v.findViewById(R.id.edt_phone_number);
        edt_address         = v.findViewById(R.id.edt_address);
        tv_locale           = v.findViewById(R.id.tv_locale);
        iv_arrow_dropdown   = v.findViewById(R.id.iv_ic_arrow_dropdown);
        btn_save            = v.findViewById(R.id.btn_save);


        Bitmap bitmap = CommonData.getImageLogo(getContext());
        siv_logo.setImageBitmap(bitmap);

        profile = getProfileStoreFromSharedPreferences();
        String localeAppName = getLocaleFromSharedPreferences();

        edt_store_name.setText(profile.getName());
        edt_phone_number.setText(profile.getPhone_number());
        edt_address.setText(profile.getAddress());

        // ===== START - Locale ==================================================
//        Logd("getDisplayLanguage = "        + Locale.getDefault().getDisplayLanguage() + "\n"+
//                "getLanguage = "                + Locale.getDefault().getLanguage() +"\n"+
//                "Resources.getLanguage = "      + Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage() +"\n"+
//                "getResources.getLanguage = "   + getResources().getConfiguration().getLocales().get(0).getLanguage() +"\n"+
//                "user preferred locale: "       + LocaleListCompat.getDefault() +"\n"+
//                "Default locale at 0th position: " + LocaleListCompat.getAdjustedDefault()
//        );
        String localApp = getResources().getConfiguration().getLocales().get(0).getLanguage();
        switch (localApp){
            case "vi":
                tv_locale.setText("Tiếng Việt");
                break;
            default:
                tv_locale.setText("English");
                break;
        }

        // ===== END - Locale ==================================================

        siv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri != null){
                    // /storage/emulated/0/.../xxx.jpg
                    String srcPath = CommonData.getRealPathFromURI(getContext(), uri);
                    String extFile = "." + srcPath.split("\\.")[1];

                    if(logoExist() != null){
                        File fileExist = new File(logoExist());
                        fileExist.delete();
                    }

                    // /data/user/0/com.c_m_p.poscoffeeshop/files/logo.jpg
                    String desPath = getContext().getFilesDir().getAbsolutePath() + "/logo" + extFile;

                    copyImageToPrivateStorage(
                            new File(srcPath),
                            new File(desPath)
                        );

                }

                updateStoreProfileOnFirebase();
                updateStoreProfileOnSharedPreferences();
                updateTitleActionBar();
                loadLocale(localeName);
            }
        });

        ln_locale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getLocaleFromSharedPreferences() {
        String currentLocaleApp = getResources().getConfiguration().getLocales().get(0).getLanguage();
        SharedPreferences sharedPreferences_locale = getContext().getSharedPreferences(CommonData.LOCALE_APP, MODE_PRIVATE);
        return sharedPreferences_locale.getString(CommonData.LOCALE, currentLocaleApp);
    }

    private void showPopupMenu(){
        PopupMenu popupMenu = new PopupMenu(getContext(), ln_locale);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_locale, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.popup_menu_en){
                    localeName = "en";
                    tv_locale.setText("English");
                }
                if(item.getItemId() == R.id.popup_menu_vi){
                    localeName = "vi";
                    tv_locale.setText("Tiếng Việt");
                }
                return true;
            }
        });
        popupMenu.show();
    }


    private void updateTitleActionBar() {
        String name = edt_store_name.getText().toString().trim();
        if(name.length() != 0) {
            ((Activity_Dashboard) getActivity()).getSupportActionBar().setTitle(name);
        }
    }

    private String logoExist(){
        String[] arrFile = getContext().getFilesDir().list();
        for(int i = 0 ; i < arrFile.length ; i++){
            if (arrFile[i].contains("logo.")){
                return getContext().getFilesDir().getAbsolutePath() + "/" + arrFile[i];
            }
        }
        return null;
    }

    private void updateStoreProfileOnSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String name         = edt_store_name.getText().toString().trim();
        String phone_number = edt_phone_number.getText().toString().trim();
        String address      = edt_address.getText().toString().trim();

        editor.putString(CommonData.STORE_NAME, name);
        editor.putString(CommonData.PHONE_NUMBER, phone_number);
        editor.putString(CommonData.ADDRESS, address);
        editor.apply();
    }

    private Profile getProfileStoreFromSharedPreferences(){
        sharedPreferences = getActivity().getSharedPreferences(CommonData.STORE_PROFILE, MODE_PRIVATE);

//        firstStart   = sharedPreferences.getBoolean(FIRST_START  , true);
        name_store   = sharedPreferences.getString(CommonData.STORE_NAME, "");
        phone_number = sharedPreferences.getString(CommonData.PHONE_NUMBER  , "");
        address      = sharedPreferences.getString(CommonData.ADDRESS       , "");
        return new Profile(name_store, phone_number, address);
    }

    private void updateStoreProfileOnFirebase() {
        String name = edt_store_name.getText().toString().trim();
        String phone = edt_phone_number.getText().toString().trim();
        String address = edt_address.getText().toString().trim();

        Profile profile = new Profile(name, phone, address);
        CommonData.fbRef_Profile.setValue(profile, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                showToast(getContext(), "Successfull");
            }
        });

    }

    ActivityResultLauncher<Intent> startActivityForResult_picker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        if(intent == null){return;}
                        uri = intent.getData();
                        siv_logo.setImageURI(uri);
                    }
                }
            }
    );

    ActivityResultLauncher<String[]> mRequestPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    if(result.get(PERMISSIONS_STORAGE[0]) && result.get(PERMISSIONS_STORAGE[1])){

                        // YOUR CODE HERE
                        // ...
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult_picker.launch(intent);

                    }else{
                        showToast(getContext(), "Permission Denied, Please try again!");
                    }
                }
            }
    );

    private String getRealPathFromURI(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, proj, null, null, null);
        if(cursor != null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }

    private void copyImageToPrivateStorage(File src, File dst){
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


    private boolean arePermissionDenied(){
        int p = 0;
        while(p < PERMISSIONS_STORAGE.length){
            if(getContext().checkSelfPermission(PERMISSIONS_STORAGE[p]) == PackageManager.PERMISSION_DENIED){
                return true;
            }
            p++;
        }
        return false;
    }

    public void openGallery() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()){
            mRequestPermissions.launch(PERMISSIONS_STORAGE);
        }else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult_picker.launch(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadLocale(String localeName){
        String currentLocaleApp = getLocaleFromSharedPreferences();
        if(!currentLocaleApp.equals(localeName)) {
            Locale locale = new Locale(localeName);
            Locale.setDefault(locale);
            Configuration config = getContext().getResources().getConfiguration();
            config.locale = locale;
            config.setLayoutDirection(locale);
//            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            // Hoặc
            getContext().getResources().updateConfiguration(config, null);

            SharedPreferences sp = getContext().getSharedPreferences(CommonData.LOCALE_APP, MODE_PRIVATE);
            sp.edit().putString(CommonData.LOCALE, localeName);
            sp.edit().apply();

//            updateAllTextViewOnCurrentActivity();
            // Hoặc
            startActivity(getActivity().getIntent());
            getActivity().finish();
        }
    }


    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Fragment_Settings.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Fragment_Settings.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Fragment_Settings.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Fragment_Settings.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}