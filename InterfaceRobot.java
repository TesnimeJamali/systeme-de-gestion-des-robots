package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceRobot {
    private static List<RobotLivraison> robots = new ArrayList<>();
    private static final List<Destination> destinations = new ArrayList<>();
    private static JFrame framePrincipale;
    private static JPanel animationPanel;
    private static Timer animationTimer;
    private static JTabbedPane onglets;
    private static JLabel lblTempsRestant;
    private static Timer rechargeVerteTimer;
    private static int oldEnergie = -1;

    public static class Destination {
        private final String name;
        private final int x;
        private final int y;
        
        public Destination(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        public String getName() { return name; }
    }

    public static void demarrerInterface() {
        framePrincipale = new JFrame("Gestion des Robots de Livraison");
        framePrincipale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePrincipale.setSize(1200, 800);

        // Initialize destinations
        if (destinations.isEmpty()) {
            destinations.add(new Destination("TechZone", 12, 15));
            destinations.add(new Destination("AgroGreen", 100, 10));
            destinations.add(new Destination("MediPharm", 120, 80));
            destinations.add(new Destination("LogiPlus", 80, 20));
            destinations.add(new Destination("ElectroTun", 50, 78));
            destinations.add(new Destination("FastPrint", 80, 50));
            destinations.add(new Destination("SoftNet Solutions", 130, 30));
            destinations.add(new Destination("NovaStyle", 70, 70));
            destinations.add(new Destination("Pharmadis", 10, 65));
            destinations.add(new Destination("BioMarket", 40, 10));
        }

        onglets = new JTabbedPane();

        // Onglet Liste
        JPanel listePanel = creerListePanel();
        onglets.addTab("Liste des Robots", listePanel);

        // Onglet Carte
        animationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dessiner un quadrillage
                g.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i < getWidth(); i += 50) {
                    g.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 50) {
                    g.drawLine(0, i, getWidth(), i);
                }

                // Dessiner les destinations (carrés noirs)
                g.setColor(Color.BLACK);
                for (Destination dest : destinations) {
                    int x = dest.getX() * 8;
                    int y = dest.getY() * 8;
                    g.fillRect(x, y, 25, 25);
                    g.setColor(Color.WHITE);
                    g.drawString(dest.getName(), x, y + 12);
                    g.setColor(Color.BLACK);
                }

                // Affichage des robots
                for (RobotLivraison robot : robots) {
                    int x = robot.getX() * 8;
                    int y = robot.getY() * 8;
                    
                    g.setColor(robot.getColor());
                    g.fillOval(x, y, 25, 25);

                    g.setColor(Color.BLACK);
                    g.drawString(robot.getId() + " (" + robot.getX() + "," + robot.getY() + ")", x, y - 5);
                }
            }
        };
        animationPanel.setPreferredSize(new Dimension(1000, 100));
        onglets.addTab("Carte", new JScrollPane(animationPanel));

        framePrincipale.add(onglets);
        framePrincipale.setVisible(true);
    }

    private static JPanel creerListePanel() {
        JPanel listePanel = new JPanel(new BorderLayout());
        JButton btnNouveau = new JButton("Nouveau Robot");
        JPanel panelRobots = new JPanel();
        panelRobots.setLayout(new BoxLayout(panelRobots, BoxLayout.Y_AXIS));

        btnNouveau.addActionListener(e -> creerNouveauRobot());

        for (RobotLivraison robot : robots) {
            ajouterBoutonRobot(panelRobots, robot);
        }

        listePanel.add(new JScrollPane(panelRobots), BorderLayout.CENTER);
        listePanel.add(btnNouveau, BorderLayout.SOUTH);
        return listePanel;
    }

    private static void creerNouveauRobot() {
        JTextField idField = new JTextField();
        JTextField xField = new JTextField();
        JTextField yField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Position X (0-100):"));
        panel.add(xField);
        panel.add(new JLabel("Position Y (0-100):"));
        panel.add(yField);

        int result = JOptionPane.showConfirmDialog(
            framePrincipale, 
            panel, 
            "Nouveau Robot", 
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                if(x < 0 || x > 100 || y < 0 || y > 100) {
                    throw new NumberFormatException();
                }
                RobotLivraison robot = new RobotLivraison(idField.getText(), x, y);
                robots.add(robot);
                actualiserInterface1();
            } catch (NumberFormatException ex) {
                afficherErreur("Coordonnées invalides ! (0-100 seulement)");
            }
        }
    }

    private static void afficherDetailsRobot(RobotLivraison robot) {
        JFrame detailsFrame = new JFrame("Détails: " + robot.getId());
        detailsFrame.setSize(900, 600);
        detailsFrame.setLayout(new BorderLayout());

        // Panel d'informations
        JPanel infoPanel = new JPanel(new GridLayout(6, 2));
        JLabel lblEnergie = new JLabel(robot.getEnergie() + "%");
        JLabel lblColis = new JLabel(robot.getColis() != null ? robot.getColis() : "Aucun");

        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(robot.getId()));
        infoPanel.add(new JLabel("Position:"));
        infoPanel.add(new JLabel(robot.getX() + ", " + robot.getY()));
        infoPanel.add(new JLabel("Énergie:"));
        infoPanel.add(lblEnergie);
        infoPanel.add(new JLabel("Colis:"));
        infoPanel.add(lblColis);
        infoPanel.add(new JLabel("Connecté:"));
        infoPanel.add(new JLabel(robot.isConnecte() ? "Oui (" + robot.getReseauConnecte() + ")" : "Non"));
        infoPanel.add(new JLabel("Heures d'utilisation:"));
        infoPanel.add(new JLabel(String.valueOf(robot.getHeuresUtilisation()) + " heures"));
        // Barre d'énergie
        JProgressBar barreEnergie = new JProgressBar(0, Robot.MAX_ENERGIE);
        barreEnergie.setValue(robot.getEnergie());
        barreEnergie.setStringPainted(true);

        JPanel gauchePanel = new JPanel(new BorderLayout());
        gauchePanel.add(infoPanel, BorderLayout.CENTER);
        gauchePanel.add(barreEnergie, BorderLayout.SOUTH);

        // Panel de contrôle
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel boutonsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // Boutons
        JButton btnPower = new JButton(robot.isEnMarche() ? "Arrêter" : "Démarrer");
        JButton btnRecharge = new JButton("Recharger");
        JButton btnRechargeVerte = new JButton("Recharge Verte");
        JButton btnCharger = new JButton("Charger Colis");
        JButton btnLivrer = new JButton("Effectuer Livraison");
        JButton btnConnecter = new JButton("Connecter");
        JButton btnDeconnecter = new JButton("Déconnecter");
        JButton btnHistorique = new JButton("Afficher Historique");

        // Gestion des événements
        btnPower.addActionListener(e -> gererDemarrage(robot, btnPower, lblEnergie, barreEnergie));
        btnRecharge.addActionListener(e -> gererRecharge(robot, lblEnergie, barreEnergie));
        btnRechargeVerte.addActionListener(e -> gererRechargeVerte(robot, btnRechargeVerte, lblEnergie, barreEnergie, lblTempsRestant));
        btnCharger.addActionListener(e -> gererChargement(robot, lblColis));
        btnLivrer.addActionListener(e -> gererLivraison(robot, lblColis));
        btnConnecter.addActionListener(e -> connecterRobot(robot, infoPanel));
        btnDeconnecter.addActionListener(e -> deconnecterRobot(robot, infoPanel));
        btnHistorique.addActionListener(e -> afficherHistorique(robot));

        // Ajout des boutons
        boutonsPanel.add(btnPower);
        boutonsPanel.add(btnRecharge);
        boutonsPanel.add(btnRechargeVerte);
        boutonsPanel.add(btnCharger);
        boutonsPanel.add(btnLivrer);
        boutonsPanel.add(btnConnecter);
        boutonsPanel.add(btnDeconnecter);
        boutonsPanel.add(btnHistorique);

        // Temps restant
        lblTempsRestant = new JLabel(" ");
        lblTempsRestant.setHorizontalAlignment(JLabel.CENTER);
        lblTempsRestant.setFont(new Font("Arial", Font.BOLD, 14));
        lblTempsRestant.setForeground(Color.BLUE);

        controlPanel.add(boutonsPanel, BorderLayout.CENTER);
        controlPanel.add(lblTempsRestant, BorderLayout.SOUTH);

        detailsFrame.add(gauchePanel, BorderLayout.CENTER);
        detailsFrame.add(controlPanel, BorderLayout.SOUTH);
        // Ajoutez un onglet pour la maintenance et l'écologie
        JTabbedPane ongletsDetails = new JTabbedPane();
        
        // Onglet principal (existant)
        ongletsDetails.addTab("Informations", gauchePanel);
        
        // Nouvel onglet Maintenance & Écologie
        JPanel ecoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        
        // Score écologique
        ecoPanel.add(new JLabel("Score écologique:"));
        JProgressBar barreEco = new JProgressBar(0, 100);
        barreEco.setValue(robot.getScoreEcologique());
        barreEco.setStringPainted(true);
        barreEco.setForeground(getCouleurScore(robot.getScoreEcologique()));
        ecoPanel.add(barreEco);
        
        // Dernière maintenance
        ecoPanel.add(new JLabel("Dernière maintenance:"));
        ecoPanel.add(new JLabel(robot.getDerniereMaintenance().toString()));
        
        // Heures depuis maintenance
        ecoPanel.add(new JLabel("Heures depuis maintenance:"));
        ecoPanel.add(new JLabel(String.valueOf(robot.getHeuresDepuisMaintenance())));
        
        // État maintenance
        ecoPanel.add(new JLabel("État maintenance:"));
        JLabel lblEtatMaintenance = new JLabel(robot.getEtatMaintenance());
        lblEtatMaintenance.setForeground(getCouleurMaintenance(robot.getEtatMaintenance()));
        ecoPanel.add(lblEtatMaintenance);
        
        // Bouton maintenance
        JButton btnMaintenance = new JButton("Effectuer Maintenance");
        btnMaintenance.addActionListener(e -> {
            robot.effectuerMaintenance();
            barreEco.setValue(robot.getScoreEcologique());
            barreEco.setForeground(getCouleurScore(robot.getScoreEcologique()));
            lblEtatMaintenance.setText(robot.getEtatMaintenance());
            lblEtatMaintenance.setForeground(getCouleurMaintenance(robot.getEtatMaintenance()));
        });
        ecoPanel.add(btnMaintenance);
        
        // Bouton livraison écologique
        JButton btnEcoLivraison = new JButton("Livraison Écologique");
        btnEcoLivraison.addActionListener(e -> {
            try {
                robot.livraisonEcologique();
                barreEco.setValue(robot.getScoreEcologique());
                barreEco.setForeground(getCouleurScore(robot.getScoreEcologique()));
                JOptionPane.showMessageDialog(detailsFrame, "Livraison écologique effectuée avec succès!");
            } catch (RobotException ex) {
                afficherErreur(ex.getMessage());
            }
        });
        ecoPanel.add(btnEcoLivraison);
        
        ongletsDetails.addTab("Durabilité", ecoPanel);
        detailsFrame.add(ongletsDetails, BorderLayout.CENTER);
        detailsFrame.setVisible(true);
    }
    private static Color getCouleurScore(int score) {
        if (score >= 75) return new Color(34, 139, 34); // Vert
        if (score >= 50) return Color.ORANGE;
        return Color.RED;
    }

    private static Color getCouleurMaintenance(String etat) {
        switch (etat) {
            case "OK": return Color.GREEN;
            case "À vérifier": return Color.ORANGE;
            case "Urgente": return Color.RED;
            default: return Color.BLACK;
        }
    }
    private static void afficherHistorique(RobotLivraison robot) {
        JTextArea textArea = new JTextArea(robot.getHistorique());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(
            framePrincipale,
            scrollPane,
            "Historique de " + robot.getId(),
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static void gererDemarrage(RobotLivraison robot, JButton btn, JLabel lblEnergie, JProgressBar barre) {
        try {
            if (robot.isEnMarche()) {
                robot.arreter();
            } else {
                robot.demarrer();
            }
            btn.setText(robot.isEnMarche() ? "Arrêter" : "Démarrer");
            actualiserDonnees(robot, lblEnergie, barre);
            actualiserCarte();
        } catch (RobotException e) {
            afficherErreur(e.getMessage());
        }
        actualiserInterface1();
    }

    private static void gererRecharge(RobotLivraison robot, JLabel lblEnergie, JProgressBar barre) {
        String input = JOptionPane.showInputDialog("Quantité d'énergie à ajouter:");
        if (input != null) {
            try {
                robot.recharger(Integer.parseInt(input));
                actualiserDonnees(robot, lblEnergie, barre);
                actualiserCarte();
                actualiserInterface();
                actualiserInterface1();
            } catch (Exception e) {
                afficherErreur(e.getMessage());
            }
        }
        actualiserInterface1();
    }

    private static void gererRechargeVerte(RobotLivraison robot, JButton btn, JLabel lblEnergie, JProgressBar barre, JLabel lblTemps) {
        if (btn.getText().equals("Recharge Verte")) {
            if (robot.getEnergie() >= Robot.MAX_ENERGIE) {
                afficherErreur("Énergie déjà au maximum !");
                return;
            }
            btn.setText("Arrêter Recharge Verte");
            oldEnergie = robot.getEnergie();
            int[] tempsRestant = {(Robot.MAX_ENERGIE - oldEnergie) * 2};
            
            rechargeVerteTimer = new Timer(2000, e -> {
                if (robot.getEnergie() < Robot.MAX_ENERGIE) {
                    robot.rechargeVerte();
                    tempsRestant[0] -= 2;
                    lblTemps.setText("Temps restant : " + tempsRestant[0] + "s");
                    actualiserDonnees(robot, lblEnergie, barre);
                    actualiserCarte();
                    actualiserInterface1();
                } else {
                    rechargeVerteTimer.stop();
                    btn.setText("Recharge Verte");
                    lblTemps.setText("Recharge terminée !");
                    int diff = robot.getEnergie() - oldEnergie;
                    if (diff > 0)
                        robot.ajouterHistorique("Recharge verte de " + diff + "%");
                    oldEnergie = -1;
                }
            });
            rechargeVerteTimer.start();
        } else {
            if (rechargeVerteTimer != null) {
                rechargeVerteTimer.stop();
                btn.setText("Recharge Verte");
                lblTemps.setText(" ");
            }
            if (oldEnergie != -1) {
                int diff = robot.getEnergie() - oldEnergie;
                if (diff > 0)
                    robot.ajouterHistorique("Recharge verte de " + diff + "%");
                oldEnergie = -1;
            }
        }
        actualiserInterface1();
    }

    private static void gererChargement(RobotLivraison robot, JLabel lblColis) {
        JTextField colisField = new JTextField();
        JTextField destField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Nom du colis:"));
        panel.add(colisField);
        int result = JOptionPane.showConfirmDialog(null, panel, "Chargement de colis", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                robot.chargerColis(colisField.getText());
                lblColis.setText(robot.getColis());
                actualiserCarte();
            } catch (RobotException e) {
                afficherErreur(e.getMessage());
            }
        }
        actualiserInterface1();
    }

    private static void gererLivraison(RobotLivraison robot, JLabel lblColis) {
        Map<Integer, Destination> destinationMap = new HashMap<>();
        destinationMap.put(0, destinations.get(0));
        destinationMap.put(1, destinations.get(1));
        destinationMap.put(2, destinations.get(2));
        destinationMap.put(3, destinations.get(3));
        destinationMap.put(4, destinations.get(4));
        destinationMap.put(5, destinations.get(5));
        destinationMap.put(6, destinations.get(6));
        destinationMap.put(7, destinations.get(7));
        destinationMap.put(8, destinations.get(8));
        destinationMap.put(9, destinations.get(9));

        JRadioButton[] options = new JRadioButton[10];
        ButtonGroup group = new ButtonGroup();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        
        for (int i = 0; i < 10; i++) {
            options[i] = new JRadioButton(destinationMap.get(i).getName());
            group.add(options[i]);
            panel.add(options[i]);
        }

        int result = JOptionPane.showConfirmDialog(null, panel, 
                "Choisissez une destination", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < 10; i++) {
                if (options[i].isSelected()) {
                    Destination dest = destinationMap.get(i);
                    robot.setDestination(dest.getName());
                    onglets.setSelectedIndex(1);
                    demarrerAnimationLivraison(robot, dest.getX(), dest.getY(), lblColis);
                    break;
                }
            }
        }
        actualiserInterface1();
        robot.ajouterHistorique("Colis chargé: " + robot.getColis()+" Destination: "+robot.getDestination());
    }
    
    private static void actualiserInterface1() {
        onglets.setComponentAt(0, creerListePanel());
    }

    private static void demarrerAnimationLivraison(RobotLivraison robot, int destX, int destY, JLabel lblColis) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        final int startX = robot.getX();
        final int startY = robot.getY();
        final int steps = 50;
        final int[] currentStep = {0};

        animationTimer = new Timer(50, e -> {
            if (currentStep[0] <= steps) {
                double ratio = (double) currentStep[0] / steps;
                int newX = (int) (startX + (destX - startX) * ratio);
                int newY = (int) (startY + (destY - startY) * ratio);
                try {
                    robot.deplacer(newX, newY);
                    actualiserCarte();
                    actualiserInterface1();
                } catch (RobotException ex) {
                    animationTimer.stop();
                    afficherErreur(ex.getMessage());
                }
                currentStep[0]++;
            } else {
                try {
                    robot.livrerColis();
                    lblColis.setText("Aucun");
                    JOptionPane.showMessageDialog(framePrincipale, "Livraison effectuée avec succès !");
                    onglets.setSelectedIndex(0);
                    //afficherDetailsRobot(robot);
                    actualiserInterface1();
                } catch (RobotException ex) {
                    afficherErreur(ex.getMessage());
                }
                animationTimer.stop();
            }
        });
        animationTimer.start();
        robot.ajouterHistorique("Déplacement vers (" + destX + "," + destY + ")");
        actualiserInterface1();
    }

    private static void connecterRobot(RobotLivraison robot, JPanel infoPanel) {
        String reseau = JOptionPane.showInputDialog("Nom du réseau:");
        if (reseau != null && !reseau.isEmpty()) {
            try {
                robot.connecter(reseau);
                actualiserDetails(infoPanel, robot);
                actualiserInterface1();
            } catch (RobotException ex) {
                afficherErreur(ex.getMessage());
            }
        }
    }

    private static void deconnecterRobot(RobotLivraison robot, JPanel infoPanel) {
        robot.deconnecter();
        actualiserDetails(infoPanel, robot);
        actualiserInterface1();
    }

    private static void actualiserDetails(JPanel infoPanel, RobotLivraison robot) {
        Component[] components = infoPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JLabel label) {
                switch (label.getText()) {
                    case "Position:":
                        ((JLabel) components[i + 1]).setText(robot.getX() + ", " + robot.getY());
                        break;
                    case "Énergie:":
                        ((JLabel) components[i + 1]).setText(robot.getEnergie() + "%");
                        break;
                    case "Colis:":
                        ((JLabel) components[i + 1]).setText(robot.getColis() != null ? robot.getColis() : "Aucun");
                        break;
                    case "Connecté:":
                        ((JLabel) components[i + 1]).setText(robot.isConnecte() ? "Oui (" + robot.getReseauConnecte() + ")" : "Non");
                        break;
                }
            }
        }
    }

    private static void actualiserDonnees(RobotLivraison robot, JLabel lblEnergie, JProgressBar barre) {
        lblEnergie.setText(robot.getEnergie() + "%");
        barre.setValue(robot.getEnergie());
    }

    private static void actualiserCarte() {
        animationPanel.repaint();
    }

    private static void actualiserInterface() {
        framePrincipale.dispose();
        demarrerInterface();
    }

    private static void ajouterBoutonRobot(JPanel panel, RobotLivraison robot) {
        JPanel robotPanel = new JPanel(new BorderLayout());
        
        JButton btn = new JButton(robot.toString());
        btn.addActionListener(e -> afficherDetailsRobot(robot));
        
        // Mini indicateur écologique
        JPanel ecoIndicator = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Icône maintenance
        JLabel maintenanceIcon = new JLabel("⚙");
        maintenanceIcon.setForeground(getCouleurMaintenance(robot.getEtatMaintenance()));
        ecoIndicator.add(maintenanceIcon);
        
        // Barre score écologique miniature
        JProgressBar miniBar = new JProgressBar(0, 100);
        miniBar.setValue(robot.getScoreEcologique());
        miniBar.setForeground(getCouleurScore(robot.getScoreEcologique()));
        miniBar.setPreferredSize(new Dimension(50, 8));
        miniBar.setStringPainted(false);
        ecoIndicator.add(miniBar);
        
        robotPanel.add(btn, BorderLayout.CENTER);
        robotPanel.add(ecoIndicator, BorderLayout.EAST);
        panel.add(robotPanel);
    }

    private static void afficherErreur(String message) {
        JOptionPane.showMessageDialog(framePrincipale, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static List<RobotLivraison> getRobots() {
        return robots;
    }
}