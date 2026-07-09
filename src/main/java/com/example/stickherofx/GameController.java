package com.example.stickherofx;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import com.example.stickherofx.models.*;
import com.example.stickherofx.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
    private Player player;
    private List<Platform> platforms;
    private List<Food> foods;
    private Stick stick;
    private GameView view;
    private AnimationTimer gameLoop;

    private int score = 0;
    private int highScore = 0;
    private boolean gameOver = false;
    private boolean isGrowingStick = false;
    private boolean isStickFalling = false;
    private double fallAngle = 0;
    private int currentPlatformIndex = 0;
    private Random random = new Random();
    private boolean isRunning = true;

    // States for stick movement
    private boolean isMovingToStickEnd = false;
    private double stickEndX = 0;
    private double stickEndY = 0;
    private boolean isFallingFromStick = false;
    private Stick lastStick = null;
    private double fallTimer = 0;
    private boolean isRestarting = false;
    private boolean stickPlaced = false;
    private boolean hasLandedOnPlatform = false;
    private boolean hasStartedMovingToStickEnd = false;

    public GameController(Canvas canvas) {
        this.view = new GameView(canvas);
        loadHighScore();
        initGame();

        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnKeyPressed(this::handleKeyPressed);
        canvas.setOnKeyReleased(this::handleKeyReleased);
        canvas.setFocusTraversable(true);

        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (!isRunning) return;
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                update(deltaTime);
            }
        };
        gameLoop.start();
    }

    private void initGame() {
        platforms = new ArrayList<>();
        foods = new ArrayList<>();
        score = 0;
        gameOver = false;
        isGrowingStick = false;
        isStickFalling = false;
        isMovingToStickEnd = false;
        isFallingFromStick = false;
        isRestarting = false;
        stickPlaced = false;
        hasLandedOnPlatform = false;
        hasStartedMovingToStickEnd = false;
        fallTimer = 0;
        fallAngle = 0;
        currentPlatformIndex = 0;
        isRunning = true;
        stick = null;
        lastStick = null;

        double x = 50;
        double y = Constants.WINDOW_HEIGHT - Constants.GROUND_Y;
        Platform firstPlatform = new Platform(x, y, 120);
        platforms.add(firstPlatform);
        generatePlatforms(8);

        // Player starts at LEFT EDGE of platform
        player = new Player(
                platforms.get(0).getX() + 5,
                platforms.get(0).getY() - Constants.CHARACTER_SIZE
        );
        platforms.get(0).setHasCharacter(true);

        generateFoodOnPlatforms();

        if (view != null) {
            view.render(player, platforms, null, foods, 0, false, highScore);
        }

        System.out.println(" Game initialized! Player at: " + player.getX() + ", " + player.getY());
    }

    private void generatePlatforms(int count) {
        double lastX = platforms.get(platforms.size() - 1).getX();
        double lastWidth = platforms.get(platforms.size() - 1).getWidth();

        for (int i = 0; i < count; i++) {
            double gap = Constants.GAP_MIN + random.nextDouble() * (Constants.GAP_MAX - Constants.GAP_MIN);
            double width = Constants.PLATFORM_WIDTH_MIN + random.nextDouble() *
                    (Constants.PLATFORM_WIDTH_MAX - Constants.PLATFORM_WIDTH_MIN);
            double x = lastX + lastWidth + gap;
            double y = Constants.WINDOW_HEIGHT - Constants.GROUND_Y;
            Platform platform = new Platform(x, y, width);

            if (random.nextDouble() < 0.15) {
                platform.setSpecial(true);
            }

            platforms.add(platform);
            lastX = x;
            lastWidth = width;
        }
    }

    private void generateFoodOnPlatforms() {
        for (Platform platform : platforms) {
            if (!platform.hasFood() && random.nextInt(100) < Constants.FOOD_SPAWN_CHANCE) {
                double foodX = platform.getX() + random.nextDouble() * (platform.getWidth() - Constants.FOOD_SIZE);
                Food food = new Food(foodX, platform.getY() - 10);
                platform.setFood(food);
                foods.add(food);
            }
        }
    }

    private void update(double deltaTime) {
        if (gameOver || !isRunning || isRestarting) return;

        // Update player physics (skip if falling from stick)
        if (!isFallingFromStick) {
            player.update(deltaTime, platforms);
        }

        // Update platforms
        for (Platform platform : platforms) {
            platform.update(deltaTime);
        }

        // Update foods and check collection
        updateFoods();

        // ===== HANDLE FALLING FROM STICK =====
        if (isFallingFromStick) {
            fallTimer += deltaTime;

            // Apply gravity - use FAST gravity
            double gravity = Constants.GRAVITY_FAST;
            player.getCharacter().setVelocityY(player.getCharacter().getVelocityY() + gravity * deltaTime);

            // Update Y position
            double newY = player.getY() + player.getCharacter().getVelocityY() * deltaTime;
            player.setY(newY);
            player.getCharacter().setY(newY);

            // Debug output (only first few frames)
            if (fallTimer < 0.5) {
                System.out.println(" Falling! Y: " + newY + ", Velocity: " + player.getCharacter().getVelocityY());
            }

            // Check if player fell off screen
            if (player.getCharacter().getY() > Constants.WINDOW_HEIGHT + 100) {
                gameOver = true;
                updateHighScore();
                System.out.println(" Player fell off screen!");
            }
            return;
        }

        // ===== MOVE PLAYER TO STICK END =====
        if (isMovingToStickEnd) {
            double moveSpeed = Constants.CHARACTER_WALK_SPEED * 1.5;
            double dx = moveSpeed * deltaTime;
            double distance = Math.abs(player.getX() - stickEndX);

            if (distance < dx) {
                // Reached stick end
                player.setX(stickEndX);
                player.setY(stickEndY - Constants.CHARACTER_SIZE);
                isMovingToStickEnd = false;
                hasStartedMovingToStickEnd = false;

                // Check if landed on platform
                Platform nextPlatform = null;
                if (currentPlatformIndex + 1 < platforms.size()) {
                    nextPlatform = platforms.get(currentPlatformIndex + 1);
                }

                boolean onPlatform = false;
                if (nextPlatform != null) {
                    double platformLeft = nextPlatform.getX();
                    double platformRight = nextPlatform.getX() + nextPlatform.getWidth();
                    if (stickEndX >= platformLeft - 5 && stickEndX <= platformRight + 5) {
                        onPlatform = true;
                    }
                }

                if (onPlatform && nextPlatform != null) {
                    // SUCCESS - Landed on platform!
                    player.setY(nextPlatform.getY() - Constants.CHARACTER_SIZE);
                    System.out.println(" Landed on platform! Score: " + (score + 1));
                    hasLandedOnPlatform = true;
                    score++;
                    currentPlatformIndex++;
                    platforms.get(currentPlatformIndex).setHasCharacter(true);

                    double targetX = nextPlatform.getX() + nextPlatform.getWidth() - Constants.CHARACTER_SIZE - 5;
                    player.moveTo(targetX);

                    if (currentPlatformIndex >= platforms.size() - 3) {
                        generatePlatforms(5);
                        generateFoodOnPlatforms();
                    }

                    if (score > highScore) {
                        highScore = score;
                        saveHighScore();
                    }
                } else {
                    // FAILURE - Fall down from stick end with HIGH velocity
                    System.out.println("❌ Stick ended in gap! Falling from stick end!");
                    player.getCharacter().setGrounded(false);
                    player.getCharacter().setVelocityY(300);
                    isFallingFromStick = true;
                    fallTimer = 0;
                }

                if (lastStick != null) {
                    lastStick.setShouldRender(true);
                }
            } else {
                // Move towards stick end
                Platform currentPlatform = platforms.get(currentPlatformIndex);

                if (stickEndX > player.getX()) {
                    player.getCharacter().moveRight(moveSpeed);
                } else {
                    player.getCharacter().moveLeft(moveSpeed);
                }
                player.getCharacter().setGrounded(true);

                if (currentPlatform != null) {
                    player.setY(currentPlatform.getY() - Constants.CHARACTER_SIZE);
                }
            }
            return;
        }

        // Check if player fell off screen
        if (player.getCharacter().getY() > Constants.WINDOW_HEIGHT + 100) {
            gameOver = true;
            updateHighScore();
            return;
        }

        // ===== HANDLE STICK GROWTH =====
        if (isGrowingStick && stick != null) {
            stick.grow(deltaTime);
        }

        // ===== HANDLE STICK FALLING =====
        if (isStickFalling && stick != null) {
            stick.update(deltaTime);

            Platform currentPlatform = platforms.get(currentPlatformIndex);
            Platform nextPlatform = null;
            if (currentPlatformIndex + 1 < platforms.size()) {
                nextPlatform = platforms.get(currentPlatformIndex + 1);
            }

            // Check if stick reaches the next platform
            if (nextPlatform != null && stick.isOnPlatform(nextPlatform)) {
                // SUCCESS: Stick reached the next platform
                System.out.println(" Stick reached platform!");
                stick.setFalling(false);
                stick.setPlaced(true);

                double targetX = nextPlatform.getX() + nextPlatform.getWidth() - Constants.CHARACTER_SIZE - 5;
                player.moveTo(targetX);
                score++;
                currentPlatformIndex++;
                platforms.get(currentPlatformIndex).setHasCharacter(true);
                isStickFalling = false;
                stick.setShouldRender(true);
                lastStick = stick;
                stick = null;
                stickPlaced = true;

                if (currentPlatformIndex >= platforms.size() - 3) {
                    generatePlatforms(5);
                    generateFoodOnPlatforms();
                }

                if (score > highScore) {
                    highScore = score;
                    saveHighScore();
                }
            } else if (stick.getAngle() > Math.PI / 2 + 0.1) {
                // Stick has fallen completely
                isStickFalling = false;
                stick.setShouldRender(true);

                // Check if stick tip reaches the platform (edge case)
                if (nextPlatform != null && stick.getEndX() >= nextPlatform.getX() &&
                        stick.getEndX() <= nextPlatform.getX() + nextPlatform.getWidth()) {
                    // Success! Stick tip reaches platform
                    System.out.println(" Stick tip reached platform!");
                    stick.setPlaced(true);

                    double targetX = nextPlatform.getX() + nextPlatform.getWidth() - Constants.CHARACTER_SIZE - 5;
                    player.moveTo(targetX);
                    score++;
                    currentPlatformIndex++;
                    platforms.get(currentPlatformIndex).setHasCharacter(true);
                    lastStick = stick;
                    stick = null;
                    stickPlaced = true;

                    if (currentPlatformIndex >= platforms.size() - 3) {
                        generatePlatforms(5);
                        generateFoodOnPlatforms();
                    }

                    if (score > highScore) {
                        highScore = score;
                        saveHighScore();
                    }
                } else {
                    // ===== STICK DOESN'T REACH PLATFORM =====
                    // Character should move to the end of the stick
                    if (!hasStartedMovingToStickEnd) {
                        System.out.println(" Moving to stick end at X: " + stick.getEndX());
                        stickEndX = stick.getEndX();
                        stickEndY = stick.getEndY();
                        isMovingToStickEnd = true;
                        hasStartedMovingToStickEnd = true;
                        hasLandedOnPlatform = false;

                        stick.setShouldRender(true);
                        lastStick = stick;
                        stick = null;
                        stickPlaced = true;
                    }
                }
            }
        }

        // Check if player is falling off platform (normal case)
        if (!player.isMoving() && !player.getCharacter().isGrounded() && !isMovingToStickEnd && !isFallingFromStick) {
            Platform currentPlatform = platforms.get(currentPlatformIndex);
            if (!player.isOnPlatform(currentPlatform)) {
                player.getCharacter().setGrounded(false);
            }
        }
    }

    private void updateFoods() {
        for (int i = foods.size() - 1; i >= 0; i--) {
            Food food = foods.get(i);
            food.update(0.016);

            if (!food.isCollected() && player.getCharacter().collidesWith(food.getBounds())) {
                food.setCollected(true);
                player.addScore(food.getPoints());
                player.addFood();
                score += food.getPoints();
                foods.remove(i);
                generateFoodOnPlatforms();
            }
        }
    }

    private void handleMousePressed(MouseEvent event) {
        if (gameOver || !isRunning || isRestarting) return;
        if (isGrowingStick || isStickFalling) return;
        if (player.isMoving()) return;
        if (isMovingToStickEnd || isFallingFromStick) return;
        if (currentPlatformIndex >= platforms.size() - 1) return;
        if (player == null) return;

        Platform currentPlatform = platforms.get(currentPlatformIndex);
        stick = new Stick(
                currentPlatform.getX() + currentPlatform.getWidth(),
                currentPlatform.getY()
        );
        stick.setGrowing(true);
        isGrowingStick = true;
        lastStick = null;
        stickPlaced = false;
        hasLandedOnPlatform = false;
        hasStartedMovingToStickEnd = false;

        System.out.println(" Stick growing started!");
    }

    private void handleMouseReleased(MouseEvent event) {
        if (gameOver || !isRunning || isRestarting) return;
        if (!isGrowingStick) return;
        if (stick == null) return;

        isGrowingStick = false;
        if (stick.getLength() > Constants.STICK_MIN_LENGTH) {
            stick.setGrowing(false);
            stick.fall();
            isStickFalling = true;
            fallAngle = 0;
            System.out.println(" Stick falling! Length: " + stick.getLength());
        } else {
            stick = null;
            System.out.println(" Stick too short!");
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case R:
                if (gameOver) {
                    restart();
                }
                break;
            case SPACE:
                if (!gameOver && !isGrowingStick && !isStickFalling && !player.isMoving() &&
                        !isMovingToStickEnd && !isFallingFromStick && !isRestarting) {
                    handleMousePressed(null);
                }
                break;
            case ESCAPE:
                stop();
                break;
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case SPACE:
                if (!gameOver && isGrowingStick && !isRestarting) {
                    handleMouseReleased(null);
                }
                break;
        }
    }

    public void restart() {
        System.out.println(" Restarting game...");

        if (gameLoop != null) {
            gameLoop.stop();
        }

        isRestarting = true;
        isRunning = false;
        gameOver = false;

        // Hide game over screen
        if (view != null) {
            view.hideGameOverScreen();
        }

        initGame();

        if (gameLoop != null) {
            gameLoop.start();
        }

        isRestarting = false;
        isRunning = true;

        render();

        System.out.println(" Game restarted successfully! Player at: " + player.getX() + ", " + player.getY());
    }

    public void stop() {
        isRunning = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }
        saveHighScore();
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            saveHighScore();
        }
    }

    private void saveHighScore() {
        System.out.println(" High Score saved: " + highScore);
    }

    private void loadHighScore() {
        highScore = 0;
    }

    public void render() {
        if (view != null && player != null && platforms != null) {
            Stick renderStick = stick != null ? stick : lastStick;
            view.render(player, platforms, renderStick, foods, score, gameOver, highScore);
        }
    }

    // Show Game Over Screen
    public void showGameOverScreen() {
        if (view != null && gameOver) {
            GameOverScreen gameOverScreen = new GameOverScreen(score, highScore);

            // Handle restart
            gameOverScreen.setOnRestart(() -> {
                restart();
            });

            // Handle home - will be handled by ScreensController
            gameOverScreen.setOnHome(() -> {
                // Stop the game
                stop();
                // The ScreensController will handle returning to home
            });

            view.showGameOverScreen(gameOverScreen);
        }
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isRunning() {
        return isRunning;
    }
}