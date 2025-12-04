package gui;
import javax.swing.*;

import game.Item;

import java.awt.*;
import java.util.ArrayList;

/**
 * InventoryPanel - Displays player's consumable items
 * 
 * Shows a vertical list of Item objects with their custom icons
 */
public class InventoryPanel extends JPanel {
    
    private ArrayList<Item> items;
    private JPanel itemListPanel;
    
    // Visual constants
    private static final Color PANEL_BG = new Color(50, 50, 60);
    private static final Color ITEM_BG = new Color(70, 70, 80);
    private static final Font ITEM_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final int PANEL_WIDTH = 200;
    
    /**
     * Constructor
     */
    public InventoryPanel() {
        items = new ArrayList<>();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(PANEL_WIDTH, 0));
        setBackground(PANEL_BG);
        
        // Title
        JLabel titleLabel = new JLabel("SPECIAL PROGRESS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        add(titleLabel, BorderLayout.NORTH);
        
        // Scrollable item list
        itemListPanel = new JPanel();
        itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));
        itemListPanel.setBackground(PANEL_BG);
        
        JScrollPane scrollPane = new JScrollPane(itemListPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(PANEL_BG);
        scrollPane.getViewport().setBackground(PANEL_BG);
        add(scrollPane, BorderLayout.CENTER);
        
        // Initial empty message
        showEmptyMessage();
    }
    
    /**
     * Set the items to display
     * @param items List of Item objects
     */
    public void setItems(ArrayList<Item> items) {
        this.items = items;
        refreshDisplay();
    }
    
    /**
     * Refresh the item display
     */
    private void refreshDisplay() {
        itemListPanel.removeAll();
        
        if (items == null || items.isEmpty()) {
            showEmptyMessage();
        } else {
            // Create a panel for each item (no grouping/stacking)
            for (int i = 0; i < items.size(); i++) {
                itemListPanel.add(createItemPanel(items.get(i), i));
                itemListPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        itemListPanel.revalidate();
        itemListPanel.repaint();
    }
    
    /**
     * Show empty inventory message
     */
    private void showEmptyMessage() {
        JLabel emptyLabel = new JLabel("Start Game to see progess.", SwingConstants.CENTER);
        emptyLabel.setFont(ITEM_FONT);
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        itemListPanel.add(Box.createVerticalGlue());
        itemListPanel.add(emptyLabel);
        itemListPanel.add(Box.createVerticalGlue());
    }
    
    /**
     * Create a panel for a single item
     * @param item The Item object
     * @param index The item index in the inventory
     * @return The item panel
     */
    private JPanel createItemPanel(Item item, int index) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(ITEM_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.setMaximumSize(new Dimension(PANEL_WIDTH - 20, 50));
        
        // Item icon from the Item object
        JLabel iconLabel = new JLabel(item.getIcon());
        iconLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        panel.add(iconLabel, BorderLayout.WEST);
        
        // Item name
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(ITEM_FONT);
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel, BorderLayout.CENTER);
        
        // Item index/slot number
        JLabel indexLabel = new JLabel("⭐");
        indexLabel.setFont(new Font("Arial", Font.BOLD, 12));
        indexLabel.setForeground(Color.CYAN);
        panel.add(indexLabel, BorderLayout.EAST);
        
        return panel;
    }
}