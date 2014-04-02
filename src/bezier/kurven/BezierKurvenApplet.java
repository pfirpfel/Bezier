package bezier.kurven;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 *
 * @author Elias Schorr
 */
public class BezierKurvenApplet extends JApplet {

    //Variablen
    private int bezierKurvenzaehler = 0; //zählt die erstellten Punkte
    private BezierKurvenBerechnung bezierKurven[] = new BezierKurvenBerechnung[10];
    private JPanel hintergrund;
    private MouseEvent klickEvent; //zum spiechern des Mouse events

    /**
     * Initialization method that will be called after the applet is loaded into
     * the browser.
     */
    @Override
    public void init() {
        initObjekte();//erstellen der Objekte
        createPopupMenu();//ertellen eines Mausklick Menüs
        this.setSize(1000, 600);//setzten der Grösse
        //estellen eriner neuen BezierKurve
        bezierKurven[bezierKurvenzaehler] = new BezierKurvenBerechnung(true, null);
    }

    private void initObjekte() {
        Container contentPane = getContentPane();//Container erstellen
        contentPane.setLayout(new BorderLayout());//Layout des Containers bestimmen
        //Erstellung eines Hintergrund und Positionierung
        hintergrund = new DrawPanel();
        contentPane.add(hintergrund, BorderLayout.CENTER);
        //Eigenschaften des Hintergrundes
        hintergrund.setBackground(Color.white);
        //InfoLabel
        JLabel info = new JLabel("Pro Kurve können maximal 30 Punkte übergeben werden. Es können max 10 Kurven erstellt werden. Die Punkte können mit der Maus verschobenwerden");
        hintergrund.add(info);
    }

    private void createPopupMenu() {
        JMenuItem menuNeuerPunkt, menuReset, menuKurveerweitern;//Die Menü-unter-Punkte
        //Create the popup menu.
        JPopupMenu popup = new JPopupMenu();
        menuNeuerPunkt = new JMenuItem("Einen neuen Punkt erzeugen");
        menuNeuerPunkt.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                //auslesen der Position der Maus
                Point p = klickEvent.getPoint();
                //übergeben der Daten
                bezierKurven[bezierKurvenzaehler].setPunkt(p);
                resetKordinatenPunkte();
            }
        });
        popup.add(menuNeuerPunkt);
        menuKurveerweitern = new JMenuItem("Bézier Kurve erweitern");
        menuKurveerweitern.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (bezierKurven[bezierKurvenzaehler].getPunktezaehler() < 3) {
                    JOptionPane.showMessageDialog(null, "Sie müssen zuerst min. 3 Punkte erstellen um eine neue Kurve zu erstellen. ", "Bézier Kurven", JOptionPane.WARNING_MESSAGE);
                } else {
                    bezierKurvenzaehler++;//Erstellen einer neuen Kurve
                    bezierKurven[bezierKurvenzaehler] = new BezierKurvenBerechnung(false, bezierKurven[bezierKurvenzaehler - 1]);
                    bezierKurven[bezierKurvenzaehler - 1].setChildKlasse(bezierKurven[bezierKurvenzaehler]);
                }
            }
        });
        popup.add(menuKurveerweitern);
        menuReset = new JMenuItem("Reset");
        menuReset.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                resetAll();
            }
        });
        popup.add(menuReset);
        //Add listener to the text area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(popup);
        hintergrund.addMouseListener(popupListener);
    }

    private void resetAll() {
        for (int i = 0; i <= bezierKurvenzaehler; i++) {
            try {
                bezierKurven[i].removeHintergrundPunkte();
                bezierKurven[i] = null;
            } catch (Exception ex) {
                break;
            }
        }
        bezierKurvenzaehler = 0;
        hintergrund.repaint();
        bezierKurven[bezierKurvenzaehler] = new BezierKurvenBerechnung(true, null);

    }

    private void resetKordinatenPunkte() {
        bezierKurven[bezierKurvenzaehler].resetKordinatenPunkte();
    }

    private class BezierKurvenBerechnung {

        //Variablen
        private int punkteZaehler = 0;
        private Point eingabePunkte[] = new Point[30];
        private Point kurvenPunkte[] = new Point[30]; //speichert die Punkte der Bérset Kurve
        private JPanel kordPunkt[] = new JPanel[30];
        private BezierKurvenBerechnung parent, child;
        private boolean erster;

        public BezierKurvenBerechnung(boolean erster, BezierKurvenBerechnung parent) {
            this.erster = erster;
            this.parent = parent;
            if (erster == false) {
                JPanel panels[] = parent.getKordPunkte();
                Point punkte[] = parent.getEingabePunkte();
                int zaehler = parent.getPunktezaehler();

                kordPunkt[punkteZaehler] = panels[zaehler - 1];
                setPunkt(punkte[zaehler - 1]);
                int xVersch = (punkte[zaehler - 1].x - punkte[zaehler - 2].x);
                int yVersch = (punkte[zaehler - 1].y - punkte[zaehler - 2].y);
                xVersch = punkte[zaehler - 1].x + xVersch;
                yVersch = punkte[zaehler - 1].y + yVersch;
                Point p = new Point(xVersch, yVersch);
                setPunkt(p);
                erster = true;
                hintergrund.repaint();
            }
        }

        public void setChildKlasse(BezierKurvenBerechnung child) {
            this.child = child;
        }

        //setzten der Punkte
        public void setPunkt(Point p) {
            //es werden die Punkte mit den Mauskordinaten gesetzt, sofern es noch Platz hat
            try {
                eingabePunkte[punkteZaehler] = new Point(p);
                if (erster == true) {
                    kordPunkt[punkteZaehler] = new JPanel();
                    kordPunkt[punkteZaehler].setSize(10, 10);
                    kordPunkt[punkteZaehler].setBackground(Color.red);
                    kordPunkt[punkteZaehler].setName("" + punkteZaehler);
                    hintergrund.add(kordPunkt[punkteZaehler]);
                    hintergrund.repaint();
                    kordPunkt[punkteZaehler].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent event) {
                        }
                    });
                    kordPunkt[punkteZaehler].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseReleased(MouseEvent event) {
                            //beim Losslassen der  Maus wird die aktuelle Mausposition in die Liste und das Panel geschrieben, anschliessend wird der Hintergrund neu ausgegeben.
                            JPanel h = (JPanel) event.getSource();
                            punktverschieben(h, getMousePosition());
                        }
                    });
