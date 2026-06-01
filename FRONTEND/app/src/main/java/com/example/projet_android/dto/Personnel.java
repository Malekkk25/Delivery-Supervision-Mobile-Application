package com.example.projet_android.dto;

public class Personnel {
    private Long id;
    private String nom;
    private String prenom;
    private String nomComplet;
    private String telephone;
    private String login;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
}