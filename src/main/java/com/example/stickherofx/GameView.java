package com.example.stickherofx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import com.example.stickherofx.models.Player;
import com.example.stickherofx.models.Platform;
import com.example.stickherofx.models.Stick;
import com.example.stickherofx.models.Food;
import com.example.stickherofx.utils.Constants;
import java.util.List;

public class GameView {
    private Canvas canvas;
    private GraphicsContext gc;
    private double offsetX = 0;
    private double parallaxOffset = 0;
    private StackPane parentContainer;
    private GameOverScreen gameOverScreen;

    public GameView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        // Get parent container
        if (canvas.getParent() instanceof StackPane) {
            this.parentContainer = (StackPane) canvas.getParent();
        }
    }

    // Reset camera offset
    public void resetCamera() {
        offsetX = 0;
        parallaxOffset = 0;
    }

    public void render(Player player, List<Platform> platforms, Stick stick,
                       List<Food> foods, int score, boolean gameOver, int highScore) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Update camera - only if player exists
        if (player != null) {
            if (player.getX() > canvas.getWidth() * 0.3) {
                offsetX = player.getX() - canvas.getWidth() * 0.3;
                parallaxOffset = offsetX * 0.3;
            } else {
                // Reset offset if player is near start
                offsetX = 0;
                parallaxOffset = 0;
            }
        }

        // Draw background
        drawBackground();

        // Draw gap indicators between platforms
        drawGaps(platforms);

        // Draw platforms
        for (Platform platform : platforms) {
            platform.render(gc, offsetX);
        }

        // Draw foods
        if (foods != null) {
            for (Food food : foods) {
                food.render(gc, offsetX);
            }
        }

        // Draw stick - ALWAYS draw stick if it exists
        if (stick != null) {
            stick.render(gc, offsetX);
        }

        // Draw player
        if (player != null) {
            player.render(gc, offsetX);
        }

        // Draw UI
        drawUI(player, score, highScore, gameOver);
    }

    private void drawBackground() {
        // Sky gradient
        LinearGradient skyGradient = new LinearGradient(
                0, 0, 0, canvas.getHeight(),
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(135, 206, 235)),
                new Stop(1, Color.rgb(255, 200, 150))
        );
        gc.setFill(skyGradient);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Sun
        gc.setFill(Color.rgb(255, 255, 100, 0.3));
        gc.fillOval(canvas.getWidth() - 120 - parallaxOffset * 0.2, 40, 80, 80);
        gc.setFill(Color.rgb(255, 255, 100, 0.5));
        gc.fillOval(canvas.getWidth() - 110 - parallaxOffset * 0.2, 50, 60, 60);
        gc.setFill(Color.rgb(255, 255, 0));
        gc.fillOval(canvas.getWidth() - 100 - parallaxOffset * 0.2, 60, 40, 40);

        // Clouds
        drawCloud(canvas.getWidth() * 0.2 - parallaxOffset * 0.5, 60, 80);
        drawCloud(canvas.getWidth() * 0.6 - parallaxOffset * 0.4, 100, 100);
        drawCloud(canvas.getWidth() * 0.9 - parallaxOffset * 0.3, 40, 70);

        // Background mountains
        gc.setFill(Color.rgb(100, 150, 100, 0.3));
        double[] mountainPoints = {0, 200, 150, 100, 300, 180, 450, 80, 600, 160, 750, 90, 900, 170};
        for (int i = 0; i < mountainPoints.length - 2; i += 2) {
            double x1 = mountainPoints[i] - parallaxOffset * 0.5;
            double x2 = mountainPoints[i+2] - parallaxOffset * 0.5;
            double y1 = canvas.getHeight() - Constants.GROUND_Y - mountainPoints[i+1];
            double y2 = canvas.getHeight() - Constants.GROUND_Y - mountainPoints[i+3];
            gc.fillPolygon(
                    new double[]{x1, (x1+x2)/2, x2},
                    new double[]{canvas.getHeight() - Constants.GROUND_Y,
                            y1 < y2 ? y1-30 : y2-30,
                            canvas.getHeight() - Constants.GROUND_Y},
                    3
            );
        }
    }

    private void drawGaps(List<Platform> platforms) {
        for (int i = 0; i < platforms.size() - 1; i++) {
            Platform current = platforms.get(i);
            Platform next = platforms.get(i + 1);

            double gapStart = current.getX() + current.getWidth() - offsetX;
            double gapEnd = next.getX() - offsetX;
            double gapWidth = gapEnd - gapStart;

            if (gapStart < canvas.getWidth() && gapEnd > 0 && gapWidth > 10) {
                gc.setFill(Color.rgb(255, 0, 0, 0.15));
                gc.fillRect(gapStart, current.getY() - 20, gapWidth, current.getHeight() + 40);

                gc.setStroke(Color.rgb(255, 0, 0, 0.3));
                gc.setLineWidth(1);
                gc.setLineDashes(5);
                gc.strokeLine(gapStart, current.getY() - 10, gapStart, current.getY() + current.getHeight() + 10);
                gc.strokeLine(gapEnd, current.getY() - 10, gapEnd, current.getY() + current.getHeight() + 10);
                gc.setLineDashes(0);

                if (gapWidth > 80) {
                    gc.setFill(Color.rgb(255, 0, 0, 0.3));
                    gc.setFont(Font.font(16));
                    double centerX = (gapStart + gapEnd) / 2;
                    gc.fillText("⚠️ KHAAI ⚠️", centerX - 50, current.getY() + current.getHeight() / 2 + 5);
                }
            }
        }
    }

    private void drawCloud(double x, double y, double size) {
        gc.setFill(Color.rgb(255, 255, 255, 0.7));
        gc.fillOval(x, y, size * 0.8, size * 0.5);
        gc.fillOval(x + size * 0.3, y - size * 0.2, size * 0.6, size * 0.6);
        gc.fillOval(x + size * 0.6, y, size * 0.7, size * 0.4);
        gc.fillOval(x + size * 0.15, y - size * 0.1, size * 0.8, size * 0.5);
    }

    private void drawUI(Player player, int score, int highScore, boolean gameOver) {
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRoundRect(10, 10, 200, 110, 10, 10);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("🎮 STICK HERO", 20, 40);

        gc.setFill(Color.rgb(255, 215, 0));
        gc.setFont(Font.font(18));
        gc.fillText("Score: " + score, 20, 70);

        gc.setFill(Color.rgb(255, 255, 255, 0.7));
        gc.fillText("🏆 Best: " + highScore, 20, 95);

        if (player != null) {
            gc.setFill(Color.rgb(255, 200, 100));
            gc.fillText(" x" + player.getFoodCollected(), 20, 115);
        }

        if (player != null && !gameOver) {
            int health = player.getHealth();
            gc.setFill(Color.rgb(0, 0, 0, 0.5));
            gc.fillRoundRect(canvas.getWidth() - 150, 20, 120, 25, 5, 5);
            gc.setFill(health > 60 ? Color.GREEN : health > 30 ? Color.ORANGE : Color.RED);
            gc.fillRoundRect(canvas.getWidth() - 145, 25, 110 * health / 100.0, 15, 3, 3);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(12));
            gc.fillText(" " + health + "%", canvas.getWidth() - 120, 37);
        }

        if (!gameOver) {
            gc.setFill(Color.rgb(255, 255, 255, 0.7));
            gc.setFont(Font.font(14));
            gc.fillText(" Click & hold to grow stick", canvas.getWidth() / 2 - 100, canvas.getHeight() - 60);
            gc.fillText(" Release to place stick", canvas.getWidth() / 2 - 80, canvas.getHeight() - 35);
            gc.fillText("⌨️ Space = Click (grow/place)", canvas.getWidth() / 2 - 85, canvas.getHeight() - 10);
        }
    }

    // Show Game Over Screen
    public void showGameOverScreen(GameOverScreen screen) {
        this.gameOverScreen = screen;
        if (parentContainer != null) {
            parentContainer.getChildren().add(screen);
        }
    }

    // Hide Game Over Screen
    public void hideGameOverScreen() {
        if (gameOverScreen != null && parentContainer != null) {
            parentContainer.getChildren().remove(gameOverScreen);
            gameOverScreen = null;
        }
    }
}