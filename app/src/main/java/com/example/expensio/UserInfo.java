package com.example.expensio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfo extends AppCompatActivity {
    private TextView amountValue, pendingValue;
    private TextView amount;
    TextView nameid;
    TextView statustext,usernametext;
    CircleImageView profileImage;
    Button advanceSettings;
    String userName;
    String loggedUser;
    String openedUser;
    int imageResource;
    private DatabaseReference databaseReference;
    private DatabaseReference dr;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ImageView editAmount = findViewById(R.id.editAmount);
        ImageView editPending = findViewById(R.id.editPending);
        loggedUser=getIntent().getStringExtra("loggeduser1");
        userName = getIntent().getStringExtra("name");
       dr = FirebaseDatabase.getInstance().getReference("login").child(userName.toLowerCase()).child("username");
       dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    openedUser=String.valueOf(snapshot.getValue());
                    if(openedUser.equals(loggedUser)){
                        editAmount.setVisibility(View.VISIBLE);
                        editPending.setVisibility(View.VISIBLE);
                    }
                    else{
                        editAmount.setVisibility(View.INVISIBLE);
                        editPending.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       //cuser.setText(openedUser);
        advanceSettings=findViewById(R.id.advancesettings);
        if (loggedUser.equals("venu123")){
            advanceSettings.setVisibility(View.VISIBLE);
        }
        else{
            advanceSettings.setVisibility(View.INVISIBLE);
        }
        imageResource=getIntent().getIntExtra("image",1);
        nameid=findViewById(R.id.nameid);
        nameid.setText(userName);
        statustext=findViewById(R.id.status);
        usernametext=findViewById(R.id.username);
        profileImage=findViewById(R.id.userProfile);
        profileImage.setImageResource(imageResource);
        amountValue = findViewById(R.id.amountValue);
        pendingValue = findViewById(R.id.pendingValue);


        editAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDialog("amount");
            }
        });

        editPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDialog("pending");
            }
        });
       advanceSettings.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i=new Intent(UserInfo.this,Settings.class);
               startActivity(i);
           }
       });
        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("room").child(userName);

        // Load initial data
        loadData();
    }

    private void loadData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String amount = "";
                    String pending = "";
                    String status="";
                    String username="";

                    // Check if 'amount' exists and determine its type
                    if (snapshot.child("amount").exists()) {
                        Object amountObj = snapshot.child("amount").getValue();
                        if (amountObj instanceof Long) {
                            amount = String.valueOf(amountObj);
                        } else if (amountObj instanceof String) {
                            amount = (String) amountObj;
                        }
                    }

                    // Check if 'pending' exists and determine its type
                    if (snapshot.child("pending").exists()) {
                        Object pendingObj = snapshot.child("pending").getValue();
                        if (pendingObj instanceof Long) {
                            pending = String.valueOf(pendingObj);
                        } else if (pendingObj instanceof String) {
                            pending = (String) pendingObj;
                        }
                    }

                    if (snapshot.child("status").exists()) {
                        Object statusOnj = snapshot.child("status").getValue();
                        if (statusOnj instanceof Long) {
                            status = String.valueOf(statusOnj);
                        } else if (statusOnj instanceof String) {
                            status = (String) statusOnj;
                        }
                    }

                    if (snapshot.child("username").exists()) {
                        Object usernameObj = snapshot.child("username").getValue();
                        if (usernameObj instanceof Long) {
                            username = String.valueOf(usernameObj);
                        } else if (usernameObj instanceof String) {
                            username = (String) usernameObj;
                        }
                    }
                    statustext.setText(status);
                    usernametext.setText(username);
                    amountValue.setText(amount);
                    pendingValue.setText(pending);
                    int amountInt = Integer.parseInt(amount);
                    int pendingInt = Integer.parseInt(pending);
                    setColor(amountValue, amountInt);
                    setColor(pendingValue, pendingInt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }


    private void openEditDialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_amount, null);
        builder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.editTextAmount);

        if (type.equals("amount")) {
            editText.setText(amountValue.getText().toString());
        } else {
            editText.setText(pendingValue.getText().toString());
        }

        builder.setTitle("Edit" + type)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newValue = editText.getText().toString();
                        databaseReference.child(type).setValue(newValue);

                        if (type.equals("amount")) {
                            amountValue.setText(newValue);
                            setColor(amountValue,Integer.parseInt(newValue));
                        } else {
                            pendingValue.setText(newValue);
                            int pendingInt = Integer.parseInt(newValue);
                            setColor(pendingValue, pendingInt);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void setColor(TextView tag,int val) {
        if (val >= 0) {
            tag.setTextColor(Color.parseColor("#00FF00"));
        } else if (val < 0) {
            tag.setTextColor(Color.parseColor("#FF0000"));
        }
    }
}
