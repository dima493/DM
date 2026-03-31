package com.example.logic;

class Enemy {
    String type;
    int health;
    int maxHealth;
    int damage;
    boolean enraged;

    public Enemy(String type, int health, int damage) {
        this.type = type;
        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.enraged = false;
    }

    public void checkEnrage() {
        if (this.health < 20 && !enraged) {
            enraged = true;
            damage = (int) (damage * 1.5);
        }
    }
}