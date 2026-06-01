package com.example.projet_android.Views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_android.dto.DashboardStat;
import com.example.projet_android.R;
import com.example.projet_android.service.RetrofitClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private String login, password;
    private ProgressBar progressBar;
    private TextView tvTotalLivrees, tvTotalProbleme, tvTauxReussite;
    private PieChart pieGlobal;
    private BarChart barLivreurs;
    private BarChart barClients;
    private List<DashboardStat> statsLivreur = new ArrayList<>();
    private List<DashboardStat> statsClient  = new ArrayList<>();
    private int loaded = 0;
    private android.os.Handler handler = new android.os.Handler();
    private Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            chargerDonnees();
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        login    = getIntent().getStringExtra("login");
        password = getIntent().getStringExtra("password");

        progressBar       = findViewById(R.id.progressBar);
        tvTotalLivrees    = findViewById(R.id.tvTotalLivrees);
        tvTotalProbleme   = findViewById(R.id.tvTotalProbleme);
        tvTauxReussite    = findViewById(R.id.tvTauxReussite);
        pieGlobal         = findViewById(R.id.pieGlobal);
        barLivreurs       = findViewById(R.id.barLivreurs);
        barClients        = findViewById(R.id.barClients);

        setupNavigation();
        chargerDonnees();
    }

    private void chargerDonnees() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService()
                .getDashLivreur(login, password)
                .enqueue(new Callback<List<DashboardStat>>() {
                    @Override
                    public void onResponse(Call<List<DashboardStat>> call, Response<List<DashboardStat>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            statsLivreur = response.body();
                        }
                        loaded++;
                        if (loaded == 2) afficherTout();
                    }
                    @Override
                    public void onFailure(Call<List<DashboardStat>> call, Throwable t) {
                        loaded++;
                        if (loaded == 2) afficherTout();
                    }
                });

        RetrofitClient.getApiService()
                .getDashClient(login, password)
                .enqueue(new Callback<List<DashboardStat>>() {
                    @Override
                    public void onResponse(Call<List<DashboardStat>> call, Response<List<DashboardStat>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            statsClient = response.body();
                        }
                        loaded++;
                        if (loaded == 2) afficherTout();
                    }
                    @Override
                    public void onFailure(Call<List<DashboardStat>> call, Throwable t) {
                        loaded++;
                        if (loaded == 2) afficherTout();
                    }
                });
    }

    private void afficherTout() {
        progressBar.setVisibility(View.GONE);

        int livrees = 0, probleme = 0;
        for (DashboardStat s : statsLivreur) {
            if ("LI".equals(s.getEtat())) {
                livrees += s.getTotal();
            } else if ("AL".equals(s.getEtat()) || "PR".equals(s.getEtat())) {
                probleme += s.getTotal();
            }
        }
        int total = livrees + probleme;
        int taux  = total > 0 ? (livrees * 100 / total) : 0;

        tvTotalLivrees.setText(String.valueOf(livrees));
        tvTotalProbleme.setText(String.valueOf(probleme));
        tvTauxReussite.setText(taux + "%");

        afficherPieChart(livrees, probleme);
        afficherBarLivreurs();
        afficherBarClients();
    }

    private void afficherPieChart(int livrees, int probleme) {
        List<PieEntry> entries = new ArrayList<>();
        if (livrees  > 0) entries.add(new PieEntry(livrees,  "Livrées"));
        if (probleme > 0) entries.add(new PieEntry(probleme, "Problème"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.parseColor("#2E7D32"), Color.parseColor("#C62828"));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(6f);
        dataSet.setValueTextSize(13f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieGlobal.setData(data);
        pieGlobal.setDrawHoleEnabled(true);
        pieGlobal.setHoleRadius(52f);
        pieGlobal.setTransparentCircleRadius(57f);
        pieGlobal.setHoleColor(Color.WHITE);
        pieGlobal.setCenterText("Répartition");
        pieGlobal.setCenterTextSize(14f);
        pieGlobal.setCenterTextColor(Color.parseColor("#1B4332"));
        pieGlobal.setDescription(null);
        pieGlobal.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieGlobal.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        pieGlobal.animateY(800);
        pieGlobal.invalidate();
    }

    private void afficherBarLivreurs() {
        Map<String, int[]> map = new LinkedHashMap<>();
        for (DashboardStat s : statsLivreur) {
            if (!map.containsKey(s.getNom())) map.put(s.getNom(), new int[2]);
            int[] arr = map.get(s.getNom());
            if ("LI".equals(s.getEtat())) {
                arr[0] += s.getTotal();
            } else if ("AL".equals(s.getEtat()) || "PR".equals(s.getEtat())) {
                arr[1] += s.getTotal();
            }
        }

        List<String> labels = new ArrayList<>(map.keySet());
        List<BarEntry> entriesLI = new ArrayList<>();
        List<BarEntry> entriesPR = new ArrayList<>();

        int i = 0;
        for (String nom : labels) {
            int[] arr = map.get(nom);
            entriesLI.add(new BarEntry(i, arr[0]));
            entriesPR.add(new BarEntry(i, arr[1]));
            i++;
        }

        BarDataSet dsLI = new BarDataSet(entriesLI, "Livrées");
        dsLI.setColor(Color.parseColor("#2E7D32"));
        dsLI.setValueTextColor(Color.BLACK);

        BarDataSet dsPR = new BarDataSet(entriesPR, "Problème");
        dsPR.setColor(Color.parseColor("#C62828"));
        dsPR.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dsLI, dsPR);

        float groupSpace = 0.30f;
        float barSpace = 0.05f;
        float barWidth = 0.30f;

        barData.setBarWidth(barWidth);
        barLivreurs.setData(barData);
        barLivreurs.groupBars(0f, groupSpace, barSpace);
        barLivreurs.setFitBars(true);

        XAxis xAxis = barLivreurs.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(labels.size());

        barLivreurs.getAxisLeft().setDrawGridLines(false);
        barLivreurs.getAxisRight().setEnabled(false);
        barLivreurs.setDescription(null);
        barLivreurs.getLegend().setTextSize(11f);
        barLivreurs.animateY(800);
        barLivreurs.invalidate();
    }

    private void afficherBarClients() {
        Map<String, int[]> map = new LinkedHashMap<>();
        int totalLI = 0;
        int totalPR = 0;

        for (DashboardStat s : statsClient) {
            if (!map.containsKey(s.getNom())) map.put(s.getNom(), new int[2]);
            int[] arr = map.get(s.getNom());
            if ("LI".equals(s.getEtat())) {
                arr[0] += s.getTotal();
                totalLI += s.getTotal();
            } else {
                arr[1] += s.getTotal();
                totalPR += s.getTotal();
            }
        }

        tvTotalLivrees.setText(String.valueOf(totalLI));
        tvTotalProbleme.setText(String.valueOf(totalPR));
        if ((totalLI + totalPR) > 0) {
            int taux = (totalLI * 100) / (totalLI + totalPR);
            tvTauxReussite.setText(taux + "%");
        }

        List<String> labels = new ArrayList<>(map.keySet());
        List<BarEntry> entriesLI = new ArrayList<>();
        List<BarEntry> entriesPR = new ArrayList<>();

        int i = 0;
        for (String nom : labels) {
            int[] arr = map.get(nom);
            entriesLI.add(new BarEntry(i, arr[0]));
            entriesPR.add(new BarEntry(i, arr[1]));
            i++;
        }

        BarDataSet dsLI = new BarDataSet(entriesLI, "Livrées");
        dsLI.setColor(Color.parseColor("#2E7D32"));

        BarDataSet dsPR = new BarDataSet(entriesPR, "Problème");
        dsPR.setColor(Color.parseColor("#C62828"));

        BarData barData = new BarData(dsLI, dsPR);

        float groupSpace = 0.30f;
        float barSpace = 0.05f;
        float barWidth = 0.30f;

        barData.setBarWidth(barWidth);
        barClients.setData(barData);
        barClients.groupBars(0f, groupSpace, barSpace);

        XAxis xAxis = barClients.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(barData.getGroupWidth(groupSpace, barSpace) * labels.size());
        xAxis.setLabelRotationAngle(-30f);
        xAxis.setDrawGridLines(false);

        barClients.getAxisLeft().setDrawGridLines(false);
        barClients.getAxisRight().setEnabled(false);
        barClients.setDescription(null);
        barClients.animateY(800);
        barClients.invalidate();
    }

    private void setupNavigation() {
        View btnNavListe = findViewById(R.id.menu_liste);
        if (btnNavListe != null) {
            btnNavListe.setOnClickListener(v -> {
                finish();
            });
        }
        View btnNavMessages = findViewById(R.id.menu_messages);
        if (btnNavMessages != null) {
            btnNavMessages.setOnClickListener(v -> {
                Intent intent = new Intent(this, ChatControleurActivity.class);
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

    @Override
    public void onResume() {
        super.onResume();
        handler.removeCallbacks(pollRunnable);
        handler.postDelayed(pollRunnable, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(pollRunnable);
    }
}