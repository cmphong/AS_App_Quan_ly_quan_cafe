package com.c_m_p.poscoffeeshop;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Model.FirebaseAPI;
import com.c_m_p.poscoffeeshop.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Fragment_Add_New_Category extends Fragment implements View.OnClickListener {

    private Button btn_addData, btn_getData, btn_user_info;

    private FirebaseUser fbUser;
    private FirebaseDatabase fbDatabase;
    private DatabaseReference fbDatabaseRefUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_new_category, container, false);
        btn_getData = v.findViewById(R.id.btn_getData);
        btn_addData = v.findViewById(R.id.btn_addData);
        btn_user_info = v.findViewById(R.id.btn_user_info);

//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        Logdln(mDatabase.toString(), 40);


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        String url = "https://pos-coffee-shop-default-rtdb.asia-southeast1.firebasedatabase.app";
        fbDatabase = FirebaseDatabase.getInstance(url);
        fbDatabaseRefUsers = fbDatabase.getReference("users");
//        FirebaseAPI.writeNewUser(myRef, "user_0002", "Chrisp", "chrisp@cmp.com");
//        FirebaseAPI.writeNewUser(myRef, "user_0003", "GinP", "ginp@cmp.com");
//        FirebaseAPI.writeNewUser(myRef, "user_0004", "oz", "oz@cmp.com");

//        FirebaseAPI.readData(userRef);
        btn_getData.setOnClickListener(this);
        btn_addData.setOnClickListener(this);
        btn_user_info.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_getData:
//                FirebaseAPI.readData(fbDatabaseRefUsers);
//                FirebaseAPI.readDataOnce(fbDatabaseRefUsers, "user_0001");

                Logdln("ty_tho: " +fbDatabase.getReference("Tý_Thơ"), 79);

                break;
            case R.id.btn_addData:
//                id++;
//                FirebaseAPI.writeNewUser(
//                        myRef,
//                        "user_00" + id,
//                        "user" +id,
//                        "user" +id+ "@cmp.com");

                // ========================
                FirebaseAPI.writeNewPost(
                        fbDatabase.getReference(),
                        "userId_001",
                        "gin",
                        "my_title_here",
                        "This is body content, this is body content"
                );
                break;

            case R.id.btn_user_info:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Logdln("User uuid: " +user.getUid(), 100);
                Logdln("Email: " +user.getEmail(), 101);
                Logdln("Name: " +user.getDisplayName(), 102);
                break;
        }
    }







    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Fragment_Add_New_Category.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Fragment_Add_New_Category.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Fragment_Add_New_Category.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Fragment_Add_New_Category.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}