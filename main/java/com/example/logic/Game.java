package com.example.logic;

import com.example.audio.AudioManager;

import java.util.*;

public class Game {

    private final GameEventListener listener;

    private int shieldDelay = 0;
    private int abilityDelay = 0;

    public List<Character> characters = new ArrayList<>();
    public List<Enemy> enemies = new ArrayList<>();
    public List<String> artifacts = new ArrayList<>();
    public Character chosenCharacter;
    public String chosenWeapon;
    public String playerName;
    public int defeatedEnemies = 0;
    public boolean holyShieldActive = false;
    public GameState currentState = GameState.ENTER_NAME;
    public Enemy currentEnemy;

    public Game(GameEventListener listener) {
        this.listener = listener;
        initializeCharacters();

        listener.onGameOutput("=== ВІТАЄМО У DUNGEON MASTER ===\n");
        listener.onGameOutput("ВВЕДІТЬ ВАШЕ ІМ'Я:");
    }


    public void processInput(String input) {
        switch (currentState) {
            case ENTER_NAME:
                playerName = input;
                listener.onGameOutput("ЛАСКАВО ПРОСИМО, " + playerName + "!");
                showCharacterSelection();
                break;
            case CHOOSE_CHARACTER:
                handleCharacterSelection(input);
                break;
            case CHOOSE_WEAPON:
                handleWeaponSelection(input);
                break;
            case CHOOSE_MODE:
                handleModeSelection(input);
                break;
            case FIGHTING:
                handleCombatAction(input);
                break;
            case GAME_OVER:
                handleGameOver(input);
                break;
        }
    }

    private void initializeCharacters() {
        characters.clear();
        characters.add(new Character("ЛИЦАР", 120, 10, 15, "МЕЧ СВЯТОГО", Arrays.asList("Меч (15)", "Сокира (20)"), "Щитова Оборона"));
        characters.add(new Character("АНГЕЛ", 100, 5, 10, "БОЖЕСТВЕННЕ СВІТЛО", Arrays.asList("Святий Посох (10)", "Сяючий Клинок (15)"), "Святе Відродження"));
        characters.add(new Character("ДЕМОН", 130, 8, 20, "ПАЛАЮЧИЙ КЛИНОК", Arrays.asList("Вогняний Меч (15)", "Кігті Хаосу (10)"), "Пекельне Полум'я"));
        characters.add(new Character("ТЕМНИЙ МАГ", 90, 3, 25, "ПОСОХ МЕРЛІНА", Arrays.asList("Темний Посох (10)", "Книга Проклять (10)"), "Темна Магія"));
        characters.add(new Character("ЛУЧНИК", 100, 4, 12, "ЛУК СТРІЛОПАДА", Arrays.asList("Лук (10)", "Арбалет (15)"), "Дощ із Стріл"));
    }

    private void showCharacterSelection() {
        listener.onGameOutput("\nОБЕРІТЬ ПЕРСОНАЖА:");
        for (int i = 0; i < Math.min(characters.size(), 5); i++) {
            Character c = characters.get(i);
            listener.onGameOutput((i + 1) + ". " + c.name + " (HP: " + c.health + ", УРОН: " + c.baseDamage + ")");
        }
        currentState = GameState.CHOOSE_CHARACTER;
    }

    private void handleCharacterSelection(String input) {
        try {
            int choice = Integer.parseInt(input) - 1;
            if (choice >= 0 && choice < Math.min(characters.size(), 5)) {
                chosenCharacter = characters.get(choice);
                listener.onGameOutput("ВИ ОБРАЛИ: " + chosenCharacter.name);
                showWeaponSelection();
            } else {
                listener.onGameOutput("НЕВІРНИЙ ВИБІР! СПРОБУЙТЕ ЗНОВУ! (1-5):");
            }
        } catch (NumberFormatException e) {
            listener.onGameOutput("ВВЕДІТЬ ЧИСЛО ВІД 1 ДО 5:");
        }
    }

    private void showWeaponSelection() {
        listener.onGameOutput("\nОБЕРІТЬ ЗБРОЮ:");
        for (int i = 0; i < chosenCharacter.weapons.size(); i++) {
            listener.onGameOutput((i + 1) + ". " + chosenCharacter.weapons.get(i));
        }
        currentState = GameState.CHOOSE_WEAPON;
    }

    private void handleWeaponSelection(String input) {
        try {
            int weaponChoice = Integer.parseInt(input) - 1;
            if (weaponChoice >= 0 && weaponChoice < chosenCharacter.weapons.size()) {
                chosenWeapon = chosenCharacter.weapons.get(weaponChoice);
                int weaponDamage;
                if (chosenWeapon.contains("(")) {
                    String damageStr = chosenWeapon.substring(chosenWeapon.indexOf("(") + 1, chosenWeapon.indexOf(")"));
                    weaponDamage = Integer.parseInt(damageStr);
                } else {
                    weaponDamage = 10;
                }
                chosenCharacter.baseDamage = weaponDamage;
                listener.onGameOutput("ВИ ОБРАЛИ: " + chosenWeapon + " (УРОН: " + weaponDamage + ")");
                showModeSelection();
            } else {
                listener.onGameOutput("НЕВІРНИЙ ВИБІР! СПРОБУЙТЕ ЗНОВУ:");
            }
        } catch (NumberFormatException e) {
            listener.onGameOutput("ВВЕДІТЬ НОМЕР ЗБРОЇ:");
        }
    }

