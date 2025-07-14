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

        if (identifier.equals("saved_xp")) {
            return String.valueOf(plugin.getXpManager().getPlayerSavedXP(player));
        }

        return null;
    }
}


