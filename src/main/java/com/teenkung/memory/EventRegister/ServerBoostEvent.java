package com.teenkung.memory.EventRegister;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerBoostEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final double boostMultiplier;
    private final long boostDuration;
    private final long boostTimeout;
    private final double oldBoostMultiplier;
    private final long oldBoostDuration;
    private final long oldBoostTimeout;

    public ServerBoostEvent(double multiplier, long duration, long timeout, double old_multiplier, long old_duration, long old_timeout) {
        this.boostMultiplier = multiplier;
        this.boostDuration = duration;
        this.boostTimeout = timeout;
        this.oldBoostMultiplier = old_multiplier;
        this.oldBoostDuration = old_duration;
        this.oldBoostTimeout = old_timeout;
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

    public double getOldMultiplier() {return oldBoostMultiplier;}

    public long getOldDuration() {return oldBoostDuration;}

    public long getOldTimeout() {return oldBoostTimeout;}

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
