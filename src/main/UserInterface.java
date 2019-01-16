package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
    private TreeMap<String, Capteur> mapCapteurs = new TreeMap<>();
    private DefaultCategoryDataset dataChart = new DefaultCategoryDataset();
    private ArrayList<JPanel> listeEau = new ArrayList<>();
    private ArrayList<JPanel> listeElec = new ArrayList<>();
    private ArrayList<JPanel> listeAir = new ArrayList<>();
    private ArrayList<JPanel> listeTemp = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private Set<JPanel> listeAffiches = new TreeSet<>(new ComparatorPanel());

    // Pannel
    JPanel conteneurCapteurs = new JPanel();
    JScrollPane fenetreCapteurs = new JScrollPane();
    JFrame ic = new JFrame();
    SocketManager sm;
    DBUtility db;

    /**
     * @category Constructor
     */

    public UserInterface(SocketManager s, DBUtility dbu) {
        db = dbu;
        sm = s;

        sm.addUIandDBUtility(this, db);

        ic.add(connecterPort());
        ic.pack();
        // Définit un titre pour notre fenêtre
        ic.setTitle("Gestion de capteurs");
        // Positionnement au centre
        ic.setLocationRelativeTo(null);
        ic.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ic.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                db.closeDBConnection();
                ic.dispose();
                System.exit(0);
            }
        });
        ic.setVisible(true);
    }

    public static void main(String[] args) {
        new UserInterface(new SocketManager(), new DBUtility());
    }

    /*
     * private void addFiltreBatiment(ArrayList<JPanel> liste, String batVoulu) {
     * Iterator<JPanel> it = liste.iterator(); if(it.hasNext()) { JPanel j =
     * it.next(); if(j.getClass() == PanelEau.class) { PanelEau i = (PanelEau) j;
     * if(i.getBat() == batVoulu) { conteneurCapteurs.add(j); } while(it.hasNext())
     * { i = (PanelEau) it.next(); if(i.getBat() == batVoulu) {
     * conteneurCapteurs.add(j); } } } else if(j.getClass() == PanelElec.class) {
     * PanelElec i = (PanelElec) j; if(i.getBat() == batVoulu) {
     * conteneurCapteurs.add(j); } while(it.hasNext()) { i = (PanelElec) it.next();
     * if(i.getBat() == batVoulu) { conteneurCapteurs.add(j); } } } else
     * if(j.getClass() == PanelTemp.class) { PanelTemp i = (PanelTemp) j;
     * if(i.getBat() == batVoulu) { conteneurCapteurs.add(j); } while(it.hasNext())
     * { i = (PanelTemp) it.next(); if(i.getBat() == batVoulu) {
     * conteneurCapteurs.add(j); } } } else if(j.getClass() == PanelAir.class) {
     * PanelAir i = (PanelAir) j; if(i.getBat() == batVoulu) {
     * conteneurCapteurs.add(j); } while(it.hasNext()) { i = (PanelAir) it.next();
     * if(i.getBat() == batVoulu) { conteneurCapteurs.add(j); } } } } }
     */

    private void addFiltre(Set<JPanel> liste) {
        Iterator<JPanel> it = liste.iterator();
        while (it.hasNext()) {
            conteneurCapteurs.add(it.next());
        }
    }

    /**
     * Cette fonction permet de cr?er l'?cran de visualisation des capteurs et du
     * raphique, la fenetre est la valeur retourn?e.
     * 
     * @return JPanel
     */
    public JPanel fenetreCapteurs() {
        // Fenetre des capteurs
        JPanel fenetreCap = new JPanel();

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
        nbCapteurs.setText(String.valueOf(mapCapteurs.size()));
        filtrage.add(nbCapteurs, BorderLayout.WEST);

        JPanel infoEtFiltrage = new JPanel();
        filtrage.add(infoEtFiltrage, BorderLayout.EAST);
        infoEtFiltrage.setLayout(new GridLayout(4, 0));

        // Checkbox de filtrage

        JCheckBox cbEau = new JCheckBox("Eau");
        cbEau.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                /*
                 * addFiltreBatiment(listeEau, "U1"); addFiltreBatiment(listeEau, "U2");
                 * addFiltreBatiment(listeEau, "U3");
                 */
                listeAffiches.addAll(listeEau);
                conteneurCapteurs.removeAll();
                addFiltre(listeAffiches);
                conteneurCapteurs.revalidate();
                conteneurCapteurs.repaint();
            } else {
                Component[] comp = conteneurCapteurs.getComponents();
                /*
                 * for (int i = 0; i < comp.length; i++) { if (comp[i].getClass() ==
                 * PanelEau.class) { conteneurCapteurs.remove(comp[i]);
                 * conteneurCapteurs.revalidate(); conteneurCapteurs.repaint(); } }
                 */
                conteneurCapteurs.removeAll();
                listeAffiches.removeAll(listeElec);
                addFiltre(listeAffiches);
                conteneurCapteurs.revalidate();
                conteneurCapteurs.repaint();
            }
        });

        cbEau.setSelected(true);
        infoEtFiltrage.add(cbEau);

        JCheckBox cbElectricite = new JCheckBox("Electricite");
        cbElectricite.addItemListener(e -> {

            if (e.getStateChange() == ItemEvent.SELECTED) {
                /*
                 * addFiltreBatiment(listeElec, "U1"); addFiltreBatiment(listeElec, "U2");
                 * addFiltreBatiment(listeElec, "U3");
                 */
                listeAffiches.addAll(listeElec);
                conteneurCapteurs.removeAll();
                addFiltre(listeAffiches);
                conteneurCapteurs.revalidate();
                conteneurCapteurs.repaint();
            } else {
                Component[] comp = conteneurCapteurs.getComponents();
                /*
                 * for (int i = 0; i < comp.length; i++) { if (comp[i].getClass() ==
                 * PanelElec.class) { conteneurCapteurs.remove(comp[i]);
                 * conteneurCapteurs.revalidate(); conteneurCapteurs.repaint(); } }
                 */
                conteneurCapteurs.removeAll();
                listeAffiches.removeAll(listeElec);
                addFiltre(listeAffiches);
                conteneurCapteurs.revalidate();
                conteneurCapteurs.repaint();
            }

        });
        cbElectricite.setSelected(true);
        infoEtFiltrage.add(cbElectricite);

        JCheckBox cbTemperature = new JCheckBox("Temperature");
        cbTemperature.addItemListener(e -> {

            if (e.getStateChange() == ItemEvent.SELECTED) {
                /*
                 * addFiltreBatiment(listeTemp, "U1"); addFiltreBatiment(listeTemp, "U2");
                 * addFiltreBatiment(listeTemp, "U3");
                 */
                listeAffiches.addAll(listeTemp);
                conteneurCapteurs.removeAll();
                addFiltre(listeAffiches);
                conteneurCapteurs.revalidate();
                conteneurCapteurs.repaint();
            } else {
                Component[] comp = conteneurCapteurs.getComponents();
                /*
                 * for (int i = 0; i < comp.length; i++) { if (comp[i].getClass() ==
                 * PanelTemp.class) { conteneurCapteurs.remove(comp[i]);
                 * conteneurCapteurs.revalidate(); conteneurCapteurs.repaint(); } }
                 */
                conteneurCapteurs.removeAll();
                listeAffiches.removeAll(listeTemp);
                addFiltre(listeAffiches);
                conteneurCapteurs.revalidate();
                conteneurCapteurs.repaint();
            }

        });
        cbTemperature.setSelected(true);
        infoEtFiltrage.add(cbTemperature);

        JCheckBox cbAir = new JCheckBox("Air");
        cbAir.addItemListener(e -> {

            if (e.getStateChange() == ItemEvent.SELECTED) {
                /*
                 * addFiltreBatiment(listeAir, "U1"); addFiltreBatiment(listeAir, "U2");
                 * addFiltreBatiment(listeAir, "U3");
                 */
                listeAffiches.addAll(listeTemp);
                conteneurCapteurs.removeAll();
                addFiltre(listeAffiches);
                conteneurCapteurs.revalidate();
                conteneurCapteurs.repaint();
            } else {
                Component[] comp = conteneurCapteurs.getComponents();
                /*
                 * for (int i = 0; i < comp.length; i++) { if (comp[i].getClass() ==
                 * PanelAir.class) { conteneurCapteurs.remove(comp[i]);
                 * conteneurCapteurs.revalidate(); conteneurCapteurs.repaint(); } }
                 */
                conteneurCapteurs.removeAll();
                listeAffiches.removeAll(listeAir);
                addFiltre(listeAffiches);
                conteneurCapteurs.revalidate();
                conteneurCapteurs.repaint();
            }

        });
        cbAir.setSelected(true);
        infoEtFiltrage.add(cbAir);

        // Fin filtrage
        fenetreCap.add(fenetreCapteurs, 0, 0);

        // Graphique

        JFreeChart lineChart = ChartFactory.createLineChart("", "Temps", "Valeur", dataChart, PlotOrientation.VERTICAL,
                false, false, false);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        fenetreCap.add(chartPanel);

        conteneurCapteurs.setLayout(new BoxLayout(conteneurCapteurs, BoxLayout.Y_AXIS));
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
                newCapteur = new PanelAir(c.getBatiment());
                listeAir.add(newCapteur);
                break;
            case ELECTRICITE:
                newCapteur = new PanelElec(c.getBatiment());
                listeElec.add(newCapteur);
                break;
            default:
                newCapteur = new PanelTemp(c.getBatiment());
                listeTemp.add(newCapteur);
                break;
            }

            newCapteur.setLayout(new BoxLayout(newCapteur, BoxLayout.Y_AXIS));
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
            periode.setLayout(new GridLayout(2,4));

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
                        TreeMap<Date, Double> logCapteur = db.getLogs(c, date1, date2);
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
                    try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                    //periode.revalidate();
                    //periode.repaint();
                }
            };

            JLabel errorTemps = new JLabel("Err : hh:mm:ss");

            DocumentListener listenerTemps = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent de) {
                	try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                	event(de);
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                	try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                	event(de);
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                	try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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
                        TreeMap<Date, Double> logCapteur = db.getLogs(c, date1, date2);
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
                    //periode.revalidate();
                    //periode.repaint();
                }
            };

            deTF.getDocument().addDocumentListener(listenerDate);
            aTF.getDocument().addDocumentListener(listenerDate);
            deTemps.getDocument().addDocumentListener(listenerTemps);
            aTemps.getDocument().addDocumentListener(listenerTemps);

            periode.add(deBorne);
            periode.add(deTF);
            periode.add(deTemps);
            periode.add(aBorne);
            periode.add(aTF);
            periode.add(aTemps);

            //
            newCapteur.add(nomloc, Component.LEFT_ALIGNMENT);
            newCapteur.add(ressconn, Component.LEFT_ALIGNMENT);
            newCapteur.add(val, Component.LEFT_ALIGNMENT);
            //
            newCapteur.add(periode, Component.LEFT_ALIGNMENT);
            newCapteur.add(errorDate, Component.LEFT_ALIGNMENT);
            newCapteur.add(errorTemps, Component.LEFT_ALIGNMENT);

            // Seuils

            ImageIcon image = new ImageIcon("warning.png");
            Image img = image.getImage();
            BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(img, 0, 0, 20, 20, null);
            ImageIcon newimage = new ImageIcon(bi);
            JLabel warning = new JLabel("Le capteur est hors seuil ", newimage, JLabel.LEFT);

            JLabel seuilMin = new JLabel("Min :");
            JLabel seuilMax = new JLabel("Max :");
            JTextField min = new JTextField(Double.toString(c.getSeuilMin()));
            JTextField max = new JTextField(Double.toString(c.getSeuilMax()));

            JPanel minPanel = new JPanel();
            JPanel maxPanel = new JPanel();
            minPanel.setLayout(new FlowLayout());
            maxPanel.setLayout(new FlowLayout());
            minPanel.add(seuilMin);
            minPanel.add(min);
            maxPanel.add(seuilMax);
            maxPanel.add(max);
            warning.setVisible(false);
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
                    warning.setVisible(error);
                    try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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
                    warning.setVisible(error);
                }

            };

            min.getDocument().addDocumentListener(listenerMin);
            max.getDocument().addDocumentListener(listenerMax);

            newCapteur.add(minPanel,Component.LEFT_ALIGNMENT);
            newCapteur.add(maxPanel,Component.LEFT_ALIGNMENT);
            newCapteur.add(warning,Component.LEFT_ALIGNMENT);
            conteneurCapteurs.add(newCapteur);
            fenetreCapteurs.revalidate();
            fenetreCapteurs.repaint();
        } else {
            // Update
            mapCapteurs.put(c.getNom(), c);
            fenetreCapteurs.revalidate();
            fenetreCapteurs.repaint();
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
            if (isInteger(porttf.getText())) {
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
                JOptionPane.showMessageDialog(null, "Attention, le numero de port fourni doit etre un entier");
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
        private String batiment;

        public PanelElec(String bat) {
            super();
            this.batiment = bat;
        }

        public String getBat() {
            return batiment;
        }
    }

    private class PanelAir extends JPanel {
        private String batiment;

        public PanelAir(String bat) {
            super();
            this.batiment = bat;
        }

        public String getBat() {
            return batiment;
        }
    }

    private class PanelTemp extends JPanel {
        private String batiment;

        public PanelTemp(String bat) {
            super();
            this.batiment = bat;
        }

        public String getBat() {
            return batiment;
        }
    }

    private class ComparatorPanel implements Comparator<JPanel> {
        public int compare(JPanel o1, JPanel o2) {
            String bat1 = null;
            String bat2 = null;
            if (o1.getClass() == PanelEau.class) {
                PanelEau j1 = (PanelEau) o1;
                bat1 = j1.getBat();
            } else if (o1.getClass() == PanelAir.class) {
                PanelAir j1 = (PanelAir) o1;
                bat1 = j1.getBat();
            } else if (o1.getClass() == PanelTemp.class) {
                PanelTemp j1 = (PanelTemp) o1;
                bat1 = j1.getBat();
            } else if (o1.getClass() == PanelElec.class) {
                PanelElec j1 = (PanelElec) o1;
                bat1 = j1.getBat();
            }
            if (o2.getClass() == PanelEau.class) {
                PanelEau j2 = (PanelEau) o2;
                bat2 = j2.getBat();
            } else if (o2.getClass() == PanelAir.class) {
                PanelAir j2 = (PanelAir) o2;
                bat2 = j2.getBat();
            } else if (o2.getClass() == PanelTemp.class) {
                PanelTemp j2 = (PanelTemp) o2;
                bat2 = j2.getBat();
            } else if (o2.getClass() == PanelElec.class) {
                PanelElec j2 = (PanelElec) o2;
                bat2 = j2.getBat();
            }
            return bat1.compareTo(bat2);
        }
    }
}