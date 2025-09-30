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

public class XPVault extends JavaPlugin {

    private XPManager xpManager;
    private SQLiteManager sqLiteManager;
    private FileConfiguration messagesConfig;
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

        this.sqLiteManager = new SQLiteManager(this);
        this.sqLiteManager.load();
        this.xpManager = new XPManager(this, this.sqLiteManager);
        
        // Register commands
        registerCommands();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new XPSavedPlaceholder(this).register();
        }

        // تحقق من التحديثات
        checkForUpdates();
        // إذا يوجد تحديث، أرسل رسالة للأوب عند دخولهم
        if (updateAvailable) {
            Bukkit.getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
                @org.bukkit.event.EventHandler
                public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) {
                    if (event.getPlayer().isOp()) {
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "XPVault: يوجد تحديث جديد للإضافة! الإصدار الحالي: " + getDescription().getVersion() + ", آخر إصدار: " + latestVersion);
                    }
                }
            }, this);
        }
    }
    // دالة التحقق من التحديثات
    private void checkForUpdates() {
        // مثال: تحقق من آخر إصدار عبر GitHub Releases (عدل الرابط حسب مستودعك)
        String versionUrl = "https://api.github.com/repos/falafel77/XPVault/releases/latest";
        try {
            java.net.URI uri = java.net.URI.create(versionUrl);
            java.net.URL url = uri.toURL();
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            int status = conn.getResponseCode();
            if (status == 200) {
                java.io.InputStream is = conn.getInputStream();
                java.util.Scanner scanner = new java.util.Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();
                is.close();
                // استخراج رقم الإصدار من JSON
                int tagIndex = response.indexOf("\"tag_name\":");
                if (tagIndex != -1) {
                    int start = response.indexOf('"', tagIndex + 11) + 1;
                    int end = response.indexOf('"', start);
                    latestVersion = response.substring(start, end);
                    String currentVersion = getDescription().getVersion();
                    if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                        updateAvailable = true;
                        getLogger().warning("يوجد تحديث جديد للإضافة! الإصدار الحالي: " + currentVersion + ", آخر إصدار: " + latestVersion);
                    }
                }
            }
        } catch (Exception e) {
            getLogger().info("تعذر التحقق من التحديثات: " + e.getMessage());
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
    this.getCommand("checkxp").setTabCompleter(new CheckXPCompleter());
        
        // Register reload command
    this.getCommand("xpvaultreload").setExecutor(new ReloadCommand(this));
    this.getCommand("xpvaultreload").setTabCompleter(new ReloadTabCompleter());
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
