package me.uniodex.uniofactions.managers;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import lombok.Getter;
import lombok.Setter;
import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MainManager {

    private UnioFactions plugin;
    @Getter
    private Map<Integer, List<String>> commandListPages;
    @Getter
    private Map<String, String> commands = new HashMap<>();
    @Getter
    private Map<String, Long> enderPearlUsers = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private long enderPearlCooldown;
    @Getter
    private List<String> noFlyZones = new ArrayList<>();
    @Getter
    private List<String> noTeleportZones = new ArrayList<>();

    @Getter
    int entityLimitRange;
    @Getter
    int entityBreedingLimit;
    @Getter
    int entityNaturalLimit;
    @Getter
    int entitySpawnerLimit;
    @Getter
    int entitySpawnEggLimit;

    public MainManager(UnioFactions plugin) {
        this.plugin = plugin;
        setupVariables();
        updateCommandListPages();
        loadCommandInstances();
    }

    public void setupVariables() {
        enderPearlCooldown = plugin.getConfig().getLong("settings.enderPearlCooldown", 3) * 1000;
        noFlyZones.addAll(plugin.getConfig().getStringList("flyDisabledRegions"));
        noTeleportZones.addAll(plugin.getConfig().getStringList("teleportDisabledRegions"));
        entityLimitRange = plugin.getConfig().getInt("settings.entityLimits.range");
        entityBreedingLimit = plugin.getConfig().getInt("settings.entityLimits.breeding");
        entityNaturalLimit = plugin.getConfig().getInt("settings.entityLimits.natural");
        entitySpawnerLimit = plugin.getConfig().getInt("settings.entityLimits.spawner");
        entitySpawnEggLimit = plugin.getConfig().getInt("settings.entityLimits.spawnegg");
    }

    public void updateCommandListPages() {
        commandListPages = new HashMap<>();
        List<String> pageLines;

        List<String> contentClone = new ArrayList<>(plugin.getConfig().getStringList("messages.commandCommandsContent"));

        for (int i = 1; i <= (contentClone.size() / 8); i++) {
            if (contentClone.size() < 1) break;
            pageLines = new ArrayList<>();
            for (int x = 0; x < 8; x++) {
                if (contentClone.size() < 1) break;
                pageLines.add(contentClone.get(0));
                contentClone.remove(0);
            }
            commandListPages.put(i, pageLines);
        }
    }

    public void loadCommandInstances() {
        commands.clear();
        for (String command : plugin.getConfig().getConfigurationSection("commandInstances").getKeys(false)) {
            String instance = plugin.getConfig().getConfigurationSection("commandInstances").getString(command);
            commands.put(command, instance);
        }
    }

    @SuppressWarnings("unused") // This will only used in specific events.
    private void unclaimRandomClaims(int amount, String factionId) {
        List<FLocation> claims = new ArrayList<>(Board.getInstance().getAllClaims(factionId));
        Collections.shuffle(claims);

        if (claims.size() <= 0) {
            return;
        }

        if (claims.size() < amount) {
            Board.getInstance().unclaimAll(factionId);
            return;
        }

        List<String> coords = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            String[] chunks = claims.get(i).getCoordString().split(",");
            Block b = Bukkit.getWorld("world").getChunkAt(Integer.parseInt(chunks[0]), Integer.parseInt(chunks[1])).getBlock(8, 0, 8);

            int X = b.getX();
            int Y = b.getY();
            int Z = b.getZ();

            coords.add(X + ", " + Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), X, Y, Z)) + ", " + Z);
            Board.getInstance().removeAt(claims.get(i));
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b&lUNIOCRAFT &2-> &aAşağıdaki koordinatlardaki claimler salındı! Hemen yakala!"));
        for (String coord : coords) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4&l➸ &c" + coord));
        }
    }

}
