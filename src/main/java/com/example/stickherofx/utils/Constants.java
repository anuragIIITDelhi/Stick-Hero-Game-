package com.example.stickherofx.utils;

public class Constants {
    // Window
    public static final double WINDOW_WIDTH = 900;
    public static final double WINDOW_HEIGHT = 600;

    // Platform
    public static final double PLATFORM_WIDTH_MIN = 80;
    public static final double PLATFORM_WIDTH_MAX = 200;
    public static final double PLATFORM_HEIGHT = 600;
    public static final double GAP_MIN = 100;
    public static final double GAP_MAX = 280;
    public static final double GROUND_Y = 150;

    // Character - Size
    public static final double CHARACTER_SIZE = 45;
    public static final double CHARACTER_WALK_SPEED = 150;

    // Character - Physics (FALLING SPEED)
    public static final double GRAVITY = 600;
    public static final double GRAVITY_SLOW = 500;
    public static final double GRAVITY_NORMAL = 600;
    public static final double GRAVITY_FAST = 1000;
    public static final double GRAVITY_VERY_FAST = 1200;

    // Character - Jump (if you add jumping later)
    public static final double CHARACTER_JUMP_SPEED = -400;

    // Stick
    public static final double STICK_GROWTH_RATE = 180;
    public static final double STICK_MIN_LENGTH = 10;
    public static final double STICK_MAX_LENGTH = 600;
    public static final double STICK_FALL_SPEED = 400;

    // Food
    public static final double FOOD_SIZE = 25;
    public static final int FOOD_SPAWN_CHANCE = 30;
    public static final int FOOD_SCORE_BONUS = 10;

    // Game
    public static final int MAX_PLATFORMS = 15;
    public static final double GAME_SPEED_INCREASE = 1.05;



    // Splash Screen
    public static final double SPLASH_DISPLAY_TIME = 2.5; // seconds
    public static final double SPLASH_FADE_DURATION = 1.0; // seconds

    // Screen Transitions
    public static final double FADE_DURATION = 0.8; // seconds
    public static final double SCREEN_TRANSITION_SPEED = 0.5; // seconds

    // Game Over Screen
    public static final double GAME_OVER_FADE_DURATION = 0.5; // seconds
    public static final double GAME_OVER_SCALE_DURATION = 0.5; // seconds

    // UI Colors (for consistent theming)
    public static final String COLOR_PRIMARY = "#f7971e";
    public static final String COLOR_SECONDARY = "#ffd200";
    public static final String COLOR_SUCCESS = "#00b894";
    public static final String COLOR_DANGER = "#ff3b30";
    public static final String COLOR_INFO = "#6c5ce7";

    // Button Sizes
    public static final double BUTTON_WIDTH = 200;
    public static final double BUTTON_HEIGHT = 50;
    public static final double BUTTON_CORNER_RADIUS = 30;

    // Font Sizes
    public static final double FONT_TITLE = 48;
    public static final double FONT_SUBTITLE = 24;
    public static final double FONT_BUTTON = 18;
    public static final double FONT_SCORE = 56;
    public static final double FONT_HIGH_SCORE = 32;
    public static final double FONT_GAME_OVER = 48;
}