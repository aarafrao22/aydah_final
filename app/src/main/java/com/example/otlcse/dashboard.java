package com.example.otlcse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class dashboard extends AppCompatActivity {
    CardView viewuser, adduser, logout, googlemap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        viewuser = findViewById(R.id.viewuser);
        adduser = findViewById(R.id.useradd);
        logout = findViewById(R.id.logout);
//        googlemap = findViewById(R.id.googlemap);

        viewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboard.this, viewuser.class);
                startActivity(intent);
            }
        });

        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboard.this, adduser.class);
                startActivity(intent);
            }
        });

//        googlemap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(dashboard.this, googlemap.class);
//                startActivity(intent);
//            }
//        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);
                sharedPref.edit().remove("userid").commit();
                Intent intent = new Intent(dashboard.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}