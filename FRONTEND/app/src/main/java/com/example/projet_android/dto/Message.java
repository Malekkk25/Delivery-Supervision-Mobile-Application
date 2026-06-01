package com.example.projet_android.dto;

public class Message {
    private int id;
    private int id_expediteur;
    private int id_destinataire;
    private String contenu;
    private int is_urgent;
    private Long date_envoi;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_expediteur() {
        return id_expediteur;
    }

    public void setId_expediteur(int id_expediteur) {
        this.id_expediteur = id_expediteur;
    }

    public int getId_destinataire() {
        return id_destinataire;
    }

    public void setId_destinataire(int id_destinataire) {
        this.id_destinataire = id_destinataire;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getIs_urgent() {
        return is_urgent;
    }

    public void setIs_urgent(int is_urgent) {
        this.is_urgent = is_urgent;
    }

    public Long getDate_envoi() {
        return date_envoi;
    }

    public void setDate_envoi(Long date_envoi) {
        this.date_envoi = date_envoi;
    }

}
