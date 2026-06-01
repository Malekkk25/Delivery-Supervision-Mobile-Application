package com.example.projet_android.Views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.R;
import com.example.projet_android.adapter.LivreurBulleAdapter;
import com.example.projet_android.adapter.MessageAdapter;
import com.example.projet_android.dto.Message;
import com.example.projet_android.dto.Personnel;
import com.example.projet_android.service.ApiService;
import com.example.projet_android.service.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatControleurActivity extends AppCompatActivity {

    private String login, password;
    private Long chefId = -1L, livreurSelectionneId = -1L;
    private String livreurNom = "Sélectionnez un livreur";

    private TextView tvChatTitle, tvDate;
    private RecyclerView rvLivreursBulles, rvMessages;
    private EditText etMessageBody;
    private ImageView btnSend;

    private MessageAdapter messageAdapter;
    private LivreurBulleAdapter bulleAdapter;
    private List<Message> messageList = new ArrayList<>();
    private List<Personnel> livreursList = new ArrayList<>();

    private ApiService apiService;
    private android.os.Handler handler = new android.os.Handler();
    private Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            chargerMessages();
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_controleur);

        login = getIntent().getStringExtra("login");
        password = getIntent().getStringExtra("password");

        apiService = RetrofitClient.getApiService();
        setupNavigation();
        initViews();
        initRecyclerViews();

        getCurrentId(() -> {
            chargerLivreurs();
            if (livreurSelectionneId != -1L) {
                chargerMessages();
            }
        });
    }

    private void initViews() {
        tvChatTitle = findViewById(R.id.tvChatTitle);
        tvDate = findViewById(R.id.tvDate);
        rvLivreursBulles = findViewById(R.id.rvLivreursBulles);
        rvMessages = findViewById(R.id.rvMessagesControleur);
        etMessageBody = findViewById(R.id.etMessageControleur);
        btnSend = findViewById(R.id.btnSendControleur);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM", Locale.getDefault());
        String today = sdf.format(new Date());
        tvDate.setText(today.substring(0, 1).toUpperCase() + today.substring(1));

        btnSend.setOnClickListener(v -> envoyerMessage());
    }

    private void initRecyclerViews() {
        rvLivreursBulles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messageList, chefId);
        rvMessages.setAdapter(messageAdapter);
    }

    private void getCurrentId(Runnable onSuccess) {
        apiService.trouverId(login, password).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chefId = ((Number) response.body().get("id")).longValue();
                    if (messageAdapter != null) {
                        messageAdapter.setCurrentUserId(chefId);
                    }
                    onSuccess.run();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
        });
    }

    private void chargerLivreurs() {
        if (chefId == -1L) return;

        apiService.getTousLivreurs(login, password).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> rawList = response.body();
                    livreursList.clear();

                    for (Map<String, Object> map : rawList) {
                        if (!(map.get("id") instanceof Number)) continue;

                        Personnel p = new Personnel();
                        p.setId(((Number) map.get("id")).longValue());
                        p.setNom((String) map.get("nom"));
                        p.setPrenom((String) map.get("prenom"));
                        p.setNomComplet((String) map.get("nomComplet"));
                        p.setTelephone((String) map.get("telephone"));
                        p.setLogin((String) map.get("login"));

                        livreursList.add(p);
                    }

                    if (livreursList.isEmpty()) {
                        Toast.makeText(ChatControleurActivity.this, "Aucun livreur trouvé", Toast.LENGTH_SHORT).show();
                    } else {
                        if (bulleAdapter == null) {
                            bulleAdapter = new LivreurBulleAdapter(livreursList, livreur -> {
                                livreurSelectionneId = livreur.getId();
                                livreurNom = livreur.getPrenom() + " " + livreur.getNom();
                                tvChatTitle.setText(livreurNom);
                                chargerMessages();
                            });
                            rvLivreursBulles.setAdapter(bulleAdapter);
                        } else {
                            bulleAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Toast.makeText(ChatControleurActivity.this, "Erreur chargement livreurs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(ChatControleurActivity.this, "Erreur réseau (livreurs)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chargerMessages() {
        if (livreurSelectionneId == -1L || chefId == -1L) return;

        apiService.getMessages(login, password, chefId, livreurSelectionneId)
                .enqueue(new Callback<List<Message>>() {
                    @Override
                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            messageList.clear();
                            messageList.addAll(response.body());
                            messageAdapter.notifyDataSetChanged();

                            if (!messageList.isEmpty()) {
                                rvMessages.scrollToPosition(messageList.size() - 1);
                            }
                        } else {
                            Toast.makeText(ChatControleurActivity.this, "Erreur chargement messages", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Message>> call, Throwable t) {
                        Toast.makeText(ChatControleurActivity.this, "Problème connexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void envoyerMessage() {
        String texte = etMessageBody.getText().toString().trim();
        if (texte.isEmpty() || livreurSelectionneId == -1L || chefId == -1L) return;

        int isUrgent = texte.toUpperCase().startsWith("URGENT") ? 1 : 0;

        apiService.sendMessage(login, password, chefId, livreurSelectionneId, texte, isUrgent)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            etMessageBody.setText("");
                            chargerMessages();
                        } else {
                            Toast.makeText(ChatControleurActivity.this, "Erreur envoi", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(ChatControleurActivity.this, "Problème connexion (envoi)", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.removeCallbacks(pollRunnable);
        handler.postDelayed(pollRunnable, 0);
        if (livreurSelectionneId != -1L && chefId != -1L) {
            chargerMessages();
        }
    }

    private void setupNavigation() {
        findViewById(R.id.menu_messages).setOnClickListener(v -> chargerMessages());
        findViewById(R.id.menu_liste).setOnClickListener(v -> finish());

        View btnNavDashboard = findViewById(R.id.menu_dashboard);
        if (btnNavDashboard != null) {
            btnNavDashboard.setOnClickListener(v -> {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.putExtra("login", login);
                intent.putExtra("password", password);
                startActivity(intent);
                finish();
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