package com.example.screens;

import com.example.base.BaseScreen;
import com.example.logic.Game;
import com.example.logic.GameConfig;
import com.example.logic.GameEventListener;
import com.example.navigation.ScreenNavigator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.example.audio.AudioManager;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;

public class GameScreen implements GameEventListener, ScreenNavigator {
    private Game game;
    private Label hpLabel;
    private ProgressBar hpBar;
    private TextArea gameOutput;
    private TextField playerInput;

    private StackPane root;
    private StartScreen startScreen;
    private SettingsScreen settingsScreen;
    private MediaScreen mediaScreen;
    private GeneralScreen generalScreen;
    private GraphicScreen graphicScreen;

    private boolean isAnimating = false;
    private Timeline currentTimeline;
    private final Queue<String> outputQueue = new LinkedList<>();

    private VBox createGameLayout() {
        VBox mainLayout = new VBox(16);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getStyleClass().add("game-root"); // Стилі в CSS
        // 1. Заголовок
        Label title = new Label("DUNGEON MASTER");
        title.getStyleClass().add("title-label");

        // 2. Секція HP (важливо: hpBar та hpLabel мають бути полями класу GameScreen)
        this.hpLabel = new Label("HP: 100/100");
        this.hpBar = new ProgressBar(1.0);
        this.hpBar.setPrefWidth(300);

        HBox hpBox = new HBox(10, hpLabel, hpBar);
        hpBox.setAlignment(Pos.CENTER);

        this.gameOutput = new TextArea();
        this.gameOutput.setEditable(false);
        this.gameOutput.setWrapText(true);
        this.gameOutput.setPrefRowCount(15);

        this.playerInput = new TextField();
        this.playerInput.setPromptText("Твій вибір...");

        Button actionButton = new Button("ВІДПРАВИТИ");

        actionButton.setOnAction(e -> handlePlayerInput());
        playerInput.setOnAction(e -> handlePlayerInput());

        // 5. Кнопка виходу (КРИТИЧНО для очищення)
        Button toMenuButton = new Button("ВИЙТИ В МЕНЮ");
        toMenuButton.setOnAction(e -> exitToMenu());

        HBox inputLayout = new HBox(10, playerInput, actionButton);
        inputLayout.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(title, hpBox, gameOutput, inputLayout, toMenuButton);

        return mainLayout;
    }

    public void start(Stage stage) {
        GameConfig.load();
        this.root = new StackPane();
        this.startScreen = new StartScreen(this, stage);
        this.settingsScreen = new SettingsScreen(this, stage);
        this.mediaScreen = new MediaScreen(this, stage);
        this.generalScreen = new GeneralScreen(this, stage);
        this.graphicScreen = new GraphicScreen(this, stage);

        showStartScreen();

        Scene scene = new Scene(root, 800, 600);

        System.out.println("Завантаження звукових ефектів...");
        AudioManager.Sound.initializeSounds();

        try {
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS не знайдено, працюємо без стилів.");
        }

        stage.setScene(scene);
        stage.setTitle("DUNGEON MASTER - JavaFX Edition");
        stage.show();

        AudioManager.Music.playMenuMusic();
    }

    public void startGame() {
        updateHpBar();
    }

    private void updateHpBar() {
        if (game != null && game.chosenCharacter != null) {
            hpLabel.setText("HP: " + game.chosenCharacter.health + "/" + game.chosenCharacter.maxHealth);
            double progress = Math.max(0, (double) game.chosenCharacter.health / game.chosenCharacter.maxHealth);
            hpBar.setProgress(progress);
        } else {
            hpLabel.setText("HP: --/--");
            hpBar.setProgress(1.0);
        }
    }

    private void appendToOutput(String text) {
        outputQueue.add(text);
        if (!isAnimating) {
            playNextOutput();
        }
    }

