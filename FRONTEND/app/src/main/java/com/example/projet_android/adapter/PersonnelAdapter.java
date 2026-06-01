package com.example.projet_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.R;
import com.example.projet_android.Views.ChatActivity;

import java.util.List;
import java.util.Map;

public class PersonnelAdapter extends RecyclerView.Adapter<PersonnelAdapter.ViewHolder> {

    private List<Map<String, Object>> personnelList;
    private final Context context;
    private final String login;
    private final String password;
    private final Long currentUserId;
    private final OnPersonnelClickListener listener;

    public interface OnPersonnelClickListener {
        void onPersonnelClick(Map<String, Object> personnel);
    }

    public PersonnelAdapter(List<Map<String, Object>> personnelList, Context context, String login, String password, Long currentUserId) {
        this(personnelList, context, login, password, currentUserId, null);
    }

    public PersonnelAdapter(List<Map<String, Object>> personnelList, Context context, String login, String password, Long currentUserId, OnPersonnelClickListener listener) {
        this.personnelList = personnelList;
        this.context = context;
        this.login = login;
        this.password = password;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_livreur_bulle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> personnel = personnelList.get(position);
        if (personnel == null) return;

        String nomComplet = (String) personnel.get("nomComplet");

        holder.tvBulleNom.setText(nomComplet != null ? nomComplet : "Nom inconnu");

        if (nomComplet != null && !nomComplet.isEmpty()) {
            holder.tvBulleInitiale.setText(String.valueOf(nomComplet.charAt(0)).toUpperCase());
        } else {
            holder.tvBulleInitiale.setText("?");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPersonnelClick(personnel);
            } else {
                Long idLivreur = ((Number) personnel.get("id")).longValue();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("login", login);
                intent.putExtra("password", password);
                intent.putExtra("current_id", currentUserId);
                intent.putExtra("id_controleur", idLivreur);
                intent.putExtra("nom_controleur", nomComplet);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (personnelList != null) ? personnelList.size() : 0;
    }

    public void updateList(List<Map<String, Object>> newList) {
        this.personnelList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvBulleNom, tvBulleInitiale;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBulleNom = itemView.findViewById(R.id.tvBulleNom);
            tvBulleInitiale = itemView.findViewById(R.id.tvBulleInitiale);
        }
    }
}