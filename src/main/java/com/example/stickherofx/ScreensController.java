package com.example.stickherofx;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.stickherofx.utils.Constants;

public class ScreensController {
    private Stage primaryStage;
    private StackPane root;
    private Scene scene;
    private GameController gameController;
    private Canvas canvas;
    private HomeScreen homeScreen;
    private GameOverScreen gameOverScreen;
    private javafx.animation.AnimationTimer renderLoop;

    public ScreensController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.root = new StackPane();
        this.scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        primaryStage.setTitle(" Stick Hero");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Show Splash Screen
    public void showSplashScreen() {
        SplashScreen splash = new SplashScreen();
        root.getChildren().add(splash);

        // Fade out after 2.5 seconds and show home screen
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2.5), splash);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            root.getChildren().remove(splash);
            showHomeScreen();
        });
        fadeOut.play();
    }

    // Show Home Screen
    public void showHomeScreen() {
        homeScreen = new HomeScreen();
        root.getChildren().add(homeScreen);

        // Handle play button click
        homeScreen.setOnPlayClicked(() -> {
            root.getChildren().remove(homeScreen);
            homeScreen = null;
            startGame();
        });
    }

    // Start the actual game
    private void startGame() {
        canvas = new Canvas(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        root.getChildren().add(canvas);

        gameController = new GameController(canvas);
        canvas.setFocusTraversable(true);

        // Set up game loop for rendering
        renderLoop = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameController != null && gameController.isRunning()) {
                    gameController.render();

                    // Check for game over
                    if (gameController.isGameOver() && gameOverScreen == null) {
                        // Show game over screen
                        showGameOverScreen();
                    }

                    // Update window title with score
                    int currentScore = gameController.getScore();
                    if (currentScore > 0) {
                        primaryStage.setTitle(" Stick Hero - Score: " + currentScore);
                    }
                }
            }
        };
        renderLoop.start();

        // Handle window close
        primaryStage.setOnCloseRequest(e -> {
            if (gameController != null) {
                gameController.stop();
            }
            if (renderLoop != null) {
                renderLoop.stop();
            }
        });
    }

    // Show Game Over Screen
    private void showGameOverScreen() {
        if (gameOverScreen != null) return; // Already showing

        int score = gameController.getScore();
        int highScore = gameController.getHighScore();

        gameOverScreen = new GameOverScreen(score, highScore);
        root.getChildren().add(gameOverScreen);

        // Handle restart
        gameOverScreen.setOnRestart(() -> {
            root.getChildren().remove(gameOverScreen);
            gameOverScreen = null;
            if (gameController != null) {
                gameController.restart();
            }
        });

        // Handle home
        gameOverScreen.setOnHome(() -> {
            root.getChildren().remove(gameOverScreen);
            gameOverScreen = null;

            // Stop the game
            if (gameController != null) {
                gameController.stop();
            }

            // Stop render loop
            if (renderLoop != null) {
                renderLoop.stop();
            }

            // Remove canvas
            root.getChildren().remove(canvas);
            canvas = null;
            gameController = null;

            // Show home screen
            showHomeScreen();
        });
    }

    // Show Game Over from outside (if needed)
    public void triggerGameOver() {
        if (gameController != null && gameController.isGameOver() && gameOverScreen == null) {
            showGameOverScreen();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void stop() {
        if (renderLoop != null) {
            renderLoop.stop();
        }
        if (gameController != null) {
            gameController.stop();
        }
    }
}