package me.uniodex.uniofactions.managers;

import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class VIPManager {

    private UnioFactions plugin;

    public VIPManager(UnioFactions plugin) {
        this.plugin = plugin;
    }

    public void giveVIPReward(String player, VIPType vipType) {
        // All
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

        if (plugin.getPermission().playerHas(null, offlinePlayer, "plots.merge.2")) {
            plugin.getPermission().playerAdd(null, offlinePlayer, "plots.merge.3");
        } else if (plugin.getPermission().playerHas(null, offlinePlayer, "plots.merge.1")) {
            plugin.getPermission().playerAdd(null, offlinePlayer, "plots.merge.2");
        } else {
            plugin.getPermission().playerAdd(null, offlinePlayer, "plots.merge.1");
        }
    }

    public VIPType getVIPType(String vipType) {
        for (VIPType type : VIPType.values()) {
            if (vipType.equalsIgnoreCase(type.toString())) {
                return type;
            }
        }
        return null;
    }

    public enum VIPType {
        VIP, VIPPlus, UVIP, UVIPPlus
    }
}
