package game;

public class Monster {

    private int health;
    private double damage;
    private int speed;
    private int originalSpeed;
    private String special;

    // CONSTRUCTOR
    public Monster(){
        health = (int)(Math.random() * 61) + 20;
        damage = (Math.random() * 21) + 10;   
        speed = (int)(Math.random() * 8) + 1;

        // IMPORTANT: store original immediately - used for Infinite Void special
        originalSpeed = speed;
        special = "";
    }

    // OVERLOADED CONSTRUCTOR
    public Monster(String special){
        this();
        this.special = special;
    }

    // ORIGINAL SPEED IS ALREADY SET IN CONSTRUCTOR
    public void setOriginalSpeed(int speed) {
        this.originalSpeed = speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getOriginalSpeed() {
        return originalSpeed;
    }

    // ACCESSORS
    public int health() { return health; }
    public double damage() { return Math.round(damage * 100.0) / 100.0; }
    public int speed() { return speed; }
    public String special() { return special; }

    // MUTATOR
    public void takeDamage(int dmg){
        health -= dmg;
    }
}