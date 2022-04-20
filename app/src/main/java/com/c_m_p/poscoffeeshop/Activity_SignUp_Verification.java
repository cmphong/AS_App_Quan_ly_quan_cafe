package com.c_m_p.poscoffeeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Activity_SignUp_Verification extends AppCompatActivity {

    private EditText edt_enter_otp;
    private Button btn_verify;
    private TextView tv_code_sent_to,
                     tv_resend_code;

    private String mVerificationId = "",
            mPhoneNumber = "";
    private Dialog dialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_verification);
        Logd("onCreate()");

        edt_enter_otp = findViewById(R.id.edt_enter_otp);
        btn_verify = findViewById(R.id.btn_signup_verify);
        tv_code_sent_to = findViewById(R.id.tv_code_sent_to);
        tv_resend_code = findViewById(R.id.tv_resend_code);

        edt_enter_otp.requestFocus();

        dialog = CommonData.getMyDialog(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mVerificationId = bundle.getString(Activity_Login.VERIFICATION_ID);
            mPhoneNumber    = bundle.getString(Activity_Login.PHONE_NUMBER);
            mToken          = bundle.getParcelable(Activity_Login.TOKEN);
        }

        tv_code_sent_to.setText(getString(R.string.Code_is_sent_to) + " " + mPhoneNumber);

        tv_resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resendCode();
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP();
                dialog.show();
            }
        });

    }

    private void resendCode() {
        dialog.show();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Logdln("onVerificationCompleted: " + credential, 74);

                dialog.dismiss();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Logd("onVerificationFailed: " +e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Logdln("Phone number invalid", 84);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Logdln("The SMS quota for the project has been exceeded", 86);
                }

                // Show a message and update the UI
//                updateUIHere();
                dialog.dismiss();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mToken = token;
                dialog.dismiss();
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(CommonData.fbAuth)
                        .setPhoneNumber(mPhoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(Activity_SignUp_Verification.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(mToken)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void verifyOTP() {
        String code = edt_enter_otp.getText().toString().trim();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        CommonData.fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Logdln("signInWithCredential:success", 65);

                            CommonData.fbUser = task.getResult().getUser();
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
                            // Update UI
                            goToDashboard();
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Logdln("signInWithCredential:failure\n" +task.getException(), 82);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                showToast(getString(R.string.The_verification_code_entered_was_invalid));
                            }
                        }
                    }
                });
    }

    private void goToDashboard(){
        startActivity(new Intent(Activity_SignUp_Verification.this, Activity_Dashboard.class));
    }


    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_SignUp_Verification.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_SignUp_Verification.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_SignUp_Verification.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_SignUp_Verification.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast( String str ){
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}