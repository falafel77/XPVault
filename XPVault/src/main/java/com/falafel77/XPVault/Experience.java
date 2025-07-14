package com.falafel77.XPVault;

import org.bukkit.entity.Player;

/**
 * Core Experience management class with corrected Minecraft XP calculations
 * Based on Minecraft's actual XP system mechanics
 */
public class Experience {
    
    /**
     * Get the total experience points of a player
     * 
     * @param player the player
     * @return total experience points
     */
    public static long getExp(Player player) {
        return Math.round(getExpFromLevel(player.getLevel()) + (player.getExp() * getExpToNext(player.getLevel())));
    }
    
    /**
     * Get experience points required to reach a specific level from level 0
     * Uses Minecraft's official XP formula
     * 
     * @param level the target level
     * @return experience points needed
     */
    public static long getExpFromLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        
        if (level <= 15) {
            return level * level + 6 * level;
        } else if (level <= 30) {
            return (long) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (long) (4.5 * level * level - 162.5 * level + 2220);
        }
    }
    
    /**
     * Get experience points required to advance from current level to next level
     * 
     * @param level current level
     * @return experience points needed for next level
     */
    public static int getExpToNext(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }
    
    /**
     * Get the level equivalent of a specific amount of experience points
     * 
     * @param exp experience points
     * @return level equivalent
     */
    public static int getIntLevelFromExp(long exp) {
        if (exp <= 0) {
            return 0;
        }
        
        // Use binary search for efficiency
        int level = 0;
        while (getExpFromLevel(level + 1) <= exp) {
            level++;
        }
        
        return level;
    }
    
    /**
     * Change a player's experience by a specific amount
     * 
     * @param player the player
     * @param exp experience change (positive to add, negative to subtract)
     */
    public static void changeExp(Player player, int exp) {
        // Get current total experience
        long currentExp = getExp(player);
        
        // Calculate new experience
        long newExp = currentExp + exp;
        
        // Ensure experience doesn't go negative
        if (newExp < 0) {
            newExp = 0;
        }
        
        // Set the new experience
        setExp(player, newExp);
    }
    
    /**
     * Set a player's total experience to a specific amount
     * 
     * @param player the player
     * @param exp total experience to set
     */
    public static void setExp(Player player, long exp) {
        if (exp < 0) {
            exp = 0;
        }
        
        // Calculate the level and progress
        int level = getIntLevelFromExp(exp);
        long expForLevel = getExpFromLevel(level);
        float progress = 0.0f;
        
        // Calculate progress towards next level
        if (level < Integer.MAX_VALUE) {
            long expToNext = getExpToNext(level);
            if (expToNext > 0) {
                progress = (float) (exp - expForLevel) / (float) expToNext;
            }
        }
        
        // Ensure progress is within valid range
        if (progress < 0.0f) {
            progress = 0.0f;
        } else if (progress > 1.0f) {
            progress = 1.0f;
        }
        
        // Set the player's level and experience
        player.setLevel(level);
        player.setExp(progress);
    }
    
    /**
     * Get the level from experience with decimal precision
     * 
     * @param exp experience points
     * @return level with decimal precision
     */
    public static double getLevelFromExp(long exp) {
        int level = getIntLevelFromExp(exp);
        long expForLevel = getExpFromLevel(level);
        long expToNext = getExpToNext(level);
        
        if (expToNext <= 0) {
            return level;
        }
        
        double progress = (double) (exp - expForLevel) / (double) expToNext;
        return level + Math.min(progress, 1.0);
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
}