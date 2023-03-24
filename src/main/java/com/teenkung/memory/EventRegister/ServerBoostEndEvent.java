package com.teenkung.memory.EventRegister;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
@SuppressWarnings("unused")
public class ServerBoostEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final double boostMultiplier;
    private final long boostDuration;
    private final long boostTimeout;

    public ServerBoostEndEvent(double multiplier, long duration, long timeout) {
        this.boostMultiplier = multiplier;
        this.boostDuration = duration;
        this.boostTimeout = timeout;
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
