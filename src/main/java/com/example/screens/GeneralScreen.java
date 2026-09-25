package com.example.screens;

import com.example.base.BaseScreen;
import com.example.logic.GameConfig;
import com.example.navigation.ScreenNavigator;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GeneralScreen extends BaseScreen {
    public GeneralScreen(ScreenNavigator navigator, Stage stage) {
        super(navigator, stage, "ЗАГАЛЬНІ НАЛАШТУВАННЯ");

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-padding: 40;");

        Slider speedSlider = new Slider(0, 50, 50 - GameConfig.textSpeed);
        speedSlider.setMaxWidth(300);

        Label speedValueLabel = new Label();
        updateSpeedLabel(speedValueLabel, 50 - GameConfig.textSpeed); // Початкове значення

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double sliderVal = newVal.doubleValue();

            GameConfig.textSpeed = 50 - sliderVal;

            updateSpeedLabel(speedValueLabel, GameConfig.textSpeed);
        });

        Label diffLabel = new Label("СКЛАДНІСТЬ");
        diffLabel.setStyle("-fx-text-fill: white;");

        ChoiceBox<String> diffChoice = new ChoiceBox<>();
        diffChoice.getItems().addAll("ЛЕГКО", "НОРМАЛЬНО", "ВАЖКО", "DARK SOULS");
        diffChoice.setValue(GameConfig.difficulty);
        diffChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            GameConfig.difficulty = newVal;
        });

        CheckBox fullScreenCb = new CheckBox("ПОВНОЕКРАННИЙ РЕЖИМ");
        fullScreenCb.setStyle("-fx-text-fill: white;");
        fullScreenCb.setSelected(stage.isFullScreen());
        fullScreenCb.setOnAction(e -> stage.setFullScreen(fullScreenCb.isSelected()));

        content.getChildren().addAll(speedValueLabel, speedSlider, diffLabel, diffChoice, fullScreenCb);

        this.getLayout().getChildren().add(1, content);

        addNavigationButton("НАЗАД", navigator::showSettingsScreen);
    }

    private void updateSpeedLabel(Label label, double currentDelay) {
        if (currentDelay < 1) {
            label.setText("Швидкість: МИТТЄВО (Turbo)");
        } else if (currentDelay > 45) {
            label.setText("Швидкість: ДУЖЕ ПОВІЛЬНО");
        } else {
            label.setText("Затримка: " + (int)currentDelay + " мс");
        }
    }
}