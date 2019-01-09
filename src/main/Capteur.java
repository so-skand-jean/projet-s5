package main;

public class Capteur {
    private String nom;
    private Ressources type; /* enumerer a rajouter */
    private String batiment;
    private int etage;
    private String lieu;
    private double seuilMin;
    private double seuilMax;
    private double valeur;
    private boolean estConnecte;

    public Capteur(Ressources type, String batiment, int etage, String lieu) {
        this.nom = "Capteur + batiment + lieu";
        this.type = type;
        this.batiment = batiment;
        this.etage = etage;
        this.lieu = lieu;
        this.estConnecte = false;

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
            seuilMin = 10;
            seuilMax = 500;
            break;
        case TEMPERATURE:
            seuilMin = 17;
            seuilMax = 22;
            break;
        }
    }
}
