package com.falafel77.XPVault;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class XPVault extends JavaPlugin implements Listener {

    private XPManager xpManager;
    private SQLiteManager sqLiteManager;
    private WorldManager worldManager;
    private FileConfiguration messagesConfig;
    private String latestVersion = null;
    private boolean updateAvailable = false;

    @Override
    public void onEnable() {
        // Create config.yml if it doesn't exist
        saveDefaultConfig();
        
        createMessagesConfig();
        if (messagesConfig != null) {
            getLogger().info(getMessage("plugin_enabled"));
        } else {
            getLogger().warning("Failed to load messages.yml. Plugin messages may not work correctly.");
        }

        this.sqLiteManager = new SQLiteManager(this);
        this.sqLiteManager.load();
        this.xpManager = new XPManager(this, this.sqLiteManager);
        this.worldManager = new WorldManager(this);
        
        registerCommands();
        registerEvents();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new XPSavedPlaceholder(this).register();
            getLogger().info("PlaceholderAPI hooked successfully!");
        }

        checkForUpdates();
    }

    private void registerCommands() {
        this.getCommand("savexp").setExecutor(new SaveXPCommand(this));
        this.getCommand("savexp").setTabCompleter(new SaveXPTabCompleter());
        
        this.getCommand("givexp").setExecutor(new GiveXPCommand(this));
        this.getCommand("givexp").setTabCompleter(new GiveXPTabCompleter());
        
        this.getCommand("retrievexp").setExecutor(new RetrieveXPCommand(this));
        this.getCommand("retrievexp").setTabCompleter(new RetrieveXPTabCompleter(this));
        
        AdminXPCommand adminXPCommand = new AdminXPCommand(this);
        this.getCommand("adminxp").setExecutor(adminXPCommand);
        this.getCommand("adminxp").setTabCompleter(adminXPCommand);
        
        this.getCommand("checkxp").setExecutor(new CheckXPCommand(xpManager, this));
        this.getCommand("checkxp").setTabCompleter(new CheckXPCompleter());
        
        this.getCommand("xpvaultreload").setExecutor(new ReloadCommand(this));
        this.getCommand("xpvaultreload").setTabCompleter(new ReloadTabCompleter());
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Load player data on join
        xpManager.loadPlayerData(event.getPlayer());
        
        // Send update notification to ops
        if (updateAvailable && event.getPlayer().isOp()) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "XPVault: A new update is available! " +
                "Current version: " + getDescription().getVersion() + 
                ", Latest version: " + latestVersion);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save and unload player data from memory on quit
        xpManager.unloadPlayerData(event.getPlayer());
    }

    private void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            String versionUrl = "https://api.github.com/repos/falafel77/XPVault/releases/latest";
            try {
                java.net.URI uri = java.net.URI.create(versionUrl);
                java.net.URL url = uri.toURL();
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestProperty("User-Agent", "XPVault-Plugin");
                
                int status = conn.getResponseCode();
                if (status == 200) {
                    java.io.InputStream is = conn.getInputStream();
                    java.util.Scanner scanner = new java.util.Scanner(is).useDelimiter("\\A");
                    String response = scanner.hasNext() ? scanner.next() : "";
                    scanner.close();
                    is.close();
                    
                    int tagIndex = response.indexOf("\"tag_name\":");
                    if (tagIndex != -1) {
                        int start = response.indexOf('"', tagIndex + 11) + 1;
                        int end = response.indexOf('"', start);
                        latestVersion = response.substring(start, end);
                        String currentVersion = getDescription().getVersion();
                        
                        if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                            updateAvailable = true;
                            getLogger().warning("A new update is available! Current version: " + 
                                currentVersion + ", Latest version: " + latestVersion);
                        }
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }

    @Override
    public void onDisable() {
        if (messagesConfig != null) {
            getLogger().info(getMessage("plugin_disabled"));
        }
        
        if (sqLiteManager != null) {
            sqLiteManager.close();
        }
    }

    public XPManager getXpManager() {
        return xpManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public void createMessagesConfig() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defaultStream = getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultStream));
            messagesConfig.setDefaults(defaultConfig);
        }
    }

    public String getMessage(String path) {
        String message = messagesConfig.getString(path);
        if (message != null) {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        return ChatColor.RED + "Message not found: " + path;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public SQLiteManager getSqLiteManager() {
        return sqLiteManager;
    }
}