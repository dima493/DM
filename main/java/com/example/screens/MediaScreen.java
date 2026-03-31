package com.example.screens;

import com.example.audio.AudioManager;
import com.example.base.BaseScreen;
import com.example.navigation.ScreenNavigator;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MediaScreen extends BaseScreen {
    public MediaScreen(ScreenNavigator navigator, Stage stage) {
        super(navigator, stage, "НАЛАШТУВАННЯ ЗВУКУ");

        VBox controls = new VBox(20);
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-padding: 40;");

        Label musicLabel = new Label("ГУЧНІСТЬ МУЗИКИ");

        Slider musicSlider = new Slider(0, 1, 0.3); // мін, макс, початкове
        musicSlider.setMaxWidth(300);
        musicSlider.setShowTickLabels(true);
        musicSlider.setShowTickMarks(true);

        musicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.Music.setVolume(newValue.doubleValue());
        });

        Label soundLabel = new Label("ГУЧНІСТЬ ЕФЕКТІВ");

        Slider soundSlider = new Slider(0, 1, 0.5);
        soundSlider.setMaxWidth(300);
        soundSlider.setShowTickLabels(true);

        soundSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.Sound.setVolume(newValue.doubleValue());
        });

        controls.getChildren().addAll(musicLabel, musicSlider, soundLabel, soundSlider);

        this.getLayout().getChildren().add(1, controls);

        addNavigationButton("НАЗАД", navigator::showSettingsScreen);
    }
}