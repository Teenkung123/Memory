package com.teenkung.memory.EventListener;

import com.teenkung.memory.ConfigLoader;
import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Manager.ServerManager;
import com.teenkung.memory.Memory;
import com.teenkung.memory.Regeneration.Regeneration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerManager.addPlayer(event.getPlayer());
        //Delay 1 second waiting for another plugin and this plugin to set all things
        Bukkit.getScheduler().runTaskLater(Memory.getInstance(), () -> {
            PlayerDataManager playerData = PlayerManager.getDataManager(event.getPlayer());
            playerData.performOfflineCalculation();

            double delay;
            double period;

            double regen_rate = ConfigLoader.getRegenTime(playerData.getRegenLevel());
            double left_over_time = playerData.getLeftOverTime();

            if (ServerManager.areNowBoosting() && playerData.isNowBoosting()) {

                double server_multiplier = ServerManager.getServerBoosterMultiplier();
                double player_multiplier = playerData.getBoosterMultiplier();

                period = (regen_rate/(server_multiplier+player_multiplier));
                delay = Math.max((regen_rate/(server_multiplier+player_multiplier)) - left_over_time, 0);

                playerData.countdownBooster(Math.max(playerData.getBoosterTimeout() - Memory.getCurrentUnixSeconds(), 0));

            } else if (ServerManager.areNowBoosting()) {

                double server_multiplier = ServerManager.getServerBoosterMultiplier();

                period = (regen_rate/(server_multiplier));
                delay = Math.max((regen_rate/server_multiplier) - left_over_time, 0);

            } else if (playerData.isNowBoosting()) {

                double player_multiplier = playerData.getBoosterMultiplier();

                period = (regen_rate/player_multiplier);
                delay = Math.max((regen_rate/player_multiplier) - left_over_time, 0);

                playerData.countdownBooster(Math.max(playerData.getBoosterTimeout() - Memory.getCurrentUnixSeconds(), 0));

            } else {

                period = (regen_rate);
                delay = Math.max(regen_rate - left_over_time, 0);

            }
            if (playerData.getCurrentMemory() > 0) {
                Regeneration.addTask(event.getPlayer(), delay, period, ServerManager.getServerBoosterMultiplier(), playerData.getBoosterMultiplier());
            }
        }, 20);
    }

}
