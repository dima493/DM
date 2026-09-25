package com.example.screens;

import com.example.base.BaseScreen;
import com.example.navigation.ScreenNavigator;
import javafx.stage.Stage;

public class SettingsScreen extends BaseScreen {
    public SettingsScreen(ScreenNavigator navigator, Stage stage) {
        super(navigator,stage,"DUNGEON MASTER");

        addNavigationButton("ГРАФІКА", navigator::showGraphicScreen);
        addNavigationButton("МЕДІА", navigator::showMediaScreen);
        addNavigationButton("ЗАГАЛЬНІ", navigator::showGeneralScreen);
        addNavigationButton("НАЗАД", navigator::showStartScreen);
    }
}