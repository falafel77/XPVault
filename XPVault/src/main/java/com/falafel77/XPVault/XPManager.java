package com.falafel77.XPVault;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class XPManager {

    private final XPVault plugin;
    private final SQLiteManager sqLiteManager;
    private final Map<UUID, Long> xpCache = new HashMap<>(); // Changed to Long

    public XPManager(XPVault plugin, SQLiteManager sqLiteManager) {
        this.plugin = plugin;
        this.sqLiteManager = sqLiteManager;
    }

    /**
     * Get player's saved XP synchronously (uses cache)
     */
    public long getPlayerSavedXP(Player player) { // Changed return type to long
        UUID playerUUID = player.getUniqueId();
        
        if (xpCache.containsKey(playerUUID)) {
            return xpCache.get(playerUUID);
        }
        
        long savedXP = 0; // Changed to long
        try (Connection conn = sqLiteManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT xp FROM player_xp WHERE uuid = ?")) {
            ps.setString(1, playerUUID.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                savedXP = rs.getLong("xp"); // Changed to getLong
            }
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error getting player XP from SQLite: " + ex.getMessage());
        }
        
        xpCache.put(playerUUID, savedXP);
        return savedXP;
    }

    /**
     * Get player's saved XP asynchronously
     */
    public CompletableFuture<Long> getPlayerSavedXPAsync(Player player) { // Changed return type to Long
        UUID playerUUID = player.getUniqueId();
        
        if (xpCache.containsKey(playerUUID)) {
            return CompletableFuture.completedFuture(xpCache.get(playerUUID));
        }
        
        return CompletableFuture.supplyAsync(() -> {
            long savedXP = 0; // Changed to long
            try (Connection conn = sqLiteManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT xp FROM player_xp WHERE uuid = ?")) {
                ps.setString(1, playerUUID.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    savedXP = rs.getLong("xp"); // Changed to getLong
                }
            } catch (SQLException ex) {
                plugin.getLogger().severe("Error getting player XP from SQLite: " + ex.getMessage());
            }
            
            final long finalSavedXP = savedXP; // Changed to long
            Bukkit.getScheduler().runTask(plugin, () -> xpCache.put(playerUUID, finalSavedXP));
            
            return savedXP;
        });
    }

    /**
     * Set player's saved XP synchronously
     */
    public void setPlayerSavedXP(Player player, long xp) { // Changed parameter type to long
        if (xp < 0) {
            xp = 0;
        }
        
        UUID playerUUID = player.getUniqueId();
        final long finalXP = xp; // Changed to long
        
        xpCache.put(playerUUID, finalXP);
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = sqLiteManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT OR REPLACE INTO player_xp (uuid, xp) VALUES (?, ?)")) {
                ps.setString(1, playerUUID.toString());
                ps.setLong(2, finalXP); // Changed to setLong
                ps.executeUpdate();
            } catch (SQLException ex) {
                plugin.getLogger().severe("Error setting player XP in SQLite: " + ex.getMessage());
            }
        });
    }

    /**
     * Add XP to player's saved XP
     */
    public void addPlayerSavedXP(Player player, long amount) { // Changed parameter type to long
        if (amount <= 0) {
            return;
        }
        
        long currentXP = getPlayerSavedXP(player); // Changed to long
        
        long newXP; // Changed to long
        if (currentXP > Long.MAX_VALUE - amount) { // Check for long overflow
            newXP = Long.MAX_VALUE;
            plugin.getLogger().warning("XP overflow prevented for player " + player.getName());
        } else {
            newXP = currentXP + amount;
        }
        
        setPlayerSavedXP(player, newXP);
    }

    /**
     * Remove XP from player's saved XP
     */
    public void removePlayerSavedXP(Player player, long amount) { // Changed parameter type to long
        if (amount <= 0) {
            return;
        }
        
        long currentXP = getPlayerSavedXP(player); // Changed to long
        long newXP = Math.max(0L, currentXP - amount); // Changed to long
        setPlayerSavedXP(player, newXP);
    }

    /**
     * Clear all saved XP data
     */
    public void clearAllSavedXP() {
        xpCache.clear();
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = sqLiteManager.getConnection();
                 Statement statement = conn.createStatement()) {
                statement.executeUpdate("DELETE FROM player_xp");
                plugin.getLogger().info("All player XP data cleared from SQLite.");
            } catch (SQLException ex) {
                plugin.getLogger().severe("Error clearing all player XP data from SQLite: " + ex.getMessage());
            }
        });
    }
    
    /**
     * Load player data into cache when they join
     */
    public void loadPlayerData(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            long savedXP = 0; // Changed to long
            try (Connection conn = sqLiteManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT xp FROM player_xp WHERE uuid = ?")) {
                ps.setString(1, playerUUID.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    savedXP = rs.getLong("xp"); // Changed to getLong
                }
            } catch (SQLException ex) {
                plugin.getLogger().severe("Error loading player data: " + ex.getMessage());
            }
            
            final long finalSavedXP = savedXP; // Changed to long
            Bukkit.getScheduler().runTask(plugin, () -> xpCache.put(playerUUID, finalSavedXP));
        });
    }
    
    /**
     * Save and remove player data from cache when they leave
     */
    public void unloadPlayerData(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        if (!xpCache.containsKey(playerUUID)) {
            return;
        }
        
        final long xp = xpCache.get(playerUUID); // Changed to long
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = sqLiteManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT OR REPLACE INTO player_xp (uuid, xp) VALUES (?, ?)")) {
                ps.setString(1, playerUUID.toString());
                ps.setLong(2, xp); // Changed to setLong
                ps.executeUpdate();
            } catch (SQLException ex) {
                plugin.getLogger().severe("Error saving player data on quit: " + ex.getMessage());
            }
        });
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> xpCache.remove(playerUUID), 20L);
    }
    
    /**
     * Clear cache entry for a specific player
     */
    public void clearCache(UUID playerUUID) {
        xpCache.remove(playerUUID);
    }
    
    /**
     * Clear entire cache
     */
    public void clearAllCache() {
        xpCache.clear();
    }
}

