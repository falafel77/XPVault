package com.falafel77.XPVault;

import org.bukkit.entity.Player;

/**
 * A utility for managing player experience with corrected calculations.
 */
public final class ExperienceUtil {

    /**
     * Calculate XP equivalent for a specific number of levels FROM LEVEL 0
     * This is used when saving/giving levels as XP
     * 
     * @param levels number of levels
     * @return XP equivalent
     */
    public static long getXPForLevels(int levels) {
        return Experience.getExpFromLevel(levels);
    }

    /**
     * Calculate XP equivalent for advancing a specific number of levels from current level
     * This is used when retrieving levels from vault
     * 
     * @param player the player
     * @param levelsToAdvance number of levels to advance
     * @return XP needed to advance those levels
     */
    public static long getXPForLevelAdvancement(Player player, int levelsToAdvance) {
        int currentLevel = player.getLevel();
        int targetLevel = currentLevel + levelsToAdvance;
        
        // Calculate XP for target level
        long xpForTargetLevel = Experience.getExpFromLevel(targetLevel);
        
        // Calculate current total XP
        long currentTotalXP = Experience.getExp(player);
        
        return xpForTargetLevel - currentTotalXP;
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
        
        int targetLevel = currentLevel - levelsToDeduct;
        
        // Calculate current total XP
        long currentTotalXP = Experience.getExp(player);
        
        // Calculate XP at target level
        long xpForTargetLevel = Experience.getExpFromLevel(targetLevel);
        
        return currentTotalXP - xpForTargetLevel;
    }

    /**
     * Safely change player's experience
     * 
     * @param player the player
     * @param xpChange the XP change (positive to add, negative to subtract)
     */
    public static void changePlayerXP(Player player, long xpChange) {
        Experience.changeExp(player, xpChange);
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
    
    /**
     * Check if player has enough XP
     * 
     * @param player the player
     * @param requiredXP required XP amount
     * @return true if player has enough XP
     */
    public static boolean hasEnoughXP(Player player, long requiredXP) {
        return Experience.hasEnoughExp(player, requiredXP);
    }
    
    /**
     * Convert levels to XP in an ABSOLUTE way (from level 0)
     * This ensures consistent XP values regardless of player's current level
     * 
     * @param levels number of levels
     * @return XP equivalent from level 0
     */
    public static long levelsToAbsoluteXP(int levels) {
        return Experience.getExpFromLevel(levels);
    }
    
    /**
     * Convert XP to level equivalent in an ABSOLUTE way (from level 0)
     * This shows how many levels this XP represents starting from 0
     * 
     * @param xp XP amount
     * @return level equivalent
     */
    public static int absoluteXPToLevels(long xp) {
        return Experience.getIntLevelFromExp(xp);
    }

    private ExperienceUtil() {}
}