    private void showModeSelection() {
        listener.onGameOutput("\nОБЕРІТЬ РЕЖИМ ГРИ:");
        listener.onGameOutput("1. СЮЖЕТ (Основна Гра)");
        listener.onGameOutput("2. ВИЖИВАННЯ (Постійне посилення ворогів)");
        currentState = GameState.CHOOSE_MODE;
    }

    private void handleModeSelection(String input) {
        try {
            int mode = Integer.parseInt(input);
            switch (mode) {
                case 1:
                    generateEnemies();
                    listener.onGameOutput("РЕЖИМ СЮЖЕТУ ОБРАНО!");
                    break;
                case 2:
                    generateSurvivalEnemies();
                    listener.onGameOutput("РЕЖИМ ВИЖИВАННЯ ОБРАНО!");
                    break;
                default:
                    listener.onGameOutput("НЕВІРНИЙ ВИБІР! ВВЕДІТЬ 1 АБО 2:");
                    break;
            }
            startCombat();
        } catch (NumberFormatException e) {
            listener.onGameOutput("ВВЕДІТЬ 1 АБО 2:");
        }
    }

    private void generateEnemies() {
        enemies.clear();

        double multiplier = 1.0;
        if (GameConfig.difficulty.equals("ЛЕГКО")) multiplier = 0.5;
        else if (GameConfig.difficulty.equals("ВАЖКО")) multiplier = 1.5;
        else if (GameConfig.difficulty.equals("DARK SOULS")) multiplier = 3.0;

        for (int i = 0; i < 3; i++) {
            enemies.add(new Enemy("ГОБЛІН", (int)(20 * multiplier), (int)(5 * multiplier)));
        }

        enemies.add(new Enemy("БОС", (int)(60 * multiplier), (int)(10 * multiplier)));
    }

    private void generateSurvivalEnemies() {
        enemies.clear();

        double multiplier = GameConfig.difficulty.equals("ВАЖКО") ? 1.5 :
                GameConfig.difficulty.equals("DARK SOULS") ? 3.0 : 1.0;

        for (int i = 0; i < 5; i++) {
            int hp = (int)((40 + i * 10) * multiplier);
            int dmg = (int)((10 + i * 2) * multiplier);
            enemies.add(new Enemy("ХВИЛЯ ВОРОГІВ", hp, dmg));
        }
    }

    private void startCombat() {
        if (!enemies.isEmpty()) {
            currentEnemy = enemies.get(0);
            currentState = GameState.FIGHTING;
            showCombatOptions();
        } else {
            listener.onGameOutput("ВІТАЮ! ВИ ПЕРЕМОГЛИ ВСІХ ВОРОГІВ!");
            currentState = GameState.GAME_OVER;
            showGameOverOptions();
        }
    }

    private void showCombatOptions() {
        listener.onGameOutput("\n=== БІЙ ===");
        listener.onGameOutput("ВОРОГ: " + currentEnemy.type + " (ЗДОРОВ'Я: " + currentEnemy.health + "/" + currentEnemy.maxHealth + ")");
        listener.onGameOutput("ВАШЕ ЗДОРОВ'Я: " + chosenCharacter.health + "/" + chosenCharacter.maxHealth);
        listener.onGameOutput("ВАША БРОНЯ: " + chosenCharacter.armor);
        listener.onGameOutput("\nОБЕРІТЬ ДІЮ:");
        listener.onGameOutput("1. АТАКУВАТИ");
        listener.onGameOutput("2. ЗАХИЩАТИСЯ");
        listener.onGameOutput("3. ВИКОРИСТАТИ ЗДІБНІСТЬ (" + chosenCharacter.ability + ")");
    }

