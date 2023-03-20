package com.teenkung.memory.EventRegister;

import com.teenkung.memory.Manager.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MemoryChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final int oldMemory;
    private final int newMemory;
    private final PlayerDataManager playerData;

    public MemoryChangeEvent(PlayerDataManager playerData, Player player, int oldMemory, int newMemory) {
        this.player = player;
        this.oldMemory = oldMemory;
        this.newMemory = newMemory;
        this.playerData = playerData;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerDataManager getPlayerData() {
        return playerData;
    }

    public int getOldMemory() {
        return oldMemory;
    }

    public int getNewMemory() {
        return newMemory;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
