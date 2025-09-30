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
        return "XPVault"; // Replace with your name or plugin author
    }

    @Override
    public String getVersion() {
        return "1.3"; // Match your plugin version
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
            // %xpvault_saved_xp%
            if (identifier.equalsIgnoreCase("saved_xp")) {
                return String.valueOf(plugin.getXpManager().getPlayerSavedXP(player));
            }
            // %xpvault_savedlevels%
            if (identifier.equalsIgnoreCase("savedlevels")) {
                long savedXP = plugin.getXpManager().getPlayerSavedXP(player);
                int levels = Experience.getIntLevelFromExp(savedXP);
                return String.valueOf(levels);
            }
            // %xpvault_currentxp%
            if (identifier.equalsIgnoreCase("currentxp")) {
                long currentXP = Experience.getExp(player);
                return String.valueOf(currentXP);
            }
            // %xpvault_currentlevels%
            if (identifier.equalsIgnoreCase("currentlevels")) {
                long currentXP = Experience.getExp(player);
                int levels = Experience.getIntLevelFromExp(currentXP);
                return String.valueOf(levels);
            }
            return null;
    }
}


