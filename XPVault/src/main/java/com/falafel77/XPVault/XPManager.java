package com.falafel77.XPVault;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class XPManager {

    private final XPVault plugin;
    private File customConfigFile;
    private FileConfiguration customConfig;

    public XPManager(XPVault plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        customConfigFile = new File(plugin.getDataFolder(), "playerdata.yml");

        if (!customConfigFile.exists()) {
            try {
                customConfigFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml!");
            }
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public FileConfiguration getCustomConfig() {
        return customConfig;
    }

    public void saveCustomConfig() {
        try {
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml!");
        }
    }

    public void reloadCustomConfig() {
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public long getPlayerSavedXP(Player player) {
        UUID playerUUID = player.getUniqueId();
        // Ensure the config is reloaded before getting the latest data
        reloadCustomConfig(); 
        return customConfig.getLong(playerUUID.toString() + ".xp", 0L);
    }
}


