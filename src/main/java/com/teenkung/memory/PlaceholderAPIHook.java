package com.teenkung.memory;

import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Manager.ServerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    @SuppressWarnings("NullableProblems")
    @Override
    public String getIdentifier() {
        return "memory";
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String getAuthor() {
        return "Teenkung123, downYoutube2548";
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player.isOnline()) {
            Player target = player.getPlayer();
            PlayerDataManager manager = PlayerManager.getDataManager(target);
            if(params.equalsIgnoreCase("currentMemory")){
                return String.valueOf(manager.getCurrentMemory());
            } else if (params.equalsIgnoreCase("regenLevelDisplay")) {
                return ConfigLoader.getDisplay(manager.getRegenLevel());
            } else if (params.equalsIgnoreCase("capacityLevelDisplay")) {
                return ConfigLoader.getDisplay(manager.getMaxCapacityLevel());
            } else if (params.equalsIgnoreCase("getRegenerationRate")) {
                return String.valueOf(ConfigLoader.getRegenTime(manager.getRegenLevel()) / (manager.getBoosterMultiplier()+ ServerManager.getServerBoosterMultiplier()));
            } else if (params.equalsIgnoreCase("getMaxCapacity")) {
                return String.valueOf(ConfigLoader.getMax(manager.getMaxCapacityLevel()));
            }
        } else {
            return null;
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
