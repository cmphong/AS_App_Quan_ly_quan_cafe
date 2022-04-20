package com.c_m_p.poscoffeeshop;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Map;

public class Activity_User_Profile extends AppCompatActivity {

    Context context = this;
    Dialog dialog;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    ShapeableImageView siv_avatar;
    EditText edt_name;
    Button btn_save;

    String  srcPath = "",
            extFile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        siv_avatar = findViewById(R.id.siv_logo_store);
        edt_name = findViewById(R.id.edt_name);
        btn_save = findViewById(R.id.btn_save);

        dialog = CommonData.getMyDialog(this);

        if(CommonData.fbUser.getPhotoUrl() != null){
            Uri uri = CommonData.fbUser.getPhotoUrl();
            siv_avatar.setImageURI(uri);
        }
        if(CommonData.fbUser.getDisplayName().length() != 0){
            String name = CommonData.fbUser.getDisplayName();
            edt_name.setText(name);
        }

        siv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalery();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri != null){
                    // /storage/emulated/0/.../xxx.jpg
                    srcPath = CommonData.getRealPathFromURI(Activity_User_Profile.this, uri);
                    extFile = "." + srcPath.split("\\.")[1];

                    if(CommonData.logoExist(context, "avatar") != null){
                        File fileExist = new File(CommonData.logoExist(context, "avatar"));
                        fileExist.delete();
                    }

                    // /data/user/0/com.c_m_p.poscoffeeshop/files/logo.jpg
                    String desPath = getFilesDir().getAbsolutePath() + "/avatar" + extFile;

                    CommonData.copyImageToPrivateStorage(
                            new File(srcPath),
                            new File(desPath)
                    );

                    updateAvatarOnFirebaseStorage(); // OK
                    dialog.show();
                }else {

                    String name = edt_name.getText().toString();
                    if (!TextUtils.isEmpty(name) && name.length() > 0) {
                        updateUserNameProfileOnFirebaseAuth(name);
                        dialog.show();
                    }
                }
            }
        });
    }

    private void updateUserNameProfileOnFirebaseAuth(String name) {

        UserProfileChangeRequest profileUpdates;

        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        if (!TextUtils.isEmpty(name)) {
            builder.setDisplayName(name);
        }
        profileUpdates = builder.build();
        CommonData.fbUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                finish();
            }
        });

    }


    // Start - Firebase Storage ========================================
    private void updateAvatarOnFirebaseStorage(){
        // AppCheck
//        FirebaseApp.initializeApp(/*context=*/ this);
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        firebaseAppCheck.installAppCheckProviderFactory(
//                SafetyNetAppCheckProviderFactory.getInstance());

        // GET reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference riversRef = storageRef.child("avatar/" +CommonData.fbUser.getUid() +"/avatar" +extFile);
        UploadTask uploadTask = riversRef.putFile(uri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Logdln("onFailure()\n" +exception, 129);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Logdln("onSuccess()", 136);
                StorageMetadata metadata = taskSnapshot.getMetadata();
                metadata.getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri url) {
//                        Logd("Url: " +url);
                        updateUsersPhotoProfileOnFirebaseAuth(url);

                    }
                });

            }
        });
    }
    // End - Firebase Storage ========================================

    private void updateUsersPhotoProfileOnFirebaseAuth(Uri url) {

        // update profile on firebase auth
        String name = edt_name.getText().toString().trim();
        if(!TextUtils.isEmpty(name) || uri != null) {
            UserProfileChangeRequest profileUpdates;

            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
            if (!TextUtils.isEmpty(name)) {
                builder.setDisplayName(name);
            }
            if (uri != null) {
                builder.setPhotoUri(url);
            }
            profileUpdates = builder.build();
            CommonData.fbUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    finish();
                }
            });
        }

//        UserProfileChangeRequest profileUpdate;
//        profileUpdate = new UserProfileChangeRequest.Builder()
//            .setPhotoUri(Uri.parse(url.toString()))
//            .build();
//        CommonData.fbUser.updateProfile(profileUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                finish();
//                Logdln("onSuccess()", 186);
//            }
//        });
    }

    Uri uri;

    ActivityResultLauncher<Intent> startActivityForResult_picker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        if(intent == null){return;}
                        uri = intent.getData();
                        siv_avatar.setImageURI(uri);
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
                        showToast("Permission Denied, Please try again!");
                    }
                }
            }
    );

    private void saveImageToLocalStorage(){

    }

    private void openGalery() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()){
            mRequestPermissions.launch(PERMISSIONS_STORAGE);
        }else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult_picker.launch(intent);
        }
    }

    private boolean arePermissionDenied(){
        int p = 0;
        while(p < PERMISSIONS_STORAGE.length){
            if(checkSelfPermission(PERMISSIONS_STORAGE[p]) == PackageManager.PERMISSION_DENIED){
                return true;
            }
            p++;
        }
        return false;
    }




    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_User_Profile.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_User_Profile.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_User_Profile.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_User_Profile.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(String str){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
}