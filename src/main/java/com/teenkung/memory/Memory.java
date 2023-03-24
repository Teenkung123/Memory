package com.teenkung.memory;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.teenkung.memory.Commands.CommandHandler;
import com.teenkung.memory.Commands.CommandTabCompleter;
import com.teenkung.memory.EventListener.JoinEvent;
import com.teenkung.memory.EventListener.QuitEvent;
import com.teenkung.memory.Manager.*;
import com.teenkung.memory.Regeneration.Regeneration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public final class Memory extends JavaPlugin {

    private static Memory instance;

    public static HashMap<Player, RegenerationTaskManager> regenerationTask = new HashMap<>();
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

        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(), this);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerManager.addPlayer(player);

                Bukkit.getScheduler().runTaskLater(this, ()-> Regeneration.updatePlayerRegenTask(player), 20);
            }
        }, 30);

        if (ServerManager.areNowBoosting()) {
            ServerManager.countdownRemoveServerBooster(Math.max(ServerManager.getServerBoosterTimeout() - getCurrentUnixSeconds(), 0));
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerDataManager manager = PlayerManager.getDataManager(player);
                if (manager != null) {
                    manager.saveDataToMySQL();
                }
            }
        }, 100, 300*20);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if (Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")).isEnabled()) {
                new PlaceholderAPIHook().register();
            }
        }
    }

    @Override
    public void onDisable() {
        Regeneration.cancelAllTasks();
        Bukkit.getScheduler().getPendingTasks().stream()
                .filter(task -> task.getOwner().equals(this))
                .forEach(BukkitTask::cancel);
        sql.Disconnect();
    }

    public static Memory getInstance() { return instance; }
    public static String colorize(String string) { return IridiumColorAPI.process(string); }
    public static Connection getConnection() { return sql.getConnection(); }
    public static Long getCurrentUnixSeconds() {
        return System.currentTimeMillis()/1000;
    }
    public static String getDurationFormat(long unixTimeStampSecond) {
        Date date = new Date(unixTimeStampSecond * 1000L); // Convert Unix timestamp to Java Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); // Define the date format you want

        return dateFormat.format(date);
    }

    public static String getTimeFormat(long unixTimeStampSecond) {
        Date date = new Date(unixTimeStampSecond * 1000L); // Convert Unix timestamp to Java Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); // Define the date format you want

        return dateFormat.format(date);
    }















}
