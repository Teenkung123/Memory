package com.teenkung.memory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
@SuppressWarnings("unused")
public class ConfigLoader {

    private static final HashMap<Integer, String> levelDisplay = new HashMap<>();
    private static final HashMap<Integer, Integer> levelRegenTime = new HashMap<>();
    private static final HashMap<Integer, Integer> levelMax = new HashMap<>();
    private static String name;

    /**
     * Request the name of this currency
     * @return the name of this currency
     */
    public static String getMemoryName() { return name; }

    /**
     * Request the List of all Levels
     * @return the list of all levels
     */
    public static List<Integer> getLevelList() { return levelDisplay.keySet().stream().toList(); }

    /**
     * Request the Display name of the level you want to get
     * @param level the level you want to get
     * @return the Display name of the level you want to get
     */
    public static String getDisplay(Integer level) { return levelDisplay.getOrDefault(level, ""); }

    /**
     * Request the Regeneration Time of the level you want to get
     * @param level the level you want to get
     * @return the Regeneration Time of the level you want to get
     */
    public static Integer getRegenTime(Integer level) { return levelRegenTime.getOrDefault(level, 1000000); }
    /**
     * Request the Maximum Capacity of the level you want to get
     * @param level the level you want to get
     * @return the Maximum Capacity of the level you want to get
     */
    public static Integer getMax(Integer level) { return levelMax.getOrDefault(level, 0); }

    public static String getMessage(String path, boolean IncludePrefix) {
        if (IncludePrefix) {
            return Memory.getInstance().getConfig().getString("Messages.Prefix", "") + Memory.getInstance().getConfig().getString("Messages."+path, "");
        } else {
            return Memory.getInstance().getConfig().getString("Messages."+path, "");
        }
    }

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
        ConfigurationSection general = config.getConfigurationSection("Levels");
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
