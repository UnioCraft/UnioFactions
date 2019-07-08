package me.uniodex.uniofactions.listeners;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ProtectionListeners implements Listener {

    private UnioFactions plugin;

    public ProtectionListeners(UnioFactions plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (player.getName().toLowerCase().startsWith("faction_")) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, plugin.getMessage("messages.disallowedName"));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getVehicle() != null) {
            player.leaveVehicle();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("uniofactions.command.bypass")) return;

        List<String> allowedCommands = new ArrayList<>(plugin.getConfig().getStringList("allowedCommands.player"));

        if (player.hasPermission("uniofactions.command.moderator")) {
            allowedCommands.addAll(plugin.getConfig().getStringList("allowedCommands.moderator"));
        }

        String command = event.getMessage().split(" ")[0].replace("/", "").toLowerCase();
        if (allowedCommands.contains(command)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(plugin.getMessage("messages.unknownCommand"));
    }

    // UnioDispenserFix Start
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.DISPENSER) {
            Player p = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            if ((item != null) && (item.getType() == Material.ITEM_FRAME)) {
                event.setCancelled(true);
                p.sendMessage(plugin.getMessage("messages.dispenserFix"));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent event) {
        if (event.getBlock().getType() == Material.ITEM_FRAME) {
            event.setCancelled(true);
        }
    }
    // UnioDispenserFix End

    // EnderPearlCooldown Start
    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunchEPC(ProjectileLaunchEvent event) {
        if (event.getEntityType() != EntityType.ENDER_PEARL) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();

        if (player.hasPermission("uniofactions.epc.bypass")) return;

        if (plugin.getMainManager().getEnderPearlUsers().containsKey(player.getName())) {
            long start = plugin.getMainManager().getEnderPearlUsers().get(player.getName());
            if (System.currentTimeMillis() - start >= plugin.getMainManager().getEnderPearlCooldown()) {
                plugin.getMainManager().getEnderPearlUsers().remove(player.getName());
            } else {
                event.setCancelled(true);
                return;
            }
        }

        plugin.getMainManager().getEnderPearlUsers().put(player.getName(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerInteractEPC(PlayerInteractEvent event) {
        if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.ENDER_PEARL) return;

        Player player = event.getPlayer();

        if (player.hasPermission("uniofactions.epc.bypass")) return;

        if (plugin.getMainManager().getEnderPearlUsers().containsKey(player.getName())) {
            long start = plugin.getMainManager().getEnderPearlUsers().get(player.getName());
            if (System.currentTimeMillis() - start >= plugin.getMainManager().getEnderPearlCooldown()) {
                plugin.getMainManager().getEnderPearlUsers().remove(player.getName());
            } else {
                event.setCancelled(true);
                player.updateInventory();
                player.sendMessage(plugin.getMessage("messages.enderPearlCooldown").replaceAll("%s", getPearlCooldown(player.getName())));
            }
        }
    }

    private String getPearlCooldown(String player) {
        double cooldown = Math.abs((plugin.getMainManager().getEnderPearlUsers().get(player) + plugin.getMainManager().getEnderPearlCooldown() - System.currentTimeMillis()) / 1000.0D);
        String asString = Double.toString(cooldown);
        String fullNumber = asString.split("\\.")[0];
        return fullNumber + "." + (asString.split("\\.")[1].length() > 2 ? asString.split("\\.")[1].substring(0, 2) : asString.split("\\.")[1]);
    }

    // EnderPearlCooldown End

    // Direct import from NoFlyZone
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();

        if (p.hasPermission("uniofactions.fly.bypass")) return;

        List<String> regions = plugin.getMainManager().getNoFlyZones();
        String world = event.getPlayer().getWorld().getName();
        double px = p.getLocation().getX();
        double py = p.getLocation().getY() + 1.0D;
        double pz = p.getLocation().getZ();
        if (p.isFlying()) {
            for (String region : regions) {
                String[] regionInfo = region.split(":");
                if ((regionInfo[0].equalsIgnoreCase(world)) &&
                        (plugin.getWorldGuard().getRegionManager(Bukkit.getWorld(world)).hasRegion(regionInfo[1]))) {
                    ProtectedRegion reg = plugin.getWorldGuard().getRegionManager(Bukkit.getWorld(world)).getRegion(regionInfo[1]);
                    if (reg.contains((int) px, (int) py, (int) pz)) {
                        for (double y = py; y >= 0.0D; y -= 1.0D) {
                            Block topBlock = p.getWorld().getBlockAt(new Location(p.getWorld(), px, y, pz));
                            Block bottomBlock = p.getWorld().getBlockAt(new Location(p.getWorld(), px, y - 1.0D, pz));
                            Block ground = p.getWorld().getBlockAt(new Location(p.getWorld(), px, y - 2.0D, pz));
                            if (((topBlock.isEmpty()) || (topBlock.isLiquid())) && ((bottomBlock.isEmpty()) || (bottomBlock.isLiquid())) &&
                                    (ground.getType().isSolid())) {
                                p.teleport(new Location(p.getWorld(), px, y, pz, p.getLocation().getYaw(), p.getLocation().getPitch()));
                                p.setFlying(false);
                                p.setAllowFlight(false);
                                p.sendMessage(plugin.getMessage("messages.youCantFlyHere"));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("uniofactions.teleport.bypass")) return;

        for (String region : plugin.getMainManager().getNoTeleportZones()) {
            if (Utils.isLocationInRegion(region, event.getTo())) {
                event.setCancelled(true);
                player.sendMessage(plugin.getMessage("messages.youCantTeleportThere"));
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        SpawnReason spawnReason = event.getSpawnReason();
        int limit;
        switch (spawnReason) {
            case BREEDING:
            case EGG:
            case DISPENSE_EGG:
                limit = plugin.getMainManager().getEntityBreedingLimit();
                break;
            case NATURAL:
            case NETHER_PORTAL:
                limit = plugin.getMainManager().getEntityNaturalLimit();
                break;
            case SPAWNER:
                limit = plugin.getMainManager().getEntitySpawnerLimit();
                break;
            case SPAWNER_EGG:
                limit = plugin.getMainManager().getEntitySpawnEggLimit();
                break;
            default:
                return;
        }

        if (Utils.isEntityLimitReached(event.getEntity(), limit, plugin.getMainManager().getEntityLimitRange())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (plugin.getCombatLogX() == null) return;
        if (!CombatUtil.isInCombat(player)) return;

        if (event.getBlockPlaced().getType().equals(Material.WEB)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessage("messages.webInCombatDisabled"));
        }
    }
}
