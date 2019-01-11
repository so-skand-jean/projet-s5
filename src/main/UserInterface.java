package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class UserInterface extends JFrame {
	
	private static final long serialVersionUID = 1L;
	TreeMap<String,Capteur> mapCapteurs;
	
	//Pannel
	JPanel conteneurCapteurs = new JPanel();
	JFrame ic = new JFrame();
	
	Logs log;
	
	public UserInterface (){
		log = new Logs();
		mapCapteurs = log.getAllCapteurs();
		
		ic.setLayout(new GridLayout(1,2));
		
		ic.add(connecterPort());
		ic.pack();
	    //DÃ©finit un titre pour notre fenÃªtre
	    ic.setTitle("Gestion de capteurs");
	    //Positionnement au centre
	    ic.setLocationRelativeTo(null);
	    ic.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	    ic.setVisible(true);       
	}

	public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        SocketCapteur sc = new SocketCapteur();
	}
	
	public JScrollPane fenetreCapteurs() {
		//Fenetre des capteurs
		
		JScrollPane fenetreCapteurs = new JScrollPane();
		ic.add(fenetreCapteurs);
		
		JScrollBar scrollBar = new JScrollBar();
		fenetreCapteurs.setRowHeaderView(scrollBar);
		
		//Debut filtrage
		JPanel filtrage = new JPanel();
		fenetreCapteurs.setColumnHeaderView(filtrage);
		filtrage.setLayout(new BorderLayout());
		
		JLabel lblCapteurs = new JLabel(" Capteurs");
		filtrage.add(lblCapteurs);
		
		JLabel nbCapteurs = new JLabel("");
		//nbCapteurs.setText(String.valueOf(mapCapteurs.size()));
		nbCapteurs.setText("NaN");
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
		
		setLayout(new BoxLayout(conteneurCapteurs,BoxLayout.Y_AXIS));
		fenetreCapteurs.setViewportView(conteneurCapteurs);
		
		return fenetreCapteurs;
	}
	
	public void handleCapteurUpdate(Capteur c) {
		//New
		if(!mapCapteurs.containsKey(c.getNom())) {
			mapCapteurs.put(c.getNom(),c);
			JPanel newCapteur = new JPanel();
			setLayout(new BoxLayout(newCapteur,BoxLayout.Y_AXIS));
			//Nom - Batiment Etage
			JLabel nomloc = new JLabel(c.getNom()+" - "+c.getBatiment()+" "+c.getEtage());
			Font font1 = new Font("Arial",Font.ITALIC,15);
			nomloc.setFont(font1);
			//Type - Connecte/Deconnecte
			JLabel ressconn = new JLabel(c.getType()+" - "+(c.getEstConnecte()?"Connecte":"Deconnecte"));
			Font font2 = new Font("Arial",Font.BOLD,13);
			ressconn.setFont(font2);
			//
			newCapteur.add(nomloc,BorderLayout.WEST);
			newCapteur.add(ressconn,BorderLayout.WEST);
			conteneurCapteurs.add(newCapteur);
		} else {
			//Update
			mapCapteurs.put(c.getNom(), c);
			conteneurCapteurs.updateUI();
		}

	}
	
	private JPanel connecterPort() {
		
		JPanel conteneurConnexion = new JPanel();
		JLabel portDeConnexion = new JLabel("Port de connexion : ");
		JTextField porttf = new JTextField("3306");
		JButton confirm = new JButton("Confirmer");
		
		confirm.addActionListener(e -> { 
								    ic.remove(conteneurConnexion);
								    ic.add(fenetreCapteurs());
								    ic.revalidate();
								    ic.repaint();
								    ic.setSize(800,900);
								    ic.setLocationRelativeTo(null);
								  });
		
		conteneurConnexion.setLayout(new FlowLayout());
		conteneurConnexion.add(portDeConnexion);
		conteneurConnexion.add(porttf);
		conteneurConnexion.add(confirm);
		
		return conteneurConnexion;
	}
	
}
