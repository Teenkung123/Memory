package com.teenkung.memory.EventRegister;

import com.teenkung.memory.Manager.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBoostEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final double boostMultiplier;
    private final long boostDuration;
    private final long boostTimeout;
    private final PlayerDataManager playerData;
    private final double delay;
    private final double period;
    private final double oldMultiplier;
    private final long oldDuration;
    private final long oldTimeout;
    private final double oldDelay;
    private final double oldPeriod;


    public PlayerBoostEvent(PlayerDataManager playerData, Player player, double multiplier, long duration, long timeout, double delay, double period, double old_multiplier, long old_duration, long old_timeout, double old_delay, double old_period) {
        this.player = player;
        this.boostMultiplier = multiplier;
        this.boostDuration = duration;
        this.boostTimeout = timeout;
        this.playerData = playerData;
        this.delay = delay;
        this.period = period;
        this.oldMultiplier = old_multiplier;
        this.oldDuration = old_duration;
        this.oldTimeout = old_timeout;
        this.oldDelay = old_delay;
        this.oldPeriod = old_period;
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

    public double getDelay() { return delay; }

    public double getPeriod() { return period; }

    public double getOldMultiplier() {return oldMultiplier;}

    public long getOldDuration() {return oldDuration;}

    public long getOldTimeout() {return oldTimeout;}

    public double getOldDelay() {return oldDelay;}

    public double getOldPeriod() {return oldPeriod;}

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
