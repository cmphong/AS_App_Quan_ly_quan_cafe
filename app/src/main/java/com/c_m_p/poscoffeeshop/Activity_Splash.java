package com.c_m_p.poscoffeeshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Model.Profile;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.c_m_p.poscoffeeshop.MyUtils.MyCustomStatusBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

/* TODO
    [] Handle First_Start
 */
public class Activity_Splash extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyCustomStatusBar.setFullScreen(Activity_Splash.this);
        MyCustomStatusBar.changeColorIcon(Activity_Splash.this);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences(CommonData.STORE_PROFILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        int count_start = sharedPreferences.getInt(CommonData.COUNT_START, 0);
        editor.putInt(CommonData.COUNT_START, count_start++);
        editor.apply();


        Handler handler = new Handler(Looper.getMainLooper());

        CommonData.fbAuth = FirebaseAuth.getInstance();
        if(CommonData.fbAuth != null){
            Logd("mAuth != null");
            CommonData.fbUser = CommonData.fbAuth.getCurrentUser();
            if(CommonData.fbUser != null) {
                CommonData.fbDatabase = FirebaseDatabase.getInstance(CommonData.urlDatabase);
                CommonData.fbRef = CommonData.fbDatabase.getReference();
                CommonData.fbRef_Category = CommonData.fbDatabase.getReference(
                        "Devices/" + CommonData.fbUser.getUid() + "/Category");
                CommonData.fbRef_Drink = CommonData.fbDatabase.getReference(
                        "Devices/" + CommonData.fbUser.getUid() + "/Drink");
                CommonData.fbRef_Table = CommonData.fbDatabase.getReference(
                        "Devices/" + CommonData.fbUser.getUid() + "/Table");
                CommonData.fbRef_Receipt = CommonData.fbDatabase.getReference(
                        "Devices/" + CommonData.fbUser.getUid() + "/Receipt");
                CommonData.fbRef_Profile = CommonData.fbDatabase.getReference(
                        "Devices/" + CommonData.fbUser.getUid() + "/Profile");

                // Tất cả xử lý khi khởi động app lần kế tiếp và Acc đã được signed
                // ...
                CommonData.loadProfileStoreFromFirebase(this);

            }else{
                Logdln("fbUser is null", 70);
            }
        }else{
            Logd("mAuth = null");
            showToast("Please connect to internet and try again");
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Activity_Splash.this, Activity_Login.class));
                finish();
            }
        }, 4000);



    }

    private void saveIntoSharePreferences(Profile profile) {

        editor.putString(CommonData.STORE_NAME, profile.getName());
        editor.putString(CommonData.PHONE_NUMBER, profile.getPhone_number());
        editor.putString(CommonData.ADDRESS, profile.getAddress());
        editor.apply();
    }


    private void loadProfileStoreFromFirebase(){
        if(CommonData.fbRef_Profile != null) {
            CommonData.fbRef_Profile.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Logdln("Error getting data", 101);
                    } else {
                        Profile profile = task.getResult().getValue(Profile.class);
                        if (profile != null) {
                            saveIntoSharePreferences(profile);
                        } else {
                            Logdln("Profile is null", 107);
                        }
                    }
                }
            });
        }else{
            Profile profile = new Profile("", "", "");
            CommonData.fbRef_Profile.setValue(profile);
            saveIntoSharePreferences(profile);
        }
    }

    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_Splash.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Splash.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_Splash.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_Splash.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(String str ){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}