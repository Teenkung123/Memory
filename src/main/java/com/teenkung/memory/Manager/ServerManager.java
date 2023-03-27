package com.teenkung.memory.Manager;

import com.teenkung.memory.ConfigLoader;
import com.teenkung.memory.Memory;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.teenkung.memory.Memory.colorize;

public class ServerManager {
    private static Double serverMultiplier;
    private static Long serverDuration;
    private static Long serverTimeout;

    /**
     * Load the Server Booster from PersistentDataContainer of the server
     */
    public static void LoadData() {
        World world = Bukkit.getWorlds().get(0);
        PersistentDataContainer serverContainer = world.getPersistentDataContainer();
        NamespacedKey multi = new NamespacedKey(Memory.getInstance(), "Memory_Server_Multiplier");
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_Server_Duration");
        NamespacedKey out = new NamespacedKey(Memory.getInstance(), "Memory_Server_Timeout");
        serverMultiplier = serverContainer.getOrDefault(multi, PersistentDataType.DOUBLE, 1D);
        serverDuration = serverContainer.getOrDefault(dura, PersistentDataType.LONG, 0L);
        serverTimeout = serverContainer.getOrDefault(out, PersistentDataType.LONG, Memory.getCurrentUnixSeconds()-1);

    }

    /**
     * set the booster of the server
     * @param multiplier multiplier you want to set (x2, x3, x4, ...)
     * @param duration duration of the booster (in seconds)
     */
    public static void setBooster(Double multiplier, Long duration) {
        serverMultiplier = multiplier;
        serverDuration = duration;
        serverTimeout = Memory.getCurrentUnixSeconds()+duration;

        saveData();
    }

    /**
     * Start the timer to count the server booster, ONLY USE ONCE when the server start
     */
    public static void startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Memory.getInstance(), () -> {
            if (Memory.getCurrentUnixSeconds() >= serverTimeout && serverMultiplier != 1) {
                serverMultiplier = 1D;
                serverDuration = 0L;
                serverTimeout = Memory.getCurrentUnixSeconds()-1;
                saveData();
                Bukkit.broadcastMessage(colorize(ConfigLoader.getMessage("Info.Global_Boost_End", true)));
            }
        }, 0, 20);
    }

    /**
     * Save the server booster to PersistentDataContainer of the server
     */
    public static void saveData() {
        World world = Bukkit.getWorlds().get(0);
        PersistentDataContainer serverContainer = world.getPersistentDataContainer();
        NamespacedKey multi = new NamespacedKey(Memory.getInstance(), "Memory_Server_Multiplier");
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_Server_Duration");
        NamespacedKey out = new NamespacedKey(Memory.getInstance(), "Memory_Server_Timeout");
        serverContainer.set(multi, PersistentDataType.DOUBLE, 1D);
        serverContainer.set(dura, PersistentDataType.LONG, 0L);
        serverContainer.set(out, PersistentDataType.LONG, Memory.getCurrentUnixSeconds()-1);
    }

    /**
     * Get the server booster multiplier
     * @return multiplier of the server booster
     */
    public static double getMultiplier() {
        return serverMultiplier;
    }

    /**
     * Get the server booster duration
     * @return duration of the server booster
     */
    public static long getDuration() {
        return serverDuration;
    }

    /**
     * Get the server booster timeout
     * @return timeout of the server booster
     */
    public static long getTimeOut() {
        return serverTimeout;
    }
}
