package game;
import java.util.ArrayList;

import gui.MonsterBattleGUI;

/**
 * GameDemo - Complete working example of a monster battle game
 * 
 * This shows you how to use the MonsterBattleGUI to make a game.
 * Look at this code to understand the pattern, then build your own in Game.java!
 * 
 * Pattern:
 * 1. Create GUI
 * 2. Setup game (monsters, items, health)
 * 3. Game loop: get action → do action → check win/loss
 * 
 * NEW: Shows how to use the 4 buttons for difficulty selection AND character selection before game starts!
 */
public class GameDemo {
    
    // The GUI (use this to display everything)
    private MonsterBattleGUI gui;
    
    // Game state
    private ArrayList<Monster> monsters;
    private ArrayList<Item> inventory;
    private int playerHealth;
    private int maxHealth;
    
    // Player stats (customized by character choice)
    private int playerDamage;
    private int playerShield;
    private int playerHeal;
    private int playerSpeed;
    
    /**
     * Main method - start the game!
     */
    public static void main(String[] args) {
        GameDemo game = new GameDemo();
        game.play();
    }
    
    /**
     * Play the game!
     */
    public void play() {
        setupGame();
        gameLoop();
    }
    
    /**
     * Setup - create the GUI and game state
     */
    private void setupGame() {
        // Create the GUI
        gui = new MonsterBattleGUI("Monster Battle - Elite Gamer");
        gui.displayMessage("Welcome to the Game, Player. You could've been the Hero, Brian.");
        
        // CHOOSE DIFFICULTY (number of monsters to face)
        int numMonsters = chooseDifficulty();
        
        // PICK YOUR CHARACTER BUILD (using the 4 action buttons!)
        pickCharacterBuild();
        
        // Setup player health (based on character choice)
        maxHealth = 100;
        playerHealth = maxHealth;
        gui.setPlayerMaxHealth(maxHealth);
        gui.updatePlayerHealth(playerHealth);
        
        // Create monsters based on chosen difficulty
        monsters = new ArrayList<>();
        for (int i = 0; i < numMonsters; i++) {
            monsters.add(new Monster());
        }
        gui.updateMonsters(monsters);
        
        // Create items
        inventory = new ArrayList<>();
        addHealthPotion(30);
        addHealthPotion(30);
        addBomb(20);
        gui.updateInventory(inventory);
        
        // Setup buttons for gameplay (now that character is chosen)
        String[] buttons = {"Attack", "Defend", "Heal", "Use Item"};
        gui.setActionButtons(buttons);
        
        // Welcome message
        gui.displayMessage("Battle Start! You are a " + getCharacterName() + " facing " + numMonsters + " monsters!");
    }
    
    /**
     * Let player choose difficulty (number of monsters) using the 4 buttons
     * This demonstrates using the GUI for menu choices!
     */
    private int chooseDifficulty() {
        // Set button labels to difficulty levels
        String[] difficulties = {"Easy (2)", "Medium (3)", "Hard (4)", "Extreme (5)"};
        gui.setActionButtons(difficulties);
        
        // Display choice prompt
        gui.displayMessage("---- CHOOSE DIFFICULTY ----");
        
        // Wait for player to click a button (0-3)
        int choice = gui.waitForAction();
        
        // Determine number of monsters based on choice
        int numMonsters = 2 + choice;  // 2, 3, 4, or 5 monsters
        
        gui.displayMessage("Difficulty selected: " + difficulties[choice] + " monsters!");
        gui.pause(1500);
        
        return numMonsters;
    }
    
    /**
     * Let player pick their character build using the 4 buttons
     * This demonstrates using the GUI for menu choices!
     */
    private void pickCharacterBuild() {
        // Set button labels to character classes
        String[] characterClasses = {"Fighter", "Tank", "Healer", "Ninja"};
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
        
        // Customize stats based on character choice
        if (choice == 0) {
            // Fighter: high damage, low healing and shield
            gui.displayMessage("You chose Fighter! High damage, but weak defense.");
            playerShield -= (int)(Math.random() * 45 + 1) + 5;  // Reduce shield by 6-50
            playerHeal -= (int)(Math.random() * 46) + 5;        // Reduce heal by 5-50
        } else if (choice == 1) {
            // Tank: high shield, low damage and speed
            gui.displayMessage("You chose Tank! Tough defense, but slow attacks.");
            playerSpeed -= (int)(Math.random() * 9) + 1;        // Reduce speed by 1-9
            playerDamage -= (int)(Math.random() * 100) + 100;   // Reduce damage by 100-199
        } else if (choice == 2) {
            // Healer: high healing, low damage and shield
            gui.displayMessage("You chose Healer! Great recovery, but fragile.");
            playerDamage -= (int)(Math.random() * 26) + 5;      // Reduce damage by 5-30
            playerShield -= (int)(Math.random() * 46) + 5;      // Reduce shield by 5-50
        } else {
            // Ninja: high speed, low healing and health
            gui.displayMessage("You chose Ninja! Fast and deadly, but risky.");
            playerHeal -= (int)(Math.random() * 46) + 5;        // Reduce heal by 5-50
            maxHealth -= (int)(Math.random() * 21) + 5;         // Reduce max health by 5-25
        }
        
        // Pause to let player see their choice
        gui.pause(1500);
    }
    
