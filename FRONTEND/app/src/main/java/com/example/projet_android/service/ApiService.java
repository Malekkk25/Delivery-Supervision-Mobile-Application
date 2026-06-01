package com.example.projet_android.service;

import com.example.projet_android.dto.DashboardStat;
import com.example.projet_android.dto.Livraison;
import com.example.projet_android.dto.LivraisonControleur;
import com.example.projet_android.dto.Message;
import com.example.projet_android.dto.LoginRequest;
import com.example.projet_android.dto.LoginResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/auth/login")
    Call<LoginResponse> verifierLogin(@Body LoginRequest loginRequest);
    @GET("api/livreur/ma-tournee")
    Call<List<Livraison>> getMaTournee(
            @Query("login") String login,
            @Query("password") String password
    );

    @PUT("api/livreur/modifier-etat")
    Call<String> modifierEtat(
            @Query("login") String login,
            @Query("password") String password,
            @Query("noCde") int id,
            @Query("nouvelEtat") String etat

    );
    
    @GET("api/controleur/livraisons")
    Call<List<LivraisonControleur>> getToutesLivraisons(
            @Query("login") String login,
            @Query("password") String password
    );

    @GET("api/controleur/dashboard/livreur-etat")
    Call<List<DashboardStat>> getDashLivreur(
            @Query("login") String login,
            @Query("password") String password
    );

    @GET("api/controleur/dashboard/client-etat")
    Call<List<DashboardStat>> getDashClient(
            @Query("login") String login,
            @Query("password") String password
    );
    @GET("api/messages/conversation")
    Call<List<Message>> getMessages(
            @Query("login") String login,
            @Query("password") String password,
            @Query("user1") Long user1,
            @Query("user2") Long user2
    );

    @POST("api/messages/envoyer")
    Call<Map<String, String>> sendMessage(
            @Query("login") String login,
            @Query("password") String password,
            @Query("idExpediteur") Long idExpediteur,
            @Query("idDestinataire") Long idDestinataire,
            @Query("contenu") String contenu,
            @Query("isUrgent") Integer isUrgent
    );

    @GET("api/messages/tous-livreurs")
    Call<List<Map<String, Object>>> getTousLivreurs(
            @Query("login") String login,
            @Query("password") String password
    );

    @GET("api/messages/tous-chefs")
    Call<List<Map<String, Object>>> getTousChefs(
            @Query("login") String login,
            @Query("password") String password
    );

    @GET("api/messages/trouver-id")
    Call<Map<String, Object>> trouverId(
            @Query("login") String login,
            @Query("password") String password
    );
}
