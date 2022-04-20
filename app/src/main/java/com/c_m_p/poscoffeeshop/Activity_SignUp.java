package com.c_m_p.poscoffeeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Activity_SignUp extends AppCompatActivity implements View.OnClickListener {
    public static String VERIFICATION_ID = "VERIFICATION_ID";

    EditText edt_name, edt_email, edt_phone, edt_password, edt_password_confirm;
    TextView tv_warning, tv_back_login;
    Button btn_create_user;
    ImageView iv_ic_log_password, iv_ic_log_password_confirm;
    boolean lock_password = true, lock_password_confirm = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initialView();
        tv_warning.setVisibility(View.GONE);

        tv_back_login.setOnClickListener(this);
        iv_ic_log_password.setOnClickListener(this);
        iv_ic_log_password_confirm.setOnClickListener(this);
        btn_create_user.setOnClickListener(this);

    }

    private void initialView() {
        tv_warning = findViewById(R.id.tv_warning);
        edt_name = findViewById(R.id.edt_signup_your_name);
        edt_email = findViewById(R.id.edt_signup_email);
        edt_phone = findViewById(R.id.edt_signup_phone);
        edt_password = findViewById(R.id.edt_signup_password);
        edt_password_confirm = findViewById(R.id.edt_signup_password_confirm);
        tv_back_login = findViewById(R.id.tv_signup_back_to_login);


        iv_ic_log_password = findViewById(R.id.imv_signup_lock_password);
        iv_ic_log_password_confirm = findViewById(R.id.imv_signup_lock_password_confirm);
        btn_create_user = findViewById(R.id.btn_signup_create_user);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_signup_back_to_login:
                startActivity(new Intent(Activity_SignUp.this, Activity_Login.class));
                break;

            case R.id.imv_signup_lock_password:
                lock_password = !lock_password;
                if(lock_password){
                    iv_ic_log_password.setImageResource(R.drawable.ic_lock);
                    edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{
                    iv_ic_log_password.setImageResource(R.drawable.ic_lock_open);
                    edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                }
                break;

            case R.id.imv_signup_lock_password_confirm:
                lock_password_confirm = !lock_password_confirm;
                if(lock_password_confirm){
                    iv_ic_log_password_confirm.setImageResource(R.drawable.ic_lock);
                    edt_password_confirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{
                    iv_ic_log_password_confirm.setImageResource(R.drawable.ic_lock_open);
                    edt_password_confirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                }
                break;

            case R.id.btn_signup_create_user:
                String email = edt_email.getText().toString().trim();
                String password = edt_password.getText().toString().trim();
                String password_confirm = edt_password_confirm.getText().toString().trim();
                String name = edt_name.getText().toString().trim();
                String phone = edt_phone.getText().toString().trim();

                // START - WORKED FINE - email & password ==========================================
//                if(
//                    isValidEmail(email) &&
//                    isValidPassword(password) && isValidPassword(password_confirm) &&
//                    password_confirm.equals(password)
//                ){
//                    tv_warning.setVisibility(View.GONE);
//                    createUser(email, password);
//                }else{
//                    tv_warning.setVisibility(View.VISIBLE);
//                    if(!isValidEmail(email)){
//                        tv_warning.setText(getString(R.string.Your_email_is_incorrect));
//                        break;
//                    }else
//                    if(!isValidPassword(password)){
//                        tv_warning.setText(getString(R.string.Password_must_container_at_least_8_characters));
//                        break;
//                    }else
//                    if(!isValidPassword(password_confirm) || !password_confirm.equals(password)){
//                        tv_warning.setText(getString(R.string.Password_and_Password_Confirm_must_be_match));
//                        break;
//                    }
//                }
                // END - WORKED FINE - email & password ==========================================

                // START - PHONE AUTH ==========================================
                createUserWithPhoneNumber();



                break;
        }
    }

    private String mVerificationId = "";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        @Override
        public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Logdln("onVerificationCompleted:" + credential, 160);

//            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Logdln("onVerificationFailed:\n" +e, 170);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
//            updateUIHere();
        }

        @Override
        public void onCodeSent(
                @NonNull @NotNull String verificationId,
                @NonNull @NotNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId, token);
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Logdln("onCodeSent: " + verificationId, 190);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            Intent intent = new Intent(Activity_SignUp.this, Activity_SignUp_Verification.class);
            Bundle bundle = new Bundle();
            bundle.putString(VERIFICATION_ID, verificationId);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    private void createUserWithPhoneNumber() {
        String phoneNumber = "+84999999999";
        String code = "112233";

        CommonData.fbAuth.useAppLanguage();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(CommonData.fbAuth)
                        .setPhoneNumber(phoneNumber)               // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)  // Timeout and unit
                        .setActivity(this)                         // Activity (for callback binding)
                        .setCallbacks(mCallbacks)                  // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }



    private void createUser(String email, String password){
        CommonData.fbAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Logdln("createUserWithEmail:success", 85);
                            showToast(getString(R.string.Create_successful));
                            CommonData.fbUser = CommonData.fbAuth.getCurrentUser();
                            startActivity(new Intent(Activity_SignUp.this, Activity_Login.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Logdln("createUserWithEmail:failure\n" +task.getException(), 91);
                            showToast("Authentication failed.");
                        }
                    }
                });
    }

    private boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password){
        return !TextUtils.isEmpty(password) && password.length() >= 8;
    }




    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_SignUp.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_SignUp.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_SignUp.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_SignUp.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(String str){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
}