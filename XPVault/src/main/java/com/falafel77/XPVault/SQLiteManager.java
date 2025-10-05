package com.falafel77.XPVault;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class SQLiteManager {

    private final XPVault plugin;
    private Connection connection;
    private String databasePath;

    public SQLiteManager(XPVault plugin) {
        this.plugin = plugin;
        this.databasePath = plugin.getDataFolder().getAbsolutePath() + File.separator + "xpvault.db";
    }

    public Connection getConnection() {
        try {
            // التحقق من صلاحية الاتصال
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            // إعادة الاتصال إذا كان مغلق (بدون رسائل إضافية)
            reconnect();
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error checking connection: " + ex.getMessage());
            reconnect();
        }
        return connection;
    }

    public void load() {
        try {
            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }
                
                File dataFolder = new File(plugin.getDataFolder(), "xpvault.db");
                if (!dataFolder.exists()) {
                    plugin.getDataFolder().mkdirs();
                    dataFolder.createNewFile();
                }
                
                Class.forName("org.sqlite.JDBC");
                this.connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
                
                // تحسين أداء SQLite
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA journal_mode=WAL");
                    stmt.execute("PRAGMA synchronous=NORMAL");
                    stmt.execute("PRAGMA temp_store=MEMORY");
                    stmt.execute("PRAGMA mmap_size=30000000000");
                }
                
                plugin.getLogger().info("SQLite connection established.");
            }
        } catch (SQLException ex) {
            plugin.getLogger().severe("SQLite connection error: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().severe("SQLite JDBC driver not found: " + ex.getMessage());
        } catch (Exception ex) {
            plugin.getLogger().severe("Unknown error establishing SQLite connection: " + ex.getMessage());
        }
        initializeDatabase();
    }

    /**
     * إعادة الاتصال بدون طباعة رسائل (للاستخدام الداخلي)
     */
    private void reconnect() {
        try {
            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }
                
                Class.forName("org.sqlite.JDBC");
                this.connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
                
                // تحسين أداء SQLite
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA journal_mode=WAL");
                    stmt.execute("PRAGMA synchronous=NORMAL");
                    stmt.execute("PRAGMA temp_store=MEMORY");
                    stmt.execute("PRAGMA mmap_size=30000000000");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().severe("SQLite reconnection error: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().severe("SQLite JDBC driver not found: " + ex.getMessage());
        } catch (Exception ex) {
            plugin.getLogger().severe("Unknown error reconnecting SQLite: " + ex.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("SQLite connection closed.");
            }
        } catch (SQLException ex) {
            plugin.getLogger().severe("SQLite close error: " + ex.getMessage());
        }
    }

    private void initializeDatabase() {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS player_xp (";
            sql += "uuid VARCHAR(36) PRIMARY KEY,";
            sql += "xp INTEGER NOT NULL DEFAULT 0";
            sql += ");";
            statement.execute(sql);
            
            // إضافة index لتحسين الأداء
            statement.execute("CREATE INDEX IF NOT EXISTS idx_uuid ON player_xp(uuid);");
            
            plugin.getLogger().info("SQLite table 'player_xp' checked/created.");
        } catch (SQLException ex) {
            plugin.getLogger().severe("SQLite table creation error: " + ex.getMessage());
        }
    }
}