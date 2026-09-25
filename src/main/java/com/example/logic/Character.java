package com.example.logic;
import java.util.List;

public class Character {
    public String name;
    public int health;
    public int maxHealth;
    public int armor;
    public int baseDamage;
    String signatureWeapon;
    List<String> weapons;
    String ability;
    boolean evolved;

    public int shieldTurnsLeft = 0;
    public int shieldCooldown = 0;
    public int ultimateCooldown = 0;

    public void updateCooldowns() {
        if (shieldTurnsLeft > 0) shieldTurnsLeft--;
        if (shieldCooldown > 0) shieldCooldown--;
        if (ultimateCooldown > 0) ultimateCooldown--;
    }

    public Character(String name, int health, int armor, int baseDamage, String signatureWeapon, List<String> weapons, String ability) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.armor = armor;
        this.baseDamage = baseDamage;
        this.signatureWeapon = signatureWeapon;
        this.weapons = weapons;
        this.ability = ability;
        this.evolved = false;
    }
}