//                PunktAuswahl.addItem("" + punkteZaehler);
                    kordPunkt[punkteZaehler].setLocation(p);
                } else {
                    erster = true;
                }

                punkteZaehler++;//bei einem erfolgreichen durchgang wird der punkteZaehler erhöht
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Es können keine weiteren Punkte erstellt werden ", "Bézier Kurven", JOptionPane.WARNING_MESSAGE);
            }
        }

        public void punktverschieben(JPanel punkt, Point neuePosition) {
            Point pv = punkt.getLocation();
            punkt.setLocation(getMousePosition());
            Point pn = punkt.getLocation();
            int xVersch = (pv.x - pn.x);
            int yVersch = (pv.y - pn.y);
            int a = Integer.parseInt(punkt.getName());
            eingabePunkte[a] = punkt.getLocation();
            try {
                if (kordPunkt[1].equals(punkt)) {
                    Point punkte[] = parent.getEingabePunkte();
                    int zaehler = parent.getPunktezaehler();
                    Point p = new Point(punkte[zaehler - 2].x + xVersch, punkte[zaehler - 2].y + yVersch);
                    parent.setKordPunkte(zaehler-2, p);
                } else if (kordPunkt[punkteZaehler - 2].equals(punkt)) {
                    Point punkte[] = child.getEingabePunkte();
                    Point p = new Point(punkte[1].x + xVersch, punkte[1].y + yVersch);
                    child.setKordPunkte(1, p);
                } else if(kordPunkt[punkteZaehler-1].equals(punkt)&&child!=null){
                    Point p = new Point(eingabePunkte[punkteZaehler - 2].x - xVersch, eingabePunkte[punkteZaehler - 2].y - yVersch);
                    this.setKordPunkte(punkteZaehler-2, p); 
                    child.setPosition(neuePosition);
                    Point punkte[] = child.getEingabePunkte();
                    Point p2 = new Point(punkte[1].x - xVersch, punkte[1].y - yVersch);
                    child.setKordPunkte(1, p2);
                }
            } catch (Exception ex) {
                //System.out.println("error");
            }
            hintergrund.repaint();
        }

        public void setKordPunkte(int zaehler, Point neuePosition) {
            eingabePunkte[zaehler] = neuePosition;
            kordPunkt[zaehler].setLocation(neuePosition);
            hintergrund.repaint();
        }
        public void setPosition(Point pn){
            eingabePunkte[0] = pn;
        }
        public void setPanel(Point neuePosition){
            
        }
        //Methode zum berechnen der Bésier Punkte
        public void Punkterechnen() {
            //Kurve wird zurückgessezt
            resetBersetKurve();
            //punkte werden in ein neues Arry uebertragen
            Point punkte[][] = new Point[30][30];//speichert die Punkte
            for (int i = 0; i < eingabePunkte.length; i++) {
                punkte[0][i] = eingabePunkte[i];
            }
            //Startpunkt der Besier Kurvee wird gesetzt
            kurvenPunkte[0] = punkte[0][0];
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
                    //sobald der letze Punkt für t errechnet wurde wird dieser in das nächste freie Feld von kurvenPunkte geschrieben
                    if (punkte[index][1] == null) {
                        for (int a = 0; a < kurvenPunkte.length; a++) {
                            if (kurvenPunkte[a] == null) {
                                kurvenPunkte[a] = punkte[index][0];
                                break;
                            }
                        }
                    }
                }
            }
            //Endpunkt der Bésier Kurve wird in das letzte freie Feld gesetzt
            for (int a = 0; a < kurvenPunkte.length; a++) {
                if (kurvenPunkte[a] == null) {
                    kurvenPunkte[a] = punkte[0][punkteZaehler - 1];
                }
            }
        }

        public Point[] getEingabePunkte() {
            return eingabePunkte;
        }

        public int getPunktezaehler() {
            return punkteZaehler;
        }

        public JPanel[] getKordPunkte() {
            return kordPunkt;
        }

        public Point[] getkurvenPunkte() {
            if (punkteZaehler > 1) {
                Punkterechnen();
            }
            return kurvenPunkte;
        }

        public void resetBersetKurve() {
            //Reset der Bersét-Kurve
            for (int index = 0; index < kurvenPunkte.length; index++) {
                kurvenPunkte[index] = null;
            }
        }

        public void removeHintergrundPunkte() {
            //Reset der Variablen und des Hintergrundes
            for (int index = 0; index < 30; index++) {
                try {
                    hintergrund.remove(kordPunkt[index]);
                } catch (Exception ex) {
                    break;
                }
            }
            hintergrund.repaint();
        }

        public void removeLastKordPunkt() {
            kordPunkt[punkteZaehler - 1].removeMouseListener(null);
        }

        private void resetKordinatenPunkte() {
            for (int index = 0; index < kordPunkt.length; index++) {
                try {
                    kordPunkt[index].setLocation(eingabePunkte[index]);
                } catch (Exception ex) {
                    break;
                }
            }
        }
    }

//ersteellen der Popup Klasse
    private class PopupListener extends MouseAdapter {

        JPopupMenu popup;//PopupMenü Variable

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
            klickEvent = e;
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
    private class DrawPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //für alle Abschnitte
            for (int pz = 0; pz <= bezierKurvenzaehler; pz++) {
                if (bezierKurven[pz] == null) {
                    break;
                }
                //zeichnen der Normalen Punkte und dessen verbindne durch Linien.
                g.setColor(Color.red);
                Point punkte[] = bezierKurven[pz].getEingabePunkte();
                for (int i = 0; i < punkte.length - 1; i++) {
                    if (punkte[i + 1] == null) {
                        break;
                    }
                    g.drawLine(punkte[i].x, punkte[i].y, punkte[i + 1].x, punkte[i + 1].y);
                }
                //zeichnen der Bésier-Kurve mit den errechneten Punkten.
                Point kurve[] = bezierKurven[pz].getkurvenPunkte();
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
}
