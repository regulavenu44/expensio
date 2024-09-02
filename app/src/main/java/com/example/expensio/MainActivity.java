package com.example.expensio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private CircleImageView arun_img, chary_img, ganesh_img, naveed_img, venu_img;
    private TextView arun_name, chary_name, ganesh_name, naveed_name, venu_name;
    private TextView arun_pending, chary_pending, ganesh_pending, naveed_pending, venu_pending;
    private TextView arun_amount, chary_amount, ganesh_amount, naveed_amount, venu_amount;
    private String loggedUser;
    private DatabaseReference databaseReference;
    private LinearLayout fabMenu;
    private boolean isFabMenuOpen = false;
    private static final int REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loggedUser = getIntent().getStringExtra("loggeduser");

        // Initialize views
        initializeViews();

        // Set the day's name
        setDayName();

        // Set click listeners
        setOnClickListeners();

        // Show FAB menu
        setupFabMenu();

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("room");
        setValues();

        // Schedule alarms
    }

    private void initializeViews() {
        arun_img = findViewById(R.id.profileImage1);
        chary_img = findViewById(R.id.profileImage2);
        ganesh_img = findViewById(R.id.profileImage3);
        naveed_img = findViewById(R.id.profileImage4);
        venu_img = findViewById(R.id.profileImage5);

        arun_name = findViewById(R.id.profileName1);
        chary_name = findViewById(R.id.profileName2);
        ganesh_name = findViewById(R.id.profileName3);
        naveed_name = findViewById(R.id.profileName4);
        venu_name = findViewById(R.id.profileName5);

        arun_pending = findViewById(R.id.profilepending1);
        chary_pending = findViewById(R.id.profilepending2);
        ganesh_pending = findViewById(R.id.profilepending3);
        naveed_pending = findViewById(R.id.profilepending4);
        venu_pending = findViewById(R.id.profilepending5);

        arun_amount = findViewById(R.id.profileAmount1);
        chary_amount = findViewById(R.id.profileAmount2);
        ganesh_amount = findViewById(R.id.profileAmount3);
        naveed_amount = findViewById(R.id.profileAmount4);
        venu_amount = findViewById(R.id.profileAmount5);

        LinearLayout arunScreen = findViewById(R.id.profileItem1);
        LinearLayout charyScreen = findViewById(R.id.profileItem2);
        LinearLayout ganeshScreen = findViewById(R.id.profileItem3);
        LinearLayout naveedScreen = findViewById(R.id.profileItem4);
        LinearLayout venuScreen = findViewById(R.id.profileItem5);

        fabMenu = findViewById(R.id.fab_menu);
        fabMenu.setVisibility(View.GONE);
    }

    private void setDayName() {
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        DayOfWeek dayOfWeek = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dayOfWeek = currentDate.getDayOfWeek();
        }
        String dayName = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault());
        }
        setDayName(dayName);
    }

    private void setOnClickListeners() {
        findViewById(R.id.profileItem1).setOnClickListener(v -> openUserInfo(arun_name, arun_pending, arun_amount, R.drawable.arun));
        findViewById(R.id.profileItem2).setOnClickListener(v -> openUserInfo(chary_name, chary_pending, chary_amount, R.drawable.chary));
        findViewById(R.id.profileItem3).setOnClickListener(v -> openUserInfo(ganesh_name, ganesh_pending, ganesh_amount, R.drawable.gani));
        findViewById(R.id.profileItem4).setOnClickListener(v -> openUserInfo(naveed_name, naveed_pending, naveed_amount, R.drawable.naveed));
        findViewById(R.id.profileItem5).setOnClickListener(v -> openUserInfo(venu_name, venu_pending, venu_amount, R.drawable.venu_img));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (isFabMenuOpen) {
                fabMenu.setVisibility(View.GONE);
            } else {
                fabMenu.setVisibility(View.VISIBLE);
            }
            isFabMenuOpen = !isFabMenuOpen;
        });

        findViewById(R.id.option2).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, TimeTable.class)));
       // findViewById(R.id.option).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, TimeTable.class)));


    }

    private void openUserInfo(TextView name, TextView pending, TextView amount, int imageResource) {
        Intent intent = new Intent(MainActivity.this, UserInfo.class);
        intent.putExtra("name", name.getText());
        intent.putExtra("pending", pending.getText());
        intent.putExtra("amount", amount.getText());
        intent.putExtra("image", imageResource);
        intent.putExtra("loggeduser1", loggedUser);
        startActivity(intent);
    }

    private void setupFabMenu() {
        FloatingActionButton option1 = findViewById(R.id.option1);
        FloatingActionButton option3 = findViewById(R.id.option3);
        FloatingActionButton option4 = findViewById(R.id.option4);
        if(loggedUser.equals("venu123")){
            option4.setVisibility(View.VISIBLE);
        }
        else {
            option4.setVisibility(View.INVISIBLE);
        }
        option1.setOnClickListener(view -> {
           Intent i=new Intent(MainActivity.this,ChatActivity.class);
           i.putExtra("loggeduserchat",loggedUser);
           startActivity(i);
        });
        option3.setOnClickListener(view -> {
            // Handle Option 3 click
        });
        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   Intent roomintent=new Intent(MainActivity.this,RoomInfo.class);
                   startActivity(roomintent);
            }
        });
    }

    private void setValues() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    updateUserInfo(snapshot, "Arun", R.id.homeArunRoom, R.id.homeArunSettele, R.id.profileItem1);
                    updateUserInfo(snapshot, "Chary", R.id.homeCharyRoom, R.id.homeCharySettele, R.id.profileItem2);
                    updateUserInfo(snapshot, "Ganesh", R.id.homeGaneshRoom, R.id.homeGaneshSettele, R.id.profileItem3);
                    updateUserInfo(snapshot, "Naveed", R.id.homeNaveedRoom, R.id.homeNaveedSettele, R.id.profileItem4);
                    updateUserInfo(snapshot, "Venu", R.id.homeVenuRoom, R.id.homeVenuSettele, R.id.profileItem5);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", "Error: " + error.getMessage());
            }
        });
    }

    private void updateUserInfo(DataSnapshot snapshot, String user, int amountViewId, int pendingViewId, int profileItemId) {
        String amount = snapshot.child(user).child("amount").getValue(String.class);
        String pending = snapshot.child(user).child("pending").getValue(String.class);

        TextView amountTextView = findViewById(amountViewId);
        TextView pendingTextView = findViewById(pendingViewId);
        amountTextView.setText(amount);
        pendingTextView.setText(pending);

        setColor(amountTextView, Integer.parseInt(amount));
        setColor(pendingTextView, Integer.parseInt(pending));
    }

    private void setColor(TextView tag, int val) {
        if (val >= 0) {
            tag.setTextColor(Color.parseColor("#00FF00"));
        } else {
            tag.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    private void setDayName(String day) {
        int visibility = View.VISIBLE;
        switch (day) {
            case "Monday":
                findViewById(R.id.cooking1).setVisibility(visibility);
                break;
            case "Tuesday":
                findViewById(R.id.cooking2).setVisibility(visibility);
                break;
            case "Wednesday":
                findViewById(R.id.cooking1).setVisibility(visibility);
                findViewById(R.id.cooking5).setVisibility(visibility);
                break;
            case "Thursday":
                findViewById(R.id.cooking4).setVisibility(visibility);
                break;
            case "Friday":
                findViewById(R.id.cooking3).setVisibility(visibility);
                break;
            case "Saturday":
                findViewById(R.id.cooking5).setVisibility(visibility);
                break;
            case "Sunday":
                findViewById(R.id.cooking2).setVisibility(visibility);
                findViewById(R.id.cooking4).setVisibility(visibility);
                break;
            default:
                break;
        }
    }

}