package com.falafel77.XPVault;

import org.bukkit.entity.Player;

/**
 * Utility class for handling player XP operations using Experience.java
 */
public class XPUtil {

    /**
     * Get the total XP of a player
     */
    public static long getPlayerTotalXP(Player player) {
        return Experience.getExp(player);
    }

    /**
     * Set the total XP of a player
     */
    public static void setPlayerTotalXP(Player player, long totalXP) {
        // Clamp to prevent overflow
        int xpToSet = (int) Math.min(totalXP, Integer.MAX_VALUE);
        // Fix: Cast the long return value to int safely
        long currentXP = Experience.getExp(player);
        int currentXPInt = (int) Math.min(currentXP, Integer.MAX_VALUE);
        Experience.changeExp(player, xpToSet - currentXPInt);
    }

    /**
     * Get the level from XP amount
     */
    public static int getLevelFromXP(long xp) {
        return Experience.getIntLevelFromExp(xp);
    }

    /**
     * Get the level from XP amount (int version)
     */
    public static int getLevelFromXP(int xp) {
        return Experience.getIntLevelFromExp(xp);
    }

    /**
     * Get the total XP required to reach a specific level
     */
    public static long getExpAtLevel(int level) {
        return Experience.getExpFromLevel(level);
    }

    /**
     * Convert levels to XP equivalent
     */
    public static long levelsToXP(int levels) {
        return Experience.getExpFromLevel(levels);
    }

    /**
     * Check if player has enough levels
     */
    public static boolean hasEnoughLevels(Player player, int requiredLevels) {
        return player.getLevel() >= requiredLevels;
    }

    /**
     * Get XP needed for next level
     */
    public static long getExpToNextLevel(Player player) {
        int currentLevel = player.getLevel();
        // Fix: Use long for both values since Experience.getExp returns long
        long currentTotalXP = Experience.getExp(player);
        long nextLevelXP = Experience.getExpFromLevel(currentLevel + 1);
        return nextLevelXP - currentTotalXP;
    }

    /**
     * Get XP progress to next level as percentage
     */
    public static float getExpProgress(Player player) {
        return player.getExp();
    }
}