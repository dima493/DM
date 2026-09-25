package com.example.base;

import java.util.Objects;

import com.example.audio.AudioManager;
import com.example.logic.GameConfig;
import com.example.navigation.ScreenNavigator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public abstract class BaseScreen {
    protected final ScreenNavigator navigator;
    protected final Stage stage;
    protected final VBox layout;
    protected final Scene scene;

    public BaseScreen(ScreenNavigator navigator, Stage stage, String titleText) {
        this.navigator = navigator;
        this.stage = stage;

        this.layout = new VBox(24);
        this.layout.setPadding(new Insets(40));
        this.layout.setAlignment(Pos.CENTER);

        Label title = new Label(titleText);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.getStyleClass().add("title-label");

        this.layout.getChildren().add(title);

        this.scene = new Scene(layout, 800, 600);
        String css = Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm();
        this.scene.getStylesheets().add(css);
    }

    public void refreshBackground() {
        if (this.layout != null && GameConfig.backgroundImage != null && !GameConfig.backgroundImage.isEmpty()) {
            try {
                String url = getClass().getResource(GameConfig.backgroundImage).toExternalForm();
                this.layout.setStyle("-fx-background-image: url('" + url + "'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;");
            } catch (Exception e) {
                // Якщо картинка не знайшлась, ставимо дефолтний колір
                this.layout.setStyle("-fx-background-color: #0d1117;");
            }
        }
    }

    protected void addNavigationButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD , 22));
        btn.setOnAction(e -> action.run());
        layout.getChildren().add(btn);
    }


    // op 1:Music
    // op 2:Sound
    protected void addAudioSlider(String text, int op) {
        Label name = new Label(text);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        name.getStyleClass().add("title-label");

        Slider slider = new Slider(0, 1, 0.5);
        slider.setMaxWidth(300);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        if (op == 1) {
            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                AudioManager.Music.setVolume(newValue.doubleValue());
            });
        }
        if (op == 2) {
            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                AudioManager.Sound.setVolume(newValue.doubleValue());
            });
        }
        layout.getChildren().add(name);
        layout.getChildren().add(slider);

    }

    public Scene getScene() { return scene; }
    public VBox getLayout() {
        return layout;
    }
}