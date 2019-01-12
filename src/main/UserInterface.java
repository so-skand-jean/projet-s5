package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class UserInterface extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private TreeMap<String,Capteur> mapCapteurs;
	private DefaultCategoryDataset dataChart = new DefaultCategoryDataset();
	
	//Pannel
	JPanel conteneurCapteurs = new JPanel();
	JFrame ic = new JFrame();
	
	Logs log;
	
	public UserInterface (){
		log = new Logs();
		mapCapteurs = log.getAllCapteurs();
		
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
	
	public JPanel fenetreCapteurs() {
		//Fenetre des capteurs
		JPanel fenetreCap = new JPanel();
		JScrollPane fenetreCapteurs = new JScrollPane();
		
		fenetreCap.setLayout(new GridLayout(1,2));
		ic.add(fenetreCap);
		
		JScrollBar scrollBar = new JScrollBar();
		fenetreCapteurs.setRowHeaderView(scrollBar);
		
		//Debut filtrage
		JPanel filtrage = new JPanel();
		fenetreCapteurs.setColumnHeaderView(filtrage);
		filtrage.setLayout(new BorderLayout());
		
		JLabel lblCapteurs = new JLabel(" Capteurs");
		filtrage.add(lblCapteurs);
		
		JLabel nbCapteurs = new JLabel("");
		nbCapteurs.setText(String.valueOf(mapCapteurs.size()));
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
		fenetreCap.add(fenetreCapteurs,0,0);
		
		// Debut Graph
		
		JFreeChart lineChart = ChartFactory.createLineChart(
		         "",
		         "Temps","Valeur",
		         dataChart,
		         PlotOrientation.VERTICAL,
		         false,false,false);
		         
		ChartPanel chartPanel = new ChartPanel( lineChart );
		fenetreCap.add(chartPanel,0,1);
		
		setLayout(new BoxLayout(conteneurCapteurs,BoxLayout.Y_AXIS));
		fenetreCapteurs.setViewportView(conteneurCapteurs);
		
		return fenetreCap;
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
			//[VALEUR]
			JLabel val = new JLabel(String.valueOf(c.getValeur()));
			Font fontVal = new Font("Arial",Font.BOLD,15);
			val.setFont(fontVal);
			val.setBackground(Color.WHITE);
			//De : x � : x
			JPanel periode = new JPanel();
			periode.setLayout(new GridLayout(2,1));
			
			Date date = new Date();
			DateFormat dtfDate = new SimpleDateFormat("dd/MM/yyyy");
			
			JLabel deBorne = new JLabel("De : ");
			JTextField deTF = new JTextField("01/01/2018");
			JLabel aBorne = new JLabel("A : ");
			JTextField aTF = new JTextField(dtfDate.format(date));
			
			DateFormat dtfTemps = new SimpleDateFormat("HH:mm:ss");
			
			JTextField deTemps = new JTextField("00:00:00");
			JTextField aTemps = new JTextField(dtfTemps.format(date));
			
			JLabel errorDate = new JLabel("Err : jj/mm/yyyy");
			
			DocumentListener listenerDate = new DocumentListener(){
			    @Override
			    public void insertUpdate(DocumentEvent de){
			       event(de);
			    }

			    @Override
			    public void removeUpdate(DocumentEvent de) {
			        event(de);
			    }

			    @Override
			    public void changedUpdate(DocumentEvent de){
			        event(de);
			    }

			    private void event(DocumentEvent de){
			    	
			    	Date verif = null;
			    	Boolean showornot = false;
			    	
		    		try {
						verif = dtfDate.parse(de.toString());
					} catch (ParseException e1) {
						verif = null;
		    			showornot = true;
						e1.printStackTrace();
					}
		    		
		    		if(de.toString().equals(dtfDate.format(verif))) {
		    			
		    			SimpleDateFormat form = new SimpleDateFormat("jj/mm/yyyy");
		    			Date date1;
		    			Date date2;
						try {
							date1 = form.parse(deTF.getText());
							date2 = form.parse(aTF.getText());
						} catch (ParseException e1) {
							date1 = null;
			    			date2 = new Date();
							e1.printStackTrace();
						}
		    			
		    			dataChart.clear();
		    			TreeMap<Date,Capteur> logCapteur = new TreeMap<>();
		    			int elem = 1;
		    			/*
		    			 * ECRIRE ICI LE CODE QUI VA PIOCHER DANS LE LOG
		    			 * 
		    			 */
		    			for(Map.Entry<Date,Capteur> e : logCapteur.entrySet()) {
		    				  Date date = e.getKey();
		    				  Capteur capteur = e.getValue();
		    				  if(date.after(date1)&&date.before(date2)) {
		    					  dataChart.addValue(capteur.getValeur(), "R"+elem, "Valeur");
			    				  dataChart.addValue(date.getTime()/1000, "R"+elem, "Date");
		    				  }
		    				  elem++;
		    			}
		    		}
		    		
			        errorDate.setVisible(showornot);
			        periode.revalidate();
			        periode.repaint();
			    }
			};
			
			JLabel errorTemps = new JLabel("Err : hh:mm:ss");
			
			DocumentListener listenerTemps = new DocumentListener(){
			    @Override
			    public void insertUpdate(DocumentEvent de){
			       event(de);
			    }

			    @Override
			    public void removeUpdate(DocumentEvent de) {
			        event(de);
			    }

			    @Override
			    public void changedUpdate(DocumentEvent de){
			        event(de);
			    }

			    private void event(DocumentEvent de){
			    	
			    	Date verif = null;
			    	Boolean showornot = false;
			    	
		    		try {
						verif = dtfDate.parse(de.toString());
					} catch (ParseException e1) {
						verif = null;
		    			showornot = true;
						e1.printStackTrace();
					}
		    		
		    		if(de.toString().equals(dtfDate.format(verif))) {
		    			
		    			SimpleDateFormat form = new SimpleDateFormat("jj/mm/yyyy hh:mm:ss");
		    			Date date1;
		    			Date date2;
						try {
							date1 = form.parse(deTF.getText()+" "+deTemps.getText());
							date2 = form.parse(aTF.getText()+" "+aTemps.getText());
						} catch (ParseException e1) {
							date1 = null;
			    			date2 = new Date();
							e1.printStackTrace();
						}
		    			
		    			dataChart.clear();
		    			TreeMap<Date,Capteur> logCapteur = new TreeMap<>();
		    			int elem = 1;
		    			/*
		    			 * ECRIRE ICI LE CODE QUI VA PIOCHER DANS LE LOG
		    			 * 
		    			 */
		    			for(Map.Entry<Date,Capteur> e : logCapteur.entrySet()) {
		    				  Date date = e.getKey();
		    				  Capteur capteur = e.getValue();
		    				  if(date.after(date1)&&date.before(date2)) {
		    					  dataChart.addValue(capteur.getValeur(), "R"+elem, "Valeur");
			    				  dataChart.addValue(date.getTime()/1000, "R"+elem, "Date");
		    				  }
		    				  elem++;
		    			}

		    		}
		    		
			        errorDate.setVisible(showornot);
			        periode.revalidate();
			        periode.repaint();
			    }
			};
			
			
			deTF.getDocument().addDocumentListener(listenerDate);
			aTF.getDocument().addDocumentListener(listenerDate);
			deTemps.getDocument().addDocumentListener(listenerTemps);
			aTemps.getDocument().addDocumentListener(listenerTemps);
			
			periode.add(deBorne,0,0);
			periode.add(deTF,0,0);
			periode.add(deTemps,0,0);
			periode.add(aBorne,1,0);
			periode.add(aTF,1,0);
			periode.add(aTemps,1,0);
			
			//Action -> Graph
			/*newCapteur.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    dataChart.clear();
                    while(logs.getValeurs(c,)) {
                    	dataChart.addValue(c.getValeur(), "R1", "C1");
                    }
                }

            });*/
			//
			newCapteur.add(nomloc,BorderLayout.WEST);
			newCapteur.add(ressconn,BorderLayout.WEST);
			newCapteur.add(val,BorderLayout.WEST);
			//
			newCapteur.add(periode,BorderLayout.EAST);
			newCapteur.add(errorDate);
			newCapteur.add(errorTemps);
			//
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
