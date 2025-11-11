package game;
import java.util.ArrayList;

import gui.MonsterBattleGUI;

/**
 * Game - YOUR monster battle game!
 * 
 * Build your game here. Look at GameDemo.java for examples.
 * 
 * Steps:
 * 1. Fill in setupGame() - create monsters, items, set health
 * 2. Fill in the action methods - what happens when player acts?
 * 3. Customize the game loop if you want
 * 4. Add your own helper methods
 * 
 * Run this file to play YOUR game
 */

//TODO: name all the mosnters on spawn with adjectives, such as Strong Ghost or Evil Zombie
//TODO: Add domain expansions after getting a certain amount of damage, Infinite Void sets all speed to 0 and Malevolent Shrine hits every monster for reduced damage
//TODO: Balance every stat for every class
//TODO: If killed a monster leak the damage to the next one
//TODO: Move Heal to potions, dropped about 50% of the time when monster dies, rework items to ultimate/special move for the player

public class Game{
    
    // The GUI (I had AI build most of this)
    private MonsterBattleGUI gui;
    
    // Game state - YOU manage these
    private ArrayList<Monster> monsters;
    private Monster lastAttacked;
    private double shieldPower = 0.0;
    private ArrayList<Item> inventory;
    private int playerHealth;
    private int playerShield;
    private int playerSpeed;
    private int playerDamage;
    private int playerHeal;
    private String[] playerSpecial = {"Malevolent Shrine, Heavenly Restriction, Idle Death Gamble, Infinite Void"};
    private boolean playerSpecialAvaliable = false;
    
    /**
     * Main method - start YOUR game!
     */
    public static void main(String[] args) {
        Game game = new Game(); // it instantiates a copy of this file. We're not running static
        game.play(); // this extra step is unnecessary AI stuff
    }
    
    /**
     * Play the game!
     */
    public void play() {
        setupGame();
        gameLoop();
    }
    
    /**
     * Setup - create the GUI and initial game state
     * 
     */
    private void setupGame() {
        // Create the GUI
        gui = new MonsterBattleGUI("Monster Battle! Epic fights!");

        // CHOOSE DIFFICULTY (number of monsters to face)
        int numMonsters = chooseDifficulty();
         monsters = new ArrayList<>();
        for(int k = 0; k < numMonsters; k++){
            if(k == 0){
                //add monster with special ability
                monsters.add(new Monster("Vampire"));
            } else {
                monsters.add(new Monster());
            }
        }
        gui.updateMonsters(monsters);
       
        // PICK YOUR CHARACTER BUILD (using the 4 action buttons!)
        pickCharacterBuild();
        
        
        
        inventory = new ArrayList<>();
        // Add items here! Look at GameDemo.java for examples
        gui.updateInventory(inventory);
        
        String[] buttons = {"Attack (" + playerDamage + ")",
                            "Defend (" + playerShield + ")",
                            "Heal (" + playerHeal + ")",
                            "Special (" + playerSpecial + " )"};
        gui.setActionButtons(buttons);
        
        // Welcome message
        gui.displayMessage("Battle Start! Choose your action.");
    }
    
    /**
     * Main game loop
     * 
     * This controls the flow: player turn → monster turn → check game over
     * You can modify this if you want!
     */
    private void gameLoop() {
        // Keep playing while monsters alive and player alive
        while (countLivingMonsters() > 0 && playerHealth > 0) {
            shieldPower = 0; //start of turn, lower shield

            // PLAYER'S TURN
            gui.displayMessage("Your turn! HP: " + playerHealth);
            int action = gui.waitForAction();  // Wait for button click (0-3)
            handlePlayerAction(action);
            gui.updateMonsters(monsters);
            gui.pause(500);
            
            // MONSTER'S TURN (if any alive and player alive)
            if (countLivingMonsters() > 0 && playerHealth > 0) {
                monsterAttack();
                gui.updateMonsters(monsters);
                gui.pause(500);
            }
        }
        
        // Game over!
        if (playerHealth <= 0) {
            gui.displayMessage("💀 DEFEAT! You have been defeated...");
        } else {
            gui.displayMessage("🎉 VICTORY! You defeated all monsters!");
        }
    }