    private void handleCombatAction(String input) {
        try {
            int action = Integer.parseInt(input);

            int receivedDamage = 0;
            boolean validAction = false;

            switch (action) {
                case 1:
                    AudioManager.Sound.playWeaponSound(chosenWeapon);
                    currentEnemy.health -= chosenCharacter.baseDamage;
                    listener.onGameOutput("ВИ АТАКУВАЛИ " + currentEnemy.type + " НА " + chosenCharacter.baseDamage + " ШКОДИ!");
                    validAction = true;
                    break;

                case 2:
                    if (shieldDelay >= 5) {
                        if (AudioManager.Sound.shieldSound != null) {
                            AudioManager.Sound.shieldSound.play();
                        }
                        chosenCharacter.shieldTurnsLeft = 2;
                        shieldDelay = 0;
                        listener.onGameOutput("ВИ АКТИВУВАЛИ ЩИТ! Урон ворога буде зменшено на 70% на 2 ходи!");
                        validAction = true;
                    } else {
                        listener.onGameOutput("ЩИТ ПЕРЕЗАРЯДЖАЄТЬСЯ! Ще ходів: " + (5 - shieldDelay));
                    }
                    break;

                case 3:
                    if (abilityDelay >= 10) {
                        if (AudioManager.Sound.magicShieldSound != null) {
                            AudioManager.Sound.magicShieldSound.play();
                        }
                        int abilityDamage = chosenCharacter.baseDamage * 3;
                        currentEnemy.health -= abilityDamage;
                        int healAmount = 25;
                        chosenCharacter.health = Math.min(chosenCharacter.maxHealth, chosenCharacter.health + healAmount);

                        listener.onGameOutput("ВИКОРИСТАНО: " + chosenCharacter.ability);
                        listener.onGameOutput("КРИТИЧНИЙ УРОН: " + abilityDamage + "! Ви також зцілилися на " + healAmount + " HP.");

                        abilityDelay = 0;
                        validAction = true;
                    } else {
                        listener.onGameOutput("ЗДІБНІСТЬ ЗАРЯДЖАЄТЬСЯ! Ще ходів: " + (10 - abilityDelay));
                    }
                    break;

                default:
                    listener.onGameOutput("НЕВІРНИЙ ВИБІР! ВВЕДІТЬ 1, 2 АБО 3:");
                    return;
            }

            if (validAction) {
                shieldDelay++;
                abilityDelay++;

                if (currentEnemy.health > 0) {
                    currentEnemy.checkEnrage();

                    int baseEnemyDamage = Math.max(currentEnemy.damage - chosenCharacter.armor, 2);

                    if (chosenCharacter.shieldTurnsLeft > 0) {
                        receivedDamage = (int) (baseEnemyDamage * 0.3);
                        chosenCharacter.shieldTurnsLeft--;
                        listener.onGameOutput("ЩИТ ЗАБЛОКУВАВ ЧАСТИНУ УДАРУ!");
                    } else {
                        receivedDamage = baseEnemyDamage;
                    }

                    chosenCharacter.health -= receivedDamage;
                    listener.onGameOutput(currentEnemy.type + " АТАКУВАВ ВАС НА " + receivedDamage + " ШКОДИ!");
                }
            }

            if (currentEnemy.health <= 0) {
                defeatedEnemies++;
                listener.onGameOutput("\nПЕРЕМОГА! " + currentEnemy.type + " ПОВАЛЕНИЙ!");

                int victoryHeal = (int) (chosenCharacter.maxHealth * 0.2);
                chosenCharacter.health = Math.min(chosenCharacter.maxHealth, chosenCharacter.health + victoryHeal);
                listener.onGameOutput("Ви відпочили та відновили " + victoryHeal + " HP.");

                if (Math.random() < 0.3) {
                    listener.onGameOutput("ЗНАЙДЕНО АРТИФАКТ: СВЯТИЙ ЩИТ (Друге життя)!");
                    holyShieldActive = true;
                }

                enemies.remove(0);
                startCombat();
                return;
            }

            if (chosenCharacter.health <= 0) {
                if (holyShieldActive) {
                    listener.onGameOutput("СВЯТИЙ ЩИТ ВОСКРЕСИВ ВАС!");
                    chosenCharacter.health = 50;
                    holyShieldActive = false;
                } else {
                    listener.onGameOutput("ВИ ЗАГИНУЛИ В БОЮ... " + playerName + ", ваша історія закінчилася.");
                    currentState = GameState.GAME_OVER;
                    listener.onGameOver();
                    return;
                }
            }

            showCombatOptions();

        } catch (NumberFormatException e) {
            listener.onGameOutput("ВВЕДІТЬ НОМЕР ДІЇ (1, 2 АБО 3):");
        }
    }

    private void showGameOverOptions() {
        listener.onGameOutput("\n=== КІНЕЦЬ ГРИ ===");
        listener.onGameOutput("БАЖАЄТЕ ЗІГРАТИ ЗНОВУ?");
        listener.onGameOutput("1. ПОЧАТИ ЗАНОВО");
        listener.onGameOutput("2. ВИЙТИ З ГРИ");
    }

    private void handleGameOver(String input) {
        try {
            listener.onGameOutput("");
            int choice = Integer.parseInt(input);

            switch (choice) {
                case 1:
                    listener.onRestartRequest();
                    break;
                case 2:
                    listener.onGameOutput("ДЯКУЮ ЗА ГРУ!");
                    System.exit(0);
                default:
                    listener.onGameOutput("ВВЕДІТЬ 1 або 2:");
                    break;
            }
        } catch (NumberFormatException e) {
            listener.onGameOutput("ВВЕДІТЬ 1 АБО 2:");
        }
    }
}