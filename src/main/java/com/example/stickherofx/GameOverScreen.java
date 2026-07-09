package com.example.stickherofx;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameOverScreen extends StackPane {
    private Runnable onRestart;
    private Runnable onHome;

    public GameOverScreen(int score, int highScore) {
        // Semi-transparent dark overlay
        Rectangle overlay = new Rectangle(800, 600);
        overlay.setFill(Color.rgb(0, 0, 0, 0.75));
        getChildren().add(overlay);

        // Main content container
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(450);
        content.setStyle(
                "-fx-background-color: rgba(30, 30, 60, 0.95);" +
                        "-fx-background-radius: 25;" +
                        "-fx-padding: 40;" +
                        "-fx-border-color: rgba(255, 215, 0, 0.5);" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 25;"
        );

        // Game Over Emoji and Text
        Text gameOverText = new Text(" GAME OVER");
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gameOverText.setFill(Color.rgb(255, 50, 50));
        gameOverText.setEffect(new DropShadow(10, Color.rgb(255, 0, 0, 0.5)));

        // Score display
        Text scoreLabel = new Text("SCORE");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        scoreLabel.setFill(Color.rgb(200, 200, 200));

        Text scoreText = new Text(String.valueOf(score));
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        scoreText.setFill(Color.rgb(255, 215, 0));
        scoreText.setEffect(new DropShadow(10, Color.rgb(255, 215, 0, 0.3)));

        // High Score
        Text highScoreLabel = new Text("🏆 BEST SCORE");
        highScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        highScoreLabel.setFill(Color.rgb(200, 200, 200));

        Text highScoreText = new Text(String.valueOf(highScore));
        highScoreText.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        highScoreText.setFill(Color.rgb(255, 215, 0));

        // Divider line
        Rectangle divider = new Rectangle(300, 2);
        divider.setFill(Color.rgb(255, 255, 255, 0.2));

        // Buttons container
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        // Restart Button
        Button restartButton = createStyledButton(" PLAY AGAIN",
                "linear-gradient(to right, #00b894, #00cec9)",
                "#00b894"
        );
        restartButton.setOnAction(e -> {
            if (onRestart != null) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(400), this);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> onRestart.run());
                fadeOut.play();
            }
        });

        // Home Button
        Button homeButton = createStyledButton(" HOME",
                "linear-gradient(to right, #6c5ce7, #a29bfe)",
                "#6c5ce7"
        );
        homeButton.setOnAction(e -> {
            if (onHome != null) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(400), this);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> onHome.run());
                fadeOut.play();
            }
        });

        // Achievement message if high score
        if (score >= highScore && score > 0) {
            Text achievement = new Text("🎉 NEW HIGH SCORE! 🎉");
            achievement.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            achievement.setFill(Color.rgb(255, 215, 0));
            achievement.setEffect(new DropShadow(10, Color.rgb(255, 215, 0, 0.5)));
            content.getChildren().add(achievement);
        }

        buttonBox.getChildren().addAll(restartButton, homeButton);

        content.getChildren().addAll(
                gameOverText,
                scoreLabel,
                scoreText,
                highScoreLabel,
                highScoreText,
                divider,
                buttonBox
        );

        getChildren().add(content);
        setAlignment(Pos.CENTER);

        // Fade in animation
        setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Scale animation for content
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(500), content);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.play();
    }

    private Button createStyledButton(String text, String gradient, String shadowColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setStyle(
                "-fx-background-color: " + gradient + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 30;" +
                        "-fx-padding: 12 35 12 35;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, " + shadowColor + ", 10, 0, 0, 4);"
        );

        // Button hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: " + gradient + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 30;" +
                            "-fx-padding: 12 35 12 35;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, " + shadowColor + ", 20, 0, 0, 8);" +
                            "-fx-scale-x: 1.05;" +
                            "-fx-scale-y: 1.05;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: " + gradient + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 30;" +
                            "-fx-padding: 12 35 12 35;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, " + shadowColor + ", 10, 0, 0, 4);"
            );
        });

        // Click animation
        button.setOnMousePressed(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
            scaleDown.setToX(0.95);
            scaleDown.setToY(0.95);
            scaleDown.play();
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);
            scaleUp.play();
        });

        return button;
    }

    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    public void setOnHome(Runnable onHome) {
        this.onHome = onHome;
    }
}