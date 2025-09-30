package com.falafel77.XPVault;

import org.bukkit.entity.Player;

/**
 * A utility for managing player experience with corrected calculations.
 */
public final class ExperienceUtil {

    /**
     * Calculate XP equivalent for a specific number of levels
     * This method calculates the XP needed to reach X levels from level 0
     * 
     * @param levels number of levels
     * @return XP equivalent
     */
    public static long getXPForLevels(int levels) {
        return Experience.getExpFromLevel(levels);
    }

    /**
     * Calculate XP equivalent for advancing a specific number of levels from current level
     * 
     * @param player the player
     * @param levelsToAdvance number of levels to advance
     * @return XP needed to advance those levels
     */
    public static long getXPForLevelAdvancement(Player player, int levelsToAdvance) {
        int currentLevel = player.getLevel();
        long xpForHigherLevel = Experience.getExpFromLevel(currentLevel + levelsToAdvance);
        long xpForCurrentLevel = Experience.getExpFromLevel(currentLevel);
        
        // Add partial XP from current level progress
        long partialXP = Math.round(Experience.getExpToNext(currentLevel) * player.getExp());
        
        return xpForHigherLevel - xpForCurrentLevel + partialXP;
    }

    /**
     * Calculate XP equivalent for going DOWN a specific number of levels from current level
     * 
     * @param player the player
     * @param levelsToDeduct number of levels to deduct
     * @return XP equivalent of those levels
     */
    public static long getXPForLevelDeduction(Player player, int levelsToDeduct) {
        int currentLevel = player.getLevel();
        if (currentLevel < levelsToDeduct) {
            // If trying to deduct more levels than player has, return all current XP
            return Experience.getExp(player);
        }
        
        // Calculate XP for specific levels only
        long xpForCurrentLevel = Experience.getExpFromLevel(currentLevel);
        long xpForLowerLevel = Experience.getExpFromLevel(currentLevel - levelsToDeduct);
        long levelXP = xpForCurrentLevel - xpForLowerLevel;
        
        // Add partial XP from current level progress
        long partialXP = Math.round(Experience.getExpToNext(currentLevel) * player.getExp());
        
        return levelXP + partialXP;
    }

    /**
     * Safely change player's experience
     * 
     * @param player the player
     * @param xpChange the XP change (positive to add, negative to subtract)
     */
    public static void changePlayerXP(Player player, long xpChange) {
        // Clamp to int range for Experience.java compatibility
        int xpChangeInt = (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, xpChange));
        Experience.changeExp(player, xpChangeInt);
    }

    /**
     * Get total XP of a player
     * 
     * @param player the player
     * @return total XP
     */
    public static long getTotalXP(Player player) {
        return Experience.getExp(player);
    }

    /**
     * Check if player has enough levels to deduct
     * 
     * @param player the player
     * @param levelsRequired levels required
     * @return true if player has enough levels
     */
    public static boolean hasEnoughLevels(Player player, int levelsRequired) {
        return player.getLevel() >= levelsRequired;
    }

    /**
     * Calculate exact XP needed for a specific number of levels starting from level 0
     * This is different from getXPForLevelAdvancement as it calculates from scratch
     * 
     * @param levels number of levels
     * @return exact XP needed
     */
    public static long getExactXPForLevels(int levels) {
        return Experience.getExpFromLevel(levels);
    }

    /**
     * Calculate exact level equivalent from XP amount
     * 
     * @param xp XP amount
     * @return level equivalent
     */
    public static int getExactLevelFromXP(long xp) {
        return Experience.getIntLevelFromExp(xp);
    }

    private ExperienceUtil() {}
}