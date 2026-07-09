package com.example.stickherofx.models;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.stickherofx.utils.Constants;

public class Platform {
    private double x, y;
    private double width, height;
    private boolean hasCharacter = false;
    private boolean hasFood = false;
    private Food food;
    private Color color;
    private boolean isSpecial = false;
    private double specialTimer = 0;

    public Platform(double x, double y, double width) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = Constants.PLATFORM_HEIGHT;  // Now 30 instead of 20
        this.color = Color.BLACK;

        if (Math.random() < 0.2) {
            this.isSpecial = true;
        }
    }

    public void update(double deltaTime) {
        // NOTE: Food is now updated centrally by GameController.updateFoods(),
        // so it is NOT updated again here. Previously this duplicated the
        // food.update() call every frame (once here with the real deltaTime,
        // once in GameController with a hardcoded 0.016), causing food
        // animations to run inconsistently / too fast.
        // Platforms are now solid black - no blinking special color anymore.
    }

    public void render(GraphicsContext gc, double offsetX) {
        double screenX = x - offsetX;

        if (screenX > -width && screenX < Constants.WINDOW_WIDTH + width) {
            // Plain solid black pillar - square corners, no texture, no border
            gc.setFill(Color.BLACK);
            gc.fillRect(screenX, y, width, height);

            // Special indicator - a small red square marker on top, matching
            // the reference image (instead of a gold glow + star emoji)
            if (isSpecial) {
                double markerSize = 10;
                gc.setFill(Color.rgb(230, 30, 30));
                gc.fillRect(screenX + width / 2 - markerSize / 2, y - markerSize / 2, markerSize, markerSize);
            }

            // Render food on platform
            if (hasFood && food != null) {
                food.render(gc, offsetX);
            }
        }
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }

    public boolean contains(double x, double y) {
        return x >= this.x && x <= this.x + width &&
                y >= this.y && y <= this.y + height;
    }

    public double getCenterX() {
        return x + width / 2;
    }

    // Getters and Setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    public double getHeight() { return height; }
    public boolean hasCharacter() { return hasCharacter; }
    public void setHasCharacter(boolean hasCharacter) { this.hasCharacter = hasCharacter; }
    public boolean hasFood() { return hasFood; }
    public void setHasFood(boolean hasFood) { this.hasFood = hasFood; }
    public Food getFood() { return food; }
    public void setFood(Food food) { this.food = food; this.hasFood = food != null; }
    public boolean isSpecial() { return isSpecial; }
    public void setSpecial(boolean special) { isSpecial = special; }
}