package com.c_m_p.poscoffeeshop;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.c_m_p.poscoffeeshop.MyUtils.MyCustomStatusBar;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/** TODO
 *   [ ] Add effect while Login
 */
public class Activity_Login extends AppCompatActivity {

    public static String VERIFICATION_ID = "VERIFICATION_ID",
                         PHONE_NUMBER = "PHONE_NUMBER",
                         TOKEN = "TOKEN";

    SignInButton btn_google_signin;
    Button btn_login;
//    TextView tv_new_account;
    TextView tv_warning;
    EditText edt_phone_number, edt_email, edt_password;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
// ==   FirebaseAuth mAuth;
// ==   FirebaseUser user;
//    ImageView iv_password;
    boolean lock_password = true;


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId, mPhoneNumber;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyCustomStatusBar.setFullScreen(Activity_Login.this);
        setContentView(R.layout.activity_login);
        Logd("onCreate()");



        initView();
        tv_warning.setVisibility(View.GONE);
        dialog = CommonData.getMyDialog(this);

        // PHONE AUTH =======================================================
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Logdln("onVerificationCompleted: " + credential, 109);
                startActivity(new Intent(Activity_Login.this, Activity_Dashboard.class));

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for
                // verification is made, for instance if the the phone
                // number format is not valid.
                Logdln("onVerificationFailed: " +e, 119);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
//                updateUIHere();
                tv_warning.setVisibility(View.VISIBLE);
                tv_warning.setText(getString(R.string.Your_phone_number_is_invalid));
                dialog.dismiss();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Logdln("onCodeSent:" + verificationId, 141);

                // Save verification ID and resending token so we can use them later
                dialog.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;

                Intent intent = new Intent(Activity_Login.this, Activity_SignUp_Verification.class);
                Bundle bundle = new Bundle();
                bundle.putString(VERIFICATION_ID, verificationId);
                bundle.putString(PHONE_NUMBER, mPhoneNumber);
                bundle.putParcelable(TOKEN, token);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };


        // BLOCKED - Login with phone number ===========================
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Login with Email & Password ===================================
//                loginUser();

                // Login with Phone Number =======================================
                tv_warning.setVisibility(View.GONE);

                String phoneNumber = edt_phone_number.getText().toString().trim();
                if (isPhoneNumberValid(phoneNumber)){
                    if(phoneNumber.length() == 10){
                        if(phoneNumber.substring(0,1).equals("0")) {
                            phoneNumber = phoneNumber.substring(1);

                        }else{
                            Logdln("Your phone number is wrong, Please try again!", 176);
                            tv_warning.setVisibility(View.VISIBLE);
                            tv_warning.setText(getString(R.string.Your_phone_number_is_invalid));
                        }
                    }
                    if (phoneNumber.length() == 9){
                        if (phoneNumber.substring(0,1).equals("0")){
                            Logdln("Your phone number is wrong, Please try again!", 183);
                            tv_warning.setVisibility(View.VISIBLE);
                            tv_warning.setText(getString(R.string.Your_phone_number_is_invalid));
                        }else{
                            mPhoneNumber = "+84" +phoneNumber;
                            onClickButtonContinue("+84" +phoneNumber);

                            dialog.show();
                        }
                    }
                }else{
                    tv_warning.setVisibility(View.VISIBLE);
                    tv_warning.setText(getString(R.string.Your_phone_number_is_invalid));
                }
            }
        });
        
        // GOOGLE SIGNIN ====================================================
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btn_google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonData.mGoogleSignInClient = mGoogleSignInClient;
                signIn();
                dialog.show();
            }
        });
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber.length() >= 9 && phoneNumber.length() <= 10;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Logd("onStart()");

        // Check if user is signed in (non-null) and update UI accordingly.
        if(CommonData.fbUser != null){
            goToDashboardActivity();
//            finish();
        }else{
            Logdln("currentUser is null", 231);
        }

//        updateUI(currentUser);
    }

    private ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data == null){
                            return;
                        }

                        // HANDLE HERE ...
                        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);

                    }else{
                        Logdln("RESULT_CANCEL", 256);
                    }
                }
            }
    );


    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
            if(account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            }else{
                Logdln("Google account is null", 273);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Logdln("signInResult:failed code=" + e.getStatusCode(), 278);
//            updateUI(null);
        }
    }

    // Sau khi đăng nhập Google sign-in thành công
    // ta tiếp tục lấy token của Google đưa vào Firebase chứng thực
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        CommonData.fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Logdln("signInWithCredential:success", 293);
                            CommonData.mGoogleSignInClient = mGoogleSignInClient;
                            CommonData.fbUser = CommonData.fbAuth.getCurrentUser();
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

//                            updateUI(user);
                            dialog.dismiss();
                            goToDashboardActivity();
//                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Logdln("signInWithCredential:failure\n" +task.getException(), 313);
//                            updateUI(null);
                        }
                    }
                });
    }

    private void goToDashboardActivity() {
        Intent intent = new Intent(Activity_Login.this, Activity_Dashboard.class);
        startActivity(intent);
    }

//    private void signIn() {
//        Intent signInIntent = oneTapClient.getSignInIntent();
//        startActivityForResult.launch(signInIntent);
//    }
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult.launch(signInIntent);
    }


    // === START - LOGIN WITH PHONE NUMBER ============================================
    private void onClickButtonContinue(String phoneNumber){
        // Set language SMS
        CommonData.fbAuth.useAppLanguage();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(CommonData.fbAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    // === END - LOGIN WITH PHONE NUMBER ============================================



    // === START - LOGIN WITH EMAIL & PASSWORD ============================================
    private void loginUser() {
        String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();
//        if(!email.isEmpty() && !password.isEmpty()){
        if(isValidEmail(email) && isValidPassword(password)){
//            mAuth.signInWithEmailAndPassword(email, password)
            CommonData.fbAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
// ==                               user = mAuth.getCurrentUser();
                                showToast(getString(R.string.Login_successful));
                                CommonData.fbUser = CommonData.fbAuth.getCurrentUser();
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

                                dialog.dismiss();
                                goToDashboardActivity();

                            }else{
                                showToast(getString(R.string.Login_failed_please_try_again));
                            }
                        }
                    });
        }else{
            showToast(getString(R.string.Email_and_Password_is_required));
        }
    }

    private boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password){
        return !TextUtils.isEmpty(password) && password.length() >= 8;
    }
    // === END - LOGIN WITH EMAIL & PASSWORD ============================================

    private void initView() {
        btn_google_signin = findViewById(R.id.btn_google_sign_in);
        btn_google_signin.setSize(SignInButton.SIZE_WIDE);

        btn_login = findViewById(R.id.btn_login);
//        tv_new_account = findViewById(R.id.tv_new_account);
        tv_warning = findViewById(R.id.tv_warning);
        edt_phone_number = findViewById(R.id.edt_phone_number);
//        iv_password = findViewById(R.id.imv_password);
    }


    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_Login.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Login.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_Login.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_Login.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(String str ){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }


}