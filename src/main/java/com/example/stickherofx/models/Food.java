package com.example.stickherofx.models;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.stickherofx.utils.Constants;

public class Food {
    private double x, y;
    private double size = Constants.FOOD_SIZE;
    private String type;
    private Color color;
    private boolean isCollected = false;
    private double floatOffset = 0;
    private double floatSpeed = 2;
    private double rotation = 0;
    private int points;

    public Food(double x, double y) {
        this.x = x;
        this.y = y - size / 2;
        this.points = Constants.FOOD_SCORE_BONUS;

        String[] types = {"🍎", "🍌", "🍇", "🍊", "🍓", "🍒", "🍑", "🍉", "🍔", "🍕"};
        int index = (int)(Math.random() * types.length);
        this.type = types[index];

        switch(index) {
            case 0: color = Color.RED; break;
            case 1: color = Color.YELLOW; break;
            case 2: color = Color.PURPLE; break;
            case 3: color = Color.ORANGE; break;
            case 4: color = Color.rgb(255, 50, 50); break;
            case 5: color = Color.rgb(200, 0, 50); break;
            case 6: color = Color.rgb(255, 200, 100); break;
            case 7: color = Color.rgb(50, 200, 50); break;
            case 8: color = Color.rgb(200, 150, 50); break;
            case 9: color = Color.rgb(255, 200, 0); break;
            default: color = Color.GREEN;
        }
    }

    public void update(double deltaTime) {
        floatOffset += deltaTime * floatSpeed;
        rotation += deltaTime * 1.5;
    }

    public void render(GraphicsContext gc, double offsetX) {
        if (isCollected) return;

        double screenX = x - offsetX;
        double screenY = y + Math.sin(floatOffset) * 8;

        // Glow effect
        gc.setFill(Color.rgb(255, 255, 255, 0.1));
        gc.fillOval(screenX - 10, screenY - 10, size + 20, size + 20);

        // Food emoji
        gc.setFill(color);
        gc.setFont(javafx.scene.text.Font.font(size * 1.2));
        gc.fillText(type, screenX, screenY + size * 0.8);

        // Sparkle effects
        for (int i = 0; i < 3; i++) {
            double angle = rotation + i * 2.094;
            double distance = size * 0.8;
            double sparkleX = screenX + size/2 + Math.cos(angle) * distance;
            double sparkleY = screenY + size/2 + Math.sin(angle) * distance;

            gc.setFill(Color.rgb(255, 255, 255, 0.3 + Math.sin(floatOffset * 2 + i) * 0.2));
            gc.fillOval(sparkleX - 2, sparkleY - 2, 4, 4);
        }

        // Score indicator
        gc.setFill(Color.rgb(255, 255, 255, 0.7));
        gc.setFont(javafx.scene.text.Font.font(12));
        gc.fillText("+" + points, screenX + size/2 - 8, screenY - 10);
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, size, size);
    }

    public boolean collidesWith(Rectangle2D other) {
        return getBounds().intersects(other);
    }

    // Getters and Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return size; }
    public String getType() { return type; }
    public Color getColor() { return color; }
    public boolean isCollected() { return isCollected; }
    public void setCollected(boolean collected) { isCollected = collected; }
    public int getPoints() { return points; }
}