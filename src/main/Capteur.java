package main;

import java.util.Date;

public class Capteur {
    private String nom;
    private TypeCapteur type;
    private String batiment;
    private int etage;
    private String infoLieu;

    private String surnom;
    private double seuilMin;
    private double seuilMax;
    private double valeurCourante;
    private Date dateDeDepassement;
    private boolean estConnecte;

    public Capteur() {
        // do nothing
    }

    public Capteur(String nom, TypeCapteur type, String batiment, int etage, String infoLieu) {
        // update seuilMin et seuilMax
        switch (type) {
        case EAU:
            seuilMin = 0;
            seuilMax = 10;
            break;
        case AIRCOMPRIME:
            seuilMin = 0;
            seuilMax = 5;
            break;
        case ELECTRICITE:
            seuilMin = 100;
            seuilMax = 500;
            break;
        case TEMPERATURE:
            seuilMin = 17;
            seuilMax = 22;
            break;
        }
        valeurCourante = 0;
        estConnecte = false;
        hydrate(nom, nom, type, batiment, etage, infoLieu, seuilMin, seuilMax, null);
    }

    public void hydrate(String nom, String surnom, TypeCapteur type, String batiment, int etage, String infoLieu,
            double seuilMin, double seuilMax, Date dateDeDepassement) {
        this.nom = nom;
        this.surnom = surnom;
        this.type = type;
        this.batiment = batiment;
        this.etage = etage;
        this.infoLieu = infoLieu;
        this.surnom = surnom;
        this.seuilMin = seuilMin;
        this.seuilMax = seuilMax;
        this.dateDeDepassement = dateDeDepassement;
    }

    public String getNom() {
        return nom;
    }

    public TypeCapteur getType() {
        return type;
    }

    public String getBatiment() {
        return batiment;
    }

    public int getEtage() {
        return etage;
    }

    public String getInfoLieu() {
        return infoLieu;
    }

    public String getSurnom() {
        return surnom;
    }

    public double getSeuilMin() {
        return seuilMin;
    }

    public double getSeuilMax() {
        return seuilMax;
    }

    public double getValeurCourante() {
        return valeurCourante;
    }

    public boolean isConnected() {
        return estConnecte;
    }

    public Date getDateDeDepassement() {
        return dateDeDepassement;
    }

    public void updateSurnom(DBUtility db, String surnom) {
        db.updateSurnomCptInDB(nom, surnom);
        this.surnom = surnom;
    }

    public void updateSeuilMin(DBUtility db, double seuilMin) {
        db.updateSeuilMinCptInDB(nom, seuilMin);
        this.seuilMin = seuilMin;
    }

    public void updateSeuilMax(DBUtility db, double seuilMax) {
        db.updateSeuilMaxCptInDB(nom, seuilMax);
        this.seuilMax = seuilMax;
    }

    public void updateValeurCourante(DBUtility db, Date datetime, double currVal) {
        db.updateValeurCouranteCptInDB(nom, datetime, currVal, (currVal < seuilMin || currVal > seuilMax));
        this.dateDeDepassement = datetime;
        this.valeurCourante = currVal;
    }

    public void setEstConnecte(boolean estConnecte) {
        this.estConnecte = estConnecte;
    }
}
