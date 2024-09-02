package com.example.expensio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private boolean isLoggedIn;
    TextView logintext;
    EditText usernameText, mpinText;
    Button loginButton;
    String username, mpin;
    private String loggedUser;
    private DatabaseReference databaseReference;
    String[] roommates = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        sharedPref = getSharedPreferences("my_prefs", MODE_PRIVATE);
        isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);
        loggedUser = sharedPref.getString("loggedUser","noone");
        // Check if the user is already logged in
        if (isLoggedIn) {
            navigateToMainActivity(loggedUser);
            return; // Exit onCreate to prevent initializing the login page
        }

        roommates[0] = "arun";
        roommates[1] = "chary";
        roommates[2] = "ganesh";
        roommates[3] = "naveed";
        roommates[4] = "venu";

        logintext = findViewById(R.id.textViewLogin);
        usernameText = findViewById(R.id.editTextUsername);
        mpinText = findViewById(R.id.editTextMPIN);
        loginButton = findViewById(R.id.buttonLogin);
        databaseReference = FirebaseDatabase.getInstance().getReference("login");

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    username = usernameText.getText().toString().trim();
                    mpin = mpinText.getText().toString().trim();

                    if (username.isEmpty() || mpin.isEmpty()) {
                        Toast.makeText(LoginPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    boolean isValid = false;
                                    for (int i = 0; i < roommates.length; i++) {
                                        String dbusername = String.valueOf(snapshot.child(roommates[i]).child("username").getValue()).trim().toLowerCase();
                                        String dbmpin = String.valueOf(snapshot.child(roommates[i]).child("password").getValue()).trim();

                                        if (username.toLowerCase().equals(dbusername) && mpin.equals(dbmpin)) {
                                            isValid = true;
                                            isLoggedIn = true;
                                            loggedUser=username;
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putBoolean("isLoggedIn", isLoggedIn);
                                            editor.putString("loggedUser", loggedUser);
                                            editor.apply();
                                            navigateToMainActivity(loggedUser);
                                            break;
                                        }
                                    }
                                    if (!isValid) {
                                        Toast.makeText(LoginPage.this, "Invalid username or mpin", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginPage.this, "Invalid username or mpin", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(LoginPage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToMainActivity(String luser) {
        Intent intent = new Intent(LoginPage.this, MainActivity.class);
        intent.putExtra("loggeduser",luser);
        startActivity(intent);
        finish(); // Finish the login activity so that the user can't go back to it
    }
}
