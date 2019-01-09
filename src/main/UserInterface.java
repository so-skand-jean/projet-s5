package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class UserInterface extends JFrame {
	
	private static final long serialVersionUID = 1L;
    static String[] info = { "capteur1", "capteur2", "capteur3" };
	
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
        new UserInterface();
        SocketCapteur.runSocket();
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
		
		return fenetreCapteurs();
	}
	
}