    /**
     * Get the character name based on stats (for messages)
     */
    private String getCharacterName() {
        if (playerDamage > 150 && playerShield < 30) return "Fighter";
        if (playerShield > 40 && playerDamage < 150) return "Tank";
        if (playerHeal > 40 && playerDamage < 180) return "Healer";
        return "Ninja";
    }
    
    /**
     * Main game loop
     */
    private void gameLoop() {
        // Keep playing while monsters alive and player alive
        while (countLivingMonsters() > 0 && playerHealth > 0) {
            
            // PLAYER'S TURN
            gui.displayMessage("Your turn! HP: " + playerHealth + " | DMG: " + playerDamage);
            int action = gui.waitForAction();  // Wait for button click
            handlePlayerAction(action);
            gui.updateMonsters(monsters);
            gui.pause(500);
            
            // MONSTER'S TURN (if any alive)
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
    
    /**
     * Handle player's action choice
     */
    private void handlePlayerAction(int action) {
        switch (action) {
            case 0: attackMonster(); break;
            case 1: defend(); break;
            case 2: heal(); break;
            case 3: useItem(); break;
        }
    }
    
    /**
     * Attack a random monster (uses playerDamage stat)
     */
    private void attackMonster() {
        Monster target = getRandomLivingMonster();
        if (target != null) {
            // Calculate damage based on player's damage stat
            int baseDamage = (int)(playerDamage * 0.15);  // 15% of damage stat
            int damage = baseDamage + (int)(Math.random() * baseDamage);  // ±50% variance
            target.takeDamage(damage);
            gui.displayMessage("💥 You hit for " + damage + " damage!");
            
            // Show which one we hit
            int index = monsters.indexOf(target);
            gui.highlightMonster(index);
            gui.pause(300);
            gui.highlightMonster(-1);
        }
    }
    
    /**
     * Defend (uses playerShield stat to reduce damage)
     */
    private void defend() {
        gui.displayMessage("🛡️ You brace for impact! (Shield: " + playerShield + ")");
        // Note: In a real game, you'd track this state and reduce incoming damage
        // This is just demonstrating the character stats
    }
    
    /**
     * Heal yourself (uses playerHeal stat)
     */
    private void heal() {
        int healAmount = (int)(playerHeal * 0.5) + (int)(Math.random() * playerHeal * 0.5);
        playerHealth = Math.min(maxHealth, playerHealth + healAmount);
        gui.updatePlayerHealth(playerHealth);
        gui.displayMessage("💚 You healed for " + healAmount + " HP!");
    }
    
    /**
     * Use an item from inventory
     */
    private void useItem() {
        if (inventory.isEmpty()) {
            gui.displayMessage("No items in inventory!");
            return;
        }
        
        // Use first item
        Item item = inventory.remove(0);
        gui.updateInventory(inventory);
        item.use();  // The item knows what to do!
    }
    
    /**
     * Monster attacks player
     */
    private void monsterAttack() {
        Monster attacker = getRandomLivingMonster();
        if (attacker != null) {
            int damage = (int)(Math.random() * attacker.damage());
            playerHealth -= damage;
            gui.updatePlayerHealth(playerHealth);
            gui.displayMessage("👹 Monster attacks! You take " + damage + " damage!");
        }
    }
    
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
    
    // ==================== ITEM CREATION HELPERS ====================
    
    /**
     * Add a health potion to inventory
     */
    private void addHealthPotion(int healAmount) {
        inventory.add(new Item("Health Potion", "🧪", () -> {
            playerHealth = Math.min(maxHealth, playerHealth + healAmount);
            gui.updatePlayerHealth(playerHealth);
            gui.displayMessage("💚 Used Health Potion! Healed " + healAmount + " HP!");
        }));
    }
    
    /**
     * Add a bomb to inventory (damages all monsters)
     */
    private void addBomb(int damage) {
        inventory.add(new Item("Bomb", "💣", () -> {
            for (Monster m : monsters) {
                if (m.health() > 0) {
                    m.takeDamage(damage);
                }
            }
            gui.displayMessage("💣 BOOM! All monsters take " + damage + " damage!");
            gui.updateMonsters(monsters);
        }));
    }
}