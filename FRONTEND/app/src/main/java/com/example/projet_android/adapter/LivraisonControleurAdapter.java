package com.example.projet_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.dto.LivraisonControleur;
import com.example.projet_android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LivraisonControleurAdapter extends RecyclerView.Adapter<LivraisonControleurAdapter.ViewHolder> implements Filterable {

    private final List<LivraisonControleur> originalList;
    private final List<LivraisonControleur> filteredList;
    private String etatFiltre = "";

    public LivraisonControleurAdapter(List<LivraisonControleur> list) {
        this.originalList = new ArrayList<>(list);
        this.filteredList = new ArrayList<>(list);
    }

    public void setEtatFiltre(String etatFiltre) {
        this.etatFiltre = etatFiltre == null ? "" : etatFiltre.trim();
    }
    public void updateData(List<LivraisonControleur> newList) {
        originalList.clear();
        originalList.addAll(newList);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommandeClient, tvEtat, tvLieuDate, tvNomLivreur, tvMontant;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommandeClient = itemView.findViewById(R.id.tvCommandeClient);
            tvEtat = itemView.findViewById(R.id.tvEtat);
            tvLieuDate = itemView.findViewById(R.id.tvLieuDate);
            tvNomLivreur = itemView.findViewById(R.id.tvNomLivreur);
            tvMontant = itemView.findViewById(R.id.tvMontant);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_livraison_controleur, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LivraisonControleur item = filteredList.get(position);

        holder.tvCommandeClient.setText("#" + item.getNocde() + " - " + item.getClient());

        String etatAffiche;
        if ("LI".equalsIgnoreCase(item.getEtat())) {
            etatAffiche = "Livrée";
        } else if ("EC".equalsIgnoreCase(item.getEtat())) {
            etatAffiche = "En cours";
        } else if ("AL".equalsIgnoreCase(item.getEtat())) {
            etatAffiche = "Annulée";
        } else {
            etatAffiche = item.getEtat();
        }
        holder.tvEtat.setText(etatAffiche);

        holder.tvLieuDate.setText(item.getDate());
        holder.tvNomLivreur.setText(item.getLivreur());
        holder.tvMontant.setText(String.format(Locale.FRANCE, "%.2f DT", item.getMontant()));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint == null ? "" : constraint.toString().toLowerCase(Locale.ROOT).trim();
                List<LivraisonControleur> temp = new ArrayList<>();

                for (LivraisonControleur item : originalList) {
                    boolean matchText =
                            query.isEmpty()
                                    || String.valueOf(item.getNocde()).contains(query)
                                    || (item.getClient() != null && item.getClient().toLowerCase(Locale.ROOT).contains(query))
                                    || (item.getLivreur() != null && item.getLivreur().toLowerCase(Locale.ROOT).contains(query))
                                    || (item.getEtat() != null && item.getEtat().toLowerCase(Locale.ROOT).contains(query))
                                    || (item.getDate() != null && item.getDate().toLowerCase(Locale.ROOT).contains(query));

                    boolean matchEtat = etatFiltre.isEmpty()
                            || (item.getEtat() != null && item.getEtat().equalsIgnoreCase(etatFiltre))
                            || ("Livrée".equalsIgnoreCase(etatFiltre) && "LI".equalsIgnoreCase(item.getEtat()))
                            || ("En cours".equalsIgnoreCase(etatFiltre) && "EC".equalsIgnoreCase(item.getEtat()))
                            || ("Annulée".equalsIgnoreCase(etatFiltre) && "AL".equalsIgnoreCase(item.getEtat()));

                    if (matchText && matchEtat) {
                        temp.add(item);
                    }
                }

                FilterResults results = new FilterResults();
                results.values = temp;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                if (results != null && results.values != null) { // ✅ null check
                    filteredList.addAll((List<LivraisonControleur>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }

}