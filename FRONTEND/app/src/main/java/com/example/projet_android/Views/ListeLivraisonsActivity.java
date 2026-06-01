package com.example.projet_android.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.Managers.LivraisonHandler;
import com.example.projet_android.Managers.SyncJourneeManager;

import com.example.projet_android.R;
import com.example.projet_android.adapter.LivraisonAdapter;
import com.example.projet_android.dto.Livraison;

import java.util.List;

public class ListeLivraisonsActivity extends AppCompatActivity {

    private LivraisonHandler dbHandler;
    private String login, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_livraisons_livreur);

        dbHandler = new LivraisonHandler(this);
        login = getIntent().getStringExtra("login");
        password = getIntent().getStringExtra("password");

        afficherLaListe();

        ImageButton btnSync = findViewById(R.id.btnSync);
        if (btnSync != null) {
            btnSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SyncJourneeManager syncManager = new SyncJourneeManager();
                    syncManager.syncVersServeur(ListeLivraisonsActivity.this, login, password);
                    Toast.makeText(ListeLivraisonsActivity.this, "Synchronisation lancée...", Toast.LENGTH_SHORT).show();
                }
            });
        }
        setupNavigation();
    }

    private void afficherLaListe() {

        List<Livraison> listeLocale = dbHandler.getAllLivraisons();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewLivraisons);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LivraisonAdapter adapter = new LivraisonAdapter(listeLocale, this, login, password);
        recyclerView.setAdapter(adapter);

        TextView tvCount = findViewById(R.id.tvDateEtCount);
        if (tvCount != null) {
            tvCount.setText("Aujourd'hui • " + dbHandler.countLivraisonsAujourdhui() + " commandes");
        }

        if (listeLocale.isEmpty()) {
            Toast.makeText(this, "Aucune donnée locale trouvée", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        afficherLaListe();
    }

    private void setupNavigation() {
        ImageView menu_liste = findViewById(R.id.menu_liste);
        ImageView menu_messages = findViewById(R.id.menu_messages);

        menu_liste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afficherLaListe();
                Toast.makeText(ListeLivraisonsActivity.this, "Rafraîchissement...", Toast.LENGTH_SHORT).show();
            }
        });

        menu_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListeLivraisonsActivity.this, ChatActivity.class);
                i.putExtra("login", login);
                i.putExtra("password", password);
                startActivity(i);
            }
        });

        findViewById(R.id.menu_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ListeLivraisonsActivity.this)
                        .setTitle("Déconnexion")
                        .setMessage("Voulez-vous vraiment quitter l'application ? (Vos données de session seront effacées)")
                        .setPositiveButton("Oui, quitter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                                prefs.edit().clear().apply();
                                getApplicationContext().deleteDatabase("LivraisonDB.db");
                                Intent intent = new Intent(ListeLivraisonsActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
            }
        });
    }
}