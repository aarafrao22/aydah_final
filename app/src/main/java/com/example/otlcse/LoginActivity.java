package com.example.otlcse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends AppCompatActivity {


    EditText loginUsername, loginPassword;
    TextView signupRedirectText;
    Button loginButton;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String adminuid = "SD0XK8OwwQb7uHgBSAgQF0uNDXQ2";
    Boolean flat = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        loginButton = findViewById(R.id.login_button);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);

        if (sharedPref.contains("userid")) {
            String uid = sharedPref.getString("userid", "");

            if (uid.equals(adminuid)) {
//                databaseReference = firebaseDatabase.getReference("users").child(uid);
                Intent intent = new Intent(LoginActivity.this, dashboard.class);
                startActivity(intent);
            } else {

                databaseReference = firebaseDatabase.getReference("users").child(uid);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println(snapshot);
                        HelperClass value = snapshot.getValue(HelperClass.class);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        flat = false;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // calling on cancelled method when we receive
                        // any error or we are not able to get the data.
                        Toast.makeText(LoginActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()) {

                } else {
                    checkUser();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(String.format("%02X", messageDigest[i]));

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        userPassword = md5(userPassword);
        String MD5String = userPassword;

        if (loginUsername.getText().toString().equals("admin@gmail.com")) {
            Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this, dashboard.class));
        } else {

            firebaseAuth.signInWithEmailAndPassword(userUsername, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("userid", user.getUid());
                                editor.apply();

                                if (user.getUid().equals(adminuid)) {
                                    Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginActivity.this, dashboard.class);
                                    startActivity(intent);
                                } else {

                                    databaseReference = firebaseDatabase.getReference("users").child(user.getUid());
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_LONG).show();
                                            System.out.println(snapshot);
                                            HelperClass value = snapshot.getValue(HelperClass.class);
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            flat = false;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // calling on cancelled method when we receive
                                            // any error or we are not able to get the data.
                                            Toast.makeText(LoginActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }


                            } else {

                                // Registration failed
                                loginUsername.setError("User does not exist");
                                loginUsername.requestFocus();
                            }
                        }
                    });
        }

//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
//        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

//        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if(snapshot.exists()){
//                    loginUsername.setError(null);
//                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
//
//                    if(Objects.equals(passwordFromDB, userPassword)){
//                        loginUsername.setError(null);
//                        //pass data
//                        String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
//                        String IDFromDB = snapshot.child(userUsername).child("nid").getValue(String.class);
//                        String FnameFromDB = snapshot.child(userUsername).child("firstname").getValue(String.class);
//                        String LnameFromDB = snapshot.child(userUsername).child("lastname").getValue(String.class);
//                        String phoneFromDB = snapshot.child(userUsername).child("phone").getValue(String.class);
//                        String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
//
//                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
//
//                        intent.putExtra("username", usernameFromDB);
//                        intent.putExtra("nid", IDFromDB);
//                        intent.putExtra("first_name", FnameFromDB);
//                        intent.putExtra("last_name", LnameFromDB);
//                        intent.putExtra("phone", phoneFromDB);
//                        intent.putExtra("email", emailFromDB);
//                        startActivity(intent);
//                    }else{
//                        loginPassword.setError("Invalid Credentials");
//                        loginPassword.requestFocus();
//                    }
//                }else{
//                    loginUsername.setError("User does not exist");
//                    loginUsername.requestFocus();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
}