package com.teenkung.memory.Regeneration;

import com.teenkung.memory.ConfigLoader;
import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Manager.RegenerationTaskManager;
import com.teenkung.memory.Manager.ServerManager;
import com.teenkung.memory.Memory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Regeneration {

    /**
     * Start player's regeneration task and add task to map
     * @param player player you want to start task
     * @param delay delay before first regeneration
     * @param period calculated regeneration rate
     */
    public static void addTask(Player player, double delay, double period, Double serverMultiplier, Double playerMultiplier) {
        RegenerationTaskManager task = startTask(player, delay, period, serverMultiplier, playerMultiplier);
        Memory.regenerationTask.put(player, task);
    }

    /**
     * Cancel player's regeneration task and remove task
     * @param player player you want to cancel task
     */
    public static void cancelTask(Player player) {
        if (Memory.regenerationTask.get(player) != null) {
            Memory.regenerationTask.get(player).getTask().cancel();
            Memory.regenerationTask.remove(player);
        }
    }

    /**
     * Cancel all Player's Regeneration Task and remove the task
     * Used for Plugin Disabling Event
     */
    public static void cancelAllTasks() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Memory.regenerationTask.get(player) != null) {
                Memory.regenerationTask.get(player).getTask().cancel();
                Memory.regenerationTask.remove(player);
            }
        }
    }

    // Start Running Regeneration task of player
    private static RegenerationTaskManager startTask(Player player, double delay, double period, Double serverMultiplier, Double playerMultiplier) {
        if (player.getName().equals("downYoutube2548")) {
            player.sendMessage(ChatColor.AQUA+"Starting Regeneration");
            player.sendMessage(ChatColor.AQUA+"REGENERATION | DELAY: " + delay);
            player.sendMessage(ChatColor.AQUA+"REGENERATION | PERIOD: " + period);
        }
        return new RegenerationTaskManager(() -> {
            PlayerDataManager playerData = PlayerManager.getDataManager(player);
            int current_mem = playerData.getCurrentMemory();
            int output_mem = current_mem - 1;

            playerData.setCurrentMemory(Math.max(output_mem, 0), true);
            if (player.getName().equals("downYoutube2548")) {
                player.sendMessage("REGENERATION | DELAY: " + delay);
                player.sendMessage("REGENERATION | PERIOD: " + period);
            }

        }, (long)(20 * delay), (long)(20 * period), serverMultiplier, playerMultiplier);
    }

    public static void updatePlayerRegenTask(Player player) {
        PlayerDataManager playerData = PlayerManager.getDataManager(player);
        double old_player_multiplier = (playerData.getRegenerationTask() != null) ? playerData.getRegenerationTask().getPlayerMultiplier() : 1.0;
        double old_server_multiplier = (playerData.getRegenerationTask() != null) ? playerData.getRegenerationTask().getServerMultiplier() : 1.0;
        double old_task_multiplier;
        if (old_player_multiplier == 1.0 && old_server_multiplier == 1.0) {
            old_task_multiplier = 1.0;
        } else if (old_player_multiplier == 1.0) {
            old_task_multiplier = old_server_multiplier;
        } else if (old_server_multiplier == 1.0) {
            old_task_multiplier = old_player_multiplier;
        } else {
            old_task_multiplier = old_player_multiplier+old_server_multiplier;
        }


        player.sendMessage(ChatColor.GREEN+"REGENERATION | PLAYER OLD MULTIPLIER: " + old_task_multiplier);

        /*
        double old_multiplied_period = ConfigLoader.getRegenTime(playerData.getRegenLevel()) / old_task_multiplier;
        double new_multiplied_period = ConfigLoader.getRegenTime(playerData.getRegenLevel()) / playerData.getBoosterMultiplier();

        double period_remain;
        if (old_multiplied_period >= new_multiplied_period) {
            period_remain = Math.max(old_multiplied_period - (Memory.getCurrentUnixSeconds() - playerData.getLastRegenerationTime()), 0);
        }
        else  {
            period_remain = Math.max(new_multiplied_period - ((Memory.getCurrentUnixSeconds() - playerData.getLastRegenerationTime()) * old_task_multiplier), 0);

        }

         */
        long full_period = ConfigLoader.getRegenTime(playerData.getRegenLevel());

        //double boosted_period_remain = (period_remain / playerData.getBoosterMultiplier());

        double boosted_period_remain = calculateRemainingTime(playerData);
        double boosted_full_period;

        player.sendMessage(ChatColor.LIGHT_PURPLE+"REGENERATION | PERIOD REMAIN: " + boosted_period_remain);

        Bukkit.broadcastMessage(ChatColor.DARK_AQUA+"REGENERATION | MULTI: "+playerData.getBoosterMultiplier());
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA+"REGENERATION | DURA: "+playerData.getBoosterDuration());
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA+"REGENERATION | OUT: "+playerData.getBoosterTimeout());

        if (ServerManager.areNowBoosting() && playerData.isNowBoosting()) {
            Bukkit.broadcastMessage("REGENERATION | BOOST ALL");
            double server_boost_multiplier = ServerManager.getServerBoosterMultiplier();

            // combine server multiplier and player multiplier
            double combined_multiplier = server_boost_multiplier + playerData.getBoosterMultiplier();

            // calculate remain period with multiplier
            boosted_full_period = (full_period / combined_multiplier);


        } else if (ServerManager.areNowBoosting()) {
            Bukkit.broadcastMessage("REGENERATION | SERVER BOOST");
            // calculate remain period with multiplier
            boosted_full_period = (full_period / ServerManager.getServerBoosterMultiplier());

        } else if (playerData.isNowBoosting()) {
            Bukkit.broadcastMessage("REGENERATION | PLAYER BOOST");
            boosted_full_period = (full_period / playerData.getBoosterMultiplier());
        } else {
            Bukkit.broadcastMessage("REGENERATION | NO BOOST");
            boosted_full_period = ConfigLoader.getRegenTime(playerData.getRegenLevel());
        }


        cancelTask(player);
        if (playerData.getCurrentMemory() < 0) { return; }

        if (playerData.getCurrentMemory() > 0) {
            player.sendMessage("REGENERATION | REGEN");
            Regeneration.addTask(player, boosted_period_remain, boosted_full_period, ServerManager.getServerBoosterMultiplier(), playerData.getBoosterMultiplier());
        }
    }

    private static double calculateRemainingTime(PlayerDataManager playerManager) {

        double period = ConfigLoader.getRegenTime(playerManager.getRegenLevel());
        double serverDuration = ServerManager.getServerBoosterTimeout();
        double playerDuration = playerManager.getBoosterTimeout();
        double serverMultiplier = ServerManager.getServerBoosterMultiplier();
        double playerMultiplier = playerManager.getBoosterMultiplier();

        long startTime = playerManager.getLastRegenerationTime();
        long currentTime = Memory.getCurrentUnixSeconds();
        long serverBoostStart = ServerManager.getServerBoosterTimeout()-ServerManager.getServerBoosterDuration();
        long playerBoostStart = playerManager.getBoosterTimeout()-playerManager.getBoosterDuration();

        // Check that input values are valid
        if (period <= 0 || serverMultiplier <= 0 || playerMultiplier <= 0 || currentTime < startTime) {
            return 0;
        }

        // Calculate elapsed durations, adjusting for multipliers if not equal to 1
        double elapsedServerDuration = (serverMultiplier != 1.0) ? Math.max(0, serverDuration - startTime) * serverMultiplier : 0;
        double elapsedPlayerDuration = (playerMultiplier != 1.0) ? Math.max(0, playerDuration - startTime) * playerMultiplier : 0;

        // Calculate remaining time based on elapsed durations
        double remainingTime = (period - Math.max(elapsedServerDuration, elapsedPlayerDuration)) +
                (Math.max(elapsedServerDuration - elapsedPlayerDuration, 0) / playerMultiplier) +
                (Math.max(elapsedPlayerDuration - elapsedServerDuration, 0) / serverMultiplier) +
                (Math.min(elapsedServerDuration, elapsedPlayerDuration) / (serverMultiplier + playerMultiplier));

        // Adjust remaining time based on server boost
        if (serverBoostStart >= startTime && serverBoostStart < startTime + period && serverDuration > 0 && serverMultiplier > 1) {
            double serverBoostEnd = serverBoostStart + serverDuration;
            double serverBoostRemaining = Math.max(0, serverBoostEnd - currentTime);
            if (serverBoostRemaining > 0) {
                remainingTime += serverBoostRemaining * (serverMultiplier - 1);
            }
        }

        // Adjust remaining time based on player boost
        if (playerBoostStart >= startTime && playerBoostStart < startTime + period && playerDuration > 0 && playerMultiplier > 1) {
            double playerBoostEnd = playerBoostStart + playerDuration;
            double playerBoostRemaining = Math.max(0, playerBoostEnd - currentTime);
            if (playerBoostRemaining > 0) {
                remainingTime += playerBoostRemaining * (playerMultiplier - 1);
            }
        }

        // Adjust remaining time based on current time and start time
        remainingTime -= (currentTime - startTime);

        // Ensure that remaining time is not negative
        Bukkit.broadcastMessage("REGENERATION | REMAINING TIME: "+remainingTime);
        return Math.max(0, remainingTime);
    }
}
