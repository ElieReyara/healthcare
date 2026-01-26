package com.healthcenter;

import com.healthcenter.controller.LoginController;
import com.healthcenter.security.SessionManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Application JavaFX intégrée avec Spring Boot.
 * Version sécurisée avec authentification obligatoire
 */
public class App extends Application {
    
    private ConfigurableApplicationContext springContext;
    private Stage primaryStage;
    private static App instance;
    
    /**
     * Init Spring AVANT JavaFX.
     */
    @Override
    public void init() throws Exception {
        // Spring Boot démarre ici (avant fenêtre JavaFX)
        this.springContext = new SpringApplicationBuilder(AppConfig.class)
            .headless(false)  // Important pour JavaFX
            .run();
        
        instance = this;
    }
    
    /**
     * Démarrage JavaFX (fenêtre).
     * Affiche d'abord l'écran de login avant le dashboard
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        
        // Afficher écran login AVANT dashboard
        afficherLogin();
    }
    
    /**
     * Affiche l'écran de connexion
     */
    public void afficherLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent loginRoot = loader.load();
            
            Stage loginStage = new Stage();
            loginStage.setTitle("Connexion - Poste de Santé");
            loginStage.setScene(new Scene(loginRoot, 400, 350));
            loginStage.setResizable(false);
            loginStage.initModality(Modality.APPLICATION_MODAL);
            
            // Callback après connexion réussie
            LoginController controller = loader.getController();
            controller.setOnLoginSuccess(() -> {
                loginStage.close();
                afficherDashboard(); // Ouvre application principale
            });
            
            loginStage.showAndWait();
            
            // Si fenêtre fermée sans connexion → quitter
            if (!SessionManager.getInstance().isConnecte()) {
                Platform.exit();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }
    
    /**
     * Affiche le dashboard principal après connexion réussie
     */
    private void afficherDashboard() {
        try {
            // Charger FXML du menu principal
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main-menu.fxml")
            );
            
            // Spring gère l'injection dans les controllers
            loader.setControllerFactory(springContext::getBean);
            
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1200, 750);
            primaryStage.setTitle("🏥 Gestion Poste de Santé");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }
    
    /**
     * Arrêt propre de Spring.
     */
    @Override
    public void stop() throws Exception {
        springContext.close();
    }
    
    /**
     * Récupère l'instance de l'application
     */
    public static App getInstance() {
        return instance;
    }
    
    /**
     * Récupère le contexte Spring
     */
    public ConfigurableApplicationContext getSpringContext() {
        return springContext;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
