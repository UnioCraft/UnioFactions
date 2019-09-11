package me.uniodex.uniofactions.managers;

import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobsManager {

    private UnioFactions plugin;
    private Map<Player, Block> lastBrokenBlocks = new HashMap<>();

    public JobsManager(UnioFactions plugin) {
        this.plugin = plugin;
    }

    public void addBrokenBlock(Player player, Block block) {
        lastBrokenBlocks.put(player, block);
    }

    public Block getLastBrokenBlock(Player player) {
        return lastBrokenBlocks.get(player);
    }

    public void clearPlayerData(Player player) {
        lastBrokenBlocks.remove(player);
    }

    public boolean isLocationInMine(Location location) {
        List<String> mineNames = new ArrayList<>();
        mineNames.add(plugin.getConfig().getString("mineNames.default"));
        mineNames.add(plugin.getConfig().getString("mineNames.vip"));
        mineNames.add(plugin.getConfig().getString("mineNames.uvip"));
        mineNames.add(plugin.getConfig().getString("mineNames.uvip+"));

        for (String mineName : mineNames) {
            if (Utils.isLocationInArea(location, mineName)) {
                return true;
            }
        }
        return false;
    }
}
