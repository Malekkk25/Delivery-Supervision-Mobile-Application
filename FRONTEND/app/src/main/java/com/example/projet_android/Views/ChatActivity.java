package com.example.projet_android.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.dto.Message;
import com.example.projet_android.R;
import com.example.projet_android.adapter.MessageAdapter;
import com.example.projet_android.service.ApiService;
import com.example.projet_android.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private String login, password;
    private Long currentUserId, otherUserId;
    private String otherUserName;

    private RecyclerView rvMessages;
    private EditText etMessageBody;
    private ImageView btnSend;

    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
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
        setContentView(R.layout.activity_chat);

        login = getIntent().getStringExtra("login");
        password = getIntent().getStringExtra("password");
        currentUserId = getIntent().getLongExtra("current_id", -1L);
        otherUserId = getIntent().getLongExtra("id_controleur", -1L);
        otherUserName = getIntent().getStringExtra("nom_controleur");

        apiService = RetrofitClient.getApiService();

        if (currentUserId == -1L) {
            getCurrentUserId(new Runnable() {
                @Override
                public void run() {
                    if (otherUserId == -1L) {
                        getControllerId(new Runnable() {
                            @Override
                            public void run() {
                                demarrerChat();
                            }
                        });
                    } else {
                        demarrerChat();
                    }
                }
            });
        } else {
            if (otherUserId == -1L) {
                getControllerId(new Runnable() {
                    @Override
                    public void run() {
                        demarrerChat();
                    }
                });
            } else {
                demarrerChat();
            }
        }
        setupNavigation();
    }
    private void demarrerChat() {
        initViews();
        setupRecyclerView();
        chargerMessages();
        setupClickListeners();
        verifierEtEnvoyerMessageAuto();
    }

    private void initViews() {
        TextView tvChatTitle = findViewById(R.id.tvChatTitle);
        TextView tvDateAujourdhui = findViewById(R.id.tvDateAujourdhui);
        rvMessages = findViewById(R.id.rvMessages);
        etMessageBody = findViewById(R.id.etMessageBody);
        btnSend = findViewById(R.id.btnSend);

        tvChatTitle.setText(otherUserName != null ? otherUserName : "Mon Contrôleur");

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE dd MMMM", java.util.Locale.getDefault());
        tvDateAujourdhui.setText(sdf.format(new java.util.Date()).substring(0, 1).toUpperCase() +
                sdf.format(new java.util.Date()).substring(1));
    }

    private void setupRecyclerView() {
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        rvMessages.setAdapter(messageAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoyerMessage();
            }
        });

        etMessageBody.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    envoyerMessage();
                    return true;
                }
                return false;
            }
        });
    }

    private void chargerMessages() {
        if (otherUserId == -1L) {
            return;
        }

        apiService.getMessages(login, password, currentUserId, otherUserId)
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
                            Toast.makeText(ChatActivity.this, "Erreur chargement messages", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Message>> call, Throwable t) {
                        Toast.makeText(ChatActivity.this, "Problème connexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void envoyerMessage() {
        String contenu = etMessageBody.getText().toString().trim();
        if (contenu.isEmpty()) return;

        int isUrgent = contenu.toUpperCase().startsWith("URGENT") ? 1 : 0;

        apiService.sendMessage(login, password, currentUserId, otherUserId, contenu, isUrgent)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        if (response.isSuccessful()) {
                            etMessageBody.setText("");
                            chargerMessages();
                        } else {
                            Toast.makeText(ChatActivity.this, "Erreur envoi", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(ChatActivity.this, "Problème connexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifierEtEnvoyerMessageAuto() {
        String msgAuto = getIntent().getStringExtra("message_auto_send");

        if (msgAuto != null && !msgAuto.isEmpty()) {
            etMessageBody.setText(msgAuto);

            envoyerMessage();

            getIntent().removeExtra("message_auto_send");
        }
    }

    private void getCurrentUserId(final Runnable onSuccess) {
        apiService.trouverId(login, password)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentUserId = ((Number) response.body().get("id")).longValue();
                            onSuccess.run();
                        } else {
                            Toast.makeText(ChatActivity.this, "Erreur récupération ID utilisateur", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(ChatActivity.this, "Problème connexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getControllerId(final Runnable onSuccess) {
        apiService.getTousChefs(login, password)
                .enqueue(new Callback<List<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Map<String, Object> controller = response.body().get(0);
                            otherUserId = ((Number) controller.get("id")).longValue();
                            otherUserName = (String) controller.get("nomComplet");
                            onSuccess.run();
                        } else {
                            Toast.makeText(ChatActivity.this, "Erreur récupération contrôleur", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                        Toast.makeText(ChatActivity.this, "Problème connexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.removeCallbacks(pollRunnable);
        handler.postDelayed(pollRunnable, 0);

        if (currentUserId != -1L && otherUserId != -1L) {
            chargerMessages();
        }
    }

    private void setupNavigation() {
        findViewById(R.id.menu_messages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargerMessages();
            }
        });

        findViewById(R.id.menu_liste).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.menu_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("Déconnexion")
                        .setMessage("Voulez-vous vraiment quitter l'application ? (Vos données de session seront effacées)")
                        .setPositiveButton("Oui, quitter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                                prefs.edit().clear().apply();

                                getApplicationContext().deleteDatabase("LivraisonDB.db");
                                Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
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