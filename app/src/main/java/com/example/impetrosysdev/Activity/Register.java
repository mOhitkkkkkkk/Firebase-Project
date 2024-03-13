package com.example.impetrosysdev.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.impetrosysdev.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextfirstName, editTextlastName, editTextconfirmPasswrod;
    EditText editTextmobileNumber;
    Button buttonReg, btn_otp;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView, getotp;
    String phoneNumber;
    FirebaseFirestore db;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String mobileNumber;
    private String confirmpassword;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // Handle failed verification
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d(TAG, "onCodeSent:" + verificationId);
            Toast.makeText(Register.this, "Code send successfully!!", Toast.LENGTH_SHORT).show();
            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;
            Log.d(TAG, "Verification Code: " + verificationId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextfirstName = findViewById(R.id.firstName);
        editTextlastName = findViewById(R.id.lastName);
        editTextmobileNumber = findViewById(R.id.mobileNumber);
        editTextmobileNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        editTextconfirmPasswrod = findViewById(R.id.confirmpassword);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        getotp = findViewById(R.id.getOtp);
        btn_otp = findViewById(R.id.btn_otp);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });

        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editTextmobileNumber.getText().toString();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    phoneNumber = "+91" + phoneNumber;

                    sendOTP(phoneNumber);
                    Log.d(TAG, "Phone number: ");
                } else {
                    editTextmobileNumber.setError("Please enter a phone number");
                }
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void sendOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.d(TAG, "signInWithPhoneAuthCredential: Attempting to sign in with phone auth credential...");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            Toast.makeText(Register.this, "Verification successfully", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = task.getResult().getUser();
                            Log.e(TAG, "signInWithCredential:success:: " + user.getDisplayName());
                            // Update UI
                            saveUserData();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);

        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        firstName = editTextfirstName.getText().toString();
        lastName = editTextlastName.getText().toString();
        mobileNumber = editTextmobileNumber.getText().toString();
        confirmpassword = editTextconfirmPasswrod.getText().toString();

        if (!isValidName(firstName)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Register.this, "Invalid first name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidName(lastName)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Register.this, "Invalid last name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Register.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Register.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmpassword)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mobileNumber.isEmpty()) {
            editTextmobileNumber.setError("Please enter mobile number ");
            return;
        }
        if (getotp.length() < 6) {
            getotp.setError("Please enter valid otp");
            return;
        }


        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, getotp.getText().toString());
            Log.d(TAG, "Phonenumber:$# " + credential.getSmsCode());
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {

            Toast.makeText(Register.this, "Verification Code is wrong, try again", Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.GONE);
        // Proceed with user registration
        /*mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registration successful, proceed to save user data
                            saveUserData();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
    }

    private void saveUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            final String[] userId = {currentUser.getUid()};
            Map<String, Object> user = new HashMap<>();
            user.put("firstname", firstName);
            user.put("lastname", lastName);
            user.put("email", email);
            user.put("mobile", mobileNumber);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "Account Created and data saved.", Toast.LENGTH_SHORT).show();
                                userId[0] = mAuth.getCurrentUser().getUid();
                                db.collection("users").document(userId[0])
                                        .set(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.GONE);
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Register.this, StartActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(Register.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean isValidEmail(String email) {
        // You can use a more sophisticated email validation regex here if needed
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                email.endsWith("@gmail.com");
    }

    private boolean isValidName(String name) {
        // You can adjust the validation criteria for names as needed
        return !TextUtils.isEmpty(name);
    }
}
