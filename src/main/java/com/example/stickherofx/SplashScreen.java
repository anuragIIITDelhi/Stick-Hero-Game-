package com.example.stickherofx;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SplashScreen extends StackPane {
    private ImageView splashImage;

    public SplashScreen() {
        try {
            // Load splash image from resources
            Image image = new Image(getClass().getResourceAsStream("/images/splash.jpg"));
            splashImage = new ImageView(image);

            // Make image fit the screen
            splashImage.setFitWidth(800);
            splashImage.setFitHeight(600);
            splashImage.setPreserveRatio(false);

            getChildren().add(splashImage);
            setAlignment(Pos.CENTER);

            // Add fade-in effect
            setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), this);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        } catch (Exception e) {
            System.err.println("Failed to load splash image: " + e.getMessage());
            // Fallback - show a colored background if image fails to load
            setStyle("-fx-background-color: #1a1a2e;");
        }
    }
}