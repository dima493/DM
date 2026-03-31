package com.example.screens;

import com.example.base.BaseScreen;
import com.example.logic.GameConfig;
import com.example.navigation.ScreenNavigator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GraphicScreen extends BaseScreen {
    private Label textPreview;

    private ImageView previewImage;

    public GraphicScreen(ScreenNavigator navigator, Stage stage) {

        super(navigator, stage, "НАЛАШТУВАННЯ ГРАФІКИ");

        VBox settingsContainer = new VBox(25);
        settingsContainer.setAlignment(Pos.CENTER);
        settingsContainer.setPadding(new Insets(30));
        settingsContainer.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); -fx-background-radius: 20; -fx-border-color: #00ffff; -fx-border-radius: 20;");
        settingsContainer.setMaxWidth(600);

        Label bgLabel = createStyledLabel("ЛОКАЦІЯ (ФОН):");

        previewImage = new ImageView();
        previewImage.setFitWidth(150);
        previewImage.setFitHeight(100);
        previewImage.setStyle("-fx-effect: dropshadow(three-pass-box, #00ffff, 10, 0, 0, 0); -fx-border-color: #00ffff; -fx-border-width: 2;");
        previewImage.setPreserveRatio(true);

        ChoiceBox<String> bgPicker = new ChoiceBox<>();
        bgPicker.getItems().addAll("Замок", "Підземелля", "Темний ліс", "Хатина відьми");
        bgPicker.setValue("Замок");
        bgPicker.setPrefWidth(150);


        bgPicker.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            updateBackgroundAndPreview(newV);
        });

        HBox bgSelectionBox = new HBox(20, bgPicker, previewImage);
        bgSelectionBox.setAlignment(Pos.CENTER);

        updateBackgroundAndPreview("Замок");

        textPreview = new Label("Приклад тексту");
        textPreview.setWrapText(true);
        textPreview.setPrefSize(350, 80);
        textPreview.setAlignment(Pos.TOP_LEFT);
        textPreview.setPadding(new Insets(10));
        // Стилізуємо як міні-консоль
        updateTextPreviewStyle();

        Label fontLabel = createStyledLabel("РОЗМІР ШРИФТУ (" + (int)GameConfig.fontSize + "px):");
        Slider fontSlider = new Slider(12, 30, GameConfig.fontSize);
        fontSlider.valueProperty().addListener((obs, oldV, newV) -> {
            GameConfig.fontSize = newV.intValue();
            fontLabel.setText("РОЗМІР ШРИФТУ (" + newV.intValue() + "px):");
            updateTextPreviewStyle();
        });

        Label opacityLabel = createStyledLabel("ПРОЗОРІСТЬ КОНСОЛІ (" + String.format("%.1f", GameConfig.consoleOpacity) + "):");
        Slider opacitySlider = new Slider(0.1, 1.0, GameConfig.consoleOpacity);
        opacitySlider.valueProperty().addListener((obs, oldV, newV) -> {
            GameConfig.consoleOpacity = newV.doubleValue();
            opacityLabel.setText("ПРОЗОРІСТЬ КОНСОЛІ (" + String.format("%.1f", newV.doubleValue()) + "):");
            updateTextPreviewStyle();
        });

        settingsContainer.getChildren().addAll(bgLabel, bgSelectionBox, textPreview, fontLabel, fontSlider, opacityLabel, opacitySlider);
        this.layout.getChildren().add(settingsContainer);

        addNavigationButton("ЗБЕРЕГТИ ТА НАЗАД", () -> {
            GameConfig.save();
            navigator.showSettingsScreen();
        });
    }

    private void updateTextPreviewStyle() {
        textPreview.setOpacity(GameConfig.consoleOpacity);
        textPreview.setStyle(
                "-fx-font-size: " + GameConfig.fontSize + "px; " +
                        "-fx-text-fill: " + GameConfig.mainColor + "; " +
                        "-fx-background-color: #161b22; " +
                        "-fx-border-color: #00ffff; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
    }

    private void updateBackgroundAndPreview(String locationName) {
        String path;
        switch (locationName) {
            case "Замок": path = "/images/background.jpg";
            break;
            case "Підземелля": path = "/images/background2.jfif";
            break;
            case "Темний ліс": path = "/images/background3.jpg";
            break;
            case "Хатина відьми": path = "/images/background4.jpg";
            break;
            default: path = "";
            break;
        }

        GameConfig.backgroundImage = path;

        if (!path.isEmpty()) {
            previewImage.setImage(new Image(getClass().getResourceAsStream(path)));
        } else {
            previewImage.setImage(null);
            this.layout.setStyle("-fx-background-color: #0d1117;");
        }
    }

    private Label createStyledLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #00ffff; -fx-font-weight: bold; -fx-font-size: 14px;");
        return l;
    }
}