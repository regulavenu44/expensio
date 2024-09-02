package com.example.expensio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Settings extends AppCompatActivity {
    Button clear, editStatus, editUsername;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        clear = findViewById(R.id.clearbutton);
        editStatus = findViewById(R.id.editStatus);
        editUsername = findViewById(R.id.editUsername);

        databaseReference = FirebaseDatabase.getInstance().getReference("room");

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllValues();
            }
        });

        editStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditStatusDialog();
            }
        });

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditUsernameDialog();
            }
        });
    }

    private void clearAllValues() {
        // Clear all values in Firebase
        databaseReference.child("Arun").child("amount").setValue("0");
        databaseReference.child("Arun").child("pending").setValue("0");
        databaseReference.child("Chary").child("amount").setValue("0");
        databaseReference.child("Chary").child("pending").setValue("0");
        databaseReference.child("Ganesh").child("amount").setValue("0");
        databaseReference.child("Ganesh").child("pending").setValue("0");
        databaseReference.child("Naveed").child("amount").setValue("0");
        databaseReference.child("Naveed").child("pending").setValue("0");
        databaseReference.child("Venu").child("amount").setValue("0");
        databaseReference.child("Venu").child("pending").setValue("0");
    }

    private void showEditStatusDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_status, null);
        dialogBuilder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        EditText statusEditText = dialogView.findViewById(R.id.statusEditText);
        Button submitButton = dialogView.findViewById(R.id.submitButton);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String status = statusEditText.getText().toString().trim();

                if (!name.isEmpty() && !status.isEmpty()) {
                    databaseReference.child(name).child("status").setValue(status);
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void showEditUsernameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_username, null);
        dialogBuilder.setView(dialogView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button submitButton = dialogView.findViewById(R.id.submitButton);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();

                if (!name.isEmpty() && !username.isEmpty()) {
                    databaseReference.child(name).child("username").setValue(username);
                    alertDialog.dismiss();
                }
            }
        });
    }
}
