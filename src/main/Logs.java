package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.TreeMap;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class Logs{
	private final int COLUMN_CAPTEUR_NOM = 1;
	private final int COLUMN_CAPTEUR_TYPE = 2;
	private final int COLUMN_CAPTEUR_BATIMENT = 3;
	private final int COLUMN_CAPTEUR_ETAGE = 4;
	private final int COLUMN_CAPTEUR_LIEU = 5;
	private final int COLUMN_CAPTEUR_SEUILMIN = 6;
	private final int COLUMN_CAPTEUR_SEUILMAX = 7;
	private final int COLUMN_CAPTEUR_SURNOM = 8;
	private final int COLUMN_CAPTEUR_DATE = 9;
	private final int COLUMN_LOGS_NOM = 1;
	private final int COLUMN_LOGS_DATE = 2;
	private final int COLUMN_LOGS_VALEUR = 3;
			
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public double getCapteurValeur(Capteur c){
		Connection con = null;
		Statement stmt = null;
		ResultSet rst = null;
		double valeur = 0;
		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetsoskandjean?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			
			stmt = con.createStatement();
			rst = stmt.executeQuery("SELECT * FROM Logs");
			Date date = new java.sql.Timestamp(0);
			while(rst.next()) {
				String nomC = rst.getString(COLUMN_LOGS_NOM);
				if(c.getNom().equals(nomC) && ((date.before(rst.getDate(COLUMN_LOGS_DATE))) || (date == rst.getDate(COLUMN_LOGS_DATE)))) {
					date = rst.getDate(COLUMN_LOGS_DATE);
					valeur = rst.getDouble(COLUMN_LOGS_VALEUR);	
				}
			}
			
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				stmt.close();
				rst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		
		return valeur;
	}
		
	public TreeMap<String, Capteur> getAllCapteurs(){ 
		
		// TreeMap ===>>> nom capteur - capteur
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rst = null;
		TreeMap<String, Capteur> liste = new TreeMap<>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetsoskandjean?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			
			stmt = con.createStatement();
			rst = stmt.executeQuery("SELECT * FROM Capteur");
			
			String nomC;
			Ressources typeC;
			String batimentC;
			int etageC;
			String lieuC;
			double seuilMinC;
			double seuilMaxC;
			
			while(rst.next()) {
				nomC = rst.getString(COLUMN_CAPTEUR_NOM);				
				typeC = Enum.valueOf(Ressources.class, rst.getString(COLUMN_CAPTEUR_TYPE));				
				batimentC = rst.getString(COLUMN_CAPTEUR_BATIMENT);
				etageC = rst.getInt(COLUMN_CAPTEUR_ETAGE);
				lieuC = rst.getString(COLUMN_CAPTEUR_LIEU);
				seuilMinC = rst.getDouble(COLUMN_CAPTEUR_SEUILMIN);
				seuilMaxC = rst.getDouble(COLUMN_CAPTEUR_SEUILMAX);
				Capteur x = new Capteur(nomC, typeC, batimentC,etageC, lieuC);
				x.setSeuilMin(seuilMinC);
				x.setSeuilMax(seuilMaxC);
				liste.put(nomC, x);
			}
			
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				stmt.close();
				rst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		return liste;
	}
	
	public void setNewCapteur(String nom, Ressources type, String batiment, int etage, String lieu) {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetsoskandjean?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			String INSERT_RECORD = "insert into capteur values(?, ?, ?, ?, ?, ?, ?, ?,  ?)";
			pstmt = con.prepareStatement(INSERT_RECORD);
			Capteur c = new Capteur(nom, type, batiment, etage, lieu);
			
			Date date = new Date();
		    Timestamp sqlTimestamp = new Timestamp(date.getTime());
			
		    pstmt.setString(COLUMN_CAPTEUR_NOM, nom);
		    pstmt.setString(COLUMN_CAPTEUR_TYPE, type.toString());
		    pstmt.setString(COLUMN_CAPTEUR_BATIMENT, batiment);
		    pstmt.setInt(COLUMN_CAPTEUR_ETAGE, etage);
		    pstmt.setString(COLUMN_CAPTEUR_LIEU, lieu);
		    pstmt.setDouble(COLUMN_CAPTEUR_SEUILMIN, c.getSeuilMin());
		    pstmt.setDouble(COLUMN_CAPTEUR_SEUILMAX, c.getSeuilMax());
		    pstmt.setString(COLUMN_CAPTEUR_SURNOM, nom);
		    pstmt.setTimestamp(COLUMN_CAPTEUR_DATE, sqlTimestamp);
		    
		    pstmt.executeUpdate();
		    
		    
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		    
		}
	}
	
	public void updateCapteur(Capteur c) {
		// le trouver dans la BDD et le modifier dans la base de donnee avec ses nouvelles informations
		Connection con = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetsoskandjean?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rst = stmt.executeQuery("SELECT * FROM Capteur");
			String nomC;
			boolean estTrouve = false;
			while(!estTrouve && rst.next()) {
				nomC = rst.getString(COLUMN_CAPTEUR_NOM);
				if(c.getNom().equals(nomC)) estTrouve = true;
			}
			if(!estTrouve) {
				setNewCapteur(c.getNom(), c.getType(), c.getBatiment(), c.getEtage(), c.getLieu());
				rst.updateDouble(COLUMN_CAPTEUR_SEUILMIN, c.getSeuilMin());
				rst.updateDouble(COLUMN_CAPTEUR_SEUILMAX, c.getSeuilMax());
			}else if(!(c.getType().toString().equals(rst.getString(2)) && c.getBatiment().equals(rst.getString(COLUMN_CAPTEUR_BATIMENT)) && (c.getEtage() == rst.getInt(COLUMN_CAPTEUR_ETAGE)) && c.getLieu().equals(rst.getString(COLUMN_CAPTEUR_LIEU)) && (c.getSeuilMin() == rst.getDouble(COLUMN_CAPTEUR_SEUILMIN))) && (c.getSeuilMax()==(rst.getDouble(COLUMN_CAPTEUR_SEUILMAX)))) 
			{
				if(!c.getType().toString().equals(rst.getString(COLUMN_CAPTEUR_TYPE))) 
					rst.updateString(COLUMN_CAPTEUR_TYPE, c.getType().toString());
				if(!c.getBatiment().equals(rst.getString(COLUMN_CAPTEUR_BATIMENT)))
					rst.updateString(COLUMN_CAPTEUR_BATIMENT, c.getBatiment());
				if(c.getEtage() != rst.getInt(COLUMN_CAPTEUR_ETAGE))
					rst.updateInt(COLUMN_CAPTEUR_ETAGE, c.getEtage());
				if(!c.getLieu().equals(rst.getString(COLUMN_CAPTEUR_LIEU)))
					rst.updateString(COLUMN_CAPTEUR_LIEU, c.getLieu());
				if(c.getSeuilMin() !=  rst.getDouble(COLUMN_CAPTEUR_SEUILMIN)) 
					rst.updateDouble(COLUMN_CAPTEUR_SEUILMIN, c.getSeuilMin());
				if(c.getSeuilMax() != rst.getDouble(COLUMN_CAPTEUR_SEUILMAX))
					rst.updateDouble(COLUMN_CAPTEUR_SEUILMAX, c.getSeuilMax());
				
				rst.updateRow();
			}
			
			
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				stmt.close();
				rst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private boolean between(Date date, Date debut, Date fin) {
		int comparaison = date.compareTo(debut);
		if(comparaison == 0) return true;
		else if(comparaison<0) {
			comparaison = date.compareTo(fin);
			if(comparaison == 0 || comparaison <0) return true;
		}
		return false;
	}
	
	public TreeMap<Date, Double> getCapteurValeurs(Capteur c, Date debut, Date fin){
		TreeMap<Date, Double> t = new TreeMap<>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			Double valeur;
			Date date;
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetsoskandjean?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			
			stmt = con.createStatement();
			rst = stmt.executeQuery("SELECT * FROM Logs");
			
			while(rst.next()) {
				String nomC = rst.getString(COLUMN_LOGS_NOM);
				if(c.getNom().equals(nomC) && (between(rst.getDate(COLUMN_LOGS_DATE), debut, fin))) {
					date = rst.getDate(COLUMN_LOGS_DATE);
					valeur = rst.getDouble(COLUMN_LOGS_VALEUR);	
					t.put(date,  valeur);
				}
			}
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				stmt.close();
				rst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return t;
	}
	
}
