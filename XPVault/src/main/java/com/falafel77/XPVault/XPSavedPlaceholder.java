package com.falafel77.XPVault;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class XPSavedPlaceholder extends PlaceholderExpansion {

    private final XPVault plugin;

    public XPSavedPlaceholder(XPVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "xpvault";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("saved_xp")) {
            return String.valueOf(plugin.getXpManager().getPlayerSavedXP(player));
        }
        
        if (identifier.equals("savedlevels")) {
            long savedXP = plugin.getXpManager().getPlayerSavedXP(player); // Changed to long
            int savedLevels = Experience.getIntLevelFromExp(savedXP);
            return String.valueOf(savedLevels);
        }
        
        if (identifier.equals("currentxp")) {
            return String.valueOf(Experience.getExp(player));
        }
        
        if (identifier.equals("currentlevels")) {
            return String.valueOf(player.getLevel());
        }
        
        return null;
    }
}

