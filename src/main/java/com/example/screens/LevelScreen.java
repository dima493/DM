package com.example.screens;

import com.example.audio.AudioManager;
import com.example.base.BaseScreen;
import com.example.navigation.ScreenNavigator;
import javafx.stage.Stage;

public class LevelScreen extends BaseScreen {
    public LevelScreen(ScreenNavigator navigator, Stage stage) {
        super(navigator, stage, "РІВЕНЬ");

        addNavigationButton("ГРАТИ", () -> {
            navigator.showGameScreen();
            AudioManager.Music.stopAllMusic();
            AudioManager.Music.playGameMusic();
        });

        addNavigationButton("НАЗАД", navigator::showStartScreen);
    }
}