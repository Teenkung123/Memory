package com.teenkung.memory;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.teenkung.memory.Commands.CommandHandler;
import com.teenkung.memory.Commands.CommandTabCompleter;
import com.teenkung.memory.Manager.MySQLManager;
import com.teenkung.memory.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public final class Memory extends JavaPlugin {

    private static Memory instance;
    private static MySQLManager sql;
    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        ConfigLoader.loadConfig();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            System.out.println(colorize("&aConnecting to MySQL Database. . ."));
            sql = new MySQLManager();
            try {
                sql.Connect();
                sql.createTable();
                sql.startSendDummyData();
            } catch (SQLException e) {
                System.out.println(colorize("&cFailed to connect to MySQL Database. Disabling Plugin"));
                Bukkit.getPluginManager().disablePlugin(this);
                throw new RuntimeException(e);
            }
        });

        //Register Command Listeners
        Objects.requireNonNull(getCommand("memory")).setExecutor(new CommandHandler());
        Objects.requireNonNull(getCommand("memory")).setTabCompleter(new CommandTabCompleter());

        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerManager.addPlayer(player);
            }
        }, 30);
    }

    @Override
    public void onDisable() { sql.Disconnect(); }

    public static Memory getInstance() { return instance; }
    public static String colorize(String string) { return IridiumColorAPI.process(string); }
    public static Connection getConnection() { return sql.getConnection(); }
    public static Long getCurrentUnixSeconds() {
        return System.currentTimeMillis()/1000;
    }
}
