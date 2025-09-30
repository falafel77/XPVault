package com.falafel77.XPVault;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class XPManager {

    private final XPVault plugin;
    private final SQLiteManager sqLiteManager;

    public XPManager(XPVault plugin, SQLiteManager sqLiteManager) {
        this.plugin = plugin;
        this.sqLiteManager = sqLiteManager;
    }

    public long getPlayerSavedXP(Player player) {
        UUID playerUUID = player.getUniqueId();
        long savedXP = 0L;
        try (Connection conn = sqLiteManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT xp FROM player_xp WHERE uuid = ?")) {
            ps.setString(1, playerUUID.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                savedXP = rs.getLong("xp");
            }
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error getting player XP from SQLite: " + ex.getMessage());
        }
        return savedXP;
    }

    public void setPlayerSavedXP(Player player, long xp) {
        UUID playerUUID = player.getUniqueId();
        try (Connection conn = sqLiteManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO player_xp (uuid, xp) VALUES (?, ?)")) {
            ps.setString(1, playerUUID.toString());
            ps.setLong(2, xp);
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error setting player XP in SQLite: " + ex.getMessage());
        }
    }

    public void addPlayerSavedXP(Player player, long amount) {
        long currentXP = getPlayerSavedXP(player);
        setPlayerSavedXP(player, currentXP + amount);
    }

    public void removePlayerSavedXP(Player player, long amount) {
        long currentXP = getPlayerSavedXP(player);
        setPlayerSavedXP(player, Math.max(0, currentXP - amount));
    }

    public void clearAllSavedXP() {
        try (Connection conn = sqLiteManager.getConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate("DELETE FROM player_xp");
            plugin.getLogger().info("All player XP data cleared from SQLite.");
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error clearing all player XP data from SQLite: " + ex.getMessage());
        }
    }
}
