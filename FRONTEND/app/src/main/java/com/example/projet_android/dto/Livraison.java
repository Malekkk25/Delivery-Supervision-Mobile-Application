package com.example.projet_android.dto;


public class Livraison {

    private int id;
    private String etat;
    private String adresse;
    private String telephone;
    private String nomClient;

    private int nbArticles;

    private Double montantTotal;

    private String modePayment;

    public Livraison() {}

    public Livraison(int id, String etat, String adresse, String telephone, String nomClient, int quantiteTotale, Double montantTotal, String modePayment) {
        this.id = id;
        this.etat = etat;
        this.adresse = adresse;
        this.telephone = telephone;
        this.nomClient = nomClient;
        this.nbArticles = quantiteTotale;
        this.montantTotal = montantTotal;
        this.modePayment = modePayment;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public Double getMontantTotale() { return montantTotal; }
    public void setMontantTotale(Double montantTotale) { this.montantTotal = montantTotale; }
    public String getModePayment() { return modePayment; }
    public void setModePayment(String modePayment) { this.modePayment = modePayment; }
    public int getNbArticle() { return nbArticles; }
    public void setNbArticle(int nbArticle) { this.nbArticles = nbArticle; }
}