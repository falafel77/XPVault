package com.falafel77.XPVault;

import com.falafel77.XPVault.SaveXPCommand;
import com.falafel77.XPVault.SaveXPTabCompleter;
import com.falafel77.XPVault.GiveXPCommand;
import com.falafel77.XPVault.GiveXPTabCompleter;
import com.falafel77.XPVault.RetrieveXPCommand;
import com.falafel77.XPVault.RetrieveXPTabCompleter;
import com.falafel77.XPVault.AdminXPCommand;
import com.falafel77.xpvault.api.XPVaultAPIProvider;
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

public class XPVault extends JavaPlugin {

    private XPManager xpManager;
    private FileConfiguration messagesConfig;
    private XPVaultAPIImpl apiImpl;

    @Override
    public void onEnable() {
        // Plugin startup logic
        createMessagesConfig();
        if (messagesConfig != null) {
            getLogger().info(getMessage("plugin_enabled"));
        } else {
            getLogger().warning("Failed to load messages.yml. Plugin messages may not work correctly.");
        }
        this.xpManager = new XPManager(this);
        
        // تهيئة XPVault API
        this.apiImpl = new XPVaultAPIImpl(this);
        XPVaultAPIProvider.setAPI(apiImpl);
        getLogger().info("XPVault API has been initialized successfully!");
        
        // Register commands
        registerCommands();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
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
    }

    @Override
    public void onDisable() {
        // إيقاف XPVault API
        if (apiImpl != null) {
            apiImpl.shutdown();
        }
        XPVaultAPIProvider.setAPI(null);
        getLogger().info("XPVault API has been shutdown!");
        
        // Plugin shutdown logic
        unregisterCommands();
        if (messagesConfig != null) {
            getLogger().info(getMessage("plugin_disabled"));
        } else {
            getLogger().warning("messages.yml was not loaded, cannot display plugin_disabled message.");
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

    private void createMessagesConfig() {
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
}
