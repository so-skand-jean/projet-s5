public class Capteur{
	private String nom;
	private Ressources type; /* enumerer a rajouter */
	private String batiment;
	private int etage;
	private String lieu;
	private double seuilMin;
	private double seuilMax;
	private double valeur;
	private boolean estConnnecte;
	
	public Capteur(Ressources type, String batiment, int etage, String lieu) {
		this.nom = Capteur + batiment + lieu;
		this.type = type;
		this.batiment = batiment;
		this.etage = etage;
		this.lieu = lieu;
		this.estConnecte = false;
		
		if(type == EAU || type == AIRCOMPRIME)this.seuilMin = 0;
		else if(type == ELECTRICITE) this.seuilMin = 10;
		else this.seuilMin = 17;
		
		if(type == EAU) this.seuilMax = 10;
		else if(type == ELECTRICITE) this.seuilMax = 500;
		else if(type == TEMPERATURE) this.seuilMax = 22;
		else this.seuilMax = 5;
		
	}
}
