package com.example.screens;

import com.example.base.BaseScreen;
import com.example.navigation.ScreenNavigator;
import javafx.stage.Stage;

public class MediaScreen extends BaseScreen {
    public MediaScreen(ScreenNavigator navigator, Stage stage) {
        super(navigator, stage, "НАЛАШТУВАННЯ ЗВУКУ");

        addAudioSlider("Гучність музики", 1);
        addAudioSlider("Гучність звуків", 2);
        addNavigationButton("НАЗАД", navigator::showSettingsScreen);
    }
}