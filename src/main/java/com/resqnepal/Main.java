package com.resqnepal;

import com.resqnepal.repository.DatabaseManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager.initializeDatabase();
        AppContext.init();

        // Load JavaFX main layout
        javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/com/resqnepal/main.fxml"));
        Scene scene = new Scene(root, 1024, 768);

        primaryStage.setTitle("ResQ Nepal - Disaster Management & Rescue Coordination System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
