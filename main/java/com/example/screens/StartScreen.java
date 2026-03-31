package com.example.screens;

import com.example.audio.AudioManager;
import com.example.base.BaseScreen;
import com.example.navigation.ScreenNavigator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class StartScreen extends BaseScreen {
    private StackPane root;
    private StartScreen startScreen;
    private SettingsScreen settingsScreen;


    public StartScreen(ScreenNavigator navigator, Stage stage) {
        super(navigator, stage, "DUNGEON MASTER");
        AudioManager.Music.playMenuMusic();

        addNavigationButton("ПОЧАТИ ГРУ", () -> {
            navigator.showGameScreen();
            AudioManager.Music.stopAllMusic();
            AudioManager.Music.playGameMusic();
        });

        addNavigationButton("НАЛАШТУВАННЯ", navigator::showSettingsScreen);
        addNavigationButton("ВИЙТИ", () -> System.exit(0));
    }
}