package com.c_m_p.poscoffeeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


/**
 * TODO<br>
 *  [x] Sign Out crashed<br>
 *      link: https://developers.google.com/identity/sign-in/android/disconnect?authuser=0#sign_out_users<br>
 *  [x] Handle Quit app<br>
 *  [ ] Tối ưu sản phẩm Popular khi 2 sản phẩm có cùng số lượng Ordered
 */
public class Activity_Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static String   FRAGMENT_TABLE            = "TABLE",
                            FRAGMENT_APP_SETTINGS     = "APP_SETTINGS",
                            FRAGMENT_CATEGORY         = "CATEGORY",
                            FRAGMENT_ADD_NEW_DRINK    = "ADD_NEW_DRINK",
                            FRAGMENT_PRINT_MANAGEMENT = "FRAGMENT_PRINT_MANAGEMENT";

    private String fragmentCurrent = FRAGMENT_TABLE;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ImageButton ib_close;
    private ImageView iv_avatar;
    private TextView tv_name;

    private String storeName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
//        Logd("onCreate()");

        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View v = navigationView.getHeaderView(0);
        ib_close = v.findViewById(R.id.drawer_ib_close);
        iv_avatar = v.findViewById(R.id.drawer_iv_avatar);
        tv_name = v.findViewById(R.id.drawer_tv_name);

        SharedPreferences sharedPreferences = getSharedPreferences(CommonData.STORE_PROFILE, MODE_PRIVATE);
        storeName = sharedPreferences.getString(CommonData.STORE_NAME, "");
        if(storeName.equals("") || storeName.length() == 0) {
            storeName = getResources().getString(R.string.app_name);
        }
        setActionBar();


        ib_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_Dashboard.this, Activity_User_Profile.class));
            }
        });

        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_Dashboard.this, Activity_User_Profile.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        Logd("onStart()");

//        mAuth = FirebaseAuth.getInstance();
        if(CommonData.fbUser != null) {
            Uri url = CommonData.fbUser.getPhotoUrl();
            if (url != null) {
                Glide.with(Activity_Dashboard.this).load(url).into(iv_avatar);
            } else {
                iv_avatar.setImageResource(R.drawable.ic_person);
            }
            String name = CommonData.fbUser.getDisplayName();
            String email = CommonData.fbUser.getEmail();
            if (name != null && name.length() != 0){
                tv_name.setText(CommonData.fbUser.getDisplayName());
            }else if(email != null && email.length() != 0){
                tv_name.setText(CommonData.fbUser.getEmail());
            }
            Logdln(CommonData.fbUser.getEmail(), 117);
        }else{
            Logdln("user is null", 119);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.store_status:
                if(fragmentCurrent != FRAGMENT_TABLE) {
                    replaceFragment(new Fragment_Table(), FRAGMENT_TABLE);
                    fragmentCurrent = FRAGMENT_TABLE;
                }
                break;
            case R.id.category_magement:
                if(fragmentCurrent != FRAGMENT_CATEGORY) {
                    replaceFragment(new Fragment_Category(), FRAGMENT_CATEGORY);
                    fragmentCurrent = FRAGMENT_CATEGORY;
                }
                break;

            case R.id.app_settings:
                if(fragmentCurrent != FRAGMENT_APP_SETTINGS) {
                    replaceFragment(new Fragment_Settings(), FRAGMENT_APP_SETTINGS);
                    fragmentCurrent = FRAGMENT_APP_SETTINGS;
                }
                break;


            case R.id.print_management:
                if(fragmentCurrent != FRAGMENT_PRINT_MANAGEMENT) {
                    replaceFragment(new Fragment_Print_Management(), FRAGMENT_PRINT_MANAGEMENT);
                    fragmentCurrent = FRAGMENT_PRINT_MANAGEMENT;
                }
                break;

            case R.id.signout:
                fragmentCurrent = FRAGMENT_TABLE;
                signOut();

                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        if (CommonData.mGoogleSignInClient != null){
            CommonData.mGoogleSignInClient.signOut();
        }
        if(CommonData.fbAuth != null) {
            CommonData.fbAuth.signOut();
        }
        CommonData.fbUser = null;
        CommonData.fbDatabase = null;
        CommonData.fbRef = null;
        CommonData.fbRef_Category = null;
        CommonData.fbRef_Drink = null;
        CommonData.fbRef_Table = null;
        CommonData.fbRef_Receipt = null;
        CommonData.fbRef_Profile = null;

        finish();
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else if(!fragmentCurrent.equals(FRAGMENT_TABLE)){
            replaceFragment(new Fragment_Table(), FRAGMENT_TABLE);
            fragmentCurrent = FRAGMENT_TABLE;
        }else{
            // Destroy activity
            super.onBackPressed();
//            finish();
//            finishAndRemoveTask();
            finishAffinity();
        }
    }


// === START - MENU TOOLBAR ====================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_new, menu);

//        return super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_add_category:
                intent = new Intent(this, Activity_Catagory_Add_New.class);
                startActivity(intent);
                break;
            case R.id.menu_add_drink:
                intent = new Intent(this, Activity_Drinks_Add_New.class);
                startActivity(intent);
                break;
            case R.id.menu_add_table:
                intent = new Intent(this, Activity_Table_Add_New.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
// === END - MENU TOOLBAR ====================================================

    private void setActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(storeName);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        replaceFragment(new Fragment_Table(), "fragment_table");
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void replaceFragment(Fragment fragment, String tag_name) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment, tag_name);
//        transaction.addToBackStack(null); // xuất hiện lỗi không load fragment khi ấn phím "Back"
        transaction.commit();
    }








    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_Dashboard.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Dashboard.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_Dashboard.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_Dashboard.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(String str ){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
}