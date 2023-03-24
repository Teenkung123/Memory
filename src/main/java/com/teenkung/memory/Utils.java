package com.teenkung.memory;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;

public class Utils {

    public static void removeContainer(PersistentDataContainer container) {
        NamespacedKey multi1 = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Multiplier");
        NamespacedKey dura1 = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Duration");
        NamespacedKey out1 = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Timeout");

        NamespacedKey multi2 = new NamespacedKey(Memory.getInstance(), "Memory_Server_Multiplier");
        NamespacedKey dura2 = new NamespacedKey(Memory.getInstance(), "Memory_Server_Duration");
        NamespacedKey out2 = new NamespacedKey(Memory.getInstance(), "Memory_Server_Timeout");

        container.remove(multi1);
        container.remove(dura1);
        container.remove(out1);
        container.remove(multi2);
        container.remove(dura2);
        container.remove(out2);
    }

}
