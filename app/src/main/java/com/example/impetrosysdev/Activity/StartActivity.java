package com.example.impetrosysdev.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.impetrosysdev.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button,updatePassword;
    TextView textView;
    FirebaseUser users;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView firstName, lastName, mobileNumber, email,profile,userProfiles;
    FirebaseDatabase database;
    DatabaseReference ref;
    private DatabaseReference mDatabase;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        mobileNumber = findViewById(R.id.mobileNumber);
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logOut);
        textView = findViewById(R.id.user_details);
        users = auth.getCurrentUser();
        profile = findViewById(R.id.profile);
        updatePassword=findViewById(R.id.updatePassword);
        userProfiles = findViewById(R.id.userProfiles);
        database = FirebaseDatabase.getInstance();
        String uid = users.getUid();
        if (users != null) {
        Log.d("StartActivity", "UID: " + uid);}
        else {
            // Handle the case where there is no authenticated user
            Log.e("StartActivity", "No authenticated user found.");
        }
        ref = database.getReference().child("users").child(uid);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        /*mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    Toast.makeText(StartActivity.this, "if not successful", Toast.LENGTH_LONG).show();
//                    {
//                        DataSnapshot userSnapshot = snapshot.child(users.getUid());
//                        // Fetch individual values
//                        String userMobileNumber = userSnapshot.child("mobileNumber").getValue(String.class);
//                        String userEmail = userSnapshot.child("email").getValue(String.class);
//                        String userFirstName = userSnapshot.child("firstName").getValue(String.class);
//                        String userLastName = userSnapshot.child("lastName").getValue(String.class);
//
//                        // Set values to TextViews
//                        if (userMobileNumber != null) {
//                            mobileNumber.setText(userMobileNumber);
//                        }
//                        if (userEmail != null) {
//                            email.setText(userEmail);
//                        }
//                        if (userFirstName != null) {
//                            firstName.setText(userFirstName);
//                        }
//                        if (userLastName != null) {
//                            lastName.setText(userLastName);
//                        }
//                    }
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Toast.makeText(StartActivity.this, "if successful", Toast.LENGTH_LONG).show();
                }
            }

        });*/

        db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                //Userdata userdata = documentSnapshot.toObject(Userdata.class);
                Log.d("StartActivity", "___uid: " + uid );
                Toast.makeText(StartActivity.this, "successs "+documentSnapshot.getString("email"), Toast.LENGTH_LONG).show();

                firstName.setText(documentSnapshot.getString("firstname"));
                lastName.setText(documentSnapshot.getString("lastname"));
                email.setText(documentSnapshot.getString("email"));
                mobileNumber.setText(documentSnapshot.getString("mobile"));

            } else {
                Toast.makeText(StartActivity.this, "not exist ", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(StartActivity.this, "failure ", Toast.LENGTH_LONG).show();
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();

            }
        });
        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdatePasswordDialog();
            }
        });
        userProfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click on userProfiles TextView
                // Example: Pass data to RecyclerViewActivity
                Intent intent = new Intent(StartActivity.this, UsersList.class);

                // Add any necessary data to the Intent using putExtra
                intent.putExtra("extra_key", "extra_value");

                // Start the new activity
                startActivity(intent);

            }
        });

        Log.d("StartActivity", "Initializing GoogleSignInOptions and GoogleSignInClient");
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personFirstName = acct.getGivenName();
            String personLastName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String fullName = personFirstName + " " + personLastName;
            Log.d("NameActivity", "Setting full name: " + fullName);

            email.setText(personEmail);
        }
        Log.d("StartActivity", "Before onDataChange");

        Log.d("StartActivity", "After onDataChange");
    }

    private void showUpdatePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Password");

        // Create the parent layout for the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create EditText fields for old password, new password, and confirm password
        LinearLayout oldPasswordLayout = new LinearLayout(this);
        oldPasswordLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        oldPasswordLayout.setOrientation(LinearLayout.HORIZONTAL);
        final EditText oldPasswordInput = new EditText(this);
        oldPasswordInput.setHint("Old Password");
        oldPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        oldPasswordInput.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        oldPasswordLayout.addView(oldPasswordInput);
        ToggleButton toggleOldPassword = new ToggleButton(this);
        toggleOldPassword.setTextOff("Show");
        toggleOldPassword.setTextOn("Hide");
        oldPasswordLayout.addView(toggleOldPassword);
        layout.addView(oldPasswordLayout);

        LinearLayout newPasswordLayout = new LinearLayout(this);
        newPasswordLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newPasswordLayout.setOrientation(LinearLayout.HORIZONTAL);
        final EditText newPasswordInput = new EditText(this);
        newPasswordInput.setHint("New Password");
        newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordInput.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        newPasswordLayout.addView(newPasswordInput);
        ToggleButton toggleNewPassword = new ToggleButton(this);
        toggleNewPassword.setTextOff("Show");
        toggleNewPassword.setTextOn("Hide");
        newPasswordLayout.addView(toggleNewPassword);
        layout.addView(newPasswordLayout);

        LinearLayout confirmPasswordLayout = new LinearLayout(this);
        confirmPasswordLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        confirmPasswordLayout.setOrientation(LinearLayout.HORIZONTAL);
        final EditText confirmPasswordInput = new EditText(this);
        confirmPasswordInput.setHint("Confirm Password");
        confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordInput.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        confirmPasswordLayout.addView(confirmPasswordInput);
        ToggleButton toggleConfirmPassword = new ToggleButton(this);
        toggleConfirmPassword.setTextOff("Show");
        toggleConfirmPassword.setTextOn("Hide");
        confirmPasswordLayout.addView(toggleConfirmPassword);
        layout.addView(confirmPasswordLayout);

        builder.setView(layout);

        // Set toggle button functionality
        toggleOldPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                oldPasswordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                oldPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        toggleNewPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newPasswordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        toggleConfirmPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                confirmPasswordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        builder.setPositiveButton("Verify and Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String oldPassword = oldPasswordInput.getText().toString();
                String newPassword = newPasswordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(StartActivity.this, "New password and confirm password do not match.", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }
                verifyPasswords(oldPassword, newPassword, confirmPassword);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }




    private void verifyPasswords(String oldPassword, String newPassword, String confirmPassword) {
        // Re-authenticate the user
        AuthCredential credential = EmailAuthProvider.getCredential(users.getEmail(), oldPassword);
        users.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Password is verified, proceed with the update
                    updatePassword(newPassword);
                } else {
                    // Password verification failed
                    Toast.makeText(StartActivity.this, "Failed to verify old password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePassword(String newPassword) {
        // Password update logic
        users.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(StartActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StartActivity.this, "Failed to update password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



  /*  private boolean verifyPasswords(String oldPassword, String newPassword, String confirmPassword) {
        // Add your password verification logic here
        // For example, you might compare oldPassword with the actual old password stored in your app
        // Also, check if newPassword and confirmPassword match

        return oldPassword.equals("actualOldPassword") && newPassword.equals(confirmPassword);
    }*/



    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(StartActivity.this, Login.class);
        startActivity(intent);
        finish();

    }



}
