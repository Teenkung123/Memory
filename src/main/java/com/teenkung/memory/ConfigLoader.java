package com.teenkung.memory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

public class ConfigLoader {

    private static final HashMap<Integer, String> levelDisplay = new HashMap<>();
    private static final HashMap<Integer, Integer> levelRegenTime = new HashMap<>();
    private static final HashMap<Integer, Integer> levelMax = new HashMap<>();
    private static String name;
    public static String getMemoryName() { return name; }
    public static List<Integer> getLevelList() { return levelDisplay.keySet().stream().toList(); }
    public static String getDisplay(Integer level) { return levelDisplay.getOrDefault(level, ""); }
    public static Integer getRegenTime(Integer level) { return levelRegenTime.getOrDefault(level, 1000000); }
    public static Integer getMax(Integer level) { return levelMax.getOrDefault(level, 0); }

    public static void reloadConfig() {
        name = "";
        Memory.getInstance().reloadConfig();
        levelDisplay.clear();
        levelRegenTime.clear();
        levelMax.clear();
        loadConfig();
    }

    public static void loadConfig() {
        Memory instance = Memory.getInstance();
        FileConfiguration config = instance.getConfig();
        ConfigurationSection general = config.getConfigurationSection("Level");
        ConfigurationSection regen = config.getConfigurationSection("Regen-Time");
        ConfigurationSection energy = config.getConfigurationSection("Max-Energy");
        name = config.getString("Memory-Name");
        if (general != null) {
            for (String key : general.getKeys(false)) {
                levelDisplay.put(Integer.valueOf(key), config.getString("Levels."+key+".Display"));
                levelRegenTime.put(Integer.valueOf(key), config.getInt("Levels."+key+".Regen-Time"));
                levelMax.put(Integer.valueOf(key), config.getInt("Levels."+key+".Max-Energy"));
            }
        }
    }
}
