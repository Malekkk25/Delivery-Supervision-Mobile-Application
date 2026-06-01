package com.example.projet_android.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.projet_android.dto.Personnel;
import com.example.projet_android.R;

import java.util.ArrayList;
import java.util.List;


public class LivreurBulleAdapter extends RecyclerView.Adapter<LivreurBulleAdapter.ViewHolder> {

    public interface OnLivreurClickListener {
        void onLivreurClick(Personnel livreur);
    }

    private List<Personnel> livreurs;
    private OnLivreurClickListener listener;
    private int selectedPosition = -1;

    private String[] couleurs = {"#4D88FF", "#4CAF50", "#FF9800", "#9C27B0", "#F44336", "#009688"};


    public LivreurBulleAdapter(List<Personnel> livreurs, OnLivreurClickListener listener) {
        this.livreurs = livreurs != null ? livreurs : new ArrayList<>();
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
        Personnel livreur = livreurs.get(position);

        String nom = livreur.getNom() != null ? livreur.getNom() : "";
        String prenom = livreur.getPrenom() != null ? livreur.getPrenom() : "";

        holder.tvBulleNom.setText(prenom + " " + (!nom.isEmpty() ? nom.substring(0, 1) + "." : ""));

        String initiale = "";
        if (!prenom.isEmpty()) initiale += prenom.substring(0, 1).toUpperCase();
        if (!nom.isEmpty()) initiale += nom.substring(0, 1).toUpperCase();
        holder.tvBulleInitiale.setText(initiale);

        String colorHex = couleurs[position % couleurs.length];
        GradientDrawable drawable = (GradientDrawable) holder.tvBulleInitiale.getBackground().mutate();
        drawable.setColor(Color.parseColor(colorHex));

        if (selectedPosition == position) {
            holder.itemView.setAlpha(1.0f);
            holder.tvBulleNom.setTextColor(Color.parseColor("#333333"));
            holder.tvBulleNom.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            holder.itemView.setAlpha(0.5f);
            holder.tvBulleNom.setTextColor(Color.parseColor("#666666"));
            holder.tvBulleNom.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onLivreurClick(livreur);
            }
        });
    }
    @Override
    public int getItemCount() {
        return livreurs != null ? livreurs.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBulleInitiale, tvBulleNom;
        View statusDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBulleInitiale = itemView.findViewById(R.id.tvBulleInitiale);
            tvBulleNom = itemView.findViewById(R.id.tvBulleNom);
            statusDot = itemView.findViewById(R.id.statusDot);
        }
    }
}