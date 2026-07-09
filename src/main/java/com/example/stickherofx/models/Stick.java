package com.example.stickherofx.models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.stickherofx.utils.Constants;

public class Stick {
    private double startX, startY;
    private double endX, endY;
    private double length = 0;
    private double angle = 0;
    private boolean isGrowing = false;
    private boolean isFalling = false;
    private boolean isPlaced = false;
    private double rotationSpeed = 10;
    private boolean shouldRender = true;

    public Stick(double startX, double startY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = startX;
        this.endY = startY;
        this.shouldRender = true;
    }

    public void grow(double deltaTime) {
        if (isGrowing && !isFalling) {
            length += Constants.STICK_GROWTH_RATE * deltaTime;
            if (length > Constants.STICK_MAX_LENGTH) {
                length = Constants.STICK_MAX_LENGTH;
            }
            endX = startX;
            endY = startY - length;
        }
    }

    public void fall() {
        if (!isFalling && length > Constants.STICK_MIN_LENGTH) {
            isFalling = true;
            isGrowing = false;
            shouldRender = true;
        }
    }

    public void update(double deltaTime) {
        if (isFalling) {
            // Rotate the stick
            angle += rotationSpeed * deltaTime;

            // Calculate end position - stick stays straight
            endX = startX + length * Math.sin(angle);
            endY = startY - length * Math.cos(angle);

            // Check if stick has fallen completely flat
            if (angle > Math.PI / 2 + 0.1) {
                isFalling = false;
                isPlaced = true;
                shouldRender = true;
                // Stick is flat on the ground
                endY = startY;
            }
        }
    }

    public void render(GraphicsContext gc, double offsetX) {
        if (!shouldRender) return;

        double screenStartX = startX - offsetX;
        double screenEndX = endX - offsetX;

        // Only render if visible on screen
        if (screenStartX < -50 || screenStartX > Constants.WINDOW_WIDTH + 50) {
            return;
        }

        // Black thin stick
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(screenStartX, startY, screenEndX, endY);

        // Small dot at the end of the stick
        gc.setFill(Color.BLACK);
        gc.fillOval(screenEndX - 2, endY - 2, 4, 4);
    }

    public boolean isOnPlatform(Platform platform) {
        // Check if the stick's end point is on the platform
        return platform.contains(endX, endY);
    }

    // Getters and Setters
    public double getStartX() { return startX; }
    public double getStartY() { return startY; }
    public double getEndX() { return endX; }
    public double getEndY() { return endY; }
    public double getLength() { return length; }
    public void setLength(double length) {
        this.length = Math.max(0, Math.min(length, Constants.STICK_MAX_LENGTH));
        updateEndPoints();
    }
    public boolean isGrowing() { return isGrowing; }
    public void setGrowing(boolean growing) {
        isGrowing = growing;
        if (growing) {
            isFalling = false;
            isPlaced = false;
            shouldRender = true;
        }
    }
    public boolean isFalling() { return isFalling; }
    public void setFalling(boolean falling) {
        isFalling = falling;
        if (falling) shouldRender = true;
    }
    public boolean isPlaced() { return isPlaced; }


    public void setPlaced(boolean placed) {
        this.isPlaced = placed;
    }

    public double getAngle() { return angle; }
    public void setAngle(double angle) {
        this.angle = angle;
        updateEndPoints();
    }
    public boolean shouldRender() { return shouldRender; }
    public void setShouldRender(boolean shouldRender) { this.shouldRender = shouldRender; }

    private void updateEndPoints() {
        endX = startX + length * Math.sin(angle);
        endY = startY - length * Math.cos(angle);
    }
}