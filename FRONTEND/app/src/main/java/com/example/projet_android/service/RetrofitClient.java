package com.example.projet_android.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Voici la VRAIE adresse de ton PC sur ton réseau Wi-Fi
   // private static final String  BASE_URL = "http://172.20.10.3:8089/"; //TEL
    //private static final String  BASE_URL = "http:// 192.168.137.1:8089/"; //PC
    //adb tcpip 5555
    //adb connect @ipdemontel:5555
    //adb devices
    //haut adress ip du pc


    private static final String  BASE_URL = "http://localhost:8089/"; //AVEC FIL
    //adb -s 5200b8b94ffeb4eb  reverse tcp:8089 tcp:8089
    //adb -e reverse tcp:8089 tcp:8089
    //netsh advfirewall firewall add rule name="Spring 8089" dir=in action=allow protocol=TCP localport=8089

    private static Retrofit retrofit = null;
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .create();
    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }



}
