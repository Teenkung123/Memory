package com.teenkung.memory;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.teenkung.memory.Commands.CommandHandler;
import com.teenkung.memory.Commands.CommandTabCompleter;
import com.teenkung.memory.EventListener.JoinEvent;
import com.teenkung.memory.EventListener.QuitEvent;
import com.teenkung.memory.Manager.MySQLManager;
import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Manager.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public final class Memory extends JavaPlugin {

    private static Memory instance;

    private static MySQLManager sql;
    @Override
    public void onEnable() {
        instance = this;

        //Server Manager Class Loading (First Priority) / Must be done before other data loading
        ServerManager.LoadData();

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        ConfigLoader.loadConfig();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            System.out.println(colorize(ConfigLoader.getMessage("MySQL.Connecting", false)));
            sql = new MySQLManager();
            try {
                sql.Connect();
                System.out.println(colorize(ConfigLoader.getMessage("MySQL.Connected", false)));
                sql.createTable();
                sql.startSendDummyData();
            } catch (SQLException e) {
                System.out.println(colorize(ConfigLoader.getMessage("MySQL.Error", false)));
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
                //Delay 1 second waiting for another plugin and this plugin to set all things
                Bukkit.getScheduler().runTaskLater(Memory.getInstance(), () -> {
                    PlayerDataManager playerData = PlayerManager.getDataManager(player);
                    playerData.startGenerationTask();
                }, 20);
            }
        }, 30);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerDataManager manager = PlayerManager.getDataManager(player);
                if (manager != null) {
                    manager.saveDataToMySQL();
                }
            }
            ServerManager.startTimer();
        }, 100, 300*20);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if (Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")).isEnabled()) {
                new PlaceholderAPIHook().register();
            }
        }
    }

    @Override
    public void onDisable() {
        for (Player player : PlayerManager.getMap().keySet()) {
            PlayerDataManager manager = PlayerManager.getDataManager(player);
            manager.saveDataToMySQL(false);
        }
        sql.Disconnect();
    }

    public static Memory getInstance() { return instance; }
    public static String colorize(String string) { return IridiumColorAPI.process(string); }
    public static Connection getConnection() { return sql.getConnection(); }
    public static Long getCurrentUnixSeconds() {
        return System.currentTimeMillis()/1000;
    }

    public static String formatUnixTime(long unixTime) {
        // Convert Unix time to LocalDateTime
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTime), ZoneId.systemDefault());

        // Create a DateTimeFormatter with the Thai Locale and custom format
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(instance.getConfig().getString("General.Pattern", "d MMMM YYYY เวลา HH:mm:ss น."), new Locale(instance.getConfig().getString("General.Language", "th"), instance.getConfig().getString("General.Country", "TH")));

        // Format the LocalDateTime using the DateTimeFormatter
        return dateTime.format(formatter);

    }
    public static void replaceList(ArrayList<String> list, ArrayList<String> replacement, String check) {
        int index = list.indexOf(check);
        if (index != -1) {
            list.remove(index);
            list.addAll(index, replacement);
        }
    }


}
