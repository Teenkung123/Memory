package com.teenkung.memory.Manager;

import com.teenkung.memory.EventRegister.ServerBoostEndEvent;
import com.teenkung.memory.EventRegister.ServerBoostEvent;
import com.teenkung.memory.Memory;
import com.teenkung.memory.Regeneration.Regeneration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

public class ServerManager {

    public static BukkitTask serverCountdownBooster = null;

    public static Double getServerBoosterMultiplier() {
        World world = Bukkit.getWorlds().get(0);
        PersistentDataContainer serverContainer = world.getPersistentDataContainer();
        NamespacedKey multi = new NamespacedKey(Memory.getInstance(), "Memory_Server_Multiplier");
        return serverContainer.getOrDefault(multi, PersistentDataType.DOUBLE, 1D);
    }

    public static Long getServerBoosterDuration() {
        World world = Bukkit.getWorlds().get(0);
        PersistentDataContainer serverContainer = world.getPersistentDataContainer();
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_Server_Duration");
        return serverContainer.getOrDefault(dura, PersistentDataType.LONG, 0L);
    }

    public static Long getServerBoosterTimeout() {
        World world = Bukkit.getWorlds().get(0);
        PersistentDataContainer serverContainer = world.getPersistentDataContainer();
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_Server_Timeout");
        return serverContainer.getOrDefault(dura, PersistentDataType.LONG, 0L);
    }

    public static void setServerBooster(Double multiplier, Long duration) {
        double old_multiplier = getServerBoosterMultiplier();
        long old_duration = getServerBoosterDuration();
        long old_timeout = getServerBoosterTimeout();
        long timeout = Memory.getCurrentUnixSeconds() + duration;

        serverContainer.set(multi, PersistentDataType.DOUBLE, multiplier);
        serverContainer.set(dura, PersistentDataType.LONG, duration);
        serverContainer.set(out, PersistentDataType.LONG, timeout);

        Bukkit.getScheduler().runTask(Memory.getInstance(), () -> Bukkit.getPluginManager().callEvent(new ServerBoostEvent(multiplier, duration, timeout, old_multiplier, old_duration, old_timeout)));

        updateServerBooster(multiplier, duration, timeout);
        countdownRemoveServerBooster(duration);

        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Regeneration.updatePlayerRegenTask(player);
            }
        });
    }

    public static boolean areNowBoosting() {
        return serverTimeout > Memory.getCurrentUnixSeconds();
    }

    public static void resetData() {
        World world = Bukkit.getWorlds().get(0);
        PersistentDataContainer serverContainer = world.getPersistentDataContainer();
        Utils.removeContainer(serverContainer);

        serverMultiplier = 1D;
        serverDuration = 0L;
        serverTimeout = Memory.getCurrentUnixSeconds()-1;
    }

    public static void countdownRemoveServerBooster(long duration) {
        if (serverCountdownBooster != null) {
            serverCountdownBooster.cancel();
        }

        serverCountdownBooster = Bukkit.getScheduler().runTaskLaterAsynchronously(Memory.getInstance(), () -> {

            Bukkit.broadcastMessage(ChatColor.GOLD+"SERVER MANAGER | SERVER BOOSTER ENDED");
            Bukkit.getScheduler().runTask(Memory.getInstance(), () -> Bukkit.getPluginManager().callEvent(new ServerBoostEndEvent(getServerBoosterMultiplier(), getServerBoosterDuration(), getServerBoosterTimeout())));

            resetData();

            Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Regeneration.updatePlayerRegenTask(player);
                }
            });

        }, 20L * duration);
    }
}
