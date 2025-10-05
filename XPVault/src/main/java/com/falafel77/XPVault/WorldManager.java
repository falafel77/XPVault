package com.falafel77.XPVault;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Manages world restrictions for XPVault
 */
public class WorldManager {
    
    private final XPVault plugin;
    private List<String> enabledWorlds;
    
    public WorldManager(XPVault plugin) {
        this.plugin = plugin;
        loadEnabledWorlds();
    }
    
    /**
     * Load enabled worlds from config
     */
    public void loadEnabledWorlds() {
        this.enabledWorlds = plugin.getConfig().getStringList("enabled-worlds");
        
        if (enabledWorlds.isEmpty()) {
            plugin.getLogger().warning("No worlds configured in enabled-worlds list. XPVault will be disabled in all worlds!");
        } else {
            plugin.getLogger().info("XPVault enabled in worlds: " + String.join(", ", enabledWorlds));
        }
    }
    
    /**
     * Check if XPVault is enabled in the player's current world
     * 
     * @param player the player to check
     * @return true if enabled in player's world
     */
    public boolean isEnabledInWorld(Player player) {
        if (enabledWorlds.isEmpty()) {
            return false;
        }
        
        World world = player.getWorld();
        return enabledWorlds.contains(world.getName());
    }
    
    /**
     * Check if XPVault is enabled in a specific world
     * 
     * @param worldName the world name to check
     * @return true if enabled in that world
     */
    public boolean isEnabledInWorld(String worldName) {
        if (enabledWorlds.isEmpty()) {
            return false;
        }
        
        return enabledWorlds.contains(worldName);
    }
    
    /**
     * Get list of enabled worlds
     * 
     * @return list of enabled world names
     */
    public List<String> getEnabledWorlds() {
        return enabledWorlds;
    }
    
    /**
     * Reload enabled worlds from config
     */
    public void reload() {
        loadEnabledWorlds();
    }
}