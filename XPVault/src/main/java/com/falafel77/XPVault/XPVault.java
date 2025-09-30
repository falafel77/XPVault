package com.falafel77.XPVault;

import com.falafel77.XPVault.SaveXPCommand;
import com.falafel77.XPVault.SaveXPTabCompleter;
import com.falafel77.XPVault.GiveXPCommand;
import com.falafel77.XPVault.GiveXPTabCompleter;
import com.falafel77.XPVault.RetrieveXPCommand;
import com.falafel77.XPVault.RetrieveXPTabCompleter;
import com.falafel77.XPVault.AdminXPCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.List;

public class XPVault extends JavaPlugin {

    private XPManager xpManager;
    private SQLiteManager sqLiteManager;
    private FileConfiguration messagesConfig;
    private FileConfiguration config;
    private List<String> allowedWorlds;
    private boolean cooldownEnabled;
    private int cooldownSeconds;
    private Map<String, Boolean> commandCooldowns;

    private String latestVersion = null;
    private boolean updateAvailable = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        createMessagesConfig();
        if (messagesConfig != null) {
            getLogger().info(getMessage("plugin_enabled"));
        } else {
            getLogger().warning("Failed to load messages.yml. Plugin messages may not work correctly.");
        }

        // Check for updates
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                java.net.URL url = new java.net.URL("https://api.github.com/repos/falafel77/XPVault/releases/latest");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                int status = conn.getResponseCode();
                if (status == 200) {
                    java.io.InputStream is = conn.getInputStream();
                    java.util.Scanner scanner = new java.util.Scanner(is).useDelimiter("\\A");
                    String json = scanner.hasNext() ? scanner.next() : "";
                    scanner.close();
                    is.close();
                    String ver = json.replaceAll(".*\"tag_name\":\\s*\"([^\"]+)\".*", "$1");
                    if (!ver.isEmpty() && !ver.equalsIgnoreCase(getDescription().getVersion())) {
                        latestVersion = ver;
                        updateAvailable = true;
                        getLogger().info("A new version of XPVault is available: " + latestVersion + " (current: " + getDescription().getVersion() + ")");
                        getLogger().info("Download: https://github.com/falafel77/XPVault/releases/latest");
                    } else {
                        getLogger().info("You are using the latest version of XPVault.");
                    }
                } else {
                    getLogger().info("Could not check for updates (GitHub API error).");
                }
            } catch (Exception ex) {
                getLogger().info("Could not check for updates: " + ex.getMessage());
            }
        });

        // Send update message to OPs when they join
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) {
                Bukkit.getScheduler().runTaskLater(XPVault.this, () -> {
                    if (updateAvailable && event.getPlayer().isOp()) {
                        event.getPlayer().sendMessage(ChatColor.RED + "[XPVault] Update available! Latest version: " + latestVersion + " | Your version: " + getDescription().getVersion());
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Download: https://github.com/falafel77/XPVault/releases/latest");
                    }
                }, 40L); // 2 seconds after join
            }
        }, this);

        // Load config.yml
        saveDefaultConfig();
        config = getConfig();
        allowedWorlds = config.getStringList("allowed-worlds");
        cooldownEnabled = config.getBoolean("cooldown.enabled", true);
        cooldownSeconds = config.getInt("cooldown.seconds", 15);
        commandCooldowns = new java.util.HashMap<>();
        if (config.isConfigurationSection("cooldown.commands")) {
            for (String cmd : config.getConfigurationSection("cooldown.commands").getKeys(false)) {
                commandCooldowns.put(cmd, config.getBoolean("cooldown.commands." + cmd, true));
            }
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().info("PlaceholderAPI is not installed. If you want variable support, install it manually. The plugin will work normally without it.");
        }

        this.sqLiteManager = new SQLiteManager(this);
        this.sqLiteManager.load();
        this.xpManager = new XPManager(this, this.sqLiteManager);

        // Register commands
        registerCommands();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new XPSavedPlaceholder(this).register();
        }
    }

    private void registerCommands() {
        // تسجيل أوامر XP العادية
        this.getCommand("savexp").setExecutor(new SaveXPCommand(this));
        this.getCommand("savexp").setTabCompleter(new SaveXPTabCompleter());
        
        this.getCommand("givexp").setExecutor(new GiveXPCommand(this));
        this.getCommand("givexp").setTabCompleter(new GiveXPTabCompleter());
        
        this.getCommand("retrievexp").setExecutor(new RetrieveXPCommand(this));
        this.getCommand("retrievexp").setTabCompleter(new RetrieveXPTabCompleter(this));
        
        // تسجيل أمر adminxp مع Tab completion
        AdminXPCommand adminXPCommand = new AdminXPCommand(this);
        this.getCommand("adminxp").setExecutor(adminXPCommand);
        this.getCommand("adminxp").setTabCompleter(adminXPCommand);
        
        // تسجيل أمر checkxp
        this.getCommand("checkxp").setExecutor(new CheckXPCommand(xpManager, this));
        
        // Register reload command
        this.getCommand("xpvaultreload").setExecutor(new ReloadCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        unregisterCommands();
        if (messagesConfig != null) {
            getLogger().info(getMessage("plugin_disabled"));
        } else {
            getLogger().warning("messages.yml was not loaded, cannot display plugin_disabled message.");
        }
        if (sqLiteManager != null) {
            sqLiteManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    private void unregisterCommands() {
        // Unregister commands to prevent issues on reload
        try {
            Object pluginManager = Bukkit.getPluginManager();
            Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            org.bukkit.command.CommandMap commandMap = (org.bukkit.command.CommandMap) commandMapField.get(pluginManager);

            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>) knownCommandsField.get(commandMap);

            knownCommands.remove("savexp");
            knownCommands.remove("givexp");
            knownCommands.remove("retrievexp");
            knownCommands.remove("adminxp");
            knownCommands.remove("checkxp");
            knownCommands.remove("sxp");
            knownCommands.remove("gxp");
            knownCommands.remove("rxp");
            knownCommands.remove("cxp");
            knownCommands.remove("xpvaultreload");

            // Remove aliases as well
            Field aliasesField = commandMap.getClass().getDeclaredField("aliases");
            aliasesField.setAccessible(true);
            Map<String, org.bukkit.command.Command> aliases = (Map<String, org.bukkit.command.Command>) aliasesField.get(commandMap);
            aliases.remove("sxp");
            aliases.remove("gxp");
            aliases.remove("rxp");
            aliases.remove("cxp");

        } catch (Exception e) {
            getLogger().warning("Failed to unregister commands: " + e.getMessage());
        }
    }

    public XPManager getXpManager() {
        return xpManager;
    }

    public List<String> getAllowedWorlds() {
        return allowedWorlds;
    }

    public boolean isCooldownEnabled() {
        return cooldownEnabled;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public boolean isCommandCooldownEnabled(String commandClassName) {
        return commandCooldowns.getOrDefault(commandClassName, true);
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
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            messagesConfig.setDefaults(defaultConfig);
        }
    }

    public String getMessage(String path) {
        String message = messagesConfig.getString(path);
        if (message != null) {
                        return ChatColor.translateAlternateColorCodes('&', message);
        }
        return null;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public SQLiteManager getSqLiteManager() {
        return sqLiteManager;
    }
}
