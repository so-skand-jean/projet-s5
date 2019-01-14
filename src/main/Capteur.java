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

    /**
     * @param nom
     * @param type
     * @param batiment
     * @param etage
     * @param lieu
     */
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
            seuilMin = 100;
            seuilMax = 500;
            break;
        case TEMPERATURE:
            seuilMin = 17;
            seuilMax = 22;
            break;
        }
    }
    /**
     * 
     * @return nom
     */
    
    public String getNom() {
		return nom;
	}
	
    /**
     * 
     * @return type
     */
	public Ressources getType() {
		return type;
	}
	
	/**
	 * 
	 * @return batiment
	 */
	public String getBatiment() {
		return batiment;
	}
	
	/**
	 * 
	 * @return etage
	 */
	public int getEtage() {
		return etage;
	}
	
	/**
	 * 
	 * @return seuil minimum
	 */
	public double getSeuilMin() {
		return seuilMin;
	}
	
	/**
	 * 
	 * @return seuil maximum
	 */
	public double getSeuilMax() {
		return seuilMax;
	}
	
	/**
	 * 
	 * @return valeur si capteur connecté et si il y a une valeur, 0 sinon
	 */
	public double getValeur() {
		if(!estConnecte) return 0;
		else return valeur;
	}
	
	/**
	 * 
	 * @return lieu
	 */
	public String getLieu() {
		return lieu;
	}
	
	/**
	 * 
	 * @return si le capteur est connecté
	 */
	public boolean getEstConnecte() {
		return estConnecte;
	}

	/**
	 * 
	 * @param seuilMin
	 */
	public void setSeuilMin(double seuilMin) {
		this.seuilMin = seuilMin;
	}
	
	/**
	 * 
	 * @param seuilMax
	 */
	public void setSeuilMax(double seuilMax) {
		this.seuilMax = seuilMax;
	}
	
	/**
	 * 
	 * @return si la valeur est une valeur autorisé
	 */
	public boolean estHorsSeuil() {
		return ((valeur>seuilMax) || (valeur<seuilMin));
	}
	
	/**
	 * 
	 * @param l
	 * recherche la valeur du capteur dan sla base de donnée et l'affecte à la variable capteur
	 */
	public void updateCapteurFromDB(Logs l) {
		valeur = l.getCapteurValeur(this);
	}

	/**
	 * @return le hashcode de l'élément capteur
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * @param obj
	 * @return renvoie si les éléments sont égaux ou non
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Capteur) {
			Capteur capteur = (Capteur) obj;
			return ((capteur.getNom() == nom) && (capteur.getType() == type));
		}
		return false;
	}
}
