package com.teenkung.memory.EventListener;

import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Memory;
import com.teenkung.memory.Regeneration.Regeneration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Regeneration.cancelTask(event.getPlayer());
        if (PlayerManager.getDataManager(event.getPlayer()).getCountdownBooster() != null) {
            PlayerManager.getDataManager(event.getPlayer()).getCountdownBooster().cancel();
        }

        PlayerManager.getDataManager(event.getPlayer()).setLeaveTime(Memory.getCurrentUnixSeconds());
        PlayerManager.getDataManager(event.getPlayer()).saveDataToMySQL();
        PlayerManager.removePlayer(event.getPlayer());
    }

}
