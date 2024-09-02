package com.example.expensio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<Message> messageList;
    private String currentUser;

    public MessageAdapter(List<Message> messageList, String currentUser) {
        this.messageList = messageList;
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSender().equals(currentUser)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        CircleImageView avatarImageView;
        TextView timeTextView;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_body);
            avatarImageView = itemView.findViewById(R.id.avatar);
            timeTextView = itemView.findViewById(R.id.text_message_time_sent);
        }

        void bind(Message message) {
            messageTextView.setText(message.getMessage());
            setAvatar(avatarImageView, message.getSender());
            setTime(timeTextView, message.getTimestamp());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView senderTextView;
        CircleImageView avatarImageView;
        TextView timeTextView;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_message_body_received);
            senderTextView = itemView.findViewById(R.id.text_message_sender_received);
            avatarImageView = itemView.findViewById(R.id.avatar);
            timeTextView = itemView.findViewById(R.id.text_message_time_received);
        }

        void bind(Message message) {
            messageTextView.setText(message.getMessage());
            senderTextView.setText(message.getSender());
            setAvatar(avatarImageView, message.getSender());
            setTime(timeTextView, message.getTimestamp());
        }
    }

    private static void setAvatar(CircleImageView avatarImageView, String sender) {
        int avatarResource;
        switch (sender) {
            case "ganesh123":
                avatarResource = R.drawable.gani;
                break;
            case "naveed123":
                avatarResource = R.drawable.naveed;
                break;
            case "chary123":
                avatarResource = R.drawable.chary;
                break;
            case "arun123":
                avatarResource = R.drawable.arun;
                break;
            case "venu123":
                avatarResource = R.drawable.venu_img;
                break;
            default:
                avatarResource = R.drawable.gani; // Set a default avatar if needed
                break;
        }
        avatarImageView.setImageResource(avatarResource);
    }

    private static void setTime(TextView timeTextView, long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String time = dateFormat.format(timestamp);
        timeTextView.setText(time);
    }
}
