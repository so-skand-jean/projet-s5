package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

public class Logs{
	
	public double getCapteurValeur(Capteur c) {		/* A FINIR ET TESTER SUR UNE BDD */
		Connection con;
		double valeur = 0;
		try {
			con = DriverManager.getConnection("", "root", "");
			
			Statement stmt = con.createStatement();
			ResultSet rst = stmt.executeQuery("SELECT * FROM Log");
			boolean capteurTrouve = false;
			while(!capteurTrouve && rst.next()) {
				String nomC = rst.getObject(1).toString();
				if(nomC == c.getNom()) capteurTrouve = true;
			}
			valeur = (double) rst.getObject(3);	
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return valeur;
	}
	
	public TreeMap<String, Capteur> getAllCapteurs(){ 
		
		// TreeMap ===>>> nom capteur - capteur
		return null;
	}
	
	public void setNewCapteur(String nom, Ressources type, String batiment, int etage, String lieu) {
		Capteur c = new Capteur(nom, type, batiment, etage, lieu);
		// ajout d'un capteur dans la base de donnee
	}
	
	public void updateCapteur(Capteur c) throws Exception {
		// le trouver dans la BDD et le modifier dans la base de donnee avec ses nouvelles informations
		
	}
	
}
