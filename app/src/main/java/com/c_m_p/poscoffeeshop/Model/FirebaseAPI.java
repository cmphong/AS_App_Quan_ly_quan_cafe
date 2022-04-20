package com.c_m_p.poscoffeeshop.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.c_m_p.poscoffeeshop.Activity_Dashboard;
import com.c_m_p.poscoffeeshop.Activity_Login;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@IgnoreExtraProperties
public class FirebaseAPI {

    // ===== GOOGLE SIGN-IN =======================================================


    // ===== USER PROFILE ==============================================================
    public static void profileUpdates(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            LogdStatic("User profile updated.");
                        }
                    }
                });
    }

    // ===== REALTIME DATABASE ======================================================
    public static void readData(DatabaseReference myRef){
        // Read from the database
        myRef.child("user_0001").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                LogdlnStatic("Value is: " + user.getEmail(), 97);

//                User user = dataSnapshot.getValue(User.class);
//                Logdln("email: " + user.getEmail(), 61);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                LogdlnStatic("Failed to read value:\n" + error.toException().toString(), 106);
            }
        });
    }

    public static void readDataOnce(DatabaseReference myRef, String userId){
        myRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().getValue(User.class);
                    LogdlnStatic(user.getEmail(), 116);
                }
                else {
                    LogdlnStatic("Error getting data" + task.getException(), 119);
                }
            }
        });
    }

    public static void writeNewUser(DatabaseReference myRef, String userId, String name, String email) {
        User user = new User(name, email);

        myRef.child("users").child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            LogdlnStatic("Write Successful", 90);
                        }else {
                            LogdlnStatic("Write failed", 92);
                        }
                    }
                });
    }

    public static void writeNewPost(DatabaseReference myRef, String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
//        String key = myRef.child("posts").push().getKey();
        myRef.child("tmp").push().setValue("22222222222222");
//        LogdlnStatic("key: " +key, 126);
//        Post post = new Post(userId, username, title, body);
//        Map<String, Object> postValues = post.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/posts3/" + key, postValues);
//        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
//
//        myRef.updateChildren(childUpdates);

        String uuid = "userId_001";
        myRef.child("users").child(uuid).setValue(new User("thong20", "thong20@cmp.com"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LogdStatic("Write was successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LogdStatic("Write failed");
                    }
                });


    }






    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== FirebaseAPI.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== FirebaseAPI.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== FirebaseAPI.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== FirebaseAPI.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
