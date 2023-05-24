package com.example.otlcse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    EditText signupFName, signupLName, signupPhone, signupNID, signupEmail, signupUsername, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    String email_regex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}";
    String letter_regix = "^[a-zA-Z]+$";
    String phone_regix = "^\\d{11}$";
    String number_regix = "^[0-9]+$";
    String password_regix = "^[a-zA-Z0-9]+$";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupFName = findViewById(R.id.signup_firstname);
        signupLName = findViewById(R.id.signup_lastname);
        signupPhone = findViewById(R.id.signup_phone);
        signupNID = findViewById(R.id.signup_nid);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");
                firebaseAuth = FirebaseAuth.getInstance();

                String first_name = signupFName.getText().toString();
                String last_name = signupLName.getText().toString();
                String phone = signupPhone.getText().toString();
                String nid = signupNID.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();
                int wrco = 1;

                if (nid.isEmpty()) {
                    signupNID.setError("National ID cannot be empty");
                }
                if (username.isEmpty()) {
                    signupUsername.setError("Username cannot be empty");
                } else if (username.length() < 3) {
                    signupUsername.setError("Username must be 3 characters or more!");
                    wrco = 2;
                }
                if (first_name.isEmpty()) {
                    signupFName.setError("First Name cannot be empty");
                }
                if (last_name.isEmpty()) {
                    signupLName.setError("Last Name cannot be empty");
                }
                if (email.isEmpty()) {
                    signupEmail.setError("Email cannot be empty");
                }
                if (phone.isEmpty()) {
                    signupPhone.setError("Phone number cannot be empty");
                }
                if (password.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                } else if (password.length() < 5) {
                    signupPassword.setError("Password should be more than 6 characters/numbers");
                    wrco = 2;
                }

                Boolean pattern_status = true;
                Pattern email_pattern = Pattern.compile(email_regex);
                Matcher email_matcher = email_pattern.matcher(email.toString());
                if (!email_matcher.matches()) {
                    signupEmail.setError("Email should be correct format");
                    pattern_status = false;
                }

                Pattern fname_pattern = Pattern.compile(letter_regix);
                Matcher fname_matcher = fname_pattern.matcher(first_name.toString());

                if (!fname_matcher.matches()) {
                    signupFName.setError("First Name Should be letter format");
                    pattern_status = false;
                }

                Pattern lname_pattern = Pattern.compile(letter_regix);
                Matcher lname_matcher = lname_pattern.matcher(last_name.toString());


                if (!lname_matcher.matches()) {
                    signupLName.setError("Last Name Should be letter format");
                    pattern_status = false;
                }

                Pattern uname_pattern = Pattern.compile(letter_regix);
                Matcher uname_matcher = uname_pattern.matcher(username.toString());


                if (!uname_matcher.matches()) {
                    signupUsername.setError("User Name Should be letter format");
                    pattern_status = false;
                }

                Pattern phone_pattern = Pattern.compile(phone_regix);
                Matcher phone_matcher = phone_pattern.matcher(phone.toString());


                if (!phone_matcher.matches()) {
                    signupPhone.setError("phoner Should be number and atleast 11 digit format");
                    pattern_status = false;
                }


                Pattern nid_pattern = Pattern.compile(number_regix);
                Matcher nid_matcher = nid_pattern.matcher(nid.toString());


                if (!nid_matcher.matches()) {
                    signupNID.setError("National ID Should be number");
                    pattern_status = false;
                }

                Pattern password_pattern = Pattern.compile(password_regix);
                Matcher password_matcher = password_pattern.matcher(password.toString());


                if (!password_matcher.matches()) {
                    signupPassword.setError("Password Should be letter or  number");
                    pattern_status = false;
                }


                if (!username.isEmpty() & !first_name.isEmpty() & !password.isEmpty() & !email.isEmpty() & !last_name.isEmpty() & !phone.isEmpty() & !nid.isEmpty() & wrco == 1 & pattern_status) {


                    //  MD5 password encryption
                    password = md5(password);

                    String MD5String = password;

                    //    Firebase Authenication
                    firebaseAuth
                            .createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    HelperClass helperClass = new HelperClass(first_name, last_name, phone, nid, email, username, MD5String, user.getUid());
                                    reference.child(user.getUid()).setValue(helperClass);
                                    Toast.makeText(SignupActivity.this, "You have signup successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {

                                    // Registration failed
                                    Toast.makeText(SignupActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                                }
                            });


                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

//    public  void updateUI(FirebaseUser user){
//        return;
//    }
}