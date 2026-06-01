package com.example.projet_android.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_android.Managers.LivraisonHandler;
import com.example.projet_android.Managers.SyncJourneeManager;
import com.example.projet_android.R;
import com.example.projet_android.dto.LoginRequest;
import com.example.projet_android.dto.LoginResponse;
import com.example.projet_android.service.ApiService;
import com.example.projet_android.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String savedRole = prefs.getString("role", "");
            String savedLogin = prefs.getString("login", "");
            String savedPassword = prefs.getString("password", "");

            Intent intent;
            if ("CHEF_LIVREUR".equalsIgnoreCase(savedRole)) {
                intent = new Intent(this, ListeLivraisonControleur.class);
            } else {
                intent = new Intent(this, ListeLivraisonsActivity.class);
            }

            intent.putExtra("login", savedLogin);
            intent.putExtra("password", savedPassword);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String login = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }
            api_login(login, password);
        });
    }

    private void api_login(String login, String password) {
        ApiService apiService = RetrofitClient.getApiService();
        LoginRequest request = new LoginRequest(login, password);

        apiService.verifierLogin(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse user = response.body();
                    String role = user.getRole();

                    SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("login", login);
                    editor.putString("password", password);
                    editor.putString("role", role);
                    editor.apply();

                    if (role != null && role.equalsIgnoreCase("CHEF_LIVREUR")) {
                        Toast.makeText(LoginActivity.this, "Bienvenue Chef Livreur !", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, ListeLivraisonControleur.class);
                        intent.putExtra("login", login);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "Bienvenue Livreur !", Toast.LENGTH_SHORT).show();

                        getApplicationContext().deleteDatabase("LivraisonDB.db");
                        LivraisonHandler db = new LivraisonHandler(LoginActivity.this);

                        // ✅ Navigation dans le callback — après que les données sont chargées
                        SyncJourneeManager.chargerDepuisServeur(
                                LoginActivity.this,
                                db,
                                login,
                                password,
                                () -> {
                                    Intent intent = new Intent(LoginActivity.this, ListeLivraisonsActivity.class);
                                    intent.putExtra("login", login);
                                    intent.putExtra("password", password);
                                    startActivity(intent);
                                    finish();
                                }
                        );
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}