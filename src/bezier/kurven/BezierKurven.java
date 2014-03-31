/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bezier.kurven;
//imports

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 *
 * @author Elias
 */
public class BezierKurven extends JApplet {

    //Variablen
    private int zaehler = 0; //zählt die erstellten Punkte
    private Point punkte[][] = new Point[30][30];//speichert die Punkte
    private Point kurve[] = new Point[30]; //speichert die Punkte der Bérset Kurve
    private JPanel kordPunkt[] = new JPanel[30], menu; //zum anzeigen der Punkte auf dem Bildschirm
    JComboBox PunktAuswahl;
    JButton verschieben;
    JLabel lP, lXKord, lYKord;
    JTextField eingabeX, eingabeY;
    //Objekte
    private JPanel hintergrund;
    private MouseEvent event; //zum spiechern des Mouse events

    /**
     * Initialization method that will be called after the applet is loaded into
     * the browser.
     */
    @Override
    public void init() {
        initObjekte();//erstellen der Objekte
        initListener();//ertellen der Listener
        createPopupMenu();//ertellen eines Mausklick Menüs
        this.setSize(1000, 600);//setzten der Grösse
    }
    private void initListener(){
        PunktAuswahl.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                int index = PunktAuswahl.getSelectedIndex();
                try {
                    eingabeX.setText("" + punkte[0][index].x);
                    eingabeY.setText("" + punkte[0][index].y);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error ", "Bézier Kurven", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        verschieben.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int index = PunktAuswahl.getSelectedIndex();
                punkte[0][index].x = Integer.parseInt(eingabeX.getText());
                punkte[0][index].y = Integer.parseInt(eingabeY.getText());
                hintergrund.repaint();
                ResetKordinatenPunkte();
                try {
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error ", "Bézier Kurven", JOptionPane.WARNING_MESSAGE);
                }

            }
        });
    }
    private void initObjekte() {
        Container contentPane = getContentPane();//Container erstellen
        contentPane.setLayout(new BorderLayout());//Layout des Containers bestimmen
        //Objekterstellung
        menu = new JPanel();
        lP = new JLabel("Punkt:");
        PunktAuswahl = new JComboBox();
        lXKord = new JLabel("X-Kord.");
        eingabeX = new JTextField();
        eingabeX.setPreferredSize(new Dimension(40, 20));
        lYKord = new JLabel("Y-Kord.");
        eingabeY = new JTextField();
        eingabeY.setPreferredSize(new Dimension(40, 20));
        verschieben = new JButton("Punkt verschieben");
        //Objekte zum menu hinzufügen
//        contentPane.add(menu,BorderLayout.NORTH);
        menu.add(lP);
        menu.add(PunktAuswahl);
        menu.add(lXKord);
        menu.add(eingabeX);
        menu.add(lYKord);
        menu.add(eingabeY);
        menu.add(verschieben);
        //Erstellung eines Hintergrund und Positionierung
        hintergrund = new DrawPanel();
        contentPane.add(hintergrund, BorderLayout.CENTER);
        //Eigenschaften des Hintergrundes
        hintergrund.setBackground(Color.white);
        //InfoLabel
        JLabel info = new JLabel("Es können maximal 30 Punkte übergeben werden.");
        hintergrund.add(info);
    }
    private void createPopupMenu() {
        JMenuItem menuNeuerPunkt, menuKurve, menuReset;//Die Menü-unter-Punkte
        //Create the popup menu.
        JPopupMenu popup = new JPopupMenu();
        menuNeuerPunkt = new JMenuItem("Einen neuen Punkt erzeugen");
        menuNeuerPunkt.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                getMouseLocation();
            }
        });
        popup.add(menuNeuerPunkt);
        menuKurve = new JMenuItem("Bézier Kurve zeichnen");
        menuKurve.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                Punkterechnen();
            }
        });
        popup.add(menuKurve);
        menuReset = new JMenuItem("Reset");
        menuReset.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                ResetHintergrundPunkte();
            }
        });
        popup.add(menuReset);
        //Add listener to the text area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(popup);
        hintergrund.addMouseListener(popupListener);
    }
    private void ResetHintergrundPunkte() {
        //Reset der Variablen und des Hintergrundes
        for (int index = 0; index < 30; index++) {
            kurve[index] = null;
            try {
                hintergrund.remove(kordPunkt[index]);
            } catch (Exception ex) {

            }
            for (int i = 0; i < punkte.length; i++) {
                punkte[index][i] = null;
            }
        }
        hintergrund.repaint();
        zaehler = 0;
    }
    private void ResetBersetKurve() {
        //Reset der Bersét-Kurve
        for (int index = 0; index < kurve.length; index++) {
            kurve[index] = null;
        }
    }
    
    private void ResetKordinatenPunkte() {
        for (int index = 0; index < kordPunkt.length; index++) {
            try {
                kordPunkt[index].setLocation(punkte[0][index]);
            } catch (Exception ex) {
            }
        }
    }

    //erstellen eines neuen Punktes auf Knopfdruck 
    private void getMouseLocation() {
        //auslesen der Position der Maus
        Point p = event.getPoint();
        //übergeben der Daten
        setPunkt(p);
        ResetKordinatenPunkte();
    }

    //setzten der Punkte
    public void setPunkt(Point p) {
        //es werden die Punkte mit den Mauskordinaten gesetzt, sofern es noch Platz hat
        try {
            punkte[0][zaehler] = new Point(p);
            kordPunkt[zaehler] = new JPanel();
            kordPunkt[zaehler].setSize(10, 10);
            kordPunkt[zaehler].setBackground(Color.red);
            kordPunkt[zaehler].setName("" + zaehler);
            hintergrund.add(kordPunkt[zaehler]);
            hintergrund.repaint();
            kordPunkt[zaehler].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                }
            });
            kordPunkt[zaehler].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent event) {
                    //beim Losslassen der  Maus wird die aktuelle Mausposition in die Liste und das Panel geschrieben, anschliessend wird der Hintergrund neu ausgegeben.
                    JPanel h = (JPanel) event.getSource();
                    h.setLocation(getMousePosition());
                    int a = Integer.parseInt(h.getName());
                    punkte[0][a] = h.getLocation();
                    hintergrund.repaint();
                }
            });
            PunktAuswahl.addItem("" + zaehler);
            kordPunkt[zaehler].setLocation(p);
            zaehler++;//bei einem erfolgreichen durchgang wird der zaehler erhöht
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Es können keine weiteren Punkte erstellt werden ", "Bézier Kurven", JOptionPane.WARNING_MESSAGE);
        }
    }

    //Methode zum berechnen der Bésier Punkte
    public void Punkterechnen() {
        //Kurve wird zurückgessezt
        ResetBersetKurve();
        //Startpunkt der Besier Kurvee wird gesetzt
        kurve[0] = punkte[0][0];
        //Schleife die verschiedene t für die Berechnung setzt
        for (double t = 0.05; t < 1; t += 0.05) {
            //schleifen die alle Punkte ausrechnet
            for (int index = 0; index < punkte.length; index++) {
                for (int i = 0; i < punkte.length - 1; i++) {
                    //wenn der nachfolgenende Punkt leer ist wird abgebrochen
                    if (punkte[index][i + 1] == null) {
                        break;
                    }
                    //Berechnung des Punktes mit der Hilfe von t Speicherung in dem nächsten punkte Array
                    double x1 = ((1 - t) * punkte[index][i].x) + (t * punkte[index][i + 1].x);
                    double y1 = ((1 - t) * punkte[index][i].y) + (t * punkte[index][i + 1].y);
                    int x2 = (int) x1;
                    int y2 = (int) y1;
                    punkte[index + 1][i] = new Point(x2, y2);
                }
                //sobald der letze Punkt für t errechnet wurde wird dieser in das nächste freie Feld von kurve geschrieben
                if (punkte[index][1] == null) {
                    for (int a = 0; a < kurve.length; a++) {
                        if (kurve[a] == null) {
                            kurve[a] = punkte[index][0];
                            break;
                        }
                    }
                }
            }
        }
        //Endpunkt der Bésier Kurve wird in das letzte freie Feld geseetzt
        for (int a = 0; a < kurve.length; a++) {
            if (kurve[a] == null) {
                kurve[a] = punkte[0][zaehler - 1];
            }
        }
        hintergrund.repaint();
    }
//ersteellen der Popup Klasse

    class PopupListener extends MouseAdapter {

        JPopupMenu popup;//PopupMenü Variable

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
            event = e;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }

    //Paint Klasse
    class DrawPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int index = 0;
            //zeichnen der Normalen Punkte und dessen verbindne durch Linien.
            g.setColor(Color.red);
            for (int i = 0; i < punkte.length - 1; i++) {
                if (punkte[index][i + 1] == null) {
                    break;
                }
                g.drawLine(punkte[index][i].x, punkte[index][i].y, punkte[index][i + 1].x, punkte[index][i + 1].y);
            }
            //zeichnen der Bésier-Kurve mit dne errechneten Punkten.
            g.setColor(Color.blue);
            for (int a = 0; a < kurve.length - 1; a++) {
                if (kurve[a] == null) {
                    break;
                }
                g.drawLine(kurve[a].x, kurve[a].y, kurve[a + 1].x, kurve[a + 1].y);
            }
        }
    }
}
