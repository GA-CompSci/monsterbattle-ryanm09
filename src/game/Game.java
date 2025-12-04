package game;

import java.util.ArrayList;
import gui.MonsterBattleGUI;

//The beginning of Greatness!
public class Game {


    //TONS AND TONS OF VARIABLES!

    // The GUI
    private MonsterBattleGUI gui;
    private ArrayList<Item> inventory;


    //Monster type variables
    private ArrayList<Monster> monsters;
    private Monster lastAttacked;
    //all for one stupid monster special
    private int burn = 0;
    private int turns = 0;
    private int playerBurnDamage = 0;
    private int playerBurnTurns = 0;
    private boolean magmaBurnActive = false;


    //Player type variables
    private double shieldPower = 0.0;
    private int playerHealth;
    private int playerShield;
    private int playerSpeed;
    private int playerDamage;
    private int playerHeal;
    private int playerMaxHealth; //used to track health above 100 for specfic classes
    private int playerClass;
    //player special variables
    private boolean playerSpecialAvailable = false;
    private int specialKillCounter = 0;
    private String chosenPlayerSpecial;
    private int playerSpecialTurnsLeft = 0; // used for multi-turn specials
    private boolean justActivatedSpecial = false; //a fix for a player special bug
    private boolean playerSpecialActive = false;
    private boolean reflectActive = false;


    //Game type variables
    private int difficultyLevel;



