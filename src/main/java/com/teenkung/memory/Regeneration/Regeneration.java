package com.teenkung.memory.Regeneration;

import com.teenkung.memory.ConfigLoader;
import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Manager.ServerManager;
import com.teenkung.memory.Memory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class Regeneration {

    /**
     * Start player's regeneration task and add task to map
     * @param player player you want to start task
     * @param delay delay before first regeneration
     * @param period calculated regeneration rate
     */
    public static void addTask(Player player, double delay, double period) {
        BukkitTask task = startTask(player, delay, period);
        Memory.regenerationTask.put(player, task);
    }

    /**
     * Cancel player's regeneration task and remove task
     * @param player player you want to cancel task
     */
    public static void cancelTask(Player player) {
        if (Memory.regenerationTask.get(player) != null) {
            Memory.regenerationTask.get(player).cancel();
            Memory.regenerationTask.remove(player);
        }
    }

    // Start Running Regeneration task of player
    private static BukkitTask startTask(Player player, double delay, double period) {
        if (player.getName().equals("downYoutube2548")) {
            player.sendMessage("Starting Regeneration");
        }
        return Bukkit.getScheduler().runTaskTimerAsynchronously(Memory.getInstance(), () -> {
            PlayerDataManager playerData = PlayerManager.getDataManager(player);
            int current_mem = playerData.getCurrentMemory();
            int output_mem = current_mem - 1;

            playerData.setCurrentMemory(Math.max(output_mem, 0), true);
            if (player.getName().equals("downYoutube2548")) {
                player.sendMessage("REGENERATION | DELAY: " + delay);
                player.sendMessage("REGENERATION | PERIOD: " + period);
            }

        }, (long)(20 * delay), (long)(20 * period));
    }

    public static void updatePlayerRegenTask(Player player) {
        cancelTask(player);
        PlayerDataManager playerData = PlayerManager.getDataManager(player);
        if (playerData.getCurrentMemory() < 0) { return; }
        player.sendMessage(ChatColor.GREEN+"REGENERATION | PLAYER MULTIPLIER: " +playerData.getBoosterMultiplier());

        double old_multiplied_period = ConfigLoader.getRegenTime(playerData.getRegenLevel()) / playerData.getBoosterMultiplier();

        double period_remain = Math.max(old_multiplied_period - (Memory.getCurrentUnixSeconds() - playerData.getLastRegenerationTime()), 0);
        long full_period = ConfigLoader.getRegenTime(playerData.getRegenLevel());

        double boosted_period_remain;
        double boosted_full_period;

        if (ServerManager.areNowBoosting()) {
            player.sendMessage("REGENERATION | SERVER BOOSTING");
            double server_boost_multiplier = ServerManager.getServerBoosterMultiplier();

            // combine server multiplier and player multiplier
            double combined_multiplier = server_boost_multiplier + playerData.getBoosterMultiplier();

            // calculate remain period with multiplier
            boosted_period_remain = (period_remain / combined_multiplier);
            boosted_full_period = (full_period / combined_multiplier);


        } else {
            player.sendMessage("REGENERATION | NO SERVER BOOSTING");
            // calculate remain period with multiplier
            boosted_period_remain = (period_remain / playerData.getBoosterMultiplier());
            boosted_full_period = (full_period / playerData.getBoosterMultiplier());
        }

        playerData.setLastTaskDelay(boosted_period_remain);
        playerData.setLastTaskPeriod(boosted_full_period);

        if (playerData.getCurrentMemory() > 0) {
            player.sendMessage("REGENERATION | REGEN");
            Regeneration.addTask(player, boosted_period_remain, boosted_full_period);
        }
    }
}
