package code;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
public abstract class Robot {
    protected String id;
    protected int x, y;
    protected int energie;
    protected int heuresUtilisation;
    protected boolean enMarche;
    protected List<String> historiqueActions;
    protected int scoreEcologique;
    protected LocalDate derniereMaintenance;
    protected int heuresDepuisMaintenance;
    protected String etatMaintenance; // "OK", "À vérifier", "Urgente"
    public static final int MAX_ENERGIE = 85;
    public static final int MIN_ENERGIE = 15;

    public Robot(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.energie = MAX_ENERGIE;
        this.heuresUtilisation = 0;
        this.enMarche = false;
        this.historiqueActions = new ArrayList<>();
        this.scoreEcologique = 100; // Score initial
        this.derniereMaintenance = LocalDate.now();
        this.heuresDepuisMaintenance = 0;
        this.etatMaintenance = "OK";
        ajouterHistorique("Robot créé");
    }
    public void effectuerMaintenance() {
        this.derniereMaintenance = LocalDate.now();
        this.heuresDepuisMaintenance = 0;
        this.etatMaintenance = "OK";
        this.scoreEcologique = Math.min(100, scoreEcologique + 20);
        ajouterHistorique("Maintenance effectuée");
    }
    
    // Méthode pour mettre à jour l'état
    protected void mettreAJourEtatMaintenance() {
        if (heuresDepuisMaintenance > 150) {
            etatMaintenance = "Urgente";
            scoreEcologique = Math.max(0, scoreEcologique - 10);
        } else if (heuresDepuisMaintenance > 100) {
            etatMaintenance = "À vérifier";
            scoreEcologique = Math.max(0, scoreEcologique - 5);
        }
    }
    protected void ajouterHistorique(String action) {
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        historiqueActions.add(date + " - " + action);
    }

    protected void verifierEnergie(int energieRequise) throws EnergieInsuffisanteException {
        if (energie < energieRequise+15) {
            throw new EnergieInsuffisanteException("Énergie insuffisante (actuelle: " + energie + "%, requise: " + energieRequise + "%)");
        }
    }

    protected void verifierMaintenance() throws MaintenanceRequiseException {
        if (heuresUtilisation > 100) {
            throw new MaintenanceRequiseException("Maintenance requise après 100 heures d'utilisation");
        }
    }

    public void demarrer() throws RobotException {
        if (energie < 10) {
            throw new RobotException("Pas assez d'énergie pour démarrer (minimum 10%)");
        }
        enMarche = true;
        ajouterHistorique("Démarrage du robot");
    }

    public void arreter() {
        enMarche = false;
        ajouterHistorique("Arrêt du robot");
    }

    protected void consommerEnergie(int quantite) throws EnergieInsuffisanteException {
        if (energie - quantite < MIN_ENERGIE) {
            throw new EnergieInsuffisanteException("L'énergie ne peut descendre en dessous de " + MIN_ENERGIE + "%");
        }
        energie -= quantite;
        heuresDepuisMaintenance += quantite/2;
        heuresUtilisation += quantite/2;
        mettreAJourEtatMaintenance();
        calculerScoreEcologique();
    }
    public void calculerScoreEcologique() {
        int nouveauScore = 100;
        
        // Pénalités
        long joursDepuisMaintenance = ChronoUnit.DAYS.between(derniereMaintenance, LocalDate.now());
        nouveauScore -= joursDepuisMaintenance;
        
        if (heuresDepuisMaintenance > 100) {
            nouveauScore -= (heuresDepuisMaintenance - 100) / 10;
        }
        
        // Bonus
        if (energie > MAX_ENERGIE - 10) {
            nouveauScore += 5;
        }
        this.scoreEcologique = Math.max(0, Math.min(100, nouveauScore));

    }
    public int getScoreEcologique() { return scoreEcologique; }
    public String getEtatMaintenance() { return etatMaintenance; }
    public LocalDate getDerniereMaintenance() { return derniereMaintenance; }
    public int getHeuresDepuisMaintenance() { return heuresDepuisMaintenance; }
    public void recharger(int quantite) throws RobotException {
        if (quantite <= 0) {
            throw new RobotException("La quantité de recharge doit être positive");
        }
        if (energie + quantite > MAX_ENERGIE) {
            throw new RobotException("L'énergie ne peut dépasser " + MAX_ENERGIE + "%");
        }
        energie += quantite;
        ajouterHistorique("Rechargé de " + quantite + "%");
    }

    public void rechargeVerte() {
        if (energie < MAX_ENERGIE) {
            energie++;
            //ajouterHistorique("Recharge verte (+1%)");
        }
    }

    public abstract void deplacer(int newX, int newY) throws RobotException;
    public abstract void effectuerTache() throws RobotException;

    public String getHistorique() {
        String historique = "";
        for (String action : historiqueActions) {
            historique += action + "\n";
        }
        return historique;
    }

    // Getters
    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getEnergie() { return energie; }
    public int getHeuresUtilisation() { return heuresUtilisation; }
    public boolean isEnMarche() { return enMarche; }

    @Override
    public String toString() {
        return id + " - Énergie: " + energie + "% - ("+x+","+y+") - score eco : "+scoreEcologique;
    }
}