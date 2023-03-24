package com.teenkung.memory.Manager;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerManager {

    private static final HashMap<Player, PlayerDataManager> dataManager = new HashMap<>();

    public static void addPlayer(Player player) {
        dataManager.put(player, new PlayerDataManager(player));
    }

    public static void removePlayer(Player player) {
        dataManager.remove(player);
    }

    public static PlayerDataManager getDataManager(Player player) {
        return dataManager.getOrDefault(player, null);
    }

    public static HashMap<Player, PlayerDataManager> getMap() { return dataManager; }

}
