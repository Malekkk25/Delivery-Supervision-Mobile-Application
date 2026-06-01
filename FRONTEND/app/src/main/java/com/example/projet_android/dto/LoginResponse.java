package com.example.projet_android.dto;

import java.util.List;

public class LoginResponse {
    private int idPers;
    private String nomComplet;
    private String role;
    private List<Object> tournee;


    public int getIdPers() { return idPers; }
    public String getNomComplet() { return nomComplet; }
    public String getRole() { return role; }
    public List<Object> getTournee() { return tournee; }
}
