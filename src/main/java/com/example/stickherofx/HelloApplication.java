package com.example.stickherofx;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    private ScreensController screensController;

    @Override
    public void start(Stage primaryStage) {
        // Initialize screen controller
        screensController = new ScreensController(primaryStage);

        // Start with splash screen
        screensController.showSplashScreen();
    }

    @Override
    public void stop() {
        // Clean up if needed
        System.out.println("Game shutting down...");
    }

    public static void main(String[] args) {
        launch(args);
    }
}