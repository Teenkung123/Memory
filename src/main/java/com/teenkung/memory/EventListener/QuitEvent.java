package com.teenkung.memory.EventListener;

import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Memory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        PlayerManager.getDataManager(event.getPlayer()).setLeaveTime(Memory.getCurrentUnixSeconds());
        PlayerManager.getDataManager(event.getPlayer()).saveDataToMySQL();
        PlayerManager.removePlayer(event.getPlayer());
    }

}
