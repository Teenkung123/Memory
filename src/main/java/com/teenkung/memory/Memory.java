package com.teenkung.memory;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.teenkung.memory.Commands.CommandHandler;
import com.teenkung.memory.Commands.CommandTabCompleter;
import com.teenkung.memory.Manager.MySQLManager;
import com.teenkung.memory.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

public final class Memory extends JavaPlugin {

    private static Memory instance;

    public static HashMap<Player, BukkitTask> regenerationTask = new HashMap<>();
    private static MySQLManager sql;
    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        ConfigLoader.loadConfig();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            System.out.println(ConfigLoader.getMessage("MySQL.Connecting", false));
            sql = new MySQLManager();
            try {
                sql.Connect();
                System.out.println(ConfigLoader.getMessage("MySQL.Connected", false));
                sql.createTable();
                sql.startSendDummyData();
            } catch (SQLException e) {
                System.out.println(ConfigLoader.getMessage("MySQL.Error", false));
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
