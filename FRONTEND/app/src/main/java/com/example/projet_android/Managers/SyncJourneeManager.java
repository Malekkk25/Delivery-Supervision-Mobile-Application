package com.example.projet_android.Managers;

import static com.example.projet_android.Managers.SQLiteHelper.*;
import static com.example.projet_android.Managers.SQLiteHelper.KEY_SYNC_STATUS;
import static com.example.projet_android.Managers.SQLiteHelper.TABLE_LIVRAISONS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.projet_android.dto.Livraison;
import com.example.projet_android.service.ApiService;
import com.example.projet_android.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncJourneeManager {

    public static void chargerDepuisServeur(Context context, LivraisonHandler db, String login, String mdp, Runnable onSuccess) {
        ApiService api = RetrofitClient.getApiService();

        api.getMaTournee(login, mdp).enqueue(new Callback<List<Livraison>>() {
            @Override
            public void onResponse(Call<List<Livraison>> call, Response<List<Livraison>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    db.supprimerLivraisons();

                    for (Livraison liv : response.body()) {
                        db.ajouterLivraison(liv);
                    }

                    if (onSuccess != null) onSuccess.run();
                    Toast.makeText(context, "Tournée chargée !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Réponse serveur vide", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Livraison>> call, Throwable t) {
                Toast.makeText(context, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void syncVersServeur(Context context, String login, String password) {
        LivraisonHandler db = new LivraisonHandler(context);
        List<Livraison> list = db.getLivraisonsASync();

        Log.d("Sync", "Nombre à sync: " + list.size());

        if (list.isEmpty()) {
            Toast.makeText(context, "Rien à synchroniser", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Livraison liv : list) {
            Log.d("Sync", "Envoi commande #" + liv.getId() + " etat=" + liv.getEtat());

            RetrofitClient.getApiService()
                    .modifierEtat(login, password, liv.getId(), liv.getEtat())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d("Sync", "Réponse code: " + response.code());
                            Log.d("Sync", "Réponse body: " + response.body());
                            if (response.isSuccessful()) {
                                db.marquerSynchronise(liv.getId());
                                Log.d("Sync", "✅ Synchronisé #" + liv.getId());
                            } else {
                                Log.e("Sync", "❌ Erreur serveur code: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("Sync", "❌ Échec réseau : " + t.getMessage());
                        }
                    });
        }
    }

}