package com.teenkung.memory.EventListener;

import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Memory;
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
            playerData.startGenerationTask();
        }, 20);
    }

}
