package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeMap;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;

public class DBUtility {
    private TreeMap<String, Capteur> allCapteurs = new TreeMap<>();
    private Connection con;
    private UserInterface ui;

    public DBUtility() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/solange_jean_skander_projets5?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Paris",
                    "root", "");
            initAllCapteurs();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * fonction de déconnexion de la base de données
     */
    public void closeDBConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initAllCapteurs() {
        Capteur tmp;
        try {
            ResultSet r = con.createStatement().executeQuery(
                    "SELECT cpt_nom, cpt_surnom, cpt_type, cpt_batiment, cpt_etage, cpt_info_lieu, cpt_seuil_min, cpt_seuil_max, cpt_date_depassement FROM capteur");
            while (r.next()) {
                tmp = new Capteur();
                tmp.hydrate(r.getString("cpt_nom"), r.getString("cpt_surnom"),
                        TypeCapteur.valueOf(r.getString("cpt_type")), r.getString("cpt_batiment"),
                        r.getInt("cpt_etage"), r.getString("cpt_info_lieu"), r.getDouble("cpt_seuil_min"),
                        r.getDouble("cpt_seuil_max"), r.getDate("cpt_date_depassement"));
                allCapteurs.put(tmp.getNom(), tmp);
            }
            r.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TreeMap<String, Capteur> getAllCapteurs() {
        return allCapteurs;
    }

    public void handleNewCapteurFromSocketManager(UserInterface ui, Capteur cpt) {
        if (!allCapteurs.containsKey(cpt.getNom())) {
            // new capteur
            queryWithNoReturn(
                    "INSERT INTO capteur (cpt_nom, cpt_surnom, cpt_type, cpt_batiment, cpt_etage, cpt_info_lieu, cpt_seuil_min, cpt_seuil_max) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    cpt.getNom(), cpt.getSurnom(), cpt.getType(), cpt.getBatiment(), cpt.getEtage(), cpt.getInfoLieu(),
                    cpt.getSeuilMin(), cpt.getSeuilMax());
            allCapteurs.put(cpt.getNom(), cpt); // overwrite the capteur in local treemap
        } else {
            Capteur localCpt = allCapteurs.get(cpt.getNom());
            cpt.hydrate(localCpt.getNom(), localCpt.getSurnom(),

                    cpt.getType(), cpt.getBatiment(), cpt.getEtage(), cpt.getInfoLieu(),

                    localCpt.getSeuilMin(), localCpt.getSeuilMax(), localCpt.getDateDeDepassement());
            queryWithNoReturn(
                    "UPDATE capteur SET cpt_surnom = ?, cpt_type = ?, cpt_batiment = ?, cpt_etage = ?, cpt_info_lieu = ?, cpt_seuil_min = ?, cpt_seuil_max = ?, cpt_date_depassement = ? WHERE cpt_nom = ?",
                    cpt.getSurnom(), cpt.getType(), cpt.getBatiment(), cpt.getEtage(), cpt.getInfoLieu(),
                    cpt.getSeuilMin(), cpt.getSeuilMax(), cpt.getDateDeDepassement(), cpt.getNom());
        }
        cpt.setEstConnecte(this, ui, true);
    }

    public TreeMap<Date, Double> getLogs(Capteur c, Date debut, Date fin) {
        TreeMap<Date, Double> t = new TreeMap<>();
        try {
            PreparedStatement s = con.prepareStatement(
                    "SELECT log_datetime, log_valeur FROM logs WHERE log_cpt_nom = ? AND log_datetime > ? AND log_datetime < ?");
            s.setString(0, c.getNom());

            s.setTimestamp(1, new Timestamp(debut.getTime()));
            s.setTimestamp(2, new Timestamp(fin.getTime()));
            ResultSet r = s.executeQuery();

            while (r.next()) {
                t.put(r.getDate("log_datetime"), r.getDouble("log_valeur"));
            }
            s.close();
            r.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return t;
    }

    private void queryWithNoReturn(String query, Object... args) {
        try {
            PreparedStatement s = con.prepareStatement(query);
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String) {
                    s.setString(i + 1, (String) args[i]);
                } else if (args[i] instanceof TypeCapteur) {
                    s.setString(i + 1, ((TypeCapteur) args[i]).name());
                } else if (args[i] instanceof Double) {
                    s.setDouble(i + 1, (Double) args[i]);
                } else if (args[i] instanceof Integer) {
                    s.setInt(i + 1, (Integer) args[i]);
                } else if (args[i] instanceof Date) {
                    s.setTimestamp(i + 1, new Timestamp(((Date) args[i]).getTime()));
                } else if (args[i] == null) {
                    s.setNull(i + 1, Types.INTEGER);
                }
            }

            s.execute();
            s.close();
        } catch (SQLException e) {
            System.err.println(query + "\n" + Arrays.toString(args));
            e.printStackTrace();
        }
    }

    private void updateCptFieldInDB(String cptNom, String fieldName, Object fieldValue) {
        queryWithNoReturn("UPDATE capteur SET " + fieldName + " = ? WHERE cpt_nom = ?", fieldValue, cptNom);
    }

    public void updateSeuilMaxCptInDB(String cptNom, double seuilMax) {
        updateCptFieldInDB(cptNom, "cpt_seuil_max", seuilMax);
    }

    public void updateSeuilMinCptInDB(String cptNom, double seuilMin) {
        updateCptFieldInDB(cptNom, "cpt_seuil_min", seuilMin);
    }

    public void updateSurnomCptInDB(String cptNom, String surnom) {
        updateCptFieldInDB(cptNom, "cpt_surnom", surnom);
    }

    public void updateValeurCouranteCptInDB(UserInterface ui, String cptNom, Date datetime, double valeurCourante, boolean isOffRange) {
        queryWithNoReturn("INSERT INTO logs (log_cpt_nom, log_datetime, log_valeur) VALUES(?, ?, ?)", cptNom, datetime,
                valeurCourante);
        if (isOffRange) {
            updateCptFieldInDB(cptNom, "cpt_date_depassement", datetime);
        }
        ui.handleDBUpdatedEvent(allCapteurs.get(cptNom));
    }

    public void updateUICapteur(UserInterface ui, Capteur cpt) {
        allCapteurs.put(cpt.getNom(), cpt);
        ui.handleDBUpdatedEvent(cpt);
    }
}
