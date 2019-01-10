package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class UserInterface extends JFrame {
	
	private static final long serialVersionUID = 1L;
    static String[] info = { "capteur1", "capteur2", "capteur3" };
	Capteur cap1 = new Capteur(Ressources.EAU, "U4", 2, "couloir");
	Capteur cap2 = new Capteur(Ressources.AIRCOMPRIME, "U3", 2, "devant202");
	ArrayList<Capteur> listeCapteurs = new ArrayList<>();
	
	Logs log;
	
	public UserInterface (){
		
		JFrame ic = new JFrame();
	
		ic.setLayout(new GridLayout(1,2));
		
		ic.add(fenetreCapteurs());
		
	
		
	    //Définit un titre pour notre fenêtre
	    ic.setTitle("Gestion de capteurs");
	    //Définit sa taille : 400 pixels de large et 800 pixels de haut
	    ic.setSize(900, 800);
	    //Positionnement au centre
	    ic.setLocationRelativeTo(null);
	    ic.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	    ic.setVisible(true);       
	}

	public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        SocketCapteur sc = new SocketCapteur(ui);
	}
	
	public JScrollPane fenetreCapteurs() {
		//Fenetre des capteurs
		
		JScrollPane fenetreCapteurs = new JScrollPane();
		
		JScrollBar scrollBar = new JScrollBar();
		fenetreCapteurs.setRowHeaderView(scrollBar);
		
		//Debut filtrage
		JPanel filtrage = new JPanel();
		fenetreCapteurs.setColumnHeaderView(filtrage);
		filtrage.setLayout(new BorderLayout());
		
		JLabel lblCapteurs = new JLabel(" Capteurs");
		filtrage.add(lblCapteurs);
		
		JLabel nbCapteurs = new JLabel("");
		nbCapteurs.setText(String.valueOf(info.length));
		filtrage.add(nbCapteurs, BorderLayout.WEST);
		
		JPanel info_et_filtrage = new JPanel();
		filtrage.add(info_et_filtrage, BorderLayout.EAST);
		info_et_filtrage.setLayout(new GridLayout(4, 0));
		
		JCheckBox cbEau = new JCheckBox("Eau");
		info_et_filtrage.add(cbEau);
		
		JCheckBox cbElectricite = new JCheckBox("Electricite");
		info_et_filtrage.add(cbElectricite);
		
		JCheckBox cbTemperature = new JCheckBox("Temperature");
		info_et_filtrage.add(cbTemperature);
		
		JCheckBox cbGaz = new JCheckBox("Gaz");
		info_et_filtrage.add(cbGaz);
		// Fin filtrage
		

		listeCapteurs.add(cap1);
		listeCapteurs.add(cap2);
		
		JPanel conteneurCapteurs = new JPanel();
		conteneurCapteurs.setLayout(new GridLayout(listeCapteurs.size(),0));
	
		for(int i = 0; i<listeCapteurs.size();i++) {
			JPanel newCapteur = new JPanel();
			newCapteur.setLayout(new GridLayout(5,0));
			JLabel nomloc = new JLabel(listeCapteurs.get(i).getNom()+" - "+listeCapteurs.get(i).getBatiment()+" "+listeCapteurs.get(i).getEtage());
			JLabel ressconn = new JLabel(listeCapteurs.get(i).getType()+" - "+(listeCapteurs.get(i).getEstConnecte()?"Connect�":"D�connect�"));
			newCapteur.add(nomloc,BorderLayout.WEST);
			conteneurCapteurs.add(newCapteur);
		}
	

		fenetreCapteurs.setViewportView(conteneurCapteurs);
		
		return fenetreCapteurs;
	}
	
	public void handleCapteurUpdate(Capteur c) {
		//New
		if(listeCapteurs.contains(c)) {
			
		}

	}
	
}
