package com.example.stickherofx.models;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import java.util.List;

public class Player {
    private Character character;
    private double targetX;
    private boolean isMovingToTarget = false;
    private double moveSpeed = 500;
    private int health = 100;
    private int score = 0;
    private int foodCollected = 0;
    private boolean isAlive = true;

    public Player(double x, double y) {
        this.character = new Character(x, y);
        this.targetX = x;
    }

    public void update(double deltaTime, List<Platform> platforms) {
        // Update character physics
        character.update(deltaTime);

        // Check platform collisions
        for (Platform platform : platforms) {
            checkPlatformCollision(platform);
        }

        // Handle movement to target
        if (isMovingToTarget) {
            double dx = moveSpeed * deltaTime;
            if (Math.abs(character.getX() - targetX) < dx) {
                character.setX(targetX);
                isMovingToTarget = false;
                character.stopMoving();
                character.setGrounded(true);
            } else {
                if (targetX > character.getX()) {
                    character.moveRight(moveSpeed);
                } else {
                    character.moveLeft(moveSpeed);
                }
                character.setGrounded(true);
            }
        }
    }

    private void checkPlatformCollision(Platform platform) {
        Rectangle2D charBounds = character.getBounds();
        Rectangle2D platBounds = platform.getBounds();

        if (charBounds.intersects(platBounds)) {
            if (character.getVelocityY() >= 0) {
                double charBottom = character.getY() + character.getHeight();
                double platTop = platform.getY();

                if (charBottom >= platTop && charBottom <= platTop + 20) {
                    character.setY(platTop - character.getHeight());
                    character.setVelocityY(0);
                    character.setGrounded(true);
                    platform.setHasCharacter(true);
                }
            }
        }
    }

    public void moveTo(double x) {
        this.targetX = x;
        this.isMovingToTarget = true;
        // Make sure to stop any existing movement
        character.stopMoving();
        // Set grounded false so gravity can work if needed
        character.setGrounded(false);
    }

    public void render(GraphicsContext gc, double offsetX) {
        character.render(gc, offsetX);
    }

    public boolean isOnPlatform(Platform platform) {
        return character.getBounds().intersects(platform.getBounds());
    }

    // ===== ALL GETTERS AND SETTERS - PROPERLY USED =====

    // Character position
    public double getX() {
        return character.getX();
    }

    public void setX(double x) {
        character.setX(x);
    }

    public double getY() {
        return character.getY();
    }

    public void setY(double y) {
        character.setY(y);
    }

    // Character dimensions
    public double getWidth() {
        return character.getWidth();
    }

    public double getHeight() {
        return character.getHeight();
    }

    // Movement
    public boolean isMoving() {
        return character.isMoving() || isMovingToTarget;
    }

    public boolean isGrounded() {
        return character.isGrounded();
    }

    public void setGrounded(boolean grounded) {
        character.setGrounded(grounded);
    }

    public void setVelocityY(double velocityY) {
        character.setVelocityY(velocityY);
    }

    public double getVelocityY() {
        return character.getVelocityY();
    }

    // Health
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(100, health));
        character.setHealth(this.health); // Use this.health, not health param
    }

    public int getHealth() {
        return character.getHealth(); // Always return from character
    }

    public void takeDamage(int damage) {
        setHealth(getHealth() - damage);
        if (getHealth() <= 0) {
            setAlive(false);
        }
    }

    public void heal(int amount) {
        setHealth(getHealth() + amount);
    }

    // Score
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        character.setScore(score);
    }

    public void addScore(int points) {
        this.score += points;
        character.addScore(points);
    }

    // Food
    public int getFoodCollected() {
        return foodCollected;
    }

    public void setFoodCollected(int foodCollected) {
        this.foodCollected = foodCollected;
        character.setFoodCollected(foodCollected);
    }

    public void addFood() {
        this.foodCollected++;
        character.addFood();
    }

    // Life status
    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
        character.setAlive(alive);
    }

    // Character access
    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    // Movement target
    public double getTargetX() {
        return targetX;
    }

    public void setTargetX(double targetX) {
        this.targetX = targetX;
    }

    public boolean isMovingToTarget() {
        return isMovingToTarget;
    }

    public void setMovingToTarget(boolean movingToTarget) {
        isMovingToTarget = movingToTarget;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
}