package gui;
import javax.swing.*;

import game.Monster;

import java.awt.*;
import java.util.ArrayList;

/**
 * MonsterDisplayPanel - Displays monster tiles in a responsive grid
 * 
 * This panel automatically arranges monsters in rows and columns,
 * showing their health, damage, and other stats visually.
 */
public class MonsterDisplayPanel extends JPanel {
    
    private ArrayList<Monster> monsters;
    private ArrayList<MonsterTile> tiles;
    private int highlightedIndex = -1;
    
    // Visual constants
    private static final Color BACKGROUND_COLOR = new Color(40, 40, 50);
    private static final int TILE_SPACING = 10;
    
    /**
     * Constructor
     */
    public MonsterDisplayPanel() {
        monsters = new ArrayList<>();
        tiles = new ArrayList<>();
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.CYAN, 2),
            "MONSTERS",
            0, 0, new Font("Arial", Font.BOLD, 16), Color.CYAN));
    }
    
    /**
     * Set the monsters to display
     * @param monsters The list of monsters
     */
    public void setMonsters(ArrayList<Monster> monsters) {
        this.monsters = monsters;
        this.highlightedIndex = -1;
        createTiles();
    }
    
    /**
     * Highlight a specific monster
     * @param index The index to highlight (-1 for none)
     */
    public void highlightMonster(int index) {
        this.highlightedIndex = index;
        repaint();
    }
    
    /**
     * Create tiles for all monsters
     */
    private void createTiles() {
        removeAll();
        tiles.clear();
        
        if (monsters == null || monsters.isEmpty()) {
            add(new JLabel("No monsters to display"));
            revalidate();
            repaint();
            return;
        }
        
        // Determine grid layout
        int monsterCount = monsters.size();
        int cols = (int) Math.ceil(Math.sqrt(monsterCount));
        int rows = (int) Math.ceil((double) monsterCount / cols);
        
        setLayout(new GridLayout(rows, cols, TILE_SPACING, TILE_SPACING));
        
        // Create a tile for each monster
        for (int i = 0; i < monsters.size(); i++) {
            MonsterTile tile = new MonsterTile(monsters.get(i), i);
            tiles.add(tile);
            add(tile);
        }
        
        revalidate();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Update highlighting
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).setHighlighted(i == highlightedIndex);
        }
    }
    
    /**
     * MonsterTile - Individual monster display card
     */
    private class MonsterTile extends JPanel {
        private Monster monster;
        private int index;
        private boolean highlighted = false;
        
        // Colors
        private static final Color TILE_BG = new Color(60, 60, 70);
        private static final Color TILE_BORDER = new Color(100, 100, 120);
        private static final Color HIGHLIGHT_COLOR = new Color(255, 215, 0);
        private static final Color DEAD_COLOR = new Color(80, 20, 20);
        private static final Color HEALTH_BAR_BG = new Color(40, 40, 50);
        private static final Color HEALTH_BAR_FULL = new Color(50, 205, 50);
        private static final Color HEALTH_BAR_MED = new Color(255, 165, 0);
        private static final Color HEALTH_BAR_LOW = new Color(220, 20, 60);
        
        public MonsterTile(Monster monster, int index) {
            this.monster = monster;
            this.index = index;
            setLayout(new BorderLayout(5, 5));
            setBackground(monster.health() > 0 ? TILE_BG : DEAD_COLOR);
            setBorder(BorderFactory.createLineBorder(TILE_BORDER, 2));
            createLayout();
        }
        
        private void createLayout() {
            // Monster info panel
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);
            
            // Monster number/name
            JLabel numberLabel = new JLabel("Monster #" + index);
            numberLabel.setFont(new Font("Arial", Font.BOLD, 14));
            numberLabel.setForeground(Color.WHITE);
            numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Stats
            JLabel healthLabel = new JLabel("HP: " + monster.health());
            healthLabel.setForeground(Color.WHITE);
            healthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel damageLabel = new JLabel("DMG: " + monster.damage());
            damageLabel.setForeground(Color.ORANGE);
            damageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel speedLabel = new JLabel("SPD: " + monster.speed());
            speedLabel.setForeground(Color.CYAN);
            speedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Special ability if exists
            if (!monster.specialType().isEmpty()) {
                JLabel specialLabel = new JLabel("★ " + monster.specialType());
                specialLabel.setForeground(new Color(255, 215, 0));
                specialLabel.setFont(new Font("Arial", Font.ITALIC, 11));
                specialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                infoPanel.add(specialLabel);
            }
            
            infoPanel.add(numberLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(healthLabel);
            infoPanel.add(damageLabel);
            infoPanel.add(speedLabel);
            
            add(infoPanel, BorderLayout.CENTER);
            
            // Health bar at bottom
            add(new HealthBar(), BorderLayout.SOUTH);
        }
        
        public void setHighlighted(boolean highlighted) {
            if (this.highlighted != highlighted) {
                this.highlighted = highlighted;
                setBorder(BorderFactory.createLineBorder(
                    highlighted ? HIGHLIGHT_COLOR : TILE_BORDER, 
                    highlighted ? 4 : 2));
                repaint();
            }
        }
        
        /**
         * Visual health bar
         */
        private class HealthBar extends JPanel {
            private static final int BAR_HEIGHT = 20;
            
            public HealthBar() {
                setPreferredSize(new Dimension(0, BAR_HEIGHT));
                setBackground(HEALTH_BAR_BG);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                int width = getWidth();
                int height = getHeight();
                
                // Calculate health percentage (assume max 100)
                double healthPercent = Math.max(0, Math.min(100, monster.health())) / 100.0;
                int barWidth = (int) (width * healthPercent);
                
                // Choose color based on health
                Color barColor;
                if (healthPercent > 0.5) {
                    barColor = HEALTH_BAR_FULL;
                } else if (healthPercent > 0.25) {
                    barColor = HEALTH_BAR_MED;
                } else {
                    barColor = HEALTH_BAR_LOW;
                }
                
                // Draw health bar
                g.setColor(barColor);
                g.fillRect(0, 0, barWidth, height);
                
                // Draw border
                g.setColor(Color.WHITE);
                g.drawRect(0, 0, width - 1, height - 1);
            }
        }
    }
}