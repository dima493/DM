package com.example;

import com.example.logic.GameConfig;
import com.example.screens.GameScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameConfig.load();
        GameScreen gameApp = new GameScreen();

        gameApp.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}