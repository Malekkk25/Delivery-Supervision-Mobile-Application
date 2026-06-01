package com.example.projet_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.dto.Livraison;
import com.example.projet_android.R;
import com.example.projet_android.Views.DetailsLivraisonActivity;

import java.util.List;

public class LivraisonAdapter extends RecyclerView.Adapter<LivraisonAdapter.ViewHolder> {

    private List<Livraison> livraisons;
    private Context context;
    private String login, password;

    public LivraisonAdapter(List<Livraison> livraisons, Context context, String login, String password) {
        this.livraisons = livraisons;
        this.context = context;
        this.login = login;
        this.password = password;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livraison, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Livraison liv = livraisons.get(position);

        holder.tvOrderNumber.setText(String.valueOf(position + 1));
        holder.tvClientName.setText(liv.getNomClient());
        holder.tvDeliveryDetails.setText("#" + liv.getId() + " • " + liv.getAdresse());

        
        afficherStatut(holder.tvStatus, liv.getEtat());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsLivraisonActivity.class);
                intent.putExtra("nocde", liv.getId());
                intent.putExtra("login", login);
                intent.putExtra("password", password);
                context.startActivity(intent);
            }
        });

        holder.btnCallClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + liv.getTelephone()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return livraisons.size();
    }

    private void afficherStatut(TextView tvStatus, String etat) {
        if (etat == null) {
            tvStatus.setText("INCONNU");
            tvStatus.setTextColor(Color.GRAY);
            return;
        }

        switch (etat) {
            case "LI":
                tvStatus.setText("LIVRÉE");
                tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "AL":
                tvStatus.setText("ANNULÉE");
                tvStatus.setTextColor(Color.RED);
                break;
            default:
                tvStatus.setText("EN COURS");
                tvStatus.setTextColor(Color.parseColor("#EF6C00"));
                break;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvClientName, tvDeliveryDetails, tvStatus;
        ImageButton btnCallClient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvDeliveryDetails = itemView.findViewById(R.id.tvDeliveryDetails);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCallClient = itemView.findViewById(R.id.btnCallClient);
        }
    }
}