    private void pickCharacterBuild() {
        // Set button labels to character classes
        String[] characterClasses = {"Human Monster", "Heavenly Tank", "Gambler", "Limitless"};
        gui.setActionButtons(characterClasses);
        
        // Display choice prompt
        gui.displayMessage("---- PICK YOUR BUILD ----");
        
        // Wait for player to click a button (0-3)
        int choice = gui.waitForAction();
        
        // Initialize default stats
        playerDamage = 200;
        playerShield = 50;
        playerHeal = 50;
        playerSpeed = 10;
        playerHealth = 100;
        String chosenPlayerSpecial;
        
        // Customize stats based on character choice
        if (choice == 0) {
            // Fighter: high damage, low healing and shield
            gui.displayMessage("You chose Human Monster! High damage, but weak defense.");
            playerShield -= (int)(Math.random() * 20 + 1) + 5;  // Reduce shield by 5-25
            playerHeal -= (int)(Math.random() * 20 + 1) + 5;   // Reduce heal by 5-25
            playerSpeed -= (int)(Math.random() * 6) + 3;        // Calc speed 6-11
            chosenPlayerSpecial = playerSpecial[0];
        } else if (choice == 1) {
            // Tank: high shield, low damage and speed
            gui.displayMessage("You chose Heavenly Tank! Tough defense, but low attacks.");
            playerSpeed -= (int)(Math.random() * 9) + 1;        // Reduce speed by 1-9
            playerDamage -= (int)(Math.random() * 20 + 1) + 5;   // Reduce damage by 5-25
            playerSpeed -= (int)(Math.random() * 9) + 1;        // Calc speed 6-11
            chosenPlayerSpecial = playerSpecial[1];
        } else if (choice == 2) {
            // Healer: high healing, low damage and shield
            gui.displayMessage("You chose Gambler! Great heal, but flimsy.");
            playerDamage -= (int)(Math.random() * 21) + 5;      // Reduce damage by 5-25
            playerShield -= (int)(Math.random() * 21) + 5;      // Reduce shield by 5-25
            playerSpeed -= (int)(Math.random() * 10) + 1;        // Calc speed 6-11
            chosenPlayerSpecial = playerSpecial[2];
        } else {
            // Ninja: high speed, low healing and health
            gui.displayMessage("You chose Limitless! Fast and deadly, but flimsy.");
            playerHeal -= (int)(Math.random() * 46) + 5;        // Reduce heal by 5-50
            playerHealth -= (int)(Math.random() * 21) + 5;         // Reduce max health by 5-25
            playerSpeed -= (int)(Math.random() * 6) + 6;        // Calc speed 6-11
            chosenPlayerSpecial = playerSpecial[3];
        }
        if(playerHeal < 0) playerHeal = 0;

        gui.setPlayerMaxHealth(playerHealth);
        gui.updatePlayerHealth(playerHealth);
        
        // Pause to let player see their choice
        gui.pause(1500);
    }
    
    /**
     * Let player choose difficulty (number of monsters) using the 4 buttons
     * This demonstrates using the GUI for menu choices!
     */
    private int chooseDifficulty() {
        // Set button labels to difficulty levels
        String[] difficulties = {"Third Rate", "Get Stronger", "Breaking a Sweat", "Honored One"};
        gui.setActionButtons(difficulties);
        
        // Display choice prompt
        gui.displayMessage("---- CHOOSE DIFFICULTY ----");
        
        // Wait for player to click a button (0-3)
        int choice = gui.waitForAction();
        int numMonsters = 0;

        switch(choice){
            case 0:
                numMonsters = (int)(Math.random() * (4 - 2 + 1)) + 2;
                break;
            case 1:
                numMonsters = (int)(Math.random() * (5 - 4 + 1)) + 4;
                break;
            case 2:
                numMonsters = (int)(Math.random() * (8 - 6 + 1)) + 6;
                break;
            case 3:
                numMonsters = (int)(Math.random() * (15 - 10 + 1)) + 10;
                break;
        }
        
        gui.displayMessage("Judgment Chosen. " + difficulties[choice] + " difficulty selected.");
        gui.pause(1500);
        
        return numMonsters;
    }
    
