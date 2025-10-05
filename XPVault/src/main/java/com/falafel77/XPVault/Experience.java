package com.falafel77.XPVault;

import org.bukkit.entity.Player;

/**
 * Core Experience management class with corrected Minecraft XP calculations
 * Based on PlanarWrappers implementation
 */
public final class Experience {
    
    /**
     * Calculate a player's total experience based on level and progress to next.
     * 
     * @param player the Player
     * @return the amount of experience the Player has
     */
    public static long getExp(Player player) {
        return getExpFromLevel(player.getLevel())
            + Math.round(getExpToNext(player.getLevel()) * player.getExp());
    }
    
    /**
     * Calculate total experience based on level.
     * 
     * @param level the level
     * @return the total experience calculated
     */
    public static long getExpFromLevel(int level) {
        if (level > 30) {
            return (long) (4.5 * level * level - 162.5 * level + 2220);
        }
        if (level > 15) {
            return (long) (2.5 * level * level - 40.5 * level + 360);
        }
        return (long) level * level + 6 * level;
    }
    
    /**
     * Calculate level (including progress to next level) based on total experience.
     * 
     * @param exp the total experience
     * @return the level calculated
     */
    public static double getLevelFromExp(long exp) {
        int level = getIntLevelFromExp(exp);
        
        // Get remaining exp progressing towards next level
        float remainder = exp - (float) getExpFromLevel(level);
        
        // Get level progress with float precision
        float progress = remainder / getExpToNext(level);
        
        return ((double) level) + progress;
    }
    
    /**
     * Calculate level based on total experience.
     * 
     * @param exp the total experience
     * @return the level calculated
     */
    public static int getIntLevelFromExp(long exp) {
        if (exp > 1395) {
            return (int) ((Math.sqrt(72 * exp - 54215D) + 325) / 18);
        }
        if (exp > 315) {
            return (int) (Math.sqrt(40 * exp - 7839D) / 10 + 8.1);
        }
        if (exp > 0) {
            return (int) (Math.sqrt(exp + 9D) - 3);
        }
        return 0;
    }
    
    /**
     * Get the total amount of experience required to progress to the next level.
     * 
     * @param level the current level
     * @return experience needed for next level
     */
    public static int getExpToNext(int level) {
        if (level >= 30) {
            return level * 9 - 158;
        }
        if (level >= 15) {
            return level * 5 - 38;
        }
        return level * 2 + 7;
    }
    
    /**
     * Change a Player's experience.
     * 
     * @param player the Player affected
     * @param exp the amount of experience to add or remove
     */
    public static void changeExp(Player player, long exp) {
        long totalExp = getExp(player) + exp;
        
        if (totalExp < 0) {
            totalExp = 0;
        }
        
        double levelAndExp = getLevelFromExp(totalExp);
        int level = (int) levelAndExp;
        player.setLevel(level);
        player.setExp((float) (levelAndExp - level));
    }
    
    /**
     * Check if a player has enough experience
     * 
     * @param player the player
     * @param requiredExp required experience
     * @return true if player has enough experience
     */
    public static boolean hasEnoughExp(Player player, long requiredExp) {
        return getExp(player) >= requiredExp;
    }
    
    /**
     * Get remaining experience needed to reach next level
     * 
     * @param player the player
     * @return remaining experience for next level
     */
    public static long getExpToNextLevel(Player player) {
        int currentLevel = player.getLevel();
        long currentExp = getExp(player);
        long expForNextLevel = getExpFromLevel(currentLevel + 1);
        
        return expForNextLevel - currentExp;
    }
    
    /**
     * Calculate experience needed to reach a specific level from current level
     * 
     * @param player the player
     * @param targetLevel target level
     * @return experience needed
     */
    public static long getExpNeededForLevel(Player player, int targetLevel) {
        if (targetLevel <= player.getLevel()) {
            return 0;
        }
        
        long currentExp = getExp(player);
        long targetExp = getExpFromLevel(targetLevel);
        
        return targetExp - currentExp;
    }
    
    private Experience() {}
}

