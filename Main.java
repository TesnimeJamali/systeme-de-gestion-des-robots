package code;

public class Main {
    public static void main(String[] args) {
        // Initialisation de quelques robots de test
        try {
            RobotLivraison robot1 = new RobotLivraison("LIV-001", 10, 10);
            RobotLivraison robot2 = new RobotLivraison("LIV-002", 30, 30);
            
            InterfaceRobot.getRobots().add(robot1);
            InterfaceRobot.getRobots().add(robot2);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Démarrer l'interface
        javax.swing.SwingUtilities.invokeLater(() -> {
            InterfaceRobot.demarrerInterface();
        });
    }
}