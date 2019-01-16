package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    private TreeMap<String, Capteur> mapCapteurs;
    private DefaultCategoryDataset dataChart = new DefaultCategoryDataset();
    private ArrayList<JPanel> listeEau = new ArrayList<>();
    private ArrayList<JPanel> listeElec = new ArrayList<>();
    private ArrayList<JPanel> listeAir = new ArrayList<>();
    private ArrayList<JPanel> listeTemp = new ArrayList<>();

    // Pannel
    JPanel conteneurCapteurs = new JPanel();
    JFrame ic = new JFrame();
    SocketManager sm;
    DBUtility log;

    /**
     * @category Constructor
     */

    public UserInterface(SocketManager s, DBUtility l) {
        log = l;
        sm = s;

        sm.addUIandDBUtility(this, log);

        ic.add(connecterPort());
        ic.pack();
        // DÃ©finit un titre pour notre fenÃªtre
        ic.setTitle("Gestion de capteurs");
        // Positionnement au centre
        ic.setLocationRelativeTo(null);
        ic.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ic.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent event) {
        		log.closeDBConnection();
        		ic.dispose();
        		System.exit(0);
        	}
        });
        ic.setVisible(true);
    }

    public static void main(String[] args) {
        new UserInterface(new SocketManager(), new DBUtility());
    }
    
    private void addFiltreBatiment(ArrayList<JPanel> liste, String batVoulu) {
    	String nom;
    	JLabel info;
    	Iterator<JPanel> it = liste.iterator();
    	JPanel j = it.next();
    	if(j.getClass() == PanelEau.class) {
    		PanelEau i = (PanelEau) j;
    		if(i.getBat() == batVoulu) {
    			conteneurCapteurs.add(j);
    		}
    		while(it.hasNext()) {
    			i = (PanelEau) it.next();
    			if(i.getBat() == batVoulu) {
        			conteneurCapteurs.add(j);
        		}
    		}
    	}
    }

    /**
     * Cette fonction permet de cr?er l'?cran de visualisation des capteurs et du 
     *  raphique, la fenetre est la valeur retourn?e.
     * 
     * @return JPanel
     */
    public JPanel fenetreCapteurs() {
        // Fenetre des capteurs
        JPanel fenetreCap = new JPanel();
        JScrollPane fenetreCapteurs = new JScrollPane();

        fenetreCap.setLayout(new GridLayout(1, 2));
        ic.add(fenetreCap);

        JScrollBar scrollBar = new JScrollBar();
        fenetreCapteurs.setRowHeaderView(scrollBar);

        // Debut filtrage
        JPanel filtrage = new JPanel();
        fenetreCapteurs.setColumnHeaderView(filtrage);
        filtrage.setLayout(new BorderLayout());

        JLabel lblCapteurs = new JLabel(" Capteurs");
        filtrage.add(lblCapteurs);

        JLabel nbCapteurs = new JLabel("");
        nbCapteurs.setText(String.valueOf(listeAir.size()+listeEau.size()+listeTemp.size()+listeElec.size()));
        filtrage.add(nbCapteurs, BorderLayout.WEST);

        JPanel infoEtFiltrage = new JPanel();
        filtrage.add(infoEtFiltrage, BorderLayout.EAST);
        infoEtFiltrage.setLayout(new GridLayout(4, 0));

        // Checkbox de filtrage

        JCheckBox cbEau = new JCheckBox("Eau");
        cbEau.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                for (int i = 0; i < listeEau.size(); i++) {
                    //conteneurCapteurs.add(listeEau.get(i));
                    addFiltreBatiment(listeEau, "U1");
                    addFiltreBatiment(listeEau, "U2");
                    addFiltreBatiment(listeEau, "U3");
                	conteneurCapteurs.revalidate();
                    conteneurCapteurs.repaint();
                }
            } else {
                Component[] comp = conteneurCapteurs.getComponents();
                for (int i = 0; i < comp.length; i++) {
                    if (comp[i].getClass() == PanelEau.class) {
                        conteneurCapteurs.remove(comp[i]);
                        conteneurCapteurs.revalidate();
                        conteneurCapteurs.repaint();
                    }
                }
            }
        });

        cbEau.setSelected(true);
        infoEtFiltrage.add(cbEau);

        JCheckBox cbElectricite = new JCheckBox("Electricite");
        cbElectricite.addItemListener(e -> {

            if (e.getStateChange() == ItemEvent.SELECTED) {
                for (int i = 0; i < listeElec.size(); i++) {
                    conteneurCapteurs.add(listeElec.get(i));
                    conteneurCapteurs.revalidate();
                    conteneurCapteurs.repaint();
                }
            } else {
                Component[] comp = conteneurCapteurs.getComponents();
                for (int i = 0; i < comp.length; i++) {
                    if (comp[i].getClass() == PanelElec.class) {
                        conteneurCapteurs.remove(comp[i]);
                        conteneurCapteurs.revalidate();
                        conteneurCapteurs.repaint();
                    }
                }
            }

        });
        cbElectricite.setSelected(true);
        infoEtFiltrage.add(cbElectricite);

        JCheckBox cbTemperature = new JCheckBox("Temperature");
        cbTemperature.addItemListener(e -> {

            if (e.getStateChange() == ItemEvent.SELECTED) {
                for (int i = 0; i < listeTemp.size(); i++) {
                    conteneurCapteurs.add(listeTemp.get(i));
                    conteneurCapteurs.revalidate();
                    conteneurCapteurs.repaint();
                }
            } else {
                Component[] comp = conteneurCapteurs.getComponents();
                for (int i = 0; i < comp.length; i++) {
                    if (comp[i].getClass() == PanelTemp.class) {
                        conteneurCapteurs.remove(comp[i]);
                        conteneurCapteurs.revalidate();
                        conteneurCapteurs.repaint();
                    }
                }
            }

        });
        cbTemperature.setSelected(true);
        infoEtFiltrage.add(cbTemperature);

        JCheckBox cbAir = new JCheckBox("Air");
        cbAir.addItemListener(e -> {

            if (e.getStateChange() == ItemEvent.SELECTED) {
                for (int i = 0; i < listeAir.size(); i++) {
                    conteneurCapteurs.add(listeAir.get(i));
                    conteneurCapteurs.revalidate();
                    conteneurCapteurs.repaint();
                }
            } else {
                Component[] comp = conteneurCapteurs.getComponents();
                for (int i = 0; i < comp.length; i++) {
                    if (comp[i].getClass() == PanelAir.class) {
                        conteneurCapteurs.remove(comp[i]);
                        conteneurCapteurs.revalidate();
                        conteneurCapteurs.repaint();
                    }
                }
            }

        });
        cbAir.setSelected(true);
        infoEtFiltrage.add(cbAir);
        // Fin filtrage
        fenetreCap.add(fenetreCapteurs, 0, 0);

        // Debut Graph

        JFreeChart lineChart = ChartFactory.createLineChart("", "Temps", "Valeur", dataChart, PlotOrientation.VERTICAL,
                false, false, false);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        fenetreCap.add(chartPanel, 0, 1);

        setLayout(new BoxLayout(conteneurCapteurs, BoxLayout.Y_AXIS));
        fenetreCapteurs.setViewportView(conteneurCapteurs);

        return fenetreCap;
    }
    
    public void handleDBUpdatedEvent(Capteur c) {
        // New
        if (!mapCapteurs.containsKey(c.getNom())) {

            mapCapteurs.put(c.getNom(), c);
            JPanel newCapteur;

            switch (c.getType()) {
            case EAU:
                newCapteur = new PanelEau(c.getBatiment());
                listeEau.add(newCapteur);
                break;
            case AIRCOMPRIME:
                newCapteur = new PanelAir();
                listeAir.add(newCapteur);
                break;
            case ELECTRICITE:
                newCapteur = new PanelElec();
                listeElec.add(newCapteur);
                break;
            default:
                newCapteur = new PanelTemp();
                listeTemp.add(newCapteur);
                break;
            }

            setLayout(new BoxLayout(newCapteur, BoxLayout.Y_AXIS));
            // Nom - Batiment Etage
            JLabel nomloc = new JLabel(c.getNom() + " - " + c.getBatiment() + " Et." + c.getEtage());
            Font font1 = new Font("Arial", Font.ITALIC, 15);
            nomloc.setFont(font1);
            // Type - Connecte/Deconnecte
            JLabel ressconn = new JLabel(c.getType() + " - " + (c.isConnected() ? "Connecte" : "Deconnecte"));
            Font font2 = new Font("Arial", Font.BOLD, 13);
            ressconn.setFont(font2);
            // [VALEUR]
            JLabel val = new JLabel(String.valueOf(c.getValeurCourante()));
            Font fontVal = new Font("Arial", Font.BOLD, 15);
            val.setFont(fontVal);
            val.setBackground(Color.WHITE);
            // De : x ? : x
            JPanel periode = new JPanel();
            periode.setLayout(new GridLayout(2, 1));

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

            DocumentListener listenerDate = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent de) {
                    event(de);
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    event(de);
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    event(de);
                }

                private void event(DocumentEvent de) {

                    Date verif = null;
                    Boolean showornot = false;

                    try {
                        verif = dtfDate.parse(de.toString());
                    } catch (ParseException e1) {
                        verif = null;
                        showornot = true;
                        e1.printStackTrace();
                    }

                    if (de.toString().equals(dtfDate.format(verif))) {

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
                        TreeMap<Date, Double> logCapteur = log.getLogs(c, date1, date2);
                        int elem = 1;

                        for (Map.Entry<Date, Double> e : logCapteur.entrySet()) {
                            Date date = e.getKey();
                            Double valeur = e.getValue();
                            if (date.after(date1) && date.before(date2)) {
                                dataChart.addValue(valeur, "R" + elem, "Valeur");
                                dataChart.addValue(date.getTime() / 1000, "R" + elem, "Date");
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

            DocumentListener listenerTemps = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent de) {
                    event(de);
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    event(de);
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    event(de);
                }

                private void event(DocumentEvent de) {

                    Date verif = null;
                    Boolean showornot = false;

                    try {
                        verif = dtfDate.parse(de.toString());
                    } catch (ParseException e1) {
                        verif = null;
                        showornot = true;
                        e1.printStackTrace();
                    }

                    if (de.toString().equals(dtfDate.format(verif))) {

                        SimpleDateFormat form = new SimpleDateFormat("jj/mm/yyyy hh:mm:ss");
                        Date date1;
                        Date date2;
                        try {
                            date1 = form.parse(deTF.getText() + " " + deTemps.getText());
                            date2 = form.parse(aTF.getText() + " " + aTemps.getText());
                        } catch (ParseException e1) {
                            date1 = null;
                            date2 = new Date();
                            e1.printStackTrace();
                        }

                        dataChart.clear();
                        TreeMap<Date, Double> logCapteur = log.getLogs(c, date1, date2);
                        int elem = 1;
                       
                        for (Map.Entry<Date, Double> e : logCapteur.entrySet()) {
                            Date date = e.getKey();
                            double valeur = e.getValue();
                            if (date.after(date1) && date.before(date2)) {
                                dataChart.addValue(valeur, "R" + elem, "Valeur");
                                dataChart.addValue(date.getTime() / 1000, "R" + elem, "Date");
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

            periode.add(deBorne, 0, 0);
            periode.add(deTF, 0, 0);
            periode.add(deTemps, 0, 0);
            periode.add(aBorne, 1, 0);
            periode.add(aTF, 1, 0);
            periode.add(aTemps, 1, 0);

            //
            newCapteur.add(nomloc, BorderLayout.WEST);
            newCapteur.add(ressconn, BorderLayout.WEST);
            newCapteur.add(val, BorderLayout.WEST);
            //
            newCapteur.add(periode, BorderLayout.EAST);
            newCapteur.add(errorDate, BorderLayout.EAST);
            newCapteur.add(errorTemps, BorderLayout.EAST);

            // Seuils

            JPanel seuils = new JPanel();

            ImageIcon image = new ImageIcon("warning");
            JLabel warning = new JLabel("Le capteur est hors seuil ", image, JLabel.WEST);
            seuils.add(warning);

            JLabel seuilMin = new JLabel("Min :");
            JLabel seuilMax = new JLabel("Max :");
            JTextField min = new JTextField(Double.toString(c.getSeuilMin()));
            JTextField max = new JTextField(Double.toString(c.getSeuilMax()));

            JPanel sud = new JPanel();
            sud.setLayout(new GridLayout(2, 1));
            sud.add(seuilMin, 1, 1);
            sud.add(min, 1, 1);
            sud.add(seuilMax, 1, 1);
            sud.add(max, 1, 1);
            sud.add(warning, 2, 1);

            DocumentListener listenerMin = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent de) {
                    event(de);
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    event(de);
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    event(de);
                }

                private void event(DocumentEvent de) {
                    boolean error = false;

                    if (Double.parseDouble(de.toString()) > c.getValeurCourante()) {
                        error = true;
                    }
                    warning.setText(warning.getText() + " (valeur trop petite)");
                    seuils.setVisible(error);
                    seuils.revalidate();
                    seuils.repaint();
                }
            };

            DocumentListener listenerMax = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent de) {
                    event(de);
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    event(de);
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    event(de);
                }

                private void event(DocumentEvent de) {
                    boolean error = false;

                    if (Double.parseDouble(de.toString()) < c.getValeurCourante()) {
                        error = true;
                    }
                    warning.setText(warning.getText() + " (valeur trop grande)");
                    seuils.setVisible(error);
                    seuils.revalidate();
                    seuils.repaint();
                }

            };

            min.getDocument().addDocumentListener(listenerMin);
            max.getDocument().addDocumentListener(listenerMax);

            newCapteur.add(sud, BorderLayout.SOUTH);

            conteneurCapteurs.add(newCapteur);
            conteneurCapteurs.revalidate();
            conteneurCapteurs.repaint();
        } else {
            // Update
            mapCapteurs.put(c.getNom(), c);
            conteneurCapteurs.revalidate();
            conteneurCapteurs.repaint();
        }

    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    private JPanel connecterPort() {

        JPanel conteneurConnexion = new JPanel();
        JLabel portDeConnexion = new JLabel("Port de connexion : ");
        JTextField porttf = new JTextField("8952");
        JButton confirm = new JButton("Confirmer");

        confirm.addActionListener(e -> {
            if (isInteger(porttf.getText())){
                sm.startServer(Integer.parseInt(porttf.getText()));
                ic.remove(conteneurConnexion);
                ic.add(fenetreCapteurs());
                ic.revalidate();
                ic.repaint();
                ic.setSize(800, 900);
                ic.setLocationRelativeTo(null);
            } else {
                // input is not an int
                porttf.setText("8952");
                JOptionPane.showMessageDialog(null, "Attention, le numéro de port fourni doit être un entier");
            }
        });

        conteneurConnexion.setLayout(new FlowLayout());
        conteneurConnexion.add(portDeConnexion);
        conteneurConnexion.add(porttf);
        conteneurConnexion.add(confirm);

        return conteneurConnexion;
    }

    private class PanelEau extends JPanel {
    	private String batiment;
        public PanelEau(String bat) {
            super();
            this.batiment = bat;
        }
        
        public String getBat() {
        	return batiment;
        }
    }

    private class PanelElec extends JPanel {
        public PanelElec() {
            super();
        }
    }

    private class PanelAir extends JPanel {
        public PanelAir() {
            super();
        }
    }

    private class PanelTemp extends JPanel {
        public PanelTemp() {
            super();
        }
    }

}
