package com.example.logic;

import java.io.*;
import java.util.Properties;

public class GameConfig {
    public static double textSpeed = 15.0;
    public static String difficulty = "НОРМАЛЬНО";
    public static String bgColor = "#0d1117";
    public static int fontSize = 14;
    public static double consoleOpacity = 1.0;
    public static String mainColor = "#00ffff";
    public static String backgroundImage = "/images/background.jpg";

    private static final String FILE_NAME = "settings.properties";

    public static void save() {
        Properties props = new Properties();
        props.setProperty("fontSize", String.valueOf(fontSize));
        props.setProperty("opacity", String.valueOf(consoleOpacity));
        props.setProperty("difficulty", String.valueOf(difficulty));
        props.setProperty("txtSpeed", String.valueOf(textSpeed));
        props.setProperty("bg", backgroundImage);

        try (OutputStream out = new FileOutputStream(FILE_NAME)) {
            props.store(out, "Game Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
            fontSize = Integer.parseInt(props.getProperty("fontSize", "18"));
            consoleOpacity = Double.parseDouble(props.getProperty("opacity", "1.0"));
            backgroundImage = props.getProperty("bg", "");
            System.out.println("DEBUG: Завантажений фон: " + backgroundImage);
            difficulty =  props.getProperty("difficulty", "");
            textSpeed = Double.parseDouble(props.getProperty("txtSpeed", "1.0"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyBackground(javafx.scene.layout.Region region) {
        if (region == null) return;

        String path = backgroundImage;
        if (path != null && !path.isEmpty()) {
            try {
                String url = GameConfig.class.getResource(path).toExternalForm();
                region.setStyle("-fx-background-image: url('" + url + "'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;");
            } catch (Exception e) {
                region.setStyle("-fx-background-color: #0d1117;");
            }
        } else {
            region.setStyle("-fx-background-color: #0d1117;");
        }
    }
}