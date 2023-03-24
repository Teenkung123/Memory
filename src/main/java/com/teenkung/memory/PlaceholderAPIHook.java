package com.teenkung.memory;

import com.teenkung.memory.Manager.PlayerDataManager;
import com.teenkung.memory.Manager.PlayerManager;
import com.teenkung.memory.Manager.ServerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

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
    public String onRequest(OfflinePlayer player, @Nullable String params) {
        if (params == null) {
            return "";
        }
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
                return String.valueOf(ConfigLoader.getRegenTime(manager.getRegenLevel()) / (manager.getBoosterMultiplier()+ ServerManager.getMultiplier()));
            } else if (params.equalsIgnoreCase("getMaxCapacity")) {
                return String.valueOf(ConfigLoader.getMax(manager.getMaxCapacityLevel()));
            } else if (params.equalsIgnoreCase("getTimeLeft")) {
                return String.valueOf(manager.getNextPerionIn());
            } else if (params.equalsIgnoreCase("getTotalTimeLeft")) {
                return String.valueOf(manager.getFillTime());
            }
        } else {
            return null;
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
