package com.example.projet_android.Views;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.R;
import com.example.projet_android.adapter.PersonnelAdapter;
import com.example.projet_android.service.ApiService;
import com.example.projet_android.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListActivity extends AppCompatActivity {

    private String login, password;
    private Long currentUserId;

    private RecyclerView rvLivreurs;
    private PersonnelAdapter personnelAdapter;
    private List<Map<String, Object>> livreursList = new ArrayList<>();
    private ApiService apiService;
    private android.os.Handler handler = new android.os.Handler();
    private Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
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

        apiService = RetrofitClient.getApiService();

        setupRecyclerView();
        chargerLivreurs();
    }

    private void setupRecyclerView() {
        rvLivreurs.setLayoutManager(new LinearLayoutManager(this));
        personnelAdapter = new PersonnelAdapter(livreursList, this, login, password, currentUserId);
        rvLivreurs.setAdapter(personnelAdapter);
    }

    private void chargerLivreurs() {
        apiService.getTousLivreurs(login, password)
                .enqueue(new Callback<List<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            livreursList.clear();
                            livreursList.addAll(response.body());
                            personnelAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ChatListActivity.this, "Erreur chargement livreurs", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                        Toast.makeText(ChatListActivity.this, "Problème connexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}