    /**
     * Handle player's action choice
     * 
     */
    private void handlePlayerAction(int action) {
        switch (action) {
            case 0: // Attack button
                attackMonster();
                break;
            case 1: // Defend button
                defend();
                break;
            case 2: // Heal button
                heal();
                break;
            case 3: // Special button
                useSpecial();
                break;
        }
    }
    
    
    /**
     * Attack a monster
     * 
     * - How much damage?
     * - Which monster gets hit?
     * - Special effects?
     */
    private void attackMonster() {
        Monster target = getRandomLivingMonster();
        lastAttacked = target;
        int damage = (int)(Math.random() * playerDamage + 1); // 0 - playerDamage
        if(damage == 0){
            //hurt yourself
            playerHealth -= 5;
            gui.displayMessage("Critical MISS! You hit yourself!");
            gui.updatePlayerHealth(playerHealth);
        } else if(damage == playerDamage){
            gui.displayMessage("Critical HIT! You INSTAkilled the monster!");
            target.takeDamage(target.health());
        } else {
          target.takeDamage(damage); 
          gui.displayMessage("You hit the monster for " + damage + " damage"); 
        }
        // Show which one we hit
        int index = monsters.indexOf(target);
        gui.highlightMonster(index);
        gui.pause(300);
        gui.highlightMonster(-1);
        //update the list
        gui.updateMonsters(monsters);
    }
    /**
     * Defend
     * 
     * - Reduce damage?
     * - Block next attack?
     * - Something else?
     */
    private void defend() {
        shieldPower = playerShield;
        gui.displayMessage("Shield raise!");
    }
    

     // Heal yourself
    private void heal() {
        playerHealth += playerHeal;
        gui.updatePlayerHealth(playerHealth);
        gui.displayMessage("You healed for " + playerHeal + " health");
    }
    
    /**
     * Use special ability - each class has unique ability
     */
    private void useSpecial(){
        if(countDeadMonsters() >= 3) {
            playerSpecialAvaliable = true;
        }
        playerSpecialAvaliable = false;
    }
    
    /**
     * Monster attacks player
     * 
     * - How much damage?
     * - Which monster attacks?
     * - Special abilities?
     */
    private void monsterAttack() {
        // build a list of every mosnter that gets to attack player
        ArrayList<Monster> attackers = getSpeedyMonsters();
        if(lastAttacked != null && lastAttacked.health() > 0 && !attackers.contains(lastAttacked)){
            attackers.add(lastAttacked);
        }

        for(Monster m : attackers){
            double incomingDamage = m.damage();
            if(shieldPower > incomingDamage){
                double absorbedDamage = incomingDamage;
                shieldPower -= absorbedDamage;
                gui.displayMessage("You TANKED " + absorbedDamage + " damage");
            } else if(incomingDamage > shieldPower){
                double takenDamage = Math.abs(shieldPower - incomingDamage);
                playerHealth -= takenDamage;
                gui.displayMessage("You took " + takenDamage + " damage you bum");
            }
            gui.updatePlayerHealth(playerHealth);

            // Show which one that hit us
            int index = monsters.indexOf(m);
            gui.highlightMonster(index);
            gui.pause(300);
            gui.highlightMonster(-1);
        }
    }
    
    // ==================== HELPER METHODS ====================
    // Add your own helper methods here!
    
    /**
     * Count how many monsters are still alive
     */
    private int countLivingMonsters() {
        int count = 0;
        for (Monster m : monsters) {
            if (m.health() > 0) count++;
        }
        return count;
    }
    

    //Monsters with a special
    private ArrayList<Monster> getSpecialMonsters(){
        ArrayList<Monster> result = new ArrayList<>();
        for(Monster m : monsters){
            if(m.special() != null && !m.special().equals("") && m.health() > 0){
                result.add(m);
            }
        }
        return result;
    }

    // Count dead monsters
    private int countDeadMonsters(){
        int count = 0;
        for(Monster m : monsters){
            if(m.health() < 0) count++;
        }
        return count;
    } 


    //Monsters with greater speed than player
    private ArrayList<Monster> getSpeedyMonsters(){
        ArrayList<Monster> speedsters = new ArrayList<>();
        for(Monster m : monsters){
            if(m.speed() > playerSpeed && m.health() > 0){
                speedsters.add(m);
            }
        }
        return speedsters;
    }
    /**
     * Get a random living monster
     */
    private Monster getRandomLivingMonster() {
        ArrayList<Monster> alive = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.health() > 0) alive.add(m);
        }
        if (alive.isEmpty()) return null;
        return alive.get((int)(Math.random() * alive.size()));
    }
    

    private void specialProgress() {
        gui.updateInventory(inventory);
        gui.displayMessage(countDeadMonsters() + " / 3 kills till special");
        return;
    }


    // Make more helper methods if needed
    // Examples:
    // - Method to find the strongest monster
    // - Method to check if player has a specific item
    // - Method to add special effects
    // - etc.
}