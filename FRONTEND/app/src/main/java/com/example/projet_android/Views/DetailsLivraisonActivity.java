package com.example.projet_android.Views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_android.Managers.LivraisonHandler;
import com.example.projet_android.dto.Livraison;
import com.example.projet_android.R;


public class DetailsLivraisonActivity extends AppCompatActivity {

    private String login, password, telephoneClient;
    private int nocde;
    private LivraisonHandler db;

    private TextView tvTitleCommande, tvStatusBadge, tvNomClient, tvTelephone, tvAdresseExacte, tvModePayement, tvMontantTotal ,tbNbArticles;

    private Button btnOpenMaps, btnUpdateStatus, btnUrgence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_commandes);

        nocde = getIntent().getIntExtra("nocde", 0);
        login = getIntent().getStringExtra("login");
        password = getIntent().getStringExtra("password");

        db = new LivraisonHandler(this);

        initViews();
        setupNavigation();
        chargerDonneesLocales();
    }

    private void initViews() {
        tvTitleCommande = findViewById(R.id.tvTitleCommande);
        tvStatusBadge   = findViewById(R.id.tvStatusBadge);
        tvNomClient     = findViewById(R.id.tvNomClient);
        tvTelephone     = findViewById(R.id.tvTelephone);
        tvAdresseExacte = findViewById(R.id.tvAdresseExacte);
        tvModePayement  = findViewById(R.id.tvModePayement);
        tvMontantTotal  = findViewById(R.id.tvMontantTotal);
        tbNbArticles    =findViewById(R.id.tvNbArticles);
        btnOpenMaps     = findViewById(R.id.btnOpenMaps);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        btnUrgence      = findViewById(R.id.btnUrgence);
    }

    private void setupNavigation() {
        findViewById(R.id.menu_liste).setOnClickListener(v -> finish());
        findViewById(R.id.menu_messages).setOnClickListener(v -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("login", login);
            i.putExtra("password", password);
            startActivity(i);
            finish();
        });
        findViewById(R.id.menu_logout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Déconnexion")
                    .setMessage("Voulez-vous vraiment quitter l'application ? (Vos données de session seront effacées)")
                    .setPositiveButton("Oui, quitter", (dialog, which) -> {

                        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                        prefs.edit().clear().apply();

                        getApplicationContext().deleteDatabase("LivraisonDB.db");

                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    private void chargerDonneesLocales() {
        Livraison liv = db.getLivraisonById(nocde);
        if (liv != null) {
            tvTitleCommande.setText("Commande #" + liv.getId());
            tvNomClient.setText(liv.getNomClient());
            tvTelephone.setText(liv.getTelephone());
            tvAdresseExacte.setText(liv.getAdresse());
            tvModePayement.setText(liv.getModePayment());
            tvMontantTotal.setText(String.format("%.2f DT", liv.getMontantTotale()));
            tbNbArticles.setText(liv.getNbArticle() + " articles");
            updateStatus(liv.getEtat());
            telephoneClient = liv.getTelephone();

            btnOpenMaps.setOnClickListener(v -> {
                Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(liv.getAdresse()));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            });

            btnUpdateStatus.setOnClickListener(v -> afficherMenuStatut());
            btnUrgence.setOnClickListener(v -> afficherFormulaireUrgence());
        }
    }

    private void updateStatus(String etat) {
        if (etat == null) return;
        switch (etat) {
            case "LI": tvStatusBadge.setText("LIVRÉE"); break;
            case "AL": tvStatusBadge.setText("ANNULÉE"); break;
        }
    }

    private void afficherMenuStatut() {
        String[] etats = {"Livrée", "Annulée"};
        String[] codes = {"LI", "AL"};

        new AlertDialog.Builder(this)
                .setTitle("Changer le statut")
                .setItems(etats, (dialog, which) -> {
                    String nouveauCode = codes[which];


                    db.modifierEtatLocal(nocde, nouveauCode);
                    updateStatus(nouveauCode);

                    Toast.makeText(this, "Statut mis à jour localement", Toast.LENGTH_SHORT).show();


                }).show();
    }
    private void afficherFormulaireUrgence() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_urgence, null);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();
        
        android.widget.RadioButton rbAbsent = dialogView.findViewById(R.id.rbAbsent);
        android.widget.RadioButton rbRepondPas = dialogView.findViewById(R.id.rbRepondPas);
        android.widget.RadioButton rbAdresseIntrouvable = dialogView.findViewById(R.id.rbAdresseIntrouvable);
        android.widget.RadioButton rbRefus = dialogView.findViewById(R.id.rbRefus);
        android.widget.RadioButton rbPaiement = dialogView.findViewById(R.id.rbPaiement);

        android.widget.EditText etRemarques = dialogView.findViewById(R.id.etRemarques);
        android.widget.Button btnEnvoyer = dialogView.findViewById(R.id.btnEnvoyerUrgence);

        btnEnvoyer.setOnClickListener(v -> {

            String raison = "Raison inconnue";


            if (rbAbsent.isChecked()) {
                raison = "Client Absent";
            } else if (rbRepondPas.isChecked()) {
                raison = "Ne répond pas au téléphone";
            } else if (rbAdresseIntrouvable.isChecked()) {
                raison = "Adresse introuvable";
            } else if (rbRefus.isChecked()) {
                raison = "Le client a refusé la commande";
            } else if (rbPaiement.isChecked()) {
                raison = "Problème de paiement";
            }

            
            String remarque = etRemarques.getText().toString().trim();
            
            String messagePreConstruit = "URGENT - Commande #" + nocde + "\nProblème : " + raison;
            if (!remarque.isEmpty()) {
                messagePreConstruit += "\nRemarque : " + remarque;
            }

            dialog.dismiss();

            Intent i = new Intent(DetailsLivraisonActivity.this, ChatActivity.class);
            i.putExtra("login", login);
            i.putExtra("password", password);
            i.putExtra("message_auto_send", messagePreConstruit);
            startActivity(i);
            finish();
        });

        dialog.show();
    }
}