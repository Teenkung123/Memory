package com.teenkung.memory.EventRegister;

import com.teenkung.memory.Manager.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
@SuppressWarnings("unused")
public class PlayerBoostEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final double boostMultiplier;
    private final long boostDuration;
    private final long boostTimeout;
    private final PlayerDataManager playerData;

    public PlayerBoostEndEvent(PlayerDataManager playerData, Player player, double multiplier, long duration, long timeout) {
        this.player = player;
        this.boostMultiplier = multiplier;
        this.boostDuration = duration;
        this.boostTimeout = timeout;
        this.playerData = playerData;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerDataManager getPlayerData() {
        return playerData;
    }

    public double getBoostMultiplier() {
        return boostMultiplier;
    }

    public long getBoostDuration() {
        return boostDuration;
    }

    public long getBoostTimeout() {
        return boostTimeout;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
