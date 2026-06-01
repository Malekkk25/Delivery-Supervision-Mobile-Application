package com.example.projet_android.Views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.dto.LivraisonControleur;
import com.example.projet_android.R;
import com.example.projet_android.adapter.LivraisonControleurAdapter;
import com.example.projet_android.service.ApiService;
import com.example.projet_android.service.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeLivraisonControleur extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LivraisonControleurAdapter adapter;
    private List<LivraisonControleur> listLivraisons = new ArrayList<>();

    private String login, password;

    private EditText etRecherche;
    private TextView btnTous, btnLivree, btnEnCours, btnAttente;
    private android.widget.Button btnFiltrerJour;

    private android.os.Handler handler = new android.os.Handler();

    private String currentEtatFiltre = "";
    private String currentSearchQuery = "";

    private Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            chargerDonneesDepuisServeur();
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_livraisons_controleur);

        login = getIntent().getStringExtra("login");
        password = getIntent().getStringExtra("password");

        recyclerView = findViewById(R.id.recyclerViewControleur);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        etRecherche = findViewById(R.id.etRecherche);

        btnTous = findViewById(R.id.btnFiltreTous);
        btnLivree = findViewById(R.id.btnFiltreLivree);
        btnEnCours = findViewById(R.id.btnFiltreEnCours);
        btnAttente = findViewById(R.id.btnFiltreAttente);
        btnFiltrerJour = findViewById(R.id.btnFiltrerJour);

        etRecherche.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                if (adapter != null) adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        View.OnClickListener filtreClickListener = v -> {
            etRecherche.setText("");
            currentSearchQuery = "";

            if (v.getId() == R.id.btnFiltreLivree)       currentEtatFiltre = "Livrée";
            else if (v.getId() == R.id.btnFiltreEnCours)  currentEtatFiltre = "En cours";
            else if (v.getId() == R.id.btnFiltreAttente)  currentEtatFiltre = "AL";
            else if (v.getId() == R.id.btnFiltreTous)     currentEtatFiltre = "";

            if (adapter != null) {
                adapter.setEtatFiltre(currentEtatFiltre);
                adapter.getFilter().filter(currentSearchQuery);
            }
        };

        btnTous.setOnClickListener(filtreClickListener);
        btnLivree.setOnClickListener(filtreClickListener);
        btnEnCours.setOnClickListener(filtreClickListener);
        btnAttente.setOnClickListener(filtreClickListener);

        btnFiltrerJour.setOnClickListener(v -> {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            currentSearchQuery = today;
            etRecherche.setText(today);
            if (adapter != null) adapter.getFilter().filter(today);
        });

        handler.post(pollRunnable);

        setupNavigation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(pollRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(pollRunnable);
    }

    private void chargerDonneesDepuisServeur() {
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getToutesLivraisons(login, password).enqueue(new Callback<List<LivraisonControleur>>() {
            @Override
            public void onResponse(Call<List<LivraisonControleur>> call, Response<List<LivraisonControleur>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listLivraisons = response.body();

                    if (adapter == null) {
                        adapter = new LivraisonControleurAdapter(listLivraisons);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateData(listLivraisons);
                    }

                    adapter.setEtatFiltre(currentEtatFiltre);
                    adapter.getFilter().filter(currentSearchQuery);

                    btnTous.setText("Tous (" + listLivraisons.size() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<LivraisonControleur>> call, Throwable t) {
                Log.e("ControleurAPI", "Erreur réseau: " + t.getMessage());
            }
        });
    }

    private void setupNavigation() {
        findViewById(R.id.menu_messages).setOnClickListener(v -> {
            Intent i = new Intent(ListeLivraisonControleur.this, ChatControleurActivity.class);
            i.putExtra("login", login);
            i.putExtra("password", password);
            startActivity(i);
        });

        View btnNavDashboard = findViewById(R.id.menu_dashboard);
        if (btnNavDashboard != null) {
            btnNavDashboard.setOnClickListener(v -> {
                Intent intent = new Intent(ListeLivraisonControleur.this, DashboardActivity.class);
                intent.putExtra("login", login);
                intent.putExtra("password", password);
                startActivity(intent);
            });
        }

        findViewById(R.id.menu_logout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Déconnexion")
                    .setMessage("Voulez-vous vraiment quitter l'application ? (Vos données de session seront effacées)")
                    .setPositiveButton("Oui, quitter", (dialog, which) -> {
                        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                        prefs.edit().clear().apply();

                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }
}