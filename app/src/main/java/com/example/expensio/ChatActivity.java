package com.example.expensio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.Manifest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    EditText entermsg;
    Button sendbutton;
    ImageView attach;
    String currentUser;
    DatabaseReference chatDatabase;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<Message> messageList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        // Create a notification channel
        createNotificationChannel();

        currentUser = getIntent().getStringExtra("loggeduserchat");
        entermsg = findViewById(R.id.edit_text_message1);
        sendbutton = findViewById(R.id.btn_send1);
        attach = findViewById(R.id.btn_media1);
        recyclerView = findViewById(R.id.recycler_view_chat);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Initialize Firebase Database reference
        chatDatabase = FirebaseDatabase.getInstance().getReference("chat");

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        chatDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.smoothScrollToPosition(messageList.size() - 1);
                    sendNotification(message.getSender(), message.getMessage());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle message updates if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle message removal if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle message moves if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors if needed
            }
        });

        // Request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Chat Notifications";
            String description = "Notifications for new chat messages";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHAT_CHANNEL", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendMessage() {
        String messageText = entermsg.getText().toString().trim();
        if (!messageText.isEmpty()) {
            String messageId = chatDatabase.push().getKey();
            if (messageId != null) {
                long timestamp = System.currentTimeMillis(); // Get current timestamp
                Message message = new Message(currentUser, messageText, timestamp);
                chatDatabase.child(messageId).setValue(message)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                entermsg.setText(""); // Clear the message input field
                            }
                        });
            }
        }
    }

    private void sendNotification(String sender, String messageText) {
        // Create and display the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHAT_CHANNEL")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentTitle(sender)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Consider requesting the permission here if it is not granted
            return;
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
