package com.teenkung.memory.EventListener;

import com.teenkung.memory.Manager.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerManager.addPlayer(event.getPlayer());
    }

}
