package com.example.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    public static class Music {
        private static MediaPlayer menuMusicPlayer;
        private static MediaPlayer gameMusicPlayer;
        private static double musicVolume = 0.3;

        private static final String MUSIC_GAME_FILE = "/audio/game-music.mp3";
        private static final String MUSIC_MENU_FILE = "/audio/start-screen.mp3";

        public static void playMenuMusic() {
            stopAllMusic();
            play(MUSIC_MENU_FILE, true);
        }

        public static void playGameMusic() {
            stopAllMusic();
            play(MUSIC_GAME_FILE, false);
        }

        private static void play(String path, boolean isMenu) {
            try {
                URL resource = AudioManager.class.getResource(path);
                if (resource != null) {
                    Media media = new Media(resource.toString());
                    MediaPlayer player = new MediaPlayer(media);

                    if (isMenu) menuMusicPlayer = player;
                    else gameMusicPlayer = player;

                    player.setCycleCount(MediaPlayer.INDEFINITE);
                    player.setVolume(musicVolume);
                    player.play();
                } else {
                    System.err.println("Music file not found: " + path);
                }
            } catch (Exception e) {
                System.err.println("Error playing music: " + e.getMessage());
            }
        }

        public static void stopAllMusic() {
            if (menuMusicPlayer != null) { menuMusicPlayer.stop(); menuMusicPlayer.dispose(); menuMusicPlayer = null; }
            if (gameMusicPlayer != null) { gameMusicPlayer.stop(); gameMusicPlayer.dispose(); gameMusicPlayer = null; }
        }

        public static void setVolume(double volume) {
            musicVolume = volume;
            if (menuMusicPlayer != null) menuMusicPlayer.setVolume(volume);
            if (gameMusicPlayer != null) gameMusicPlayer.setVolume(volume);
        }
    }

    public static class Sound {
        public static AudioClip shieldSound;
        public static AudioClip magicShieldSound;
        private static double soundVolume = 0.5;
        private static final Map<String, AudioClip> attackSounds = new HashMap<>();

        public static void initializeSounds() {
            try {
                Map<String, String> weaponSoundMap = new HashMap<>();
                weaponSoundMap.put("Меч", "sword");
                weaponSoundMap.put("Сокира", "axe");
                weaponSoundMap.put("Лук", "bow");
                weaponSoundMap.put("Арбалет", "crossbow");
                weaponSoundMap.put("Святий Посох", "magic");
                weaponSoundMap.put("Сяючий Клинок", "sword");
                weaponSoundMap.put("Вогняний Меч", "fire-sword");
                weaponSoundMap.put("Кігті Хаосу", "claws");
                weaponSoundMap.put("Темний Посох", "magic");
                weaponSoundMap.put("Книга Проклять", "magic");

                for (Map.Entry<String, String> entry : weaponSoundMap.entrySet()) {
                    loadAttackSound(entry.getKey(), entry.getValue());
                }

                shieldSound = loadAudioClip("/audio/shield-guard.mp3");
                magicShieldSound = loadAudioClip("/audio/magic-shield.mp3");

                System.out.println("Усі звуки успішно ініціалізовані");
            } catch (Exception e) {
                System.err.println("Критична помилка ініціалізації звуків: " + e.getMessage());
            }
        }

        private static void loadAttackSound(String weaponDisplayName, String soundFilename) {
            String soundPath = "/audio/" + soundFilename + ".mp3";
            URL resource = Sound.class.getResource(soundPath);
            if (resource != null) {
                attackSounds.put(weaponDisplayName, new AudioClip(resource.toExternalForm()));
            } else {
                System.out.println("Файл звуку не знайдено: " + soundPath + " для: " + weaponDisplayName);
            }
        }

        private static AudioClip loadAudioClip(String path) {
            URL resource = Sound.class.getResource(path);
            if (resource != null) {
                return new AudioClip(resource.toExternalForm());
            }
            System.out.println("Ефект не знайдено: " + path);
            return null;
        }

        public static void setVolume(double volume) {
            soundVolume = volume;
        }

        public static void playWeaponSound(String weaponName) {
            if (weaponName == null) return;

            for (String key : attackSounds.keySet()) {
                if (weaponName.contains(key)) {
                    AudioClip clip = attackSounds.get(key);
                    if (clip != null) {
                        clip.setVolume(soundVolume);
                        clip.play();
                    }
                    return;
                }
            }
        }
    }
}