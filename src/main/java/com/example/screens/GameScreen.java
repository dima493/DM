package com.example.screens;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
    private VBox playerUnit;
    private VBox enemyUnit;
    private ProgressBar playerHpBar;
    private Label playerHpLabel;
    private ProgressBar enemyHpBar;
    private Label enemyHpLabel;
    private TextArea gameOutput;
    private TextField playerInput;
    private ImageView playerPortrait;
    private ImageView weaponPortrait;
    private StackPane playerSpriteContainer;

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
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getStyleClass().add("game-root"); // Стилі в CSS
        // 1. Заголовок
        Label title = new Label("DUNGEON MASTER");
        title.getStyleClass().add("title-label");

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

        VBox imageSection = new VBox(10); // Контейнер з відступом
        imageSection.setAlignment(Pos.CENTER);

        // 2. Контейнер для самих портретів
        GridPane spriteGrid = new GridPane(); // GridPane краще для вирівнювання у сітку
        spriteGrid.setHgap(40); // Відступи між картинками по горизонталі
        spriteGrid.setVgap(10); // Відступи по вертикалі (не обов'язково для одного ряду)
        spriteGrid.setAlignment(Pos.CENTER);

        // --- ПЕРСОНАЖ ГРАВЦЯ ---
        this.playerUnit = new VBox(5);
        this.playerUnit.setAlignment(Pos.CENTER);

        this.playerHpLabel = new Label("HP: 100/100");
        this.playerHpBar = new ProgressBar(1.0);
        this.playerHpBar.setPrefWidth(100);
        this.playerHpBar.setStyle("-fx-accent: #00ff00;");

        this.playerPortrait = new ImageView();
        this.playerPortrait.setFitWidth(128);
        this.playerPortrait.setFitHeight(128);
        this.playerPortrait.setSmooth(false);

        this.weaponPortrait = new ImageView();
        this.weaponPortrait.setFitWidth(128);
        this.weaponPortrait.setFitHeight(128);
        this.weaponPortrait.setSmooth(false);

        StackPane playerSprites = new StackPane(weaponPortrait, playerPortrait);
        playerUnit.getChildren().addAll(playerHpLabel, playerHpBar, playerSprites);

        this.weaponPortrait.setVisible(false);
        this.playerUnit.setVisible(false);

        // --- ВОРОГ ---
        this.enemyUnit = new VBox(5); // Контейнер для ворога
        this.enemyUnit.setAlignment(Pos.CENTER);

        this.enemyHpLabel = new Label("HP: 100/100");
        this.enemyHpBar = new ProgressBar(1.0);
        this.enemyHpBar.setPrefWidth(100);
        this.enemyHpBar.setStyle("-fx-accent: #00ff00;");

        // 3. СтворенняImageView для кожного персонажа
        ImageView enemyImg = createPortrait("/images/enemy.png");
        this.enemyUnit.getChildren().addAll(enemyHpLabel, enemyHpBar, enemyImg);

        this.enemyUnit.setVisible(false);
        this.enemyHpLabel.setVisible(false);
        this.enemyHpBar.setVisible(false);

        spriteGrid.add(playerUnit, 0, 0);
        spriteGrid.add(enemyUnit, 1, 0);

        // Збираємо всю секцію разом
        imageSection.getChildren().addAll(spriteGrid);

        mainLayout.getChildren().addAll(title, imageSection, gameOutput, inputLayout, toMenuButton);

        return mainLayout;
    }

    private ImageView createPortrait(String imagePath) {
        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            ImageView iv = new ImageView(img);

            // ВСТАНОВЛЮЄМО РОЗМІР 32x32
            iv.setFitWidth(128);
            iv.setFitHeight(128);
            iv.setPreserveRatio(true); // Зберігати пропорції (для pixel art)
            iv.setSmooth(false);

            return iv;
        } catch (Exception e) {
            System.out.println("Не вдалося завантажити спрайт: " + imagePath);
            return new ImageView(); // Повертаємо пустий об'єкт, щоб не впало
        }
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
    private String getCharacterImagePath(String characterName) {
        if (characterName == null) return "/images/swordsman.png";

        switch (characterName) {
            case "ЛИЦАР": return "/images/swordsman.png";
            case "ЛУЧНИК": return "/images/archer.png";
            case "АНГЕЛ": return "/images/swordsman.png";
            case "ДЕМОН": return "/images/swordsman.png";
            case "ТЕМНИЙ МАГ": return "/images/swordsman.png";
            default: return "/images/swordsman.png";
        }
    }

    private String getWeaponImagePath(String weaponName) {
        if (weaponName == null) return "/images/empty.png";

        String name = weaponName.toLowerCase();
        if (name.contains("меч")) return "/images/sword.png";
        if (name.contains("лук")) return "/images/bow.png";
        if (name.contains("сокира")) return "/images/axe128.png";
        if (name.contains("посох")) return "/images/staff128.png";
        if (name.contains("клинок")) return "/images/blade128.png";

        return "/images/sword.png"; // Дефолт
    }
    private void updateHpBar() {
        if (game == null) return;

        // --- 1. ОНОВЛЕННЯ ГРАВЦЯ (Персонаж + Зброя) ---
        if (game.chosenCharacter != null) {
            // Оновлюємо спрайт персонажа
            String charPath = getCharacterImagePath(game.chosenCharacter.name);
            updateImageIfChanged(playerPortrait, charPath);

            // Оновлюємо спрайт зброї (якщо вона вже обрана)
            if (game.chosenWeapon != null) {
                String weaponPath = getWeaponImagePath(game.chosenWeapon);
                updateImageIfChanged(weaponPortrait, weaponPath);
                weaponPortrait.setVisible(true);
            }

            // Оновлюємо цифри та смужку HP
            playerHpLabel.setText("HP: " + game.chosenCharacter.health + "/" + game.chosenCharacter.maxHealth);
            double pProgress = (double) game.chosenCharacter.health / game.chosenCharacter.maxHealth;
            playerHpBar.setProgress(Math.max(0, pProgress));

            // Показуємо весь блок гравця
            playerHpLabel.setVisible(true);
            playerHpBar.setVisible(true);
            playerPortrait.setVisible(true);
        }

        // --- 2. ОНОВЛЕННЯ ВОРОГА ---
        if (game.currentEnemy != null) {
            // Якщо у ворога теж є імена/типи, можна додати getEnemyImagePath
            enemyHpLabel.setText(game.currentEnemy.type + " HP: " + game.currentEnemy.health + "/" + game.currentEnemy.maxHealth);
            double eProgress = (double) game.currentEnemy.health / game.currentEnemy.maxHealth;
            enemyHpBar.setProgress(Math.max(0, eProgress));

            playerUnit.setVisible(true);
            enemyUnit.setVisible(true);

            playerHpLabel.setVisible(true);
            playerHpBar.setVisible(true);
            playerPortrait.setVisible(true);
            enemyHpBar.setVisible(true);
            enemyHpLabel.setVisible(true);

        } else {
            // Ховаємо інтерфейс ворога, якщо бою немає
            if (enemyHpLabel != null) enemyHpLabel.setVisible(false);
            if (enemyHpBar != null) enemyHpBar.setVisible(false);
        }
    }

    private void updateImageIfChanged(ImageView iv, String path) {
        if (path == null || path.isEmpty()) return;

        // 1. Дістаємо останній шлях, який ми зберігали в "кишені" ImageView
        String lastPath = (String) iv.getUserData();

        // 2. Якщо шлях той самий — нічого не робимо, виходимо
        if (path.equals(lastPath)) return;

        try {
            var stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                iv.setImage(new Image(stream));

                // 3. Зберігаємо новий шлях у "кишеню", щоб наступного разу порівняти
                iv.setUserData(path);

                System.out.println("Спрайт успішно оновлено: " + path);
            } else {
                System.err.println("Файл не знайдено: " + path);
            }
        } catch (Exception e) {
            System.err.println("Помилка завантаження спрайта: " + path);
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

        playerHpBar.setStyle("-fx-accent: gray;");

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