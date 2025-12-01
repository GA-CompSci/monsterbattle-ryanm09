package game;

public class Monster {

    //Monster stat variables
    private int health;
    private double damage;
    private int speed;
    private String special;

    //for Infinite Void special
    private double originalDamage;
    private int originalSpeed;
    //for Monster specials
    private int burnTurns = 0;
    private int burnDamage = 0;
    private boolean stealsHealth = false;
    private int difficulty = 1;  //used to balance monster stats based on Difficulty selected

    // CONSTRUCTOR
    public Monster(){
        health = (int)(Math.random() * 80 + 1) + 20;
        damage = (Math.random() * 21) + 10;   
        speed = (int)(Math.random() * 10) + 1;

        // store original speed immediately - used for Infinite Void special
        originalSpeed = speed;
        special = "";
    }

    // OVERLOADED CONSTRUCTOR - Monster Specials!
    public Monster(String special){
        this();
        this.special = special;
        this.originalSpeed = speed;

        //Monster specials
        if(special.equals("Vampire")){
            //handled in monsterAttack()
        }
        if(special.equals("Magma Demon")){
            //handled in monsterAttack()
        }
        if(special.equals("Dark Warlock")){
            stealsHealth = true;
        }
    }

    public Monster(int difficulty){
        this.difficulty = difficulty;

        // scale health and damage based on difficulty - Makes harder difficulties possible
        int baseHealth = (int)(Math.random() * 80 + 1) + 20; // original range
        double baseDamage = (Math.random() * 21) + 10;      // original range
        int baseSpeed = (int)(Math.random() * 10) + 1;      //original range

        switch(difficulty){
            case 1: // Third Rate
                health = baseHealth;
                damage = baseDamage;
                speed = baseSpeed;
                break;
            case 2: // Get Stronger
                health = (int)(baseHealth * 0.8);
                damage = baseDamage * 0.8;
                speed = baseSpeed;
                break;
            case 3: // Breaking a Sweat
                health = (int)(baseHealth * 0.5);
                damage = baseDamage * 0.5;
                speed = baseSpeed - 1;
                break;
            case 4: // Honored One
                health = (int)(baseHealth * 0.5);
                damage = baseDamage * 0.5;
                speed = baseSpeed - 2;
                break;
        }

        speed = (int)(Math.random() * 10) + 1;

        originalSpeed = speed;
        special = "";
        originalDamage = damage; // store original damage
    }

    // Infinite Void stuff
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
    public String specialType() { return special; }

    // MUTATORS
    public void takeDamage(int dmg){
        health -= dmg;
        if (health < 0) health = 0;   //used to prevent negative HP after being killed
    }

    public void applyBurn(int dmg, int turns){
        burnDamage = dmg;
        burnTurns = turns;
    }

    public void updateBurn(){
        if (burnTurns > 0){
            health -= burnDamage;
            if(health < 0) health = 0;
            burnTurns--;
        }
    }
}
