package game;

import java.util.ArrayList;
import gui.MonsterBattleGUI;

/**
 * Game - YOUR monster battle game!
 */
public class Game {

    // The GUI
    private MonsterBattleGUI gui;

    // Game state
    private ArrayList<Monster> monsters;
    private Monster lastAttacked;
    private double shieldPower = 0.0;
    private ArrayList<Item> inventory;
    private int playerHealth;
    private int playerShield;
    private int playerSpeed;
    private int playerDamage;
    private int playerHeal;
    private boolean playerSpecialAvailable = false;
    private int specialKillCounter = 0;
    private int playerClass;
    private String chosenPlayerSpecial;
    private int playerSpecialTurnsLeft = 0; // used for multi-turn specials

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
        for (int k = 0; k < numMonsters; k++) {
            if (k == 0) {
                monsters.add(new Monster("Syphon"));
            } else if(k == 1){
                monsters.add(new Monster("Murderous"));
            } else if(k == 2){
                monsters.add(new Monster("Armored"));
            } else {
                monsters.add(new Monster());
            }
        }
        gui.updateMonsters(monsters);

        pickCharacterBuild();

        inventory = new ArrayList<>();
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
        }

        if (playerHealth <= 0) {
            gui.displayMessage("💀 DEFEAT! You have been defeated...");
        } else {
            gui.displayMessage("🎉 VICTORY! You defeated all monsters!");
        }
    }

    private void pickCharacterBuild() {
        String[] characterClasses = { "Human Monster", "Blessed Tank", "Gambler", "Limitless" };
        gui.setActionButtons(characterClasses);
        gui.displayMessage("---- PICK YOUR BUILD ----");
        int choice = gui.waitForAction();

        playerDamage = 100;
        playerShield = 50;
        playerHeal = 50;
        playerSpeed = 10;
        playerHealth = 100;

        if (choice == 0) {
            gui.displayMessage("You chose Human Monster! High damage.");
            playerShield -= 30;
            playerHeal = 50;
            playerSpeed -= 5;
            chosenPlayerSpecial = "Malevolent Shrine";
            playerClass = 0;
        } else if (choice == 1) {
            gui.displayMessage("You chose Heavenly Tank! Tough defense.");
            playerSpeed -= 7;
            playerDamage -= 110;
            playerHealth += 30;
            chosenPlayerSpecial = "Heavenly Restriction";
            playerClass = 1;
        } else if (choice == 2) {
            gui.displayMessage("You chose Gambler! Great heal.");
            playerDamage -= (int) (Math.random() * 21) + 5;
            playerShield -= 30;
            playerSpeed -= 5;
            chosenPlayerSpecial = "Idle Death Gamble";
            playerClass = 2;
        } else {
            gui.displayMessage("You chose Limitless! Speedy.");
            playerHeal -= 10;
            playerHealth -= 5;
            playerSpeed -= (int) (Math.random() * 6) + 6;
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
            case 0:
                attackMonster();
                if (playerSpecialTurnsLeft > 0)
                    playerSpecialTurnsLeft--;
                if (playerSpecialTurnsLeft == 0 && chosenPlayerSpecial.equals("Idle Death Gamble")) {
                    playerHealth = 100;
                    gui.displayMessage("Gambler special ended! Health restored.");
                }
                if (playerSpecialTurnsLeft == 0 && chosenPlayerSpecial.equals("Infinite Void")) {
                    for (Monster m : monsters) {
                        m.setSpeed(m.getOriginalSpeed());
                    }
                }
                break;
            case 1:
                if (playerShield > 100) {
                    gui.displayMessage("You're literally invincible bro");
                } else {
                    defend();
                }
                if (playerSpecialTurnsLeft > 0)
                    playerSpecialTurnsLeft--;
                if (playerSpecialTurnsLeft == 0 && chosenPlayerSpecial.equals("Infinite Void")) {
                    for (Monster m : monsters) {
                        m.setSpeed(m.getOriginalSpeed());
                    }
                }
                break;
            case 2:
                if (playerHealth >= 100) {
                    gui.displayMessage("Already max health!");
                } else {
                    heal();
                }
                if (playerSpecialTurnsLeft > 0)
                    playerSpecialTurnsLeft--;
                if (playerSpecialTurnsLeft == 0 && chosenPlayerSpecial.equals("Infinite Void")) {
                    for (Monster m : monsters) {
                        m.setSpeed(m.getOriginalSpeed());
                    }
                }
                break;
            case 3:
                useSpecial();
                break;
        }
    }

    private void attackMonster() {
        Monster target = getRandomLivingMonster();
        lastAttacked = target;
        int damage = (int) (Math.random() * playerDamage + 1);
        
        
        //CRITICAL MISS!
        if (damage == 0) {
            playerHealth -= 5;
            gui.displayMessage("Critical MISS! You hit yourself!");
            gui.updatePlayerHealth(playerHealth);

            //CRITICAL HIT! INSTAKILL
        } else if (damage == playerDamage){
            gui.displayMessage("Critical HIT! You INSTAkilled the monster!");
            target.takeDamage(target.health());
        } else {
            //ARMORED MONSTER SPECIAL
            if(target.special().equals("Armored")){
                //25% damage reduction due to hardened skin!
                target.takeDamage((int)(damage * 0.75));
                gui.displayMessage("Armored Monsters take only 75% damage!");
            } else {
                target.takeDamage(damage);
                gui.displayMessage("You hit the monster for " + damage + " damage!");
            }
        }
        if (target.health() <= 0) {
            specialKillCounter++;
            if (specialKillCounter > 3)
                specialKillCounter = 3;
            updateSpecialProgress();
        }
        gui.highlightMonster(monsters.indexOf(target));
        gui.pause(300);
        gui.highlightMonster(-1);
        gui.updateMonsters(monsters);
    }

    private void defend() {
        shieldPower = playerShield;
        gui.displayMessage("Shield raise!");
    }

    private void heal() {
        playerHealth += playerHeal;
        gui.updatePlayerHealth(playerHealth);
        gui.displayMessage("You healed for " + playerHeal + " health");
    }

    private void updateSpecialProgress() {
        inventory.clear();
        inventory.add(new Item("Kills: " + specialKillCounter + " / 3", "⭐", null));
        gui.updateInventory(inventory);
    }

    private void useSpecial() {
        if (countDeadMonsters() >= 3) {
            playerSpecialAvailable = true;
        } else {
            gui.displayMessage("Need more blood! Kills: " + specialKillCounter + " / 3");
            return;
        }

        switch (playerClass) {
            case 0:
                gui.displayMessage("Malevolent Shrine hits all monsters for 1 hit!");
                for (Monster m : monsters)
                    if (m.health() > 0)
                        m.takeDamage(playerDamage / 2);
                break;
            case 1:
                gui.displayMessage("Heavenly Restriction active!");
                shieldPower = 9999;
                playerSpecialTurnsLeft = 2;
                break;
            case 2:
                gui.displayMessage("Idle Death Gamble active!");
                playerSpecialTurnsLeft = 3;
                playerHealth = 9999;
                break;
            case 3:
                gui.displayMessage("Infinite Void freezes all monsters!");
                playerSpecialTurnsLeft = 3;
                for (Monster m : monsters) {
                    m.setOriginalSpeed(m.speed());
                    m.setSpeed(0);
                }
                break;
        }
        specialKillCounter = 0;
        updateSpecialProgress();
        playerSpecialAvailable = false;
    }

    private void monsterAttack() {
        ArrayList<Monster> attackers = getSpeedyMonsters();
        ArrayList<Monster> cantAttack = getFrozenMonsters();
        if (lastAttacked != null && lastAttacked.health() > 0 && !attackers.contains(lastAttacked) && cantAttack.contains(lastAttacked))
            attackers.add(lastAttacked);

        for (Monster m : attackers) {
            int incomingDamage = (int)(Math.random() * m.damage()+ 1);
            
            
            //SPECIAL MONSTER TYPES

            //SPECIAL TYPE 1 - SYPHON HEALS ITSELF ON HIT FOR HALF THE DAMAGE IT DELT
            if(m.special().equals("Syphon")){
                m.takeDamage(-incomingDamage / 2);
                gui.displayMessage("The syphon healed itself while hitting you!");
            }
            
            //SPECIAL TYPE 2 - MURDEROUS DEALS 50% MORE DAMAGE PER HIT
            if(m.special().equals("Murderous")){
                incomingDamage = (int)(m.damage() * 1.5); 
            }

            if (shieldPower == 9999) {
                int dmg = (int) m.damage();
                int reflected = dmg / 2;
                m.takeDamage(reflected);
                gui.displayMessage("Heavenly Restriction deflects " + reflected + " damage back!");
                incomingDamage = 0;
            }
            if (shieldPower > incomingDamage) {
                shieldPower -= incomingDamage;
                gui.displayMessage("You TANKED " + incomingDamage + " damage");
            } else if (incomingDamage > shieldPower) {
                double taken = incomingDamage - shieldPower;
                playerHealth -= taken;
                gui.displayMessage("You took " + taken + " damage");
            }
            gui.updatePlayerHealth(playerHealth);
            gui.highlightMonster(monsters.indexOf(m));
            gui.pause(300);
            gui.highlightMonster(-1);
        }
    }

    //  HELPERS 

    private int countLivingMonsters() {
        int count = 0;
        for (Monster m : monsters)
            if (m.health() > 0)
                count++;
        return count;
    }

    private int countDeadMonsters() {
        int count = 0;
        for (Monster m : monsters)
            if (m.health() <= 0)
                count++;
        return count;
    }

    private ArrayList<Monster> getSpeedyMonsters() {
        ArrayList<Monster> speedsters = new ArrayList<>();
        for (Monster m : monsters)
            if (m.speed() > playerSpeed && m.health() > 0 && m.speed() != 0)
                speedsters.add(m);
        return speedsters;
    }

    private ArrayList<Monster> getFrozenMonsters(){
        ArrayList<Monster> voidsters = new ArrayList<>();
        for(Monster m : monsters){
            if(m.speed() == 0) voidsters.add(m);
        }
        return voidsters;
    }


    private Monster getRandomLivingMonster() {
        ArrayList<Monster> alive = new ArrayList<>();
        for (Monster m : monsters)
            if (m.health() > 0)
                alive.add(m);
        if (alive.isEmpty())
            return null;
        return alive.get((int) (Math.random() * alive.size()));
    }
}
