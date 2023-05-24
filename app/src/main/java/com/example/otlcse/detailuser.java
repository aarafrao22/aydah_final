package com.example.otlcse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class detailuser extends AppCompatActivity {

    TextView profilename, profilelastname, profileemail, profileusername, profilephone, profileID;
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, profile, contactus, logout;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailuser);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        profile = findViewById(R.id.profile);
        contactus = findViewById(R.id.contactus);
        logout = findViewById(R.id.logout);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        firebaseDatabase = FirebaseDatabase.getInstance();

        profilename = findViewById(R.id.profilename);
        profilelastname = findViewById(R.id.laname);
        profileID = findViewById(R.id.userIDD);
        profileusername = findViewById(R.id.username6);
        profileemail = findViewById(R.id.useremail);
        profilephone = findViewById(R.id.userphone);
        showUserData();


    }

    public void showUserData() {
        Intent intent = getIntent();

        String userid = getIntent().getExtras().getString("userid");

//        HelperClass helperClass = new HelperClass(first_name,last_name,phone,nid, email, username, MD5String);
        databaseReference = firebaseDatabase.getReference("users").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                System.out.println(snapshot);
                HelperClass value = snapshot.getValue(HelperClass.class);


                profilename.setText(value.firstname);
                profilelastname.setText(value.lastname);
                profileID.setText(value.nid);
                profileusername.setText(value.username);
                profilephone.setText(value.phone);
                profileemail.setText(value.email);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(detailuser.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });


    }
}