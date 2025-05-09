package code;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class RobotConnecte extends Robot implements Connectable {
    protected boolean connecte;
    protected String reseauConnecte;

    public RobotConnecte(String id, int x, int y) {
        super(id, x, y);
        this.connecte = false;
        this.reseauConnecte = null;
    }
    @Override
    public void connecter(String reseau) throws RobotException {
        verifierEnergie(5);
        if (connecte) {
            throw new RobotException("Le robot est déjà connecté");
        }
        reseauConnecte = reseau;
        connecte = true;
        consommerEnergie(5);
        ajouterHistorique("Connecté au réseau: " + reseau);
     // Utiliser un ScheduledExecutorService pour planifier la déconnexion après 15 secondes
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(this::deconnecter, 30, TimeUnit.SECONDS);
    }

    @Override
    public void deconnecter() {
        if (connecte) {
            ajouterHistorique("Déconnecté du réseau: " + reseauConnecte);
            connecte = false;
            reseauConnecte = null;
        }
    }

    @Override
    public void envoyerDonnees(String donnees) throws RobotException {
        if (!connecte) {
            throw new RobotException("Le robot n'est pas connecté");
        }
        verifierEnergie(3);
        consommerEnergie(3);
        ajouterHistorique("Données envoyées: " + donnees);
    }

    // Getters
    public boolean isConnecte() { return connecte; }
    public String getReseauConnecte() { return reseauConnecte; }

    @Override
    public String toString() {
        return super.toString() + " - " + (connecte ? "Connecté (" + reseauConnecte + ")" : "Déconnecté");
    }
}