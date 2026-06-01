package com.example.projet_android.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.dto.Message;
import com.example.projet_android.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;
    private Long currentUserId;
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public MessageAdapter(List<Message> messages, Long currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
        notifyDataSetChanged();
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = messages.get(position);
        if (msg == null) return;

        boolean isMe = false;
        if (currentUserId != null && currentUserId != -1L) {
            isMe = (msg.getId_expediteur() == currentUserId.intValue());
        }

        holder.tvMessageContent.setText(msg.getContenu());

        if (msg.getDate_envoi() != null) {
            try {
                holder.tvMessageTime.setText(timeFormatter.format(new Date(msg.getDate_envoi())));
            } catch (Exception e) {
                holder.tvMessageTime.setText("--:--");
            }
        }

        holder.tvUrgentTag.setVisibility(msg.getIs_urgent() == 1 ? View.VISIBLE : View.GONE);

        if (isMe) {
            holder.layoutMessageContainer.setGravity(Gravity.END);
            holder.bubbleMessage.setBackgroundResource(R.drawable.bg_bubble_right);
            holder.tvMessageContent.setTextColor(Color.WHITE);
            holder.tvMessageTime.setTextColor(Color.parseColor("#E3F2FD"));
            holder.tvMessageTime.setGravity(Gravity.END);
        } else {
            holder.layoutMessageContainer.setGravity(Gravity.START);
            holder.bubbleMessage.setBackgroundResource(R.drawable.bg_bubble_white);
            holder.tvMessageContent.setTextColor(Color.BLACK);
            holder.tvMessageTime.setTextColor(Color.parseColor("#999999"));
            holder.tvMessageTime.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return (messages != null) ? messages.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutMessageContainer, bubbleMessage;
        TextView tvMessageContent, tvMessageTime, tvUrgentTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutMessageContainer = itemView.findViewById(R.id.layoutMessageContainer);
            bubbleMessage = itemView.findViewById(R.id.bubbleMessage);
            tvMessageContent = itemView.findViewById(R.id.tvMessageContent);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
            tvUrgentTag = itemView.findViewById(R.id.tvUrgentTag);
        }
    }
}