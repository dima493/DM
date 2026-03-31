package com.example.logic;

public interface GameEventListener {
    void onGameOutput(String text);
    void onHpUpdate();
    void onGameOver();
    void onRestartRequest();
    void refreshBackground();
}