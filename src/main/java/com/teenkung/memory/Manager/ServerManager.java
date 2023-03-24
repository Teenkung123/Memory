package com.teenkung.memory.Manager;

import com.teenkung.memory.Memory;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ServerManager {
    private static Double serverMultiplier;
    private static Long serverDuration;
    private static Long serverTimeout;

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

    public static void setBooster(Double multiplier, Long duration) {
        serverMultiplier = multiplier;
        serverDuration = duration;
        serverTimeout = Memory.getCurrentUnixSeconds()+duration;

        //Bukkit.broadcastMessage(colorize("sM: "+serverMultiplier+" sD: "+serverDuration+" sT: "+serverTimeout));

        saveData();
    }

    public static void startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Memory.getInstance(), () -> {
            if (Memory.getCurrentUnixSeconds() >= serverTimeout && serverMultiplier != 1) {
                serverMultiplier = 1D;
                serverDuration = 0L;
                serverTimeout = Memory.getCurrentUnixSeconds()-1;
                saveData();
                //Bukkit.broadcastMessage("§bMemory §7| §aServer multiplier has been reset.");
            }
        }, 0, 20);
    }

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

    public static double getMultiplier() {
        return serverMultiplier;
    }

    public static long getDuration() {
        return serverDuration;
    }

    public static long getTimeOut() {
        return serverTimeout;
    }
}
