package com.teenkung.memory.Manager;

import com.teenkung.memory.Memory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

public class RegenerationTaskManager {

    private final Long delay;
    private final Long period;
    private final Double serverMultiplier;
    private final Double playerMultiplier;
    private final BukkitTask task;

    public RegenerationTaskManager(Runnable task, Long delay, Long period, Double serverMultiplier, Double playerMultiplier) {
        this.delay = delay;
        this.period = period;
        this.serverMultiplier = serverMultiplier;
        this.playerMultiplier = playerMultiplier;
        Bukkit.broadcastMessage(ChatColor.BLUE+"REGENERATION TASK MANAGER | SERVER MULTIPLIER: "+serverMultiplier);
        Bukkit.broadcastMessage(ChatColor.BLUE+"REGENERATION TASK MANAGER | PLAYER MULTIPLIER: "+playerMultiplier);
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(Memory.getInstance(), task, delay, period);
    }

    public Long getDelay() { return delay; }
    public Long getPeriod() { return period; }
    public Double getServerMultiplier() { return serverMultiplier; }

    public Double getPlayerMultiplier() { return playerMultiplier; }

    public BukkitTask getTask() { return task; }

}