    private void playNextOutput() {
        if (isAnimating) {
            return;
        }

        String text = outputQueue.poll();
        if (text == null) {
            return;
        }

        isAnimating = true;

        if (com.example.logic.GameConfig.textSpeed < 1) {
            gameOutput.appendText(text + "\n");
            gameOutput.positionCaret(gameOutput.getLength());
            isAnimating = false;
            playNextOutput();
            return;
        }

        currentTimeline = new Timeline();
        double delay = com.example.logic.GameConfig.textSpeed;

        for (int i = 0; i < text.length(); i++) {
            final int idx = i;
            currentTimeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(delay * (i + 1)), e -> {
                        gameOutput.appendText(String.valueOf(text.charAt(idx)));
                        gameOutput.positionCaret(gameOutput.getLength());
                    })
            );
        }

        currentTimeline.setOnFinished(e -> {
            gameOutput.appendText("\n");
            gameOutput.positionCaret(gameOutput.getLength());
            isAnimating = false;
            playNextOutput();
        });

        currentTimeline.play();
    }

    public void handlePlayerInput() {
        if (isAnimating) return;

        String input = playerInput.getText().trim();
        if (input.isEmpty()) return;

        playerInput.clear();

        appendToOutput("> " + input + "\n");

        if (game != null) {
            game.processInput(input);
        }

        updateHpBar();
    }

    private void exitToMenu() {
        if (currentTimeline != null) currentTimeline.stop(); // Зупиняємо друк тексту
        AudioManager.Music.stopAllMusic();
        showStartScreen();
    }

    @Override
    public void showGameScreen() {
        root.getChildren().setAll(createGameLayout());
        this.game = new Game(this);
        applyGraphics();

        startGame();
    }

    @Override
    public void onGameOutput(String text) {
        appendToOutput(text);
    }

    @Override
    public void onHpUpdate() {
        updateHpBar();
    }

    @Override
    public void onGameOver() {
        playerInput.setDisable(true);

        appendToOutput("\n--- ГРА ЗАКІНЧЕНА ---");

        hpBar.setStyle("-fx-accent: gray;");

        System.out.println("UI: Game over state reached.");
    }

    @Override
    public void onRestartRequest() {
        showGameScreen();
    }

    @Override
    public void refreshBackground() {

    }

    @Override
    public void showStartScreen() {
        GameConfig.applyBackground(root);
        System.out.println("Going to start screen...");

        if (currentTimeline != null) currentTimeline.stop();

        outputQueue.clear();
        isAnimating = false;

        root.getChildren().setAll(startScreen.getLayout());

        AudioManager.Music.stopAllMusic();
        AudioManager.Music.playMenuMusic();
    }

    @Override
    public void showSettingsScreen() {
        GameConfig.applyBackground(root);
        root.getChildren().setAll(settingsScreen.getLayout());
    }

    @Override
    public void showMediaScreen() {
        GameConfig.applyBackground(root);
        root.getChildren().setAll(mediaScreen.getLayout());
    }

    @Override
    public void showGraphicScreen() {
        GameConfig.applyBackground(root);
        root.getChildren().setAll(graphicScreen.getLayout());

    }

    @Override
    public void showGeneralScreen() {
        GameConfig.applyBackground(root);
        root.getChildren().setAll(generalScreen.getLayout());

    }

    private void applyGraphics() {
        GameConfig.applyBackground(root);
        try {
            if (com.example.logic.GameConfig.backgroundImage != null && !com.example.logic.GameConfig.backgroundImage.isEmpty()) {
                String url = getClass().getResource(com.example.logic.GameConfig.backgroundImage).toExternalForm();
                root.setStyle("-fx-background-image: url('" + url + "'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;");
            } else {
                root.setStyle("-fx-background-color: " + com.example.logic.GameConfig.bgColor + ";");
            }
        } catch (Exception e) {
            System.out.println("Фон не завантажено: " + e.getMessage());
            root.setStyle("-fx-background-color: " + com.example.logic.GameConfig.bgColor + ";");
        }

        // 2. Налаштування текстової консолі (gameOutput)
        if (gameOutput != null) {
            gameOutput.setOpacity(com.example.logic.GameConfig.consoleOpacity);
            gameOutput.setStyle(
                    "-fx-font-size: " + com.example.logic.GameConfig.fontSize + "px; " +
                            "-fx-text-fill: " + com.example.logic.GameConfig.mainColor + "; " +
                            "-fx-control-inner-background: #161b22; " +
                            "-fx-border-color: " + com.example.logic.GameConfig.mainColor + "; " +
                            "-fx-border-width: 2px;"
            );
        }

    }
}