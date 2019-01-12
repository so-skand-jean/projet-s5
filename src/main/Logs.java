package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class Logs{
	
	public double getCapteurValeur(Capteur c){
		Connection con;
		double valeur = 0;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetsoskandjean?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			
			Statement stmt = con.createStatement();
			ResultSet rst = stmt.executeQuery("SELECT * FROM Logs");
			boolean capteurTrouve = false;
			while(!capteurTrouve && rst.next()) {
				String nomC = rst.getString(1);
				if(c.getNom().equals(nomC)) capteurTrouve = true;
			}
			valeur = Double.parseDouble(rst.getString(3));	
			con.close();
			stmt.close();
			rst.close();
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		return valeur;
	}
		
	public TreeMap<String, Capteur> getAllCapteurs(){ 
		
		// TreeMap ===>>> nom capteur - capteur
		
		Connection con;
		TreeMap<String, Capteur> liste = new TreeMap<>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetsoskandjean?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			
			Statement stmt = con.createStatement();
			ResultSet rst = stmt.executeQuery("SELECT * FROM Capteur");
			
			String nomC;
			Ressources typeC;
			String batimentC;
			int etageC;
			String lieuC;
			double seuilMinC;
			double seuilMaxC;
			
			while(rst.next()) {
				nomC = rst.getString(1);				
				typeC = Enum.valueOf(Ressources.class, rst.getString(2));				
				batimentC = rst.getString(3);
				etageC = rst.getInt(4);
				lieuC = rst.getString(5);
				seuilMinC = Double.parseDouble(rst.getString(6));
				seuilMaxC = Double.parseDouble(rst.getString(7));
				Capteur x = new Capteur(nomC, typeC, batimentC,etageC, lieuC);
				x.setSeuilMin(seuilMinC);
				x.setSeuilMax(seuilMaxC);
				liste.put(nomC, x);
			}
			con.close();
			stmt.close();
			rst.close();
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return liste;
	}
	
	public void setNewCapteur(String nom, Ressources type, String batiment, int etage, String lieu) {
		Connection con;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetsoskandjean?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			String INSERT_RECORD = "insert into capteur values(?, ?, ?, ?, ?, ?, ?, ?,  ?)";
			PreparedStatement pstmt = con.prepareStatement(INSERT_RECORD);
			Capteur c = new Capteur(nom, type, batiment, etage, lieu);
			
			java.util.Date date = new java.util.Date();
		    Timestamp sqlTimestamp = new Timestamp(date.getTime());
			
		    pstmt.setString(1, nom);
		    pstmt.setString(2, type.toString());
		    pstmt.setString(3, batiment);
		    pstmt.setInt(4, etage);
		    pstmt.setString(5, lieu);
		    pstmt.setDouble(6, c.getSeuilMin());
		    pstmt.setDouble(7, c.getSeuilMax());
		    pstmt.setString(8, nom);
		    pstmt.setTimestamp(9, sqlTimestamp);
		    
		    pstmt.executeUpdate();
		    
		    con.close();
		    pstmt.close();
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void updateCapteur(Capteur c) {
		// le trouver dans la BDD et le modifier dans la base de donnee avec ses nouvelles informations
		Connection con;
		Statement stmt;
		ResultSet rst;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetsoskandjean?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rst = stmt.executeQuery("SELECT * FROM Capteur");
			String nomC;
			boolean estTrouve = false;
			while(!estTrouve && rst.next()) {
				nomC = rst.getString(1);
				if(c.getNom().equals(nomC)) estTrouve = true;
			}
			if(!estTrouve) {
				setNewCapteur(c.getNom(), c.getType(), c.getBatiment(), c.getEtage(), c.getLieu());
				rst.updateDouble(6, c.getSeuilMin());
				rst.updateDouble(7, c.getSeuilMax());
			}else if(!(c.getType().toString().equals(rst.getString(2)) && c.getBatiment().equals(rst.getString(3)) && (c.getEtage() == rst.getInt(4)) && c.getLieu().equals(rst.getString(5)) && (c.getSeuilMin() == rst.getDouble(6))) && (c.getSeuilMax()==(rst.getDouble(7)))) 
			{
				if(!c.getType().toString().equals(rst.getString(2))) 
					rst.updateString(2, c.getType().toString());
				if(!c.getBatiment().equals(rst.getString(3)))
					rst.updateString(3, c.getBatiment());
				if(c.getEtage() != rst.getInt(4))
					rst.updateInt(4, c.getEtage());
				if(!c.getLieu().equals(rst.getString(5)))
					rst.updateString(5, c.getLieu());
				if(c.getSeuilMin() !=  rst.getDouble(6)) 
					rst.updateDouble(6, c.getSeuilMin());
				if(c.getSeuilMax() != rst.getDouble(7))
					rst.updateDouble(7, c.getSeuilMax());
				
				rst.updateRow();
			}
			
			con.close();
			stmt.close();
			rst.close();
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
		
	}
	
}
