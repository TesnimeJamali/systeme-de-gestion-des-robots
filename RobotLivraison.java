package code;

import java.awt.Color;
import java.util.Random;

public class RobotLivraison extends RobotConnecte {
    private String colis;
    private String destination;
    private static final int ENERGIE_LIVRAISON = 10;
    private static final int ENERGIE_CHARGEMENT = 5;
    private Color color;

    public RobotLivraison(String id, int x, int y) {
        super(id, x, y);
        this.colis = null;
        this.destination = null;
        this.color = genererCouleurAleatoire();

    }
    private Color genererCouleurAleatoire() {
        Random rand = new Random();
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public Color getColor() {
        return color;
    }
    @Override
    public void deplacer(int newX, int newY) throws RobotException {
        if (!enMarche) {
            throw new RobotException("Le robot doit être démarré");
        }
        
        double distance = Math.sqrt(Math.pow(newX - x, 2) + Math.pow(newY - y, 2));
        int energieRequise = (int) Math.ceil(distance * 0.3);
        
        verifierEnergie(energieRequise);
        verifierMaintenance();
        consommerEnergie(energieRequise);
        
        // Mise à jour des deux coordonnées
        x = newX;
        y = newY;
        
        heuresUtilisation += (int)(distance / 10);
        calculerScoreEcologique();

    }
    public void livraisonEcologique() throws RobotException {
        if (colis == null) throw new RobotException("Aucun colis à livrer");
        if (energie < 30) throw new RobotException("Énergie insuffisante pour livraison écologique");
        
        consommerEnergie(15); // Moins d'énergie que la livraison normale
        scoreEcologique = Math.min(100, scoreEcologique + 5);
        ajouterHistorique("Livraison écologique effectuée");
    }
    @Override
    public void effectuerTache() throws RobotException {
        if (!enMarche) {
            throw new RobotException("Le robot doit être démarré");
        }
        
        if (colis == null) {
            throw new RobotException("Aucun colis à livrer");
        }
        
        if (destination == null) {
            throw new RobotException("Aucune destination définie");
        }
        
        ajouterHistorique("Livraison en cours du colis: " + colis);
    }

    public void chargerColis(String colis) throws RobotException {
        if (this.colis != null) {
            throw new RobotException("Un colis est déjà chargé");
        }
        verifierEnergie(ENERGIE_CHARGEMENT);
        consommerEnergie(ENERGIE_CHARGEMENT);
        
        this.colis = colis;
        ajouterHistorique("Colis chargé: " + colis);
    }

    public void livrerColis() throws RobotException {
        if (colis == null) {
            throw new RobotException("Aucun colis à livrer");
        }
        
        ajouterHistorique("Colis livré: " + colis);
        colis = null;
        destination = null;
    }

    // Getters
    public String getColis() { return colis; }
    public String getDestination() { return destination; }
    public void setDestination(String d) { this.destination = d; }

    @Override
    public String toString() {
        return super.toString() + " - " + 
               (colis != null ? "Colis: " + colis : "Aucun colis");
    }

	public void setY(int newY) {
		this.y=newY;		
	}
	public void setX(int newX) {
		this.x=newX;		
	}
}