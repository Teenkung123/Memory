package com.teenkung.memory.EventListener;

import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        PlayerDataManager manager = PlayerManager.getDataManager(event.getPlayer());
        manager.cancelTask();
        manager.setLeaveTime();
        manager.saveDataToMySQL();
        PlayerManager.removePlayer(event.getPlayer());
    }

}