    //finally in the actual game
    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }

    public void play() {
        setupGame();
        gameLoop();
    }

    private void setupGame() {
        gui = new MonsterBattleGUI("Monster Battle! Epic fights!");

        int numMonsters = chooseDifficulty();
        monsters = new ArrayList<>();
        for (int k = 0; k < numMonsters; k++) { //used to create the 3 special monster types. Always the first 3 monsters.
            if (k == 0) {
                monsters.add(new Monster("Dark Warlock"));
            } else if (k == 1){
                monsters.add(new Monster("Magma Demon"));
            } else if (k == 2){
                monsters.add(new Monster("Vampire"));
            } else {
                monsters.add(new Monster());
            }
        }
        gui.updateMonsters(monsters);

        pickCharacterBuild();

        inventory = new ArrayList<>(); //I used the inventory panel to track kills for the player Special meter
        gui.updateInventory(inventory);
        updateSpecialProgress();

        String[] buttons = {
            "Attack (" + playerDamage + ")",
            "Defend (" + playerShield + ")",
            "Heal (" + playerHeal + ")",
            chosenPlayerSpecial
        };
        gui.setActionButtons(buttons);
        gui.displayMessage("Battle Start! Choose your action.");
    }

    private void gameLoop() {
        while (countLivingMonsters() > 0 && playerHealth > 0) {
            shieldPower = 0;

            //apply burn Damage before player turn
            applyBurnDamage();

            gui.displayMessage("Your turn! HP: " + playerHealth);
            int action = gui.waitForAction();
            handlePlayerAction(action);
            gui.updateMonsters(monsters);
            gui.pause(500);

            if (countLivingMonsters() > 0 && playerHealth > 0) {
                monsterAttack();
                gui.updateMonsters(monsters);
                gui.pause(500);
            }
            //used to decremenet player special AFTER the turn ends, used for multi-turn based player specials
            if (playerSpecialActive) {
                if (playerSpecialTurnsLeft > 0) {
                    playerSpecialTurnsLeft--;
                    if (playerSpecialTurnsLeft == 0) {
                        endCurrentSpecial();

                        // stall gui messages to show special messages
                        gui.updatePlayerHealth(playerHealth);
                        gui.updateMonsters(monsters);
                        gui.pause(800);

                    }
                }
            }
        }

        if (playerHealth <= 0) {
            gui.displayMessage("DEFEAT! You have been defeated...");   //probably some of the only text I never changed
        } else {
            gui.displayMessage("VICTORY! You defeated all monsters!");
        }
    }

    private void pickCharacterBuild() {
        String[] characterClasses = { "Human Monster", "Heavenly Tank", "Gambler", "Limitless" };  //all the same classes we made in class, just renamed to be cooler
        gui.setActionButtons(characterClasses);
        gui.displayMessage("---- PICK YOUR BUILD ----");
        int choice = gui.waitForAction();

        //defaults to build off of to create player stats
        playerDamage = 200;
        playerShield = 50;
        playerHeal = 50;
        playerSpeed = 10;
        playerHealth = 100;

        if (choice == 0) {
            gui.displayMessage("You chose Human Monster! High damage."); //fighter but cooler
            playerDamage -= 50;
            playerShield -= (int) (Math.random() * 20 + 1) + 5; //Play more aggressively, shield should be unviable
            playerSpeed -= (int) (Math.random() * 2) + 3;

            //DIFFICULTY SCALING - used to make the game actually possible on harder difficulties
            double healthMultiplier = 1.0;
            double healMultiplier = 1.0;
            switch (difficultyLevel) {
                case 1: // Third Rate
                    healthMultiplier = 1.0;
                    healMultiplier = 1.0;
                    break;
                case 2: // Get Stronger
                    healthMultiplier = 1.2;
                    healMultiplier = 1.1;
                    break;
                case 3: // Breaking a Sweat
                    healthMultiplier = 1.5;
                    healMultiplier = 1.3;
                    break;
                case 4: // Honored One
                    healthMultiplier = 2.0;
                    healMultiplier = 1.5;
                    break;
            }
            // apply scaling
            playerHealth = (int)(playerHealth * healthMultiplier);
            playerHeal = (int)(playerHeal * healMultiplier);
            playerMaxHealth = playerHealth;
            gui.setPlayerMaxHealth(playerMaxHealth);
            //apply special selection
            chosenPlayerSpecial = "Malevolent Shrine";
            playerClass = 0;
        } else if (choice == 1) {
            gui.displayMessage("You chose Heavenly Tank! Tough defense."); //tank but cooler
            playerDamage -= (int) (Math.random() * 20 + 1) + 5;  //nerf Tank's damage
            playerSpeed -= (int) (Math.random() * 5) + 1;
            
            //DIFFICULTY SCALING
            double healthMultiplier = 1.0;
            double healMultiplier = 1.0;
            switch (difficultyLevel) {
                case 1: // Third Rate
                    healthMultiplier = 1.0;
                    healMultiplier = 1.0;
                    break;
                case 2: // Get Stronger
                    healthMultiplier = 1.2;
                    healMultiplier = 1.1;
                    break;
                case 3: // Breaking a Sweat
                    healthMultiplier = 1.5;
                    healMultiplier = 1.3;
                    break;
                case 4: // Honored One
                    healthMultiplier = 2.0;
                    healMultiplier = 1.5;
                    break;
            }
            // apply scaling
            playerHealth = (int)(playerHealth * healthMultiplier);
            playerHeal = (int)(playerHeal * healMultiplier);
            playerMaxHealth = playerHealth;
            gui.setPlayerMaxHealth(playerMaxHealth);
            //apply special selection
            chosenPlayerSpecial = "Heavenly Restriction";
            playerClass = 1;
        } else if (choice == 2) { //Has a passive ability, heals on hit for 20, heals 40 for kill (doesn't stack together).
            gui.displayMessage("You chose Gambler! Great heal.");   //healer but cooler
            playerDamage -= (int) (Math.random() * 21) + 5;
            playerShield -= (int) (Math.random() * 21) + 5;
            
            //DIFFICULTY SCALING
            double healthMultiplier = 1.0;
            double healMultiplier = 1.0;
            switch (difficultyLevel) {
                case 1: // Third Rate
                    healthMultiplier = 1.0;
                    healMultiplier = 1.0;
                    playerSpeed = 5;
                    break;
                case 2: // Get Stronger
                    healthMultiplier = 1.2;
                    healMultiplier = 1.1;
                    playerSpeed = 5;
                    break;
                case 3: // Breaking a Sweat
                    healthMultiplier = 1.5;
                    healMultiplier = 1.3;
                    playerSpeed = 6;
                    break;
                case 4: // Honored One
                    healthMultiplier = 2.0;
                    healMultiplier = 1.5;
                    playerSpeed = 7;
                    break;
            }
            // apply scaling
            playerHealth = (int)(playerHealth * healthMultiplier);
            playerHeal = (int)(playerHeal * healMultiplier);
            playerMaxHealth = playerHealth;
            gui.setPlayerMaxHealth(playerMaxHealth);
            //apply special selection
            gui.setPlayerMaxHealth(playerMaxHealth);
            chosenPlayerSpecial = "Idle Death Gamble";
            playerClass = 2;
        } else {
            gui.displayMessage("You chose Limitless! Speedy."); //bandit but cooler
            playerHeal -= (int) (Math.random() * 15) + 5;
            playerHealth -= (int) (Math.random() * 21) + 5;
            playerSpeed -= (int)(Math.random() * 6) + 6; //can't be too fast now!

            // Difficulty scaling multipliers
            double healthMultiplier = 1.0;
            double healMultiplier = 1.0;
            double damageIncrease = 0.0;

            switch (difficultyLevel) {   //I figured out you can make switch-case one-liners!
                case 1: healthMultiplier = 1.0; healMultiplier = 1.0; damageIncrease = 0; break;
                case 2: healthMultiplier = 1.2; healMultiplier = 1.1; damageIncrease = 10; break;
                case 3: healthMultiplier = 1.5; healMultiplier = 1.3; damageIncrease = 20; break;
                case 4: healthMultiplier = 2.0; healMultiplier = 1.5; damageIncrease = 30; break;
            }

            // Apply scaling
            playerHealth = (int)(playerHealth * healthMultiplier);
            playerHeal = (int)(playerHeal * healMultiplier);
            playerDamage += (int)damageIncrease;
            playerMaxHealth = playerHealth;
            gui.setPlayerMaxHealth(playerMaxHealth);
            // Limitless speed bonus - faster than most monsters
            playerSpeed += 5;
            //apply special selection
            chosenPlayerSpecial = "Infinite Void";
            playerClass = 3;
        }

        if (playerHeal < 0)
            playerHeal = 0;

        gui.setPlayerMaxHealth(playerHealth);
        gui.updatePlayerHealth(playerHealth);
        gui.pause(1500);
    }

    private int chooseDifficulty() {
        String[] difficulties = { "Third Rate", "Get Stronger", "Breaking a Sweat", "Honored One" }; 
        gui.setActionButtons(difficulties);
        gui.displayMessage("---- CHOOSE DIFFICULTY ----");
        int choice = gui.waitForAction();
        int numMonsters = 0;
        difficultyLevel = choice + 1;
        switch (choice) {
            case 0:
                numMonsters = (int) (Math.random() * 3) + 2;
                break;
            case 1:
                numMonsters = (int) (Math.random() * 2) + 4;
                break;
            case 2:
                numMonsters = (int) (Math.random() * 3) + 6;
                break;
            case 3:
                numMonsters = (int) (Math.random() * 6) + 10;
                break;
        }
        gui.displayMessage("Judgment Chosen. " + difficulties[choice] + " difficulty selected.");
        gui.pause(1500);
        return numMonsters;
    }

    private void handlePlayerAction(int action) {
        switch (action) {
            case 0: // Attack
                attackMonster();
                break;
            case 1: // Defend
                defend();
                break;
            case 2: // Heal
                heal();
                break;
            case 3: // Use Special
                if (playerSpecialAvailable) {
                    useSpecial();
                } else {
                    gui.displayMessage("Need more blood! Kills: " + specialKillCounter + " / 3"); //need to kill 3 monsters to unlock player special
                }
                break;
        }
        justActivatedSpecial = false; // reset check for activated special
    }


    private void attackMonster() {
        Monster target = getRandomLivingMonster();
        lastAttacked = target;
        int damage = (int) (Math.random() * playerDamage + 1); 
        target.takeDamage(damage);
        gui.displayMessage("You hit the monster for " + damage + " damage");

        //Gambler passive - heals 20 on hit, 40 on kill
        if(playerClass == 2 && !playerSpecialActive){
            playerHealth += 20;
            if(target.health() <= 0){
                playerHealth += 20;
            }
            if(playerHealth >= playerMaxHealth) playerHealth = playerMaxHealth; //don't overheal
            gui.updatePlayerHealth(playerHealth);
        }


        //only count kills if no special is active
        if (target.health() <= 0) {
            if (!playerSpecialActive) {
                specialKillCounter++;
                if (specialKillCounter > 3)
                    specialKillCounter = 3;

                if (specialKillCounter == 3 && !playerSpecialAvailable) {
                    playerSpecialAvailable = true;
                    gui.displayMessage("Your player special is now ready!");
                }
                updateSpecialProgress();
            }
        }
        
        gui.highlightMonster(monsters.indexOf(target));
        gui.pause(300);
        gui.highlightMonster(-1);
        gui.updateMonsters(monsters);
    }

    private void defend() {
        //block defend from using during Heavenly Restriction special
        if(playerSpecialActive && playerClass == 1){
            gui.displayMessage("You're body is as strong as the shield already, defend doesn't work!"); // during Heavenly Restriction, encouraged to be more aggressive due to phsycial tankiness already
            return;
        }
        shieldPower = playerShield;
        gui.displayMessage("Shield raise!");
    }

    private void heal() {
        if(playerHealth >= getPlayerMaxHealth()){
            gui.displayMessage("You are already at max health!"); //don't overheal code block for like the third time, lots of edgecases I guess
            return;
        }
        if(playerSpecialActive && playerClass == 2){
            gui.displayMessage("You are literally invincible bro"); //during Idle Death Gamble, healing does nothing as you are immortal anyway
            return;
        }
        gui.updatePlayerHealth(playerHealth);
        gui.displayMessage("You healed for " + playerHeal + " health");

        
        //cap heal to max health
        playerHealth += playerHeal;
        if(playerHealth > getPlayerMaxHealth()) playerHealth = getPlayerMaxHealth();

        gui.updatePlayerHealth(playerHealth);
        gui.pause(400);
    }

    private void useSpecial() { //This took FOREVER to make, but I had much more fun than simply adding heal potions or bombs
        if(!playerSpecialAvailable){
            gui.displayMessage("Need more blood! Kills: " + specialKillCounter + " / 3"); //needs 3 kills to charge special
            return;
        }

        playerSpecialActive = true; //player special avaliable!
        justActivatedSpecial = true;

        switch (playerClass) {
            case 0:
                gui.displayMessage("Malevolent Shrine hits all monsters for 1 hit!");
                for (Monster m : monsters)
                    if (m.health() > 0)
                        m.takeDamage(75); //Used to do the default damage, no wonder why every monster died when the default is 200
                break;
            case 1:
                gui.displayMessage("Heavenly Restriction active!");
                playerSpecialTurnsLeft = 3;
                reflectActive = true; //special version of shield used to reflect damage, but don't need to use the defend button, just works naturally so it is encouraged to play aggressively.
                break;
            case 2:
                gui.displayMessage("Idle Death Gamble active!"); //Makes you immortal by giving 9999 health, basic but fun. Also heals back to full when the special ends
                playerHealth = 9999;
                playerSpecialTurnsLeft = 3;
                break;
            case 3:
                gui.displayMessage("Infinite Void freezes all monsters!"); //The most complicated one, sets all monsters's speeds to 0, so you essentially get 3 straight turns
                playerSpecialTurnsLeft = 3;
                for (Monster m : monsters) {
                    m.setOriginalSpeed(m.speed());
                    m.setSpeed(0);
                }
                playerSpecialTurnsLeft = 3;
                break;
        }
        specialKillCounter = 0;
        updateSpecialProgress();
        playerSpecialAvailable = false; //party's over
    }

    private void monsterAttack() {
        ArrayList<Monster> attackers = getSpeedyMonsters();
        if (lastAttacked != null && lastAttacked.health() > 0 && !attackers.contains(lastAttacked) && lastAttacked.speed() != 0)
            attackers.add(lastAttacked);

        for (Monster m : attackers) {
            double incomingDamage = m.damage();
            
            if(reflectActive){ //Only used during the Heavenly Restriction player special
                double reflected = incomingDamage * 2;  //playerHealth is a lot lower than monsters' healths, double it to compensate
                incomingDamage  *= 2;
                m.takeDamage((int)reflected);
                gui.displayMessage("Heavenly Restriction reflected " + (int)reflected + " damage!");
            }
            if (shieldPower > incomingDamage) {
                shieldPower -= incomingDamage;
                gui.displayMessage("You TANKED " + incomingDamage + " damage");
            } else if (incomingDamage > shieldPower) {
                double taken = incomingDamage - shieldPower;
                playerHealth -= taken;
                gui.displayMessage("You took " + taken + " damage");
            }

            //MONSTER SPECIALS - built into monsterAttack() method
            String sp = m.specialType();

            //Vampire - 20% lifesteal on hit
            if (sp.equals("Vampire")) { //definitely the most lame one
            int heal = (int)(m.damage() * 0.75); //may seem high but player has much smaller health bar than monster
            m.takeDamage(-heal);
            gui.displayMessage("The Vampire drains life and heals for " + heal + " HP!");
            }

            // Magma Demon – applies burn to player for 2 turns
            if (sp.equals("Magma Demon")) {
                int burn = 4;
                int turns = 2;
                playerBurnDamage = burn;
                playerBurnTurns = turns; //I like the name of this variable
                magmaBurnActive = true;
                if(reflectActive){
                    burn /= 2;
                    m.takeDamage(2);
                    gui.displayMessage("Heavenly Restriction reflect 2 burn damage!"); //workaround for Heavenly Restriction player special
                }
                gui.displayMessage("Magma Demon ignites you for " + burn + " burn damage! Lasts 2 turns.");
                gui.updatePlayerHealth(playerHealth);
            }

            // Dark Warlock – nerfs player's healing permanently (-5 heal per hit)
            if (sp.equals("Dark Warlock")) { //The most creative and sneaky one, adds up over time
                int amount = 5;
                playerHeal -= amount;
                if (playerHeal < 0) playerHeal = 0;
                gui.displayMessage("Dark Warlock curses you! Your healing is reduced by " + amount + "!");
            }

            gui.updatePlayerHealth(playerHealth);
            gui.highlightMonster(monsters.indexOf(m));
            gui.pause(300);
            gui.highlightMonster(-1);
        }
    }

    
    //  HELPER METHODS, HONESTLY NOT AS MANY AS I EXPECTED 
    
    //Used to update the special progress meter in the inventory panel
    private void updateSpecialProgress() { 
        inventory.clear();
        inventory.add(new Item("Kills: " + specialKillCounter + " / 3", "⭐", null));
        gui.updateInventory(inventory);
    }

    //Used for game loop to run
    private int countLivingMonsters() {
        int count = 0;
        for (Monster m : monsters)
            if (m.health() > 0)
                count++;
        return count;
    }

    //Monsters faster than you attack you, slower and they can't
    private ArrayList<Monster> getSpeedyMonsters() {
        ArrayList<Monster> speedsters = new ArrayList<>();
        for (Monster m : monsters)
            if (m.speed() > playerSpeed && m.health() > 0)
                speedsters.add(m);
        return speedsters;
    }

    //Used for player to target a random living monster while attacking
    private Monster getRandomLivingMonster() {
        ArrayList<Monster> alive = new ArrayList<>();
        for (Monster m : monsters)
            if (m.health() > 0)
                alive.add(m);
        if (alive.isEmpty())
            return null;
        return alive.get((int) (Math.random() * alive.size()));
    }

    //Used in Magma Demon special to apply the burning effect
    private void applyBurnDamage(){
        if (playerBurnTurns > 0 && magmaBurnActive) {
            playerHealth -= playerBurnDamage;
            if (playerHealth < 0) playerHealth = 0;
            gui.displayMessage("You take " + playerBurnDamage + " burn damage from Magma Demon!");
            gui.updatePlayerHealth(playerHealth);
            playerBurnTurns--;
            if(playerBurnTurns == 0){
                magmaBurnActive = false; //turn off when done
            }
        }
    
    }

    //used to handle errors caused by player special ending too early
    private void endCurrentSpecial() {   
        switch (playerClass) {
            case 0: //Malevolent Shrine
                //nothing needed
                break;
            case 1: // Heavenly Restriction
                gui.displayMessage("Heavenly Restriction ended!");
                reflectActive = false;
                break;
            case 2: // Idle Death Gamble
                gui.displayMessage("Idle Death Gamble ended!");
                break;
            case 3: // Infinite Void
                for (Monster m : monsters) {
                    m.setSpeed(m.getOriginalSpeed());
                }
                gui.displayMessage("Infinite Void ended! Monsters returned to normal speeds.");
                break;
        }
        playerSpecialActive = false;
    }

    //As basic as it sounds
    private int getPlayerMaxHealth(){
        return playerMaxHealth;
    }
}
//that's a lot of code!