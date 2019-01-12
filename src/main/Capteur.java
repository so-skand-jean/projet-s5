package main;

public class Capteur{
    private String nom;
    private Ressources type;
    private String batiment;
    private int etage;
    private String lieu;
    private double seuilMin;
    private double seuilMax;
    private double valeur;
    private boolean estConnecte;

    public Capteur(String nom, Ressources type, String batiment, int etage, String lieu) {
        this.nom = nom;
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
    
    public String getNom() {
		return nom;
	}
	
	public Ressources getType() {
		return type;
	}
	
	public String getBatiment() {
		return batiment;
	}
	
	public int getEtage() {
		return etage;
	}
	
	public double getSeuilMin() {
		return seuilMin;
	}
	
	public double getSeuilMax() {
		return seuilMax;
	}
	
	/* retourne la veleur que si elle existe, soit que si le capteur est connecté */
	public double getValeur() {
		if(!estConnecte) return 0;
		else return valeur;
	}
	
	public String getLieu() {
		return lieu;
	}
	
	public boolean getEstConnecte() {
		return estConnecte;
	}

	public void setSeuilMin(double seuilMin) {
		this.seuilMin = seuilMin;
	}
	
	public void setSeuilMax(double seuilMax) {
		this.seuilMax = seuilMax;
	}
	
	public boolean estHorsSeuil() {
		return ((valeur>seuilMax) || (valeur<seuilMin));
	}
	
	public void updateCapteurFromDB(Logs l) {
		valeur = l.getCapteurValeur(this);
		System.out.println("valeur = " + valeur);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Capteur) {
			Capteur capteur = (Capteur) obj;
			return ((capteur.getNom() == nom) && (capteur.getType() == type));
		}
		return false;
	}
}
