package com.example.stickherofx;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class HomeScreen extends StackPane {
    private Runnable onPlayClicked;

    public HomeScreen() {
        // Background image
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("/images/splash1.jpg"));
            ImageView background = new ImageView(bgImage);
            background.setFitWidth(800);
            background.setFitHeight(600);
            background.setPreserveRatio(false);
            getChildren().add(background);
        } catch (Exception e) {
            System.err.println("Failed to load home screen image: " + e.getMessage());
            // Fallback background
            setStyle("-fx-background-color: linear-gradient(to bottom, #0f0c29, #302b63, #24243e);");
        }

        // Content container
        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);

        // Game Title
        Text title = new Text("STICK HERO");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 64));
        title.setFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0, 0, 4);");

        // Subtitle
        Text subtitle = new Text(" Reach the Platform!");
        subtitle.setFont(Font.font("Arial", 24));
        subtitle.setFill(Color.rgb(255, 215, 0));
        subtitle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 5, 0, 0, 2);");

        // Play Button
        Button playButton = new Button("▶ PLAY");
        playButton.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        playButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #f7971e, #ffd200);" +
                        "-fx-text-fill: #1a1a2e;" +
                        "-fx-background-radius: 50;" +
                        "-fx-padding: 15 50 15 50;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.5), 10, 0, 0, 4);"
        );

        // Button hover effect
        playButton.setOnMouseEntered(e -> {
            playButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, #ffd200, #f7971e);" +
                            "-fx-text-fill: #1a1a2e;" +
                            "-fx-background-radius: 50;" +
                            "-fx-padding: 15 50 15 50;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.8), 15, 0, 0, 6);" +
                            "-fx-scale-x: 1.05;" +
                            "-fx-scale-y: 1.05;"
            );
        });

        playButton.setOnMouseExited(e -> {
            playButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, #f7971e, #ffd200);" +
                            "-fx-text-fill: #1a1a2e;" +
                            "-fx-background-radius: 50;" +
                            "-fx-padding: 15 50 15 50;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.5), 10, 0, 0, 4);"
            );
        });

        // Button click animation
        playButton.setOnMousePressed(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), playButton);
            scaleDown.setToX(0.9);
            scaleDown.setToY(0.9);
            scaleDown.play();
        });

        playButton.setOnMouseReleased(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), playButton);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);
            scaleUp.play();
        });

        // Button action
        playButton.setOnAction(e -> {
            if (onPlayClicked != null) {
                // Fade out before switching
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), this);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> onPlayClicked.run());
                fadeOut.play();
            }
        });

        // Version info
        Text version = new Text("v1.0.0");
        version.setFont(Font.font("Arial", 14));
        version.setFill(Color.rgb(255, 255, 255, 0.5));

        content.getChildren().addAll(title, subtitle, playButton, version);

        // Add content on top of background
        getChildren().add(content);
        setAlignment(Pos.CENTER);

        // Fade in animation
        setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    public void setOnPlayClicked(Runnable onPlayClicked) {
        this.onPlayClicked = onPlayClicked;
    }
}