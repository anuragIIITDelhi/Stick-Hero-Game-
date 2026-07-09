package com.example.stickherofx.models;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import com.example.stickherofx.utils.Constants;
///import com.example.stickherofx.models.*;

public class Character {
    private double x, y;
    private double width = Constants.CHARACTER_SIZE;
    private double height = Constants.CHARACTER_SIZE;
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean isGrounded = false;
    private boolean isMoving = false;
    private boolean isJumping = false;
    private double animationTimer = 0;
    private int direction = 1;

    private int health = 100;
    private int score = 0;
    private int foodCollected = 0;
    private boolean isAlive = true;

    public Character(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(double deltaTime) {
        // Apply gravity
        if (!isGrounded) {
            velocityY += Constants.GRAVITY * deltaTime;
        }

        // Update position
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;

        // Update animation
        if (isMoving) {
            animationTimer += deltaTime;
        }

        // Check if falling off screen
        if (y > Constants.WINDOW_HEIGHT + 100) {
            isAlive = false;
        }
    }

    public void moveLeft(double speed) {
        velocityX = -speed;
        isMoving = true;
        direction = -1;
    }

    public void moveRight(double speed) {
        velocityX = speed;
        isMoving = true;
        direction = 1;
    }

    public void stopMoving() {
        velocityX = 0;
        isMoving = false;
    }

    public void jump() {   // this function has no usages
        if (isGrounded && !isJumping) {
            velocityY = Constants.CHARACTER_JUMP_SPEED;
            isGrounded = false;
            isJumping = true;
        }
    }

    public void render(GraphicsContext gc, double offsetX) {
        double screenX = x - offsetX;
        double screenY = y;

        // Shadow
        gc.setFill(Color.rgb(0, 0, 0, 0.2));
        gc.fillOval(screenX + 5, screenY + height - 5, width - 10, 8);

        // Character body - BLACK
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(screenX, screenY, width, height, 10, 10);

        // Body highlight
        gc.setFill(Color.rgb(40, 40, 40));
        gc.fillRoundRect(screenX + 5, screenY + 5, width - 10, height - 10, 8, 8);

        // ===== DIAGONAL RED BANDANA =====
        // A single band wrapping across the head, with a triangular flag
        // point sticking out past the left edge of the body. The flag
        // always points toward the character's facing direction's opposite
        // side (trailing behind), so it flips when direction flips.
        double bandY = screenY + height * 0.22;
        double bandThickness = height * 0.22;
        double flagLength = width * 0.85;

        gc.setFill(Color.rgb(230, 0, 0));
        if (direction == 1) {
            // Flag trails out to the left
            gc.fillPolygon(
                    new double[]{
                            screenX - flagLength,               // flag tip
                            screenX + width * 0.15,              // band top-left (on body)
                            screenX + width,                     // band top-right
                            screenX + width,                     // band bottom-right
                            screenX + width * 0.15                // band bottom-left (on body)
                    },
                    new double[]{
                            bandY + bandThickness * 0.5,          // flag tip (mid-height, pointed)
                            bandY - bandThickness * 0.35,
                            bandY - bandThickness * 0.1,
                            bandY + bandThickness * 0.9,
                            bandY + bandThickness * 1.25
                    },
                    5
            );
        } else {
            // Facing left: flag trails out to the right instead
            gc.fillPolygon(
                    new double[]{
                            screenX + width + flagLength,
                            screenX + width * 0.85,
                            screenX,
                            screenX,
                            screenX + width * 0.85
                    },
                    new double[]{
                            bandY + bandThickness * 0.5,
                            bandY - bandThickness * 0.35,
                            bandY - bandThickness * 0.1,
                            bandY + bandThickness * 0.9,
                            bandY + bandThickness * 1.25
                    },
                    5
            );
        }

        // ===== SINGLE VISIBLE EYE =====
        // Just a plain white circle - the bandana covers the rest of the face.
        gc.setFill(Color.WHITE);
        double eyeSize = width * 0.22;
        double eyeX = direction == 1 ? screenX + width * 0.60 : screenX + width * 0.18;
        gc.fillOval(eyeX, screenY + height * 0.30, eyeSize, eyeSize);

        // ===== ROUND FEET =====
        gc.setFill(Color.BLACK);
        double footRadius = width * 0.17;
        double bounce = isMoving ? Math.abs(Math.sin(animationTimer * 8)) * 2 : 0;
        gc.fillOval(screenX + width * 0.24 - footRadius, screenY + height - footRadius + bounce, footRadius * 2, footRadius * 2);
        gc.fillOval(screenX + width * 0.76 - footRadius, screenY + height - footRadius - bounce, footRadius * 2, footRadius * 2);

        // Health bar
        double healthBarWidth = 40;
        double healthBarX = screenX + (width - healthBarWidth) / 2;
        gc.setFill(Color.rgb(255, 0, 0, 0.5));
        gc.fillRect(healthBarX, screenY - 25, healthBarWidth, 5);
        gc.setFill(Color.rgb(0, 255, 0, 0.8));
        gc.fillRect(healthBarX, screenY - 25, healthBarWidth * (health / 100.0), 5);
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }

    public boolean collidesWith(Rectangle2D other) {
        return getBounds().intersects(other);
    }

    // ===== ALL GETTERS AND SETTERS =====

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; } //   setHeight is not showing no usage

    public double getVelocityX() { return velocityX; }     // get velocity aslo
    public void setVelocityX(double velocityX) { this.velocityX = velocityX; }   // set velocity

    public double getVelocityY() { return velocityY; }
    public void setVelocityY(double velocityY) { this.velocityY = velocityY; }

    public boolean isGrounded() { return isGrounded; }
    public void setGrounded(boolean grounded) {
        isGrounded = grounded;
        if (grounded) isJumping = false;
    }

    public boolean isMoving() { return isMoving; }
    public void setMoving(boolean moving) { isMoving = moving; }

    public boolean isJumping() { return isJumping; }   // isjumping also no usage
    public void setJumping(boolean jumping) { isJumping = jumping; }

    public double getAnimationTimer() { return animationTimer; }   // get Animation Timer
    public void setAnimationTimer(double animationTimer) { this.animationTimer = animationTimer; }  // set animation

    public int getDirection() { return direction; }  //<==== this also
    public void setDirection(int direction) { this.direction = direction; }  // <---- this also

    public int getHealth() { return health; }    // get health also no usage
    public void setHealth(int health) { this.health = Math.max(0, Math.min(100, health)); }

    public int getScore() { return score; }   // <===== this one

    public void setScore(int score) { this.score = score; }

    public void addScore(int points) { this.score += points; }

    public int getFoodCollected() { return foodCollected; } //<--- this one
    public void setFoodCollected(int foodCollected) { this.foodCollected = foodCollected; }

    public void addFood() { this.foodCollected++; }

    public boolean isAlive() { return isAlive; }    // <- this one
    public void setAlive(boolean alive) { isAlive = alive; }
}