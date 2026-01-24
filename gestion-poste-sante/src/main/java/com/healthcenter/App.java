package com.healthcenter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Application JavaFX intégrée avec Spring Boot.
 */
public class App extends Application {
    
    private ConfigurableApplicationContext springContext;
    
    /**
     * Init Spring AVANT JavaFX.
     */
    @Override
    public void init() throws Exception {
        // Spring Boot démarre ici (avant fenêtre JavaFX)
        this.springContext = new SpringApplicationBuilder(AppConfig.class)
            .headless(false)  // Important pour JavaFX
            .run();
    }
    
    /**
     * Démarrage JavaFX (fenêtre).
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Charger FXML
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/patient-list.fxml")
            );
            
            // Spring gère l'injection dans les controllers
            loader.setControllerFactory(springContext::getBean);
            
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 900, 600);
            primaryStage.setTitle("Gestion Poste de Santé - Patients");
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
    
    public static void main(String[] args) {
        launch(args);
    